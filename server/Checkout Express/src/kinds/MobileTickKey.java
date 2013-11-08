package kinds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.DSConverter;
import utils.Frac;
import utils.HttpErrMsg;
import utils.MyUtils;
import utils.TicketItem;
import utils.UnsupportedFeatureException;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class MobileTickKey extends AbstractKind
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "mobile_tick_key"; }

	String restr;
	String query;//How to find what's on this table's ticket
	Map<String, Frac> paidPortion;
	boolean splitStarted;

	public MobileTickKey(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public MobileTickKey(Entity e) { super(e); }

	public MobileTickKey(Key k, String restr, String query)
	{
		setKey(k);
		this.restr = restr;
		this.query = query;
		clearTickMetadata(null);
	}

	public String getRestrUsername()
	{
		return restr;
	}

	public Restaurant getRestr(DatastoreService ds)
	{
		return new Restaurant(MyUtils.get_NoFail(KeyFactory.createKey(Restaurant.getKind(), restr), ds));
	}

	public String getQuery() {
		return query;
	}

	public Map<String, Frac> getPaidMap()
	{
		return paidPortion;
	}

	public void startSplit()
	{
		splitStarted = true;
	}

	private Iterable<Entity> getClients(DatastoreService ds)
	{
		return getClients(getKey(), ds);
	}

	private static Iterable<Entity> getClients(Key k, DatastoreService ds)
	{
		return ds.prepare(new Query(MobileClient.getKind(), k)).asIterable();
	}

	public void sendItemsUpdateAndRemoveSplit(String splitID, DatastoreService ds) throws JSONException, UnsupportedFeatureException, HttpErrMsg
	{
		sendItemsUpdateAndRemoveSplit(TicketItem.getItems(this), splitID, getKey(), ds);
	}


	public static void sendItemsUpdateAndRemoveSplit(List<TicketItem> items, String splitID, Key k, DatastoreService ds)
	{
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		for(Entity e : getClients(k, ds))
			MobileClient.sendItemsUpdateAndRemoveSplit(items, splitID, e.getKey(), channelService);
	}

	public void sendSplitUpdate(String splitID, Set<String> splitItems, DatastoreService ds)
	{
		sendSplitUpdate(splitID, splitItems, ds);
	}

	public static void sendSplitUpdate(String splitID, JSONArray splitItems, Key k, DatastoreService ds)
	{
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		for(Entity e : getClients(k, ds))
			if(!e.getKey().getName().equals(splitID))
				MobileClient.sendSplitUpdate(splitID, splitItems, e.getKey(), channelService);
	}

	public void sendStartSplit(String splitID, DatastoreService ds)
	{
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		for(Entity e : getClients(ds))
			if(!e.getKey().getName().equals(splitID))
				MobileClient.sendStartSplit(e.getKey(), channelService);
	}

	public void sendErrMsg(String msg, DatastoreService ds)
	{
		sendErrMsg(msg, getKey(), ds);
	}

	public static void sendErrMsg(String msg, Key k, DatastoreService ds)
	{
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		for(Entity e : getClients(k, ds))
			MobileClient.sendErrMessage(msg, e.getKey(), channelService);
	}

	public JSONObject getSplit(String clientID, DatastoreService ds) throws JSONException
	{
		if(!splitStarted)
			return null;
		JSONObject ret = new JSONObject();
		for(Entity c : getClients(ds))
			if(!c.getKey().getName().equals(clientID))
				ret.put(c.getKey().getName(), new JSONArray(new MobileClient(c).getItems()));
		return ret;
	}

	public boolean clearOldMetadata(List<TicketItem> items, DatastoreService ds) throws JSONException
	{
		boolean commitNeeded = false;
		Set<String> ids = new HashSet<String>(items.size());
		for(int i = 0; i < items.size(); i++)
			ids.add(items.get(i).getID());
		Iterator<String> i = paidPortion.keySet().iterator();
		while(i.hasNext()) {
			String id = i.next();
			if(!ids.contains(id)) {
				i.remove();
				commitNeeded = true;
			}
		}
		if(splitStarted) {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			for(Entity e : getClients(ds)) {
				MobileClient c = new MobileClient(e);
				for(String item : c.getItems())
					if(!ids.contains(item)) {
						MobileClient.sendCloseMessage(c.getKey(), channelService);
						new ClosedMobileClient(restr, c, ClosedMobileClient.CloseCause.TICKET_CLOSED).commit(ds);
						c.rmv(ds);
						break;
					}
			}
		}
		return commitNeeded;
	}

	public boolean clearTickMetadata(DatastoreService ds)
	{
		boolean commitNeeded = (ds != null) && (splitStarted || (paidPortion.size() > 0));
		paidPortion = new HashMap<String, Frac>();
		splitStarted = false;
		if(ds != null) {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			for(Entity c : getClients(ds)) {
				MobileClient.sendCloseMessage(c.getKey(), channelService);
				ds.delete(c.getKey());
			}
		}
		return commitNeeded;
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		e.setProperty("restr", restr);
		e.setProperty("query", query);
		DSConverter.set(e, "paidPortion", paidPortion, DSConverter.DataTypes.MAP, DSConverter.DataTypes.FRAC);
		e.setProperty("splitStarted", splitStarted);
		return e;
	}

	@SuppressWarnings("unchecked")
	public void fromEntity(Entity e)
	{
		restr = (String) e.getProperty("restr");
		query = (String) e.getProperty("query");
		paidPortion = (Map<String, Frac>) DSConverter.get(e, "paidPortion", DSConverter.DataTypes.MAP, DSConverter.DataTypes.FRAC);
		splitStarted = (Boolean) e.getProperty("splitStarted");
	}
}
