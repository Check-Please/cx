package kinds;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class UserAuthKey extends AbstractAuthKey
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "user_auth_key"; }
	public static final String httpPN = "H01Z1C7ERHXPF1OR";
	public UserAuthKey(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public UserAuthKey(Entity e) { super(e); }
	public UserAuthKey(Key k, String userName) { super(k, userName); }
	protected String getLoginKind() {return User.getKind();}
}
