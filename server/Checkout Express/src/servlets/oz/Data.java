package servlets.oz;

import java.util.ArrayList;
import java.util.List;

import kinds.AbstractKind;
import utils.DSConverter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class Data extends AbstractKind
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "oz_data"; }

	List<String> data;
	String channelID;

	public Data(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public Data(Entity e) { super(e); }
	public Data(Key k, int len)
	{
		setKey(k);
		data = new ArrayList<String>(len);
		for(int i = 0; i < len; i++)
			data.add("{}");
		channelID = null;
	}

	public List<String> getData()
	{
		return data;
	}

	public String getClient()
	{
		return channelID;
	}

	public void setClient(String clientID)
	{
		this.channelID = clientID;
	}

	public void setData(int i, String datum)
	{
		data.set(i, datum);;
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		DSConverter.setList(e, "data", data);
		e.setProperty("clientID", channelID);
		return e;
	}

	public void fromEntity(Entity e)
	{
		data = DSConverter.getList(e, "data");
		channelID = (String) e.getProperty("clientID");
	}
}
