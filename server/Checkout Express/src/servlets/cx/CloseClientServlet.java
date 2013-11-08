package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import kinds.BasicPointer;
import kinds.ClosedMobileClient;
import kinds.MobileClient;
import kinds.MobileTickKey;

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
		config.keyNames = a("clientID");
		config.strs = a("?error");
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException
	{
		if(p.getStr(0) == null)
			closeChannel(p.getKeyName(0), ClosedMobileClient.CloseCause.CLIENT_CLOSE, ds);
		else 
			closeChannel(p.getKeyName(0), ClosedMobileClient.CloseCause.ERROR, p.getStr(0), ds);
	}
	public static boolean closeChannel(String cID, ClosedMobileClient.CloseCause cause, DatastoreService ds)
	{
		return closeChannel(cID, cause, null, ds);
	}
	private static boolean closeChannel(String cID, ClosedMobileClient.CloseCause cause, String errMsg, DatastoreService ds)
	{
		try {
			BasicPointer ptr = new BasicPointer(KeyFactory.createKey(BasicPointer.getKind(), cID), ds);
			Key mKey = KeyFactory.createKey(MobileTickKey.getKind(), ptr.getKeyName());
			MobileClient mc = new MobileClient(mKey.getChild(MobileClient.getKind(), cID), ds);
			new ClosedMobileClient(new MobileTickKey(mKey, ds).getRestrUsername(), mc, cause, errMsg).commit(ds);
			mc.rmv(ds);
			MobileTickKey.sendSplitUpdate(cID, null, mKey, ds);
			ds.delete(ptr.getKey());
			return true;
		} catch (EntityNotFoundException e) {
			return false;//We must have deleted the client already
		}
	}
}