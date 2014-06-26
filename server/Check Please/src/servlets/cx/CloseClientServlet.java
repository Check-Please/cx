package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.ClosedUserConnection;
import modeltypes.ConnectionToTablePointer;
import modeltypes.TableKey;
import modeltypes.UserConnection;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import utils.ParamWrapper;
import utils.PostServletBase;
import static utils.MyUtils.a;

public class CloseClientServlet extends PostServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 742609711044231605L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.txnXG = true;
		config.keyNames = a("connectionID");
		config.strs = a("?error");
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException
	{
		if(p.getStr(0) == null)
			closeChannel(p.getKeyName(0), ClosedUserConnection.CLOSE_CAUSE__CLIENT_CLOSE, ds);
		else 
			closeChannel(p.getKeyName(0), ClosedUserConnection.CLOSE_CAUSE__ERROR, p.getStr(0), ds);
	}
	public static boolean closeChannel(String cID, long cause, DatastoreService ds)
	{
		return closeChannel(cID, cause, null, ds);
	}
	private static boolean closeChannel(String cID, long cause, String errMsg, DatastoreService ds)
	{
		try {
			ConnectionToTablePointer ptr = new ConnectionToTablePointer(KeyFactory.createKey(ConnectionToTablePointer.getKind(), cID), ds);
			Key tKey = KeyFactory.createKey(TableKey.getKind(), ptr.getKeyName());
			UserConnection uc = new UserConnection(tKey.getChild(UserConnection.getKind(), cID), ds);
			new ClosedUserConnection(new TableKey(tKey, ds).getRestrUsername(), uc, cause, errMsg).commit(ds);
			uc.rmv(ds);
			TableKey.sendSplitUpdate(cID, null, tKey, ds);
			ds.delete(ptr.getKey());
			return true;
		} catch (EntityNotFoundException e) {
			return false;//We must have deleted the client already
		}
	}
}