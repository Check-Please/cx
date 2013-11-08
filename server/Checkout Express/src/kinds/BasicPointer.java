package kinds;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class BasicPointer extends AbstractKind
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "basic_ptr"; }
	String keyName;
	long keyID;
	public BasicPointer(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public BasicPointer(Entity e) { super(e); }

	public BasicPointer(Key k, String keyName)
	{
		setKey(k);
		this.keyName = keyName;
		this.keyID = 0L;
	}

	public BasicPointer(Key k, long keyID)
	{
		setKey(k);
		this.keyName = null;
		this.keyID = keyID;
	}

	public String getKeyName()
	{
		return keyName;
	}

	public long getKeyID()
	{
		return keyID;
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		if(keyName != null)
			e.setProperty("keyName", keyName);
		if(keyID != 0L)
			e.setProperty("keyID", keyID);
		return e;
	}
	public void fromEntity(Entity e)
	{
		keyName = e.hasProperty("keyName") ? (String) e.getProperty("keyName") : null;
		keyID = e.hasProperty("keyID") ? ((Long) e.getProperty("keyID")).longValue() : 0L;
	}
}
