package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.datastore.Entity;

public class DSConverter {

	public static enum DataTypes {
		FRAC, LIST, SET, MAP, MAP_KEYS, 
	};
	private static final String lenSfx = "_";
	private static final String sep = "_";
	private static final String keySfx = "__key";
	private static final String valSfx = "__val";
	private static final String numSfx = "__num";
	private static final String denomSfx = "__denom";

	public static String getIthName(String name, long i)
	{
		return name+sep+i;
	}

	private static interface Getter {
		public Object get(String k);
		public Object rawGet(String k);
	}

	private static interface Setter extends Getter {
		public void set(String k, Object v);
		public void set(String k, Object v, Object meta);//meta data on the value to assign to the key
		public void rmv(String k);
		public void rawSet(String k, Object v);
	}

	//Basic Wrappers

	private static Getter makeGetter(final HttpServletRequest req)
	{
		return new Getter() {
			public Object get(String s)
			{
				return req.getParameter(s);
			}
			public Object rawGet(String k)
			{
				return get(k);
			}
		};
	}

	private static Getter makeGetter(final Entity e)
	{
		return new Getter() {
			public Object get(String k)
			{
				return e.getProperty(k);
			}
			public Object rawGet(String k)
			{
				return get(k);
			}
		};
	}

	private static Getter makeGetter(final Map<String, Object> m)
	{
		return new Getter() {
			public Object get(String k)
			{
				return m.get(k);
			}
			public Object rawGet(String k)
			{
				return get(k);
			}
		};
	}

	private static Setter makeSetter(final Entity e)
	{
		final Getter g = makeGetter(e);
		return new Setter() {
			public Object rawGet(String k) { return g.rawGet(k); }
			public Object get(String k) { return g.get(k); }
			public void set(String k, Object v)
			{
				e.setProperty(k, v);
			}
			public void set(String k, Object v, Object meta) {set(k,v);}
			public void rmv(String k)
			{
				e.removeProperty(k);
			}
			public void rawSet(String k, Object v)
			{
				if(v == null)
					rmv(k);
				else
					set(k, v);
			}
		};
	}

	private static Setter makeSetter(final Map<String, Object> m)
	{
		final Getter g = makeGetter(m);
		return new Setter() {
			public Object rawGet(String k) { return g.rawGet(k); }
			public Object get(String k) { return g.get(k); }
			public void set(String k, Object v)
			{
				m.put(k, v);
			}
			public void set(String k, Object v, Object meta) {set(k,v);}
			public void rmv(String k)
			{
				m.remove(k);
			}
			public void rawSet(String k, Object v)
			{
				if(v == null)
					rmv(k);
				else
					set(k, v);
			}
		};
	}

	private static Long getLen(Getter g, String k)
	{
		Object len = g.rawGet(k+lenSfx);
		if((len == null) || (len instanceof Long))
			return (Long) len;
		else if(len instanceof String) try {
			return Long.parseLong((String)len);
		} catch(NumberFormatException e) {
			throw new NumberFormatException("\""+k+"\"'s length indicator is malformated");
		} else
			throw new NumberFormatException("\""+k+"\"'s length indicator is of an unknown type");
	}

	private static void setLen(Setter s, String k, Long len)
	{
		s.rawSet(k+lenSfx, len);
	}

	//Frac

	private static Getter makeFracGetter(final Getter g)
	{
		return new Getter() {
			public Object rawGet(String k) { return g.rawGet(k); }
			public Object get(String k)
			{
				Long num = (Long) g.get(k+numSfx);
				Long denom = (Long) g.get(k+denomSfx);
				return (num == null || denom == null) ? null : new Frac(num, denom);
			}
		};
	}

	private static Setter makeFracSetter(final Setter s)
	{
		final Getter g = makeFracGetter(s);
		return new Setter() {
			public Object rawGet(String k) { return g.rawGet(k); }
			public Object get(String k) { return g.get(k); }
			public void set(String k, Object v) {
				if(v == null)
					rmv(k);
				else {
					if(!(v instanceof Frac))
						throw new IllegalArgumentException("Frac setter must set fracs");
					Frac f = (Frac) v;
					s.set(k+numSfx, f.getNum());
					s.set(k+denomSfx, f.getDenom());
				}
			}
			public void set(String k, Object v, Object meta) {set(k,v);}
			public void rmv(String k) {
				s.rmv(k+numSfx);
				s.rmv(k+denomSfx);
			}
			public void rawSet(String k, Object len) {s.rawSet(k, len);};
		};
	}
	
	//List

	@SuppressWarnings("unchecked")
	private static <E> List<E> getList(Getter g, String k, Long len)
	{
		if(len == null)
			return null;

		List<E> l = new ArrayList<E>(len.intValue());
		for(int i = 0; i < len; i++)
			l.add((E) g.get(getIthName(k,i)));
		return l;
	}

	private static void setIterable(Setter elemS, Setter listS, String k, Object v)
	{
		//Deal with null lists
		if(v == null) {
			listS.rmv(k);
			return;
		} else if(!(v instanceof Iterable))
			throw new IllegalArgumentException("Collection setter must set an iterable");
		Iterable<?> src = (Iterable<?>) v;

		//Set new elements
		int i = 0;
		for(Object x : src)
			elemS.set(getIthName(k, i++), x);
	
		//Remove old elements
		Long oldLen = getLen(listS, k);
		if(oldLen == null)
			oldLen = 0L;
		setLen(listS, k, new Long(i));
		for(; i < oldLen; i++)
			elemS.rmv(getIthName(k,i));
	}

	private static void removeCollection(Setter elemS, Setter listS, String k)
	{
		long len = getLen(listS, k);
		for(int i = 0; i < len; i++)
			elemS.rmv(getIthName(k, i));
		setLen(listS, k, null);
	}

	private static Getter makeListGetter(final Getter g)
	{
		return new Getter() {
			public Object rawGet(String k) { return g.rawGet(k); }
			public Object get(String k)
			{
				return getList(g, k, getLen(this, k));
			}
		};
	}

	private static Setter makeListSetter(final Setter s)
	{
		final Getter g = makeListGetter(s);
		return new Setter() {
			public Object rawGet(String k) { return g.rawGet(k); }
			public Object get(String k) { return g.get(k); }
			public void set(String k, Object v) {
				setIterable(s, this, k, v);
			}
			public void set(String k, Object v, Object meta) {set(k,v);}
			public void rmv(String k) {
				removeCollection(s, this, k);
			}
			public void rawSet(String k, Object v) {s.rawSet(k, v);};
		};
	}

	//Set

	private static Getter makeSetGetter(final Getter g)
	{
		return new Getter() {
			public Object rawGet(String k) { return g.rawGet(k); }
			public Object get(String k)
			{
				Long len = getLen(this, k);
				if(len == null)
					return null;

				Set<Object> l = new HashSet<Object>(len.intValue());
				for(int i = 0; i < len; i++)
					l.add(g.get(getIthName(k,i)));
				return l;
			}
		};
	}

	private static Setter makeSetSetter(final Setter s)
	{
		final Getter g = makeSetGetter(s);
		return new Setter() {
			public Object rawGet(String k) { return g.rawGet(k); }
			public Object get(String k) { return g.get(k); }
			public void set(String k, Object v) {
				setIterable(s, this, k, v);
			}
			public void set(String k, Object v, Object meta) {set(k,v);}
			public void rmv(String k) {
				removeCollection(s, this, k);
			}
			public void rawSet(String k, Object v) {s.rawSet(k, v);};
		};
	}

	//Map

	private static Getter makeMapGetter(final Getter g)
	{
		return new Getter() {
			public Object rawGet(String k) { return g.rawGet(k); }
			public Object get(String k)
			{
				Long len = getLen(this, k);
				if(len == null)
					return null;
				Map<Object, Object> newMap = new HashMap<Object, Object>();
				for(int i = 0; i < len; i++)
					newMap.put(g.rawGet(getIthName(k+keySfx,i)), g.get(getIthName(k+valSfx,i)));
				return newMap;
			}
		};
	}

	private static Setter makeMapSetter(final Setter s)
	{
		final Getter g = makeMapGetter(s);
		return new Setter() {
			public Object rawGet(String k) { return g.rawGet(k); }
			public Object get(String k) { return g.get(k); }
			public void set(String k, Object v) {
				set(k, v, null);
			}
			public void set(String k, Object v, Object meta) {

				if(v == null) {
					rmv(k);
					return;
				} else if(!(v instanceof Map))
					throw new IllegalArgumentException("Map setter must set a map");
				Map<?, ?> src = (Map<?, ?>) v;

				Iterable<?> keys;
				if(meta != null) {
					if(!(meta instanceof Iterable))
						throw new IllegalArgumentException("Key order must be iterable");
					keys = (Iterable<?>) meta;
				} else
					keys = src.keySet();

				//Set new elements
				int i = 0;
				for(Object key : keys) {
					s.rawSet(getIthName(k+keySfx,i), key);
					s.set(getIthName(k+valSfx,i), src.get(key));
					i++;
				}

				//Remove old elements
				Long oldLen = getLen(this, k);
				if(oldLen == null)
					oldLen = 0L;
				setLen(this, k, new Long(i));
				for(; i < oldLen; i++) {
					s.rmv(getIthName(k+keySfx,i));
					s.rmv(getIthName(k+valSfx,i));
				}
			}
			public void rmv(String k) {
				long len = getLen(this, k);
				for(int i = 0; i < len; i++) {
					s.rmv(getIthName(k+keySfx, i));
					s.rmv(getIthName(k+valSfx, i));
				}
				setLen(this, k, null);
			}
			public void rawSet(String k, Object v) {s.rawSet(k, v);};
		};
	}

	//Map Keys

	private static Getter makeMapKeysGetter(final Getter g)
	{
		return new Getter() {
			public Object rawGet(String k) { return g.rawGet(k); }
			public Object get(String k)
			{
				return getList(g, k+keySfx, getLen(this, k));
			}
		};
	}

	//User the getters/setters

	private static Object get(Getter g, String k, DataTypes... dt)
	{
		for(int i = dt.length-1; i >= 0; i--)
			switch(dt[i]) {
				case FRAC: g = makeFracGetter(g); break;
				case LIST: g = makeListGetter(g); break;
				case SET: g = makeSetGetter(g); break;
				case MAP: g = makeMapGetter(g); break;
				case MAP_KEYS: g = makeMapKeysGetter(g); break;
				default: throw new IllegalArgumentException("Unknown data type: "+dt[i]);
			}
		return g.get(k);
	}

	private static void set(Setter s, String k, Object v, DataTypes... dt)
	{
		for(int i = dt.length-1; i >= 0; i--)
			switch(dt[i]) {
				case FRAC: s = makeFracSetter(s); break;
				case LIST: s = makeListSetter(s); break;
				case SET: s = makeSetSetter(s); break;
				case MAP: s = makeMapSetter(s); break;
				case MAP_KEYS: throw new IllegalArgumentException("Cannot set map keys alone");
				default: throw new IllegalArgumentException("Unknown data type: "+dt[i]);
			}
		s.set(k, v);
	}

	//Public stuff

	public static Object get(HttpServletRequest req, String k, DataTypes... dt)
	{
		return get(makeGetter(req), k, dt);
	}

	public static Object get(Entity e, String k, DataTypes... dt)
	{
		return get(makeGetter(e), k, dt);
	}

	public static Object get(Map<String, Object> m, String k, DataTypes... dt)
	{
		return get(makeGetter(m), k, dt);
	}

	public static void set(Entity e, String k, Object v, DataTypes... dt)
	{
		set(makeSetter(e), k, v, dt);
	}

	public static void set(Map<String, Object> m, String k, Object v, DataTypes... dt)
	{
		set(makeSetter(m), k, v, dt);
	}

	@SuppressWarnings("unchecked")
	public static <E> List<E> getList(HttpServletRequest req, String k)
	{
		return (List<E>) get(makeGetter(req), k, DataTypes.LIST);
	}

	@SuppressWarnings("unchecked")
	public static <E> List<E> getList(Entity e, String k)
	{
		return (List<E>) get(makeGetter(e), k, DataTypes.LIST);
	}

	@SuppressWarnings("unchecked")
	public static <E> List<E> getList(Map<String, Object> m, String k)
	{
		return (List<E>) get(makeGetter(m), k, DataTypes.LIST);
	}

	public static void setList(Entity e, String k, Object v)
	{
		set(makeSetter(e), k, v, DataTypes.LIST);
	}

	public static void setList(Map<String, Object> m, String k, Object v)
	{
		set(makeSetter(m), k, v, DataTypes.LIST);
	}

	@SuppressWarnings("unchecked")
	public static <E> List<List<E>> get2DList(HttpServletRequest req, String k)
	{
		return (List<List<E>>) get(makeGetter(req), k, DataTypes.LIST, DataTypes.LIST);
	}

	@SuppressWarnings("unchecked")
	public static <E> List<List<E>> get2DList(Entity e, String k)
	{
		return (List<List<E>>) get(makeGetter(e), k, DataTypes.LIST, DataTypes.LIST);
	}

	@SuppressWarnings("unchecked")
	public static <E> List<List<E>> get2DList(Map<String, Object> m, String k)
	{
		return (List<List<E>>) get(makeGetter(m), k, DataTypes.LIST, DataTypes.LIST);
	}

	public static void set2DList(Entity e, String k, Object v)
	{
		set(makeSetter(e), k, v, DataTypes.LIST, DataTypes.LIST);
	}

	public static void set2DList(Map<String, Object> m, String k, Object v)
	{
		set(makeSetter(m), k, v, DataTypes.LIST, DataTypes.LIST);
	}

	public static Long getLen(HttpServletRequest req, String name)
	{
		return getLen(makeGetter(req), name);
	}

	public static Long getLen(Entity e, String name)
	{
		return getLen(makeGetter(e), name);
	}

	public static Long getLen(Map<String, Object> m, String name)
	{
		return getLen(makeGetter(m), name);
	}

	public static String getIth(HttpServletRequest req, String name, long i)
	{
		return req.getParameter(getIthName(name,i));
	}

	@SuppressWarnings("unchecked")
	public static <E> E getIth(Entity e, String name, long i)
	{
		return (E) e.getProperty(getIthName(name,i));
	}

	@SuppressWarnings("unchecked")
	public static <E> E getIth(Map<String, Object> m, String name, long i)
	{
		return (E) m.get(getIthName(name,i));
	}
}
