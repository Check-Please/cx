package modeltypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import utils.DSConverter;
import utils.Frac;
import utils.HttpErrMsg;
import utils.MyUtils;
import utils.TicketItem;

import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class TableKey extends AbstractModelType
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "table_key"; }

	String restr;
	String query;//How to find what's on this table's ticket
	Map<String, Long> connectionStatus;
	Map<String, List<Frac>> itemFracs;
	Map<String, List<Long>> itemNums;
	Map<String, List<String>> itemOwners;
	public static enum ConnectionStatus {PROCESSING, LEFT_WHILE_PROCESSING, PAID, INPUTTING, CLOSED};
	public TableKey(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public TableKey(Entity e) { super(e); }

	public TableKey(Key k, String restr, String query)
	{
		setKey(k);
		this.restr = restr;
		this.query = query;
	}

	//-------------------
	//| PRIVATE METHODS |
	//-------------------
	private void removeConnections(Set<String> connections, DatastoreService ds)
	{
		for(String cID : connections) {
			UserConnection uc;
			try {
				uc = new UserConnection(this.getKey().getChild(UserConnection.getKind(), cID), ds);
				new ClosedUserConnection(this.getRestrUsername(), uc, ClosedUserConnection.CLOSE_CAUSE__TICKET_CLOSED, null).commit(ds);
				uc.rmv(ds);
			} catch (EntityNotFoundException e) {}//Must already be gone
			ds.delete(KeyFactory.createKey(ConnectionToTablePointer.getKind(), cID));
			connectionStatus.remove(cID);
		}
	}
	private class ItemJSONInfo {
		public JSONObject json;
		public String id;
		public String owner;
		public ItemJSONInfo(JSONObject json, String id, String owner) {
			this.json = json;
			this.id = id;
			this.owner = owner;
		}
	}

	private void updateListeners(List<TicketItem> items, DatastoreService ds) throws JSONException
	{
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		List<ItemJSONInfo> ppItems = preprocessItems(items);
		for(String cID : connectionStatus.keySet()) try {
			channelService.sendMessage(new ChannelMessage(cID, "SET_ITEMS\n"+myGetItemsJSON(ppItems, cID)));
		} catch(ChannelFailureException e) {}
	}

	private List<ItemJSONInfo> preprocessItems(List<TicketItem> items) throws JSONException
	{
		List<ItemJSONInfo> ret = new ArrayList<ItemJSONInfo>();
		for(TicketItem item : items) {
			String itemID = item.getID();
			Frac fillerFrac = Frac.ONE;
			Long denom = 0L;
			List<Frac> fracs = itemFracs.get(itemID);
			for(int i = 0; i < fracs.size(); i++) {
				Frac f = fracs.get(i);
				if(f == null)
					denom++;
				else
					fillerFrac = fillerFrac.sub(f);
			}
			if(denom != 0L)
				fillerFrac = fillerFrac.div(denom);
			List<Long> nums = itemNums.get(itemID);
			List<String> owners = itemOwners.get(itemID);
			for(int i = 0; i < fracs.size(); i++) {
				Frac frac = fracs.get(i);
				if(frac == null)
					frac = fillerFrac;
				JSONObject json = item.toJSON();
				json.put("num", frac.getNum());
				json.put("denom", frac.getDenom());
				ret.add(new ItemJSONInfo(json, itemID+nums.get(i), owners.get(i)));
			}
		}
		return ret;
	}
	private JSONObject myGetItemsJSON(List<ItemJSONInfo> items, String connectionID) throws JSONException {
		JSONObject ret = new JSONObject();
		for(int i = 0; i < items.size(); i++) {
			ItemJSONInfo item = items.get(i);
			if(connectionID.equals(item.owner))
				item.json.put("status", 0);
			else if(item.owner == null)
				item.json.put("status", 1);
			else {
				Long status = connectionStatus.get(item.owner);
				if((status == ConnectionStatus.PROCESSING.ordinal()) || (status == ConnectionStatus.LEFT_WHILE_PROCESSING.ordinal()) || (status == ConnectionStatus.PAID.ordinal()))
					item.json.put("status", 3);
				else
					item.json.put("status", 2);
			}
			ret.put(item.id, item.json);
		}
		return ret;
	}

	//---------------------
	//| ACCESSORS METHODS |
	//---------------------
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

	public Map<String, Frac> getPayFracs(String connectionID) {
		Map<String, Frac> ret = new HashMap<String, Frac>();
		for(String id : itemFracs.keySet()) {
			Frac unaccounted = Frac.ONE;
			Frac zerAmount = Frac.ZERO;
			int num = 0;
			int denom = 0;
			List<String> owners = itemOwners.get(id);
			List<Frac> fracs = itemFracs.get(id);
			for(int i = 0; i < fracs.size(); i++) {
				Frac f = fracs.get(i);
				if(connectionID.equals(owners.get(i))) {
					if(f == null)
						num++;
					else
						zerAmount = zerAmount.add(f);
				};
				if(f == null) {
					denom++;
				} else {
					unaccounted = unaccounted.sub(f);
				}
			}
			if(denom > 0)
				zerAmount = zerAmount.add(unaccounted.mult(new Frac(num, denom)));
			ret.put(id, zerAmount);
		}
		return ret;
	}

	/**	Figures out how much some connection needs to pay (not including tip)
	 *
	 *	@param	items The items on the ticket
	 *	@param	connectionID The person to measure the payment of
	 *	@return	The total to pay in cents
	 */
	public Long getTotalToPay(List<TicketItem> items, String connectionID) {
		long total = 0L;
		Map<String, Frac> payFracs = getPayFracs(connectionID);
		for(TicketItem item : items)
			total += payFracs.get(item.getID()).mult(item.getNetPrice()).ceil();
		return new Frac(total, 100L).ceil();
	}

	public boolean isPaid() {
		Set<String> allOwners = new HashSet<String>();
		for(String id : itemOwners.keySet()) {
			List<String> owners = itemOwners.get(id);
			List<Frac> fracs = itemFracs.get(id);
			Frac unaccounted = Frac.ONE;
			for(Frac f : fracs)
				unaccounted = unaccounted.sub(f == null ? Frac.ZERO : f);
			for(int i = 0; i < owners.size(); i++) {
				Frac f = fracs.get(i);
				if(!Frac.ZERO.equals(f)) {
					if(owners.get(i) == null) {
						if((f == null) && (Frac.ZERO.equals(unaccounted)))
							return false;
					} else
						allOwners.add(owners.get(i));
				}
			}
		}
		for(String owner : allOwners)
			if(connectionStatus.get(owner) != ConnectionStatus.PAID.ordinal())
				return false;
		return true;
	}

	public JSONObject getItemsJSON(List<TicketItem> items, String connectionID) throws JSONException {
		return myGetItemsJSON(preprocessItems(items), connectionID);
	}

	//--------------------
	//| MUTATORS METHODS |
	//--------------------
	public void clearMetadata(DatastoreService ds)
	{
		removeConnections(connectionStatus.keySet(), ds);
		itemFracs = new HashMap<String, List<Frac>>();
		itemNums = new HashMap<String, List<Long>>();
		itemOwners = new HashMap<String, List<String>>();
	}
	public boolean initMetadata(List<TicketItem> items, DatastoreService ds) throws JSONException
	{
		//Figure out what's we should have on the ticket
		Set<String> currentItems = new HashSet<String>();
		for(TicketItem item : items)
			currentItems.add(item.getID());

		//Compare that to what's currently being tracked, and figure out what connections own
		//current items vs ones no longer on the ticket
		Set<String> itemsAlreadyTracked = new HashSet<String>();
		Set<String> itemsToUntrack = new HashSet<String>();
		Set<String> currentConnections = new HashSet<String>();
		Set<String> oldConnections = new HashSet<String>();
		for(String itemID : itemOwners.keySet())
			if(currentItems.contains(itemID)) {
				for(String connectionID : itemOwners.get(itemID))
					currentConnections.add(connectionID);
				itemsAlreadyTracked.add(itemID);
			} else {
				for(String connectionID : itemOwners.get(itemID))
					if(connectionID != null)
						oldConnections.add(connectionID);
				itemsToUntrack.add(itemID);
			}

		//Figure out what we need to add
		Set<String> itemsToTrack = new HashSet<String>();
		for(TicketItem item : items)
			if(!itemsAlreadyTracked.contains(item.getID()))
				itemsToTrack.add(item.getID());

		//Figure out what connections need to be closed
		Set<String> connectionsToClose = new HashSet<String>();
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		String uuid = UUID.randomUUID().toString();
		for(String cID : connectionStatus.keySet()) {
			boolean close = true;
			if(connectionStatus.get(cID) == ConnectionStatus.LEFT_WHILE_PROCESSING.ordinal())
				close = !currentConnections.contains(cID) && oldConnections.contains(cID);
			else try {
				channelService.sendMessage(new ChannelMessage(cID, "HEARTBEAT\n"+uuid));
				if(currentConnections.contains(cID) || (!oldConnections.contains(cID) &&
									!(connectionStatus.get(cID) == ConnectionStatus.PAID.ordinal())))
					close = false;
			} catch(ChannelFailureException e) {}
			if(close)
				connectionsToClose.add(cID);
		}

		//Remove shit, add shit, do shit
		removeConnections(connectionsToClose, ds);
		for(String item : itemsToUntrack) {
			itemFracs.remove(item);
			itemNums.remove(item);
			itemOwners.remove(item);
		}
		for(String item : itemsToTrack) {
			itemFracs.put(item, Arrays.asList(new Frac(1,1)));
			itemNums.put(item, Arrays.asList(1L));
			itemOwners.put(item, Arrays.asList((String)null));
		}

		//Sync all connections and return bool showing if commit needed
		if(itemsToUntrack.size() > 0 || itemsToTrack.size() > 0) {
			updateListeners(items, ds);
			return true;
		} else
			return false;
	}
	public void setConnectionStatus(String connectionID, ConnectionStatus status, List<TicketItem> items, DatastoreService ds) throws JSONException {
		ConnectionStatus oldStatus = ConnectionStatus.values()[connectionStatus.get(connectionID).intValue()];
		if((status == ConnectionStatus.CLOSED) && (oldStatus == ConnectionStatus.PROCESSING)) {
			status = ConnectionStatus.LEFT_WHILE_PROCESSING;
		} else if((status == ConnectionStatus.LEFT_WHILE_PROCESSING) && (oldStatus == ConnectionStatus.INPUTTING)) {
			status = ConnectionStatus.CLOSED;
		}

		//Ignore redundant updates
		if(oldStatus == status)
			return;

		//Unfix fractions
		if((status == ConnectionStatus.INPUTTING) && (status == ConnectionStatus.CLOSED))
			for(String id : itemOwners.keySet()) {
				List<String> owners =  itemOwners.get(id);
				for(int i = 0; i < owners.size(); i++)
					if(connectionID.equals(owners.get(i)))
						itemFracs.get(id).set(i, null);
			}

		//Actually set the status
		if(status == ConnectionStatus.CLOSED)
			connectionStatus.remove(connectionID);
		else {
			connectionStatus.put(connectionID, new Long(status.ordinal()));
			
			//Fix fractions
			if(oldStatus == ConnectionStatus.INPUTTING)
				for(String id : itemFracs.keySet()) {
					List<Frac> fracs = itemFracs.get(id);
					Frac fillIn = Frac.ONE;
					Long denom = 0L;
					for(Frac f : fracs)
						if(f == null)
							denom++;
						else
							fillIn = fillIn.sub(f);
					if(denom > 0L) {
						fillIn = fillIn.div(denom);
						List<String> owners =  itemOwners.get(id);
						for(int i = 0; i < owners.size(); i++)
							if(connectionID.equals(owners.get(i)) && (fracs.get(i) == null))
								fracs.set(i, fillIn);
					}
				}
		}
		updateListeners(items, ds);
	}
	public void setOwner(String id, String connectionID, List<TicketItem> items, DatastoreService ds) throws JSONException, HttpErrMsg {
		Long status = connectionStatus.get(connectionID);
		if(status != ConnectionStatus.INPUTTING.ordinal())
			throw new HttpErrMsg("Cannot add items to this connection");
		String [] tokens = id.split(":");
		id = tokens[0];
		Long num = Long.parseLong(tokens[1]);
		Integer index = null;
		List<Long> nums = itemNums.get(id);
		if(nums == null)
			throw new HttpErrMsg("No such ticket item");
		for(int i = 0; i < nums.size() && index == null; i++)
			if(nums.get(i) == num)
				index = i;
		if(index == null)
			throw new HttpErrMsg("Invalid split information");
		itemOwners.get(id).set(index, connectionID);
		updateListeners(items, ds);
	}

	public void checkAll(String connectionID, List<TicketItem> items, DatastoreService ds) throws JSONException, HttpErrMsg {
		Long status = connectionStatus.get(connectionID);
		if(status != ConnectionStatus.INPUTTING.ordinal())
			throw new HttpErrMsg("Cannot add items to this connection");
		for(String id : itemOwners.keySet()) {
			List<String> owners = itemOwners.get(id);
			for(int i = 0; i < owners.size(); i++) {
				String owner = owners.get(i);
				if((owner == null) || (connectionStatus.get(owner) == null))
					owners.set(i, connectionID);
			}
		}
		updateListeners(items, ds);
	}

	public void uncheckAll(String connectionID, List<TicketItem> items, DatastoreService ds) throws JSONException, HttpErrMsg {
		Long status = connectionStatus.get(connectionID);
		if(status != ConnectionStatus.INPUTTING.ordinal())
			throw new HttpErrMsg("Cannot remove items from this connection");
		for(String id : itemOwners.keySet()) {
			List<String> owners = itemOwners.get(id);
			for(int i = 0; i < owners.size(); i++)
				if(owners.get(i) == connectionID)
					owners.set(i, null);
		}
		updateListeners(items, ds);
	}

	public void split(String id, Long nWays, String connectionID, List<TicketItem> items, DatastoreService ds) throws JSONException, HttpErrMsg {
		Long status = connectionStatus.get(connectionID);
		if(status != ConnectionStatus.INPUTTING.ordinal())
			throw new HttpErrMsg("Cannot split items this connection");
		List<Integer> myParts = new ArrayList<Integer>();
		List<Integer> theirParts = new ArrayList<Integer>();
		List<Integer> unownedParts = new ArrayList<Integer>();

		List<String> owners = itemOwners.get(id);
		for(int i = 0; i < owners.size(); i++) {
			String owner = owners.get(i);
			if(connectionID.equals(owner))
				myParts.add(i);
			else if((owner == null) || (connectionStatus.get(owner) == null))
				unownedParts.add(i);
			else if(connectionStatus.get(owner) == ConnectionStatus.INPUTTING.ordinal())
				theirParts.add(i);
		}

		long diff = nWays - myParts.size() - theirParts.size() - unownedParts.size();
		if(diff == 0)
			return;

		List<Frac> fracs = itemFracs.get(id);
		List<Long> nums = itemNums.get(id);
		if(diff > 0) {
			Long maxNum = Collections.max(nums);
			while(diff > 0) {
				owners.add(null);
				fracs.add(null);
				nums.add(++maxNum);
				diff--;
			}
		} else while(diff < 0) {
			int index;
			if(unownedParts.size() > 0)
				index = unownedParts.remove(unownedParts.size()-1);
			else if(theirParts.size() > 0)
				index = theirParts.remove(theirParts.size()-1);
			else
				index = myParts.remove(myParts.size()-1);
			owners.remove(index);
			fracs.remove(index);
			nums.remove(index);
			diff++;
		}
		updateListeners(items, ds);
	}

	//--------------------------
	//| to/from ENTITY METHODS |
	//--------------------------

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		e.setProperty("restr", restr);
		e.setProperty("query", query);
		DSConverter.set(e, "connectionStatus", connectionStatus, DSConverter.DataTypes.MAP);
		DSConverter.set(e, "itemFracs", itemFracs, DSConverter.DataTypes.MAP, DSConverter.DataTypes.LIST, DSConverter.DataTypes.FRAC);
		DSConverter.set(e, "itemNums", itemNums, DSConverter.DataTypes.MAP, DSConverter.DataTypes.LIST);
		DSConverter.set(e, "itemOwners", itemOwners, DSConverter.DataTypes.MAP, DSConverter.DataTypes.LIST);
		return e;
	}

	@SuppressWarnings("unchecked")
	public void fromEntity(Entity e)
	{
		restr = (String) e.getProperty("restr");
		query = (String) e.getProperty("query");
		connectionStatus = (Map<String, Long>) DSConverter.get(e, "connectionStatus", DSConverter.DataTypes.MAP);
		itemFracs = (Map<String, List<Frac>>) DSConverter.get(e, "itemFracs", DSConverter.DataTypes.MAP, DSConverter.DataTypes.LIST, DSConverter.DataTypes.FRAC);
		itemNums = (Map<String, List<Long>>) DSConverter.get(e, "itemNums", DSConverter.DataTypes.MAP, DSConverter.DataTypes.LIST);
		itemOwners = (Map<String, List<String>>) DSConverter.get(e, "itemOwners", DSConverter.DataTypes.MAP, DSConverter.DataTypes.LIST);
	}
}
