package kinds;

import utils.HttpErrMsg;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class User extends AbstractAccount
{
	private String subtleUsername;
	private int subtleID;
	protected String kindName() { return getKind(); }
	public static String getKind() { return "user"; }
	public User(Key k, DatastoreService ds) throws EntityNotFoundException {super(k, ds); }
	public User(Entity e) { super(e); }
	public User(Key k, String password, String subtleUsername, int subtleID) throws HttpErrMsg {
		super(k, password);
		this.subtleUsername = subtleUsername;
		this.subtleID = subtleID;
	}
	public String getSubtleUsername()
	{
		return subtleUsername;
	}
	public int getSubtleID()
	{
		return subtleID;
	}
	public Entity toEntity()
	{
		Entity e = super.toEntity();
		e.setProperty("subtleID", (long) subtleID);
		e.setProperty("subtleUsername", subtleUsername);
		return e;
	}
	public void fromEntity(Entity e)
	{
		super.fromEntity(e);
		subtleID = ((Long) e.getProperty("subtleID")).intValue();
		subtleUsername = (String) e.getProperty("subtleUsername");
	}
}
