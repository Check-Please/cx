package modeltypes;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class CardHashToClientPointer extends AbstractModelType
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "ch_to_c_ptr"; }
	String client;
	public CardHashToClientPointer(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public CardHashToClientPointer(Entity e) { super(e); }

	public CardHashToClientPointer(Key k, String client)
	{
		setKey(k);
		this.client = client;
	}

	public String getKeyName()
	{
		return client;
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		e.setProperty("client", client);
		return e;
	}
	public void fromEntity(Entity e)
	{
		client = (String) e.getProperty("client");
	}
}
