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

public class TableKey extends AbstractKind
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "table_key"; }

	String restr;
	String query;//How to find what's on this table's ticket
	Map<String, Frac> paidPart;
	Map<String, Frac> outstandingPart;
	boolean splitStarted;

	public TableKey(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public TableKey(Entity e) { super(e); }

	public TableKey(Key k, String restr, String query)
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

	public Map<String, Frac> getPaidPart()
	{
		return paidPart;
	}

	public Map<String, Frac> getOutstandingPart()
	{
		return outstandingPart;
	}

	public Map<String, Frac> getAccountedForPart()
	{
		Map<String, Frac> ret = new HashMap<String, Frac>();
		Set<String> keys = new HashSet<String>();
		keys.addAll(paidPart.keySet());
		keys.addAll(outstandingPart.keySet());
		for(String key : keys) {
			Frac v = paidPart.get(key);
			if(v == null)
				v = Frac.ZERO;
			Frac f = outstandingPart.get(key);
			if(f != null)
				v = v.add(f);
			ret.put(key, v);
		}
		return ret;
	}

	public void startSplit()
	{
		splitStarted = true;
	}

	public void cancelSplit(DatastoreService ds)
	{
		splitStarted = false;
		for(Entity e : ds.prepare(new Query(UserConnection.getKind(), getKey())).asIterable()) {
			UserConnection c = new UserConnection(e);
			c.removeItems();
			c.commit(ds);
		}
	}

	private Iterable<Entity> getClients(DatastoreService ds)
	{
		return getClients(getKey(), ds);
	}

	private static Iterable<Entity> getClients(Key k, DatastoreService ds)
	{
		return ds.prepare(new Query(UserConnection.getKind(), k)).asIterable();
	}

	public void sendItemsUpdateAndRemoveSplit(String splitID, DatastoreService ds) throws JSONException, UnsupportedFeatureException, HttpErrMsg
	{
		sendItemsUpdateAndRemoveSplit(TicketItem.getItems(this, ds), splitID, getKey(), ds);
	}

	public static void sendItemsUpdateAndRemoveSplit(List<TicketItem> items, String splitID, Key k, DatastoreService ds)
	{
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		for(Entity e : getClients(k, ds))
			if(!e.getKey().getName().equals(splitID))
				UserConnection.sendItemsUpdateAndRemoveSplit(items, splitID, e.getKey(), channelService);
	}

	public void sendItemsUpdateAndRestoreSplit(String splitID, List<String> splitItems, DatastoreService ds) throws JSONException, UnsupportedFeatureException, HttpErrMsg
	{
		sendItemsUpdateAndRestoreSplit(TicketItem.getItems(this, ds), splitID, splitItems, getKey(), ds);
	}

	public static void sendItemsUpdateAndRestoreSplit(List<TicketItem> items, String splitID, List<String> splitItems, Key k, DatastoreService ds)
	{
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		for(Entity e : getClients(k, ds))
			if(!e.getKey().getName().equals(splitID))
				UserConnection.sendItemsUpdateAndRestoreSplit(items, splitID, splitItems, e.getKey(), channelService);
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
				UserConnection.sendSplitUpdate(splitID, splitItems, e.getKey(), channelService);
	}

	public void sendStartSplit(String splitterID, DatastoreService ds)
	{
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		for(Entity e : getClients(ds))
			if(!e.getKey().getName().equals(splitterID))
				UserConnection.sendStartSplit(e.getKey(), channelService);
	}

	public void sendCancelSplit(String splitterID, DatastoreService ds)
	{
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		for(Entity e : getClients(ds))
			if(!e.getKey().getName().equals(splitterID))
				UserConnection.sendCancelSplit(e.getKey(), channelService);
	}

	public void sendErrMsg(String msg, DatastoreService ds)
	{
		sendErrMsg(msg, getKey(), ds);
	}

	public static void sendErrMsg(String msg, Key k, DatastoreService ds)
	{
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		for(Entity e : getClients(k, ds))
			UserConnection.sendErrMessage(msg, e.getKey(), channelService);
	}

	public JSONObject getSplit(String connectionID, DatastoreService ds) throws JSONException
	{
		if(!splitStarted)
			return null;
		JSONObject ret = new JSONObject();
		for(Entity c : getClients(ds))
			if(!c.getKey().getName().equals(connectionID))
				ret.put(c.getKey().getName(), new JSONArray(new UserConnection(c).getItems()));
		return ret;
	}

	public boolean clearOldMetadata(List<TicketItem> items, DatastoreService ds) throws JSONException
	{
		boolean commitNeeded = false;
		Set<String> ids = new HashSet<String>(items.size());
		for(int i = 0; i < items.size(); i++)
			ids.add(items.get(i).getID());
		for(int j = 0; j < 2; j++) {
			Iterator<String> i = (j == 0 ? paidPart : outstandingPart).keySet().iterator();
			while(i.hasNext()) {
				String id = i.next();
				if(!ids.contains(id)) {
					i.remove();
					commitNeeded = true;
				}
			}
		}
		if(splitStarted) {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			for(Entity e : getClients(ds)) {
				UserConnection c = new UserConnection(e);
				for(String item : c.getItems())
					if(!ids.contains(item)) {
						UserConnection.sendCloseMessage(c.getKey(), channelService);
						new ClosedUserConnection(restr, c, ClosedUserConnection.CLOSE_CAUSE__TICKET_CLOSED).commit(ds);
						c.rmv(ds);
						break;
					}
			}
		}
		return commitNeeded;
	}

	public boolean clearTickMetadata(DatastoreService ds)
	{
		boolean commitNeeded = (ds != null) && (splitStarted || (paidPart.size() > 0) || (outstandingPart.size() > 0));
		paidPart = new HashMap<String, Frac>();
		outstandingPart = new HashMap<String, Frac>();
		splitStarted = false;
		if(ds != null) {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			for(Entity c : getClients(ds)) {
				UserConnection.sendCloseMessage(c.getKey(), channelService);
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
		DSConverter.set(e, "paidPort", paidPart, DSConverter.DataTypes.MAP, DSConverter.DataTypes.FRAC);
		DSConverter.set(e, "outstandingPayments", outstandingPart, DSConverter.DataTypes.MAP, DSConverter.DataTypes.FRAC);
		e.setProperty("splitStarted", splitStarted);
		return e;
	}

	@SuppressWarnings("unchecked")
	public void fromEntity(Entity e)
	{
		restr = (String) e.getProperty("restr");
		query = (String) e.getProperty("query");
		paidPart = (Map<String, Frac>) DSConverter.get(e, "paidPort", DSConverter.DataTypes.MAP, DSConverter.DataTypes.FRAC);
		outstandingPart = (Map<String, Frac>) DSConverter.get(e, "outstandingPayments", DSConverter.DataTypes.MAP, DSConverter.DataTypes.FRAC);
		splitStarted = (Boolean) e.getProperty("splitStarted");
	}
}
