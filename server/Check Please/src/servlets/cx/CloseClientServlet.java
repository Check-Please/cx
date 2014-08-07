package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.Globals;
import modeltypes.ConnectionToTablePointer;
import modeltypes.TableKey;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import utils.HttpErrMsg;
import utils.ParamWrapper;
import utils.PostServletBase;
import utils.TicketItem;
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
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		if(p.getStr(0) == null)
			closeChannel(p.getKeyName(0), Globals.CLOSE_CAUSE__CLIENT_CLOSE, ds);
		else 
			closeChannel(p.getKeyName(0), Globals.CLOSE_CAUSE__ERROR, p.getStr(0), ds);
	}
	public static boolean closeChannel(String cID, long cause, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		return closeChannel(cID, cause, null, ds);
	}
	private static boolean closeChannel(String cID, long cause, String errMsg, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		try {
			ConnectionToTablePointer ptr = new ConnectionToTablePointer(KeyFactory.createKey(ConnectionToTablePointer.getKind(), cID), ds);

			TableKey table = new TableKey(KeyFactory.createKey(TableKey.getKind(), ptr.getKeyName()), ds);
			table.setConnectionStatus(cID, TableKey.ConnectionStatus.CLOSED, TicketItem.getItems(table, ds), ds);
			table.commit(ds);
			return true;
		} catch (EntityNotFoundException e) {
			return false;//We must have deleted the client already
		}
	}
}