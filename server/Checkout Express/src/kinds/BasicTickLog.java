package kinds;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import utils.DSConverter;
import utils.MyUtils;
import utils.TicketItem;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class BasicTickLog extends AbstractKind
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "basic_ticket_log"; }
	List<TicketItem> items;
	public BasicTickLog(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public BasicTickLog(Entity e) { super(e); }
	public BasicTickLog(Key k, List<TicketItem> items)
	{
		setKey(k);
		this.items = items;
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		List<String> strItems = new ArrayList<String>(items.size());
		for(int i = 0; i < items.size(); i++)
			strItems.add(items.get(i).toString());
		DSConverter.setList(e, "items", strItems);
		return e;
	}
	public void fromEntity(Entity e)
	{
		List<String> strItems = new ArrayList<String>(items.size());
		items = new ArrayList<TicketItem>(strItems.size());
		for(int i = 0; i < strItems.size(); i++)
			try {
				items.add(new TicketItem(new JSONObject(strItems.get(i))));
			} catch (JSONException e1) {
				items.add(null);
			}
	}
	public static String makeUniqueKeyName(String mobileKey, List<TicketItem> items)
	{
		long startTime = Long.MAX_VALUE;
		for(int i = 0; i < items.size(); i++)
			startTime = Math.min(startTime, items.get(i).getOrderDate().getTime());
		return mobileKey + "," + 
				MyUtils.encode64(Math.abs(items.hashCode())) + "," +
				MyUtils.encode64(startTime);
	}
	public static Key makeKey(String restr, String mobileKey, List<TicketItem> items)
	{
		return KeyFactory.createKey(Restaurant.getKind(),
			restr).getChild(getKind(), makeUniqueKeyName(mobileKey, items));
	}
}
