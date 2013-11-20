package kinds;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ClosedMobileClient extends MobileClient
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "closed_mobile_client"; }
	private Long rating;
	private Long tip;
	public static final long CLOSE_CAUSE__PAID = 1L;
	public static final long CLOSE_CAUSE__DISCONNECTED = 2L;
	public static final long CLOSE_CAUSE__TICKET_CLOSED = 3L;
	public static final long CLOSE_CAUSE__CLIENT_CLOSE = 4L;
	public static final long CLOSE_CAUSE__ERROR = 5L;
	private Long closeCause;
	private String errMsg;


	//Define list of possible close causes
	//We do not use an enum because we don't want the ordinal value to change if some causes are 
	//added and some are removed.  This would make results previously stored in the database
	//incorrect
	public ClosedMobileClient(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public ClosedMobileClient(Entity e) { super(e); }

	public ClosedMobileClient(String restr, MobileClient mc, Long c)
	{
		super(KeyFactory.createKey(Restaurant.getKind(), restr).getChild(getKind(), mc.getKey().getName()), mc.itemsToPay, mc.username, mc.startTimes, mc.ticketLogID);
		init(restr, mc, c, null);
	}
	public ClosedMobileClient(String restr, MobileClient mc, Long c, String errMsg)
	{
		super(KeyFactory.createKey(Restaurant.getKind(), restr).getChild(getKind(), mc.getKey().getName()), mc.itemsToPay, mc.username, mc.startTimes, mc.ticketLogID);
		init(restr, mc, c, errMsg);
	}
	public ClosedMobileClient(String restr, MobileClient mc, String errMsg)
	{
		super(KeyFactory.createKey(Restaurant.getKind(), restr).getChild(getKind(), mc.getKey().getName()), mc.itemsToPay, mc.username, mc.startTimes, mc.ticketLogID);
		init(restr, mc, CLOSE_CAUSE__ERROR, errMsg);
	}
	private void init(String restr, MobileClient mc, Long c, String err)
	{
		rating = null;
		tip = null;
		closeCause = c;
		errMsg = err;
		logPosition(-1);
	}

	public void setRating(long r)
	{
		rating = r;
	}

	public void setTip(long t)
	{
		tip = t;
	}

	public Entity toEntity()
	{
		Entity e = super.toEntity();
		e.setProperty("rating", rating);
		e.setProperty("tip", tip);
		e.setProperty("closeCause", closeCause);
		e.setProperty("errMsg", errMsg);
		return e;
	}
	public void fromEntity(Entity e)
	{
		super.fromEntity(e);
		rating = (Long) e.getProperty("rating");
		tip = (Long) e.getProperty("tip");
		closeCause = (Long) e.getProperty("closeCause");
		errMsg = (String) e.getProperty("errMsg");
	}
}
