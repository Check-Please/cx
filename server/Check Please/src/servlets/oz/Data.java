package servlets.oz;

import java.util.ArrayList;
import java.util.List;

import kinds.AbstractKind;
import utils.DSConverter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

public class Data extends AbstractKind
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "oz_data"; }

	List<String> data;
	String channelID;
	Boolean disabled;

	public Data(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public Data(Entity e) { super(e); }
	public Data(Key k, int len)
	{
		setKey(k);
		data = new ArrayList<String>(len);
		for(int i = 0; i < len; i++)
			data.add("[]");
		channelID = null;
		disabled = false;
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

	public void enable(boolean enabled)
	{
		disabled = !enabled;
	}
	public boolean isDisabled()
	{
		return disabled;
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		List<Text> d = new ArrayList<Text>(data.size());
		for(int i = 0; i < data.size(); i++)
			d.add(new Text(data.get(i)));
		DSConverter.setList(e, "data", d);
		e.setProperty("clientID", channelID);
		e.setProperty("disabled", disabled);
		return e;
	}

	public void fromEntity(Entity e)
	{
		List<Text> d = DSConverter.getList(e, "data");
		data = new ArrayList<String>(d.size());
		for(int i = 0; i < d.size(); i++)
			data.add(d.get(i).getValue());
		channelID = (String) e.getProperty("clientID");
		disabled = (Boolean) e.getProperty("disabled");
	}

}
