package kinds;

import java.util.Date;

import utils.MyUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public abstract class AbstractAuthKey extends AbstractKind {

	String username;
	public static final String usernamePN = "username";
	Date expr;
	public AbstractAuthKey(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public AbstractAuthKey(Entity e) { super(e); }
	public AbstractAuthKey(Key k, String userName)
	{
		setKey(k);
		this.username = userName;
		updateExpr();
	}

	public void updateExpr()
	{
		expr = new Date((new Date()).getTime() + MyUtils.week);
	}

	protected abstract String getLoginKind();

	public Key getAccountKey()
	{
		return KeyFactory.createKey(getLoginKind(), username);
	}
	
	public Date getExprDate()
	{
		return expr;
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		e.setProperty(usernamePN, username);
		e.setProperty("expr", expr);
		return e;
	}

	public void fromEntity(Entity e)
	{
		username = (String) e.getProperty(usernamePN);
		expr = (Date) e.getProperty("expr");
	}

	public static AbstractAuthKey build(Entity e)
	{
		if(e.getKind().equals(UserAuthKey.getKind()))
			return new UserAuthKey(e);
		else
			throw new IllegalArgumentException("Unknown auth key type");
	}
}
