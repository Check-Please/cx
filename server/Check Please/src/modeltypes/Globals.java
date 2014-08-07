package modeltypes;

import java.util.Random;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class Globals extends AbstractModelType
{

	protected String kindName() { return getKind(); }
	public static String getKind() { return "globals"; }

	Long ticketKeysBuilt;

	public static final String CARD_CT_COOKIE = "CCT8";
	public static final int COOKIE_CARD_CT_LEN = 8;
	public static final String COOKIED_CARD_CT_PREFIX = "[cookie]";

	public static final long defaultID = 1L;
	public static final long minSupportedVersion = 1L;
	public static final String devTableID = "IKA";

	public static final long CLOSE_CAUSE__CLIENT_CLOSE = 0;
	public static final long CLOSE_CAUSE__ERROR = 1;
	public static final long CLOSE_CAUSE__DISCONNECTED = 2;

	public Globals(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public Globals(Entity e) { super(e); }

	public Globals(Key k, Long ticketKeysBuilt)
	{
		setKey(k);
		this.ticketKeysBuilt = ticketKeysBuilt;
	}

	public String getNextTicketKey(DatastoreService ds)
	{
		final int numChars = 26;
		final int asciiA = 65;
		final int sfxLen = 3;
		final Random rnd = new Random();
		if(ticketKeysBuilt == null)
			ticketKeysBuilt = 0L;
		String pfx = "";
		for(long pfxNum = ticketKeysBuilt / 1000; pfxNum > 0; pfxNum /= numChars)
			pfx += Character.toString((char)(asciiA+(pfxNum % numChars)));
		String key;
		boolean inDS;
		do {
			key = pfx;
			for(int i = 0; i < sfxLen; i++)
				key += Character.toString((char)(asciiA+rnd.nextInt(numChars)));
			try {
				ds.get(KeyFactory.createKey(TableKey.getKind(), key));
				inDS = true;
			} catch (EntityNotFoundException e) {
				inDS = false;
			}
		} while(inDS);
		ticketKeysBuilt++;
		return key;
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		e.setProperty("ticketKeysBuilt", ticketKeysBuilt);
		return e;
	}

	public void fromEntity(Entity e)
	{
		ticketKeysBuilt = (Long) e.getProperty("ticketKeysBuilt");
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
}
