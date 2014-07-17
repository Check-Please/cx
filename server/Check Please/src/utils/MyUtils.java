package utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import modeltypes.AbstractModelType;
import modeltypes.Globals;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.utils.SystemProperty;

public class MyUtils
{
	//A utility function for declaring static arrays
	//a(x,y,z) is equivalent to the more traditional C-syntax [x,y,x]
	@SafeVarargs
	public static <E> E[] a(E... es)
	{
		return es;
	}

	public static boolean isDevServer() throws HttpErrMsg
	{
		SystemProperty.Environment.Value env = SystemProperty.environment.value();
		if(env == SystemProperty.Environment.Value.Development)
			return true;
		else if(env == SystemProperty.Environment.Value.Production)
			return false;
		else
			throw new HttpErrMsg(500, "Cannot determine if server is in development or production mode");
	}

	private static Random rand = null;
	public static Random getRandom()
	{
		if(rand == null)
			rand = new Random();
		return rand;
	}

	private static byte [] saltAndHash(String str, byte [] salt) throws HttpErrMsg
	{
		byte [] data = str.getBytes(Charset.forName("UTF-8"));
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new HttpErrMsg(500, "SHA-256 missing");
		}
		final byte [] saltyData = new byte[salt.length + data.length];
		System.arraycopy(salt, 0, saltyData, 0, salt.length);
		System.arraycopy(data, 0, saltyData, salt.length, data.length);
		md.update(saltyData);

		final byte [] hash = md.digest();
		final byte [] saltyHash = new byte[salt.length + hash.length];
		System.arraycopy(salt, 0, saltyHash, 0, salt.length);
		System.arraycopy(hash, 0, saltyHash, salt.length, hash.length);
		return saltyHash;
	}

	private static final int saltiness = 16;//Mmm... Salt

	public static String protectPassword(String password) throws HttpErrMsg
	{
		final byte [] salt = new byte[saltiness];
		try {
			SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
		} catch (NoSuchAlgorithmException e) {
			throw new HttpErrMsg(500, "Cannot generate salt");
		}
		return DatatypeConverter.printBase64Binary(saltAndHash(password, salt));
	}


	public static boolean checkProtectedPassword(String password, String proPass64) throws HttpErrMsg
	{
		final byte [] proPass = DatatypeConverter.parseBase64Binary(proPass64);
		final byte [] salt = new byte[saltiness];
		if(proPass.length < salt.length)
			return false;
		System.arraycopy(proPass, 0, salt, 0, salt.length);
		return Arrays.equals(proPass, saltAndHash(password, salt));
	}

	public static AbstractModelType wrapEntity(Entity e)
	{
		if(e.getKind().equals(modeltypes.ConnectionToTablePointer.getKind()))
			return new modeltypes.ConnectionToTablePointer(e);
		else if(e.getKind().equals(modeltypes.Globals.getKind()))
			return new modeltypes.Globals(e);
		else if(e.getKind().equals(modeltypes.TableKey.getKind()))
			return new modeltypes.TableKey(e);
		else if(e.getKind().equals(modeltypes.UserConnection.getKind()))
			return new modeltypes.UserConnection(e);
		else if(e.getKind().equals(modeltypes.Restaurant.getKind()))
			return new modeltypes.Restaurant(e);
		else
			throw new IllegalArgumentException("Unknown entity kind");
	}
	
	public static Key newKey(Key parent, String kind)
	{
		return KeyFactory.createKey(parent, kind, UUID.randomUUID().toString());
	}

	public static String ensureNDigits(int x, int n)
	{
		String y = ""+x;
		if(y.length() > n)
			y = y.substring(y.length()-n);
		while(y.length() < n)
			y = "0"+y;
		return y;
	}
	
	public static long toCentHundredths(double money)
	{
		return Math.round(Math.ceil(money*10000-0.001));
	}

	public static long toCents(double money)
	{
		return Math.round(Math.ceil(money*100-0.001));
	}

	public static final long week = 604800000;//One week in miliseconds

	public static Entity get_NoFail(Key key, DatastoreService ds)
	{
		try {
			return ds.get(key);
		} catch (EntityNotFoundException e) {
			PANIC("We could not find "+key+" even though it was guaranteed to exist", e);
			return null;
		}
	}
	
	//Does not return!
	public static void PANIC(String msg)
	{
		PANIC(msg, null);
	}
	public static void PANIC(Exception e)
	{
		PANIC(null, e);
	}
	public static void PANIC(String msg, Exception e)
	{
		throw new RuntimeException("PANIC!!!"+
			(msg == null ? "" : "\nMessage: " + msg + (e==null?"":"\n"))+
			(e == null ? "" : "\nTriggering Exception: " + e));
	}
	public static Globals getGlobals(DatastoreService ds)
	{
		Key k = KeyFactory.createKey(Globals.getKind(), Globals.defaultID);
		try {
			return new Globals(k, ds);
		} catch (EntityNotFoundException e) {
			Globals g = new Globals(k, 0L);
			return g;
		}
	}

	public static String removeParam(String query, String param)
	{
		param = param+"=";
		int paramStart = query.indexOf("&"+param);
		if(paramStart == -1) {
			if(query.startsWith(param))
				paramStart = 0;
			else
				return query;
		}
		int paramEnd = query.substring(paramStart+1).indexOf('&');
		return query.substring(0, paramStart) + (paramEnd == -1 ? "" :
			query.substring(paramEnd+paramStart+1+(paramStart == 0 ? 1 : 0)));
	}
}