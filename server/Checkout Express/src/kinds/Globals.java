package kinds;

import java.util.Random;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class Globals extends AbstractKind
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "globals"; }

	Long ticketKeysBuilt;
	public static final long defaultID = 1L;
	public static final String subtleApiKey = "U0IrN0Mx";

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
				ds.get(KeyFactory.createKey(MobileTickKey.getKind(), key));
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
}
