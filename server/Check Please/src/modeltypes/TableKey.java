package modeltypes;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Frac;
import utils.MyUtils;
import utils.TicketItem;

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
	public static enum cIDStatuses {PROCESSING, LEFT_WHILE_PROCESSING, PAID, INPUTTING, CLOSED};

	public TableKey(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public TableKey(Entity e) { super(e); }

	public TableKey(Key k, String restr, String query)
	{
		setKey(k);
		this.restr = restr;
		this.query = query;
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

	public boolean clearMetadata(DatastoreService ds)
	{
		//TODO
		return false;
	}
	public boolean initMetadata(List<TicketItem> items, DatastoreService ds) throws JSONException
	{
		//TODO
		return false;
	}

	public JSONObject getItemsJSON(List<TicketItem> items, String connectionID) {
		// TODO Auto-generated method stub
		return null;
	}
	public void setConnectionStatus(String connectionID, cIDStatuses status, DatastoreService ds) {
		//NOTE: special cases:
		//		PROCESSING -> CLOSED = LEFT_WHILE_PROCESSING
		//		LEFT_WHILE_PROCESSING -> INPUTTING = CLOSED
		// TODO Auto-generated method stub
		
	}
	public Map<String, Frac> getPayFracs(String connectionID) {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isPaid() {
		// TODO Auto-generated method stub
		return false;
	}
	public Long getTotalToPay(List<TicketItem> items, String connectionID) {
		// TODO Auto-generated method stub
		return 0L;
	}
	public void setOwner(String id, String connectionID) {
		// TODO Auto-generated method stub
		
	}
	public void checkAll(String connectionID, Boolean checked) {
		// TODO Auto-generated method stub
		
	}
	public void split(String id, Long nWays, String connectionID) {
		//TODO
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		e.setProperty("restr", restr);
		e.setProperty("query", query);
		return e;
	}

	public void fromEntity(Entity e)
	{
		restr = (String) e.getProperty("restr");
		query = (String) e.getProperty("query");
	}
}
