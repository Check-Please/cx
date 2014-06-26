package modeltypes;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class ConnectionToTablePointer extends AbstractModelType
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "c_to_t_ptr"; }
	String tableKey;
	public ConnectionToTablePointer(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public ConnectionToTablePointer(Entity e) { super(e); }

	public ConnectionToTablePointer(Key k, String tableKey)
	{
		setKey(k);
		this.tableKey = tableKey;
	}

	public String getKeyName()
	{
		return tableKey;
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		e.setProperty("tableKey", tableKey);
		return e;
	}
	public void fromEntity(Entity e)
	{
		tableKey = (String) e.getProperty("tableKey");
	}
}
