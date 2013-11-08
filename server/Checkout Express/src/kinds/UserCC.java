package kinds;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class UserCC extends AbstractKind
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "user_cc"; }

	int firstSix;//IIN
	int lastFour;
	String name;
	public static final int minNameLen = 2;
	public static final int maxNameLen = 26;
	int expr;//YYMM
	int zip;//5 digit
	int subtleID;
	Date lastUse;

	public UserCC(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public UserCC(Entity e) { super(e); }

	public UserCC(Key k, Integer firstSix, Integer lastFour, String name, Integer exprYear, Integer exprMonth, Integer zip, Integer subtleKey)
	{
		init(k, firstSix, lastFour, name, exprYear*100+exprMonth, zip, subtleKey);
	}

	public UserCC(Key k, Integer firstSix, Integer lastFour, String name, Integer expr, Integer zip, Integer subtleKey)
	{
		init(k, firstSix, lastFour, name, expr, zip, subtleKey);
	}

	private void init(Key k, Integer firstSix, Integer lastFour, String name, Integer expr, Integer zip, Integer subtleID)
	{
		setKey(k);
		this.firstSix = firstSix;
		this.lastFour = lastFour;
		this.name = name;
		this.expr = expr;
		this.zip = zip;
		this.subtleID = subtleID;
		updateLastUse();
	}

	public int getFirstSix()
	{
		return firstSix;
	}
	
	public int getLastFour()
	{
		return lastFour;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getExpr()
	{
		return expr;
	}
	
	public int getExprYear()
	{
		return expr / 100;
	}
	
	public int getExprMonth()
	{
		return expr % 100;
	}
	
	public int getZip()
	{
		return zip;
	}
	
	public int getSubtleID()
	{
		return subtleID;
	}
	
	public Date getLastUse()
	{
		return lastUse;
	}

	public void updateLastUse()
	{
		lastUse = new Date();
	}

	/**	Checks if a PAN can be legitimate using things like The Luhn algorithm
	 *	when appropriate.  Note that passing this check does not ensure that
	 *	a PAN is legitimate, but failing it does insure that the PAN is not.
	 *
	 *	@param pan	The PAN to be checked
	 *	@return The problem with the PAN if one could be found.  null otherwise
	 */
	public static String checkPAN(String pan)
	{
		//Check basic formatting
		if(!pan.matches("[0-9]{6,}"))
			return "Invalid credit card number format";
		//TODO: the Luhn algorithm for most IINs.  Return something like "typo"
		return null;
	}
	/**	Checks if a CVV can be legitimate by seeing if it matches the pattern
	 *	of the CVVs used by a specific IIN
	 *
	 *	@param iin	The IIN of the card
	 *	@param cvv	The CVV of the card
	 *	@return The problem with the CVV if one could be found.  null otherwise
	 */
	public static String checkCVV(int iin, int cvv)
	{
		//Something, anything...
		return null;
	}

	public int getPANLen() {
		return getPANLen(firstSix);
	}
	
	public static int getPANLen(int iin)
	{
		int fourD = iin/100;
		int threeD = fourD/10;
		int twoD = threeD/10;
		int mii = twoD/10;
		//We special case Diners Club and take a guess on the others based on the MII
		if(threeD >= 300 && threeD <= 305)
			return 14;
		else if(fourD == 2014 || fourD == 2149)
			return 15;
		else if(twoD == 36)
			return 14;
		else
			return mii == 3 ? 15 : 16;
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		e.setProperty("firstSix", (long) firstSix);
		e.setProperty("lastFour", (long) lastFour);
		e.setProperty("name", name);
		e.setProperty("expr", (long) expr);
		e.setProperty("zip", (long) zip);
		e.setProperty("subtleID", (long) subtleID);
		e.setProperty("lastUse", lastUse);
		return e;
	}

	public void fromEntity(Entity e)
	{
		firstSix = ((Long) e.getProperty("firstSix")).intValue();
		lastFour = ((Long) e.getProperty("lastFour")).intValue();
		name = (String) e.getProperty("name");
		expr = ((Long) e.getProperty("expr")).intValue();
		zip = ((Long) e.getProperty("zip")).intValue();
		subtleID = ((Long) e.getProperty("subtleID")).intValue();
		lastUse = (Date) e.getProperty("lastUse");
		
	}
}
