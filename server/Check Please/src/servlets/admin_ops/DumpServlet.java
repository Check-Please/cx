package servlets.admin_ops;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import utils.GetServletBase;
import utils.ParamWrapper;
import static utils.MyUtils.a;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;

public class DumpServlet extends GetServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 9155044483273262990L;

	//If a kind is prefixed with this string, its entities belong to google
	public static final String googPrefex = "_";

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.adminReq = true;
		config.txnReq = false;
		config.bools = a("?all");
	}

	private static String escape(String s)
	{
		return s.replaceAll("\"", "\\\"");
	}

	public static void printSession(HttpSession session, PrintWriter out)
	{
		String s = "<session";
		for(@SuppressWarnings("rawtypes") Enumeration e = session.getAttributeNames(); e.hasMoreElements() ;) {
			String attrName = (String) e.nextElement();
			s += " " + attrName + "=\"" + escape((String)session.getAttribute(attrName)) +  "\"";
		}
		out.println(s + "/>");
	}
	
	private static String getClassEnding(Object o)
	{
		String fullName = o.getClass().getName();
		return fullName.substring(fullName.lastIndexOf('.')+1);
	}

	private static class Tree {
		public Key k;
		public Entity e;
		public Set<Tree> c;
		public Tree(Key k)
		{
			this.k = k;
			e = null;
			c = new HashSet<Tree>();
		}
		public int hashCode()
		{
			return k.hashCode();
		}
	}

	private void ensureInTree(Map<Key, Tree> nodeFinder, Key k)
	{
		if(!nodeFinder.containsKey(k)) {
			Tree t = new Tree(k);
			Key p = k.getParent();
			ensureInTree(nodeFinder, p);
			nodeFinder.get(p).c.add(t);
			nodeFinder.put(k, t);
		}
	}

	private StringBuffer dumpChildren(Tree t, int d)
	{
		StringBuffer sb = new StringBuffer();
		List<Tree> l = new ArrayList<Tree>(t.c.size());
		l.addAll(t.c);
		Collections.sort(l, new Comparator<Tree>() {
			public int compare(Tree x, Tree y) {
				//Not having an entity puts you at the end
				if(x.e == null) {
					if(y.e != null)
						return 1;
				} else if(y.e == null)
					return -1;
				//Sort by kind first
				String k1 = x.k.getKind();
				String k2 = y.k.getKind();
				if(k1.equals(k2)) {
					//Sort by name second
					String n1 = x.k.getName();
					String n2 = y.k.getName();
					if(n1 == null) {
						if(n2 == null) {
							//Finally, sort by id
							long i1 = x.k.getId();
							long i2 = y.k.getId();
							if(i1 == 0L) {
								if(i2 == 0L)
									return 0;
								else
									return -1;
							} else if(i2 == 0L)
								return 1;
							else
								return i1 < i2 ? -1 : i1 == i2 ? 0 : 1;
						} else
							return -1;
					} else if(n2 == null)
						return 1;
					else
						return n1.compareTo(n2);
				} else
					return k1.compareTo(k2);
			}
		});
		for(Tree c : l)
			sb.append(dumpTree(c, d));
		return sb;
	}

	private static String nTabs(int n)
	{
		StringBuffer sb = new StringBuffer(n);
		for(int i = 0; i < n; i++)
			sb.append('\t');
		return sb.toString();
	}

	private StringBuffer dumpTree(Tree t, int d)
	{
		StringBuffer sb = new StringBuffer(nTabs(d)+"<"+t.k.getKind()+
				"["+(t.k.getName() != null ? t.k.getName() : t.k.getId())+"]");
		if(t.e != null) {
			for(String pName : t.e.getProperties().keySet()) {
				sb.append(" "+pName+"=");
				Object p = t.e.getProperty(pName);
				if(p == null)
					sb.append("null");
				else if(p instanceof Text)
					sb.append("{Text: \""+escape(((Text)p).getValue())+"\"}");
				else if(p instanceof String)
					sb.append("{String: \""+escape((String) p)+"\"}");
				else
					sb.append("{"+getClassEnding(p)+": "+p.toString()+"}");
			}
		} else
			sb.append(" (no entity)");
		if(t.c.isEmpty())
			sb.append(" />\n");
		else {
			sb.append(">\n");
			sb.append(dumpChildren(t, d+1));
			sb.append(nTabs(d)+"</"+t.k.getKind()+">\n");
		}
		return sb;
	}
	public void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException
	{
		printSession(sesh, out);
		Map<Key, Tree> nodeFinder = new HashMap<Key, Tree>();
		Tree root = new Tree(null);
		nodeFinder.put(null, root);
		Set<Entity> googEs = new HashSet<Entity>();
		for(Entity e : ds.prepare(new Query()).asIterable()) {
			Key k = e.getKey();
			if(k.getKind().startsWith(googPrefex))
				googEs.add(e);
			else {
				ensureInTree(nodeFinder, k);
				nodeFinder.get(k).e = e;
			}
		}
		out.println(dumpChildren(root, 0));
		nodeFinder = new HashMap<Key, Tree>();
		root = new Tree(null);
		nodeFinder.put(null, root);
		for(Entity e : googEs) {
			Key k = e.getKey();
			ensureInTree(nodeFinder, k);
			nodeFinder.get(k).e = e;
		}
		if((p.getBool(0) != null) && (p.getBool(0)))
			out.println("<__GOOGLE__DATA__ (no entity)>\n"+
							dumpChildren(root, 1)+
						"</__GOOGLE__DATA__>");
	}
}