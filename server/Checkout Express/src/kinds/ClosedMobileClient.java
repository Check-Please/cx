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
	public enum CloseCause { PAID, DISCONNECTED, TICKET_CLOSED, CLIENT_CLOSE, ERROR }
	private CloseCause closeCause;
	private String errMsg;
	public ClosedMobileClient(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public ClosedMobileClient(Entity e) { super(e); }

	public ClosedMobileClient(String restr, MobileClient mc, CloseCause c)
	{
		super(KeyFactory.createKey(Restaurant.getKind(), restr).getChild(getKind(), mc.getKey().getName()), mc.itemsToPay, mc.username, mc.startTimes, mc.ticketLogID);
		init(restr, mc, c, null);
	}
	public ClosedMobileClient(String restr, MobileClient mc, CloseCause c, String errMsg)
	{
		super(KeyFactory.createKey(Restaurant.getKind(), restr).getChild(getKind(), mc.getKey().getName()), mc.itemsToPay, mc.username, mc.startTimes, mc.ticketLogID);
		init(restr, mc, c, errMsg);
	}
	public ClosedMobileClient(String restr, MobileClient mc, String errMsg)
	{
		super(KeyFactory.createKey(Restaurant.getKind(), restr).getChild(getKind(), mc.getKey().getName()), mc.itemsToPay, mc.username, mc.startTimes, mc.ticketLogID);
		init(restr, mc, CloseCause.ERROR, errMsg);
	}
	private void init(String restr, MobileClient mc, CloseCause c, String err)
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
		e.setProperty("closeCause", (long) closeCause.ordinal());
		e.setProperty("errMsg", errMsg);
		return e;
	}
	public void fromEntity(Entity e)
	{
		super.fromEntity(e);
		rating = (Long) e.getProperty("rating");
		tip = (Long) e.getProperty("tip");
		closeCause = CloseCause.values()[((Long) e.getProperty("closeCause")).intValue()];
		errMsg = (String) e.getProperty("errMsg");
	}
}
