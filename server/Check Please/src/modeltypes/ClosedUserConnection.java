package modeltypes;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ClosedUserConnection extends UserConnection
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "closed_user_connection"; }
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
	public ClosedUserConnection(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public ClosedUserConnection(Entity e) { super(e); }

	public ClosedUserConnection(String restr, UserConnection uc, Long c)
	{
		super(KeyFactory.createKey(Restaurant.getKind(), restr).getChild(getKind(), uc.getKey().getName()), uc.itemsToPay, uc.username, uc.startTimes);
		init(restr, uc, c, null);
	}
	public ClosedUserConnection(String restr, UserConnection uc, Long c, String errMsg)
	{
		super(KeyFactory.createKey(Restaurant.getKind(), restr).getChild(getKind(), uc.getKey().getName()), uc.itemsToPay, uc.username, uc.startTimes);
		init(restr, uc, c, errMsg);
	}
	public ClosedUserConnection(String restr, UserConnection uc, String errMsg)
	{
		super(KeyFactory.createKey(Restaurant.getKind(), restr).getChild(getKind(), uc.getKey().getName()), uc.itemsToPay, uc.username, uc.startTimes);
		init(restr, uc, CLOSE_CAUSE__ERROR, errMsg);
	}
	private void init(String restr, UserConnection uc, Long c, String err)
	{
		rating = null;
		tip = null;
		closeCause = c;
		errMsg = err;
		logPosition("CLOSE");
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
		String eMsg = null;
		if(errMsg != null)
			eMsg = errMsg.substring(0, Math.min(500, errMsg.length()));
		e.setProperty("errMsg", eMsg);
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
