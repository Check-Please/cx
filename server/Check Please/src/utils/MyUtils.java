package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

import modeltypes.AbstractModelType;
import modeltypes.Globals;

import org.apache.commons.codec.binary.Base64;

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
/*	private static String [] firstNames = {"Sophia", "Emma", "Olivia", "Isabella", "Ava", "Lily", "Zoe", "Chloe", "Mia", "Madison", "Emily", "Ella", "Madelyn", "Abigail", "Aubrey", "Addison", "Avery", "Layla", "Hailey", "Amelia", "Hannah", "Charlotte", "Kaitlyn", "Harper", "Kaylee", "Sophie", "Mackenzie", "Peyton", "Riley", "Grace", "Brooklyn", "Sarah", "Aaliyah", "Anna", "Arianna", "Ellie", "Natalie", "Isabelle", "Lillian", "Evelyn", "Elizabeth", "Lyla", "Lucy", "Claire", "Makayla", "Kylie", "Audrey", "Maya", "Leah", "Gabriella", "Annabelle", "Savannah", "Nora", "Reagan", "Scarlett", "Samantha", "Alyssa", "Allison", "Elena", "Stella", "Alexis", "Victoria", "Aria", "Molly", "Maria", "Bailey", "Sydney", "Bella", "Mila", "Taylor", "Kayla", "Eva", "Jasmine", "Gianna", "Alexandra", "Julia", "Eliana", "Kennedy", "Brianna", "Ruby", "Lauren", "Alice", "Violet", "Kendall", "Morgan", "Caroline", "Piper", "Brooke", "Elise", "Alexa", "Sienna", "Reese", "Clara", "Paige", "Kate", "Nevaeh", "Sadie", "Quinn", "Isla", "Eleanor", "Aiden", "Jackson", "Ethan", "Liam", "Mason", "Noah", "Lucas", "Jacob", "Jayden", "Jack", "Logan", "Ryan", "Caleb", "Benjamin", "William", "Michael", "Alexander", "Elijah", "Matthew", "Dylan", "James", "Owen", "Connor", "Brayden", "Carter", "Landon", "Joshua", "Luke", "Daniel", "Gabriel", "Nicholas", "Nathan", "Oliver", "Henry", "Andrew", "Gavin", "Cameron", "Eli", "Max", "Isaac", "Evan", "Samuel", "Grayson", "Tyler", "Zachary", "Wyatt", "Joseph", "Charlie", "Hunter", "David", "Anthony", "Christian", "Colton", "Thomas", "Dominic", "Austin", "John", "Sebastian", "Cooper", "Levi", "Parker", "Isaiah", "Chase", "Blake", "Aaron", "Alex", "Adam", "Tristan", "Julian", "Jonathan", "Christopher", "Jace", "Nolan", "Miles", "Jordan", "Carson", "Colin", "Ian", "Riley", "Xavier", "Hudson", "Adrian", "Cole", "Brody", "Leo", "Jake", "Bentley", "Sean", "Jeremiah", "Asher", "Nathaniel", "Micah", "Jason", "Ryder", "Declan", "Hayden", "Brandon", "Easton", "Lincoln", "Harrison"};
	private static String [] lastNames = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Hall", "Allen", "Young", "Hernandez", "King", "Wright", "Lopez", "Hill", "Scott", "Green", "Adams", "Baker", "Gonzalez", "Nelson", "Carter", "Mitchell", "Perez", "Roberts", "Turner", "Phillips", "Campbell", "Parker", "Evans", "Edwards", "Collins", "Stewart", "Sanchez", "Morris", "Rogers", "Reed", "Cook", "Morgan", "Bell", "Murphy", "Bailey", "Rivera", "Cooper", "Richardson", "Cox", "Howard", "Ward", "Torres", "Peterson", "Gray", "Ramirez", "James", "Watson", "Brooks", "Kelly", "Sanders", "Price", "Bennett", "Wood", "Barnes", "Ross", "Henderson", "Coleman", "Jenkins", "Perry", "Powell", "Long", "Patterson", "Hughes", "Flores", "Washington", "Butler", "Simmons", "Foster", "Gonzales", "Bryant", "Alexander", "Russell", "Griffin", "Diaz", "Hayes"};
	public static String randomFirstName()
	{
		return firstNames[getRandom().nextInt(firstNames.length)];
	}
	public static String randomLastName()
	{
		return lastNames[getRandom().nextInt(lastNames.length)];
	}
*/

	private static char [] alphabet64 = null;
	public static String encode64(long val)
	{
		if(alphabet64 == null)
			alphabet64 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_".toCharArray();
		return encode(val, alphabet64);
	}
	public static String encode(long val, char [] alphabet)
	{
		if(val < 0)
			throw new IllegalArgumentException("Cannot encode negative value");
		if(alphabet.length < 2)
			throw new IllegalArgumentException("Cannot encode into alphabet with less than two symbols");
		String ret = "";
		do {
			int d = (int) (val % alphabet.length);
			val /= alphabet.length;
			ret += alphabet[d];
		} while(val > 0);
		return ret;
	}

	/**  Runs SHA-256 on a string and returns a base 64 encoding of the hash
	 *
	 * @param	s The string to be encoded
	 * @return	A base 64 encoding of the hash
	 * @throws	HttpErrMsg if SHA-256 is somehow missing
	 */
	public static String sha256(String s) throws HttpErrMsg
	{
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new HttpErrMsg(500, "Encription algorithm missing");
		}
		md.update(s.getBytes());
		return new String(Base64.encodeBase64(md.digest()));
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