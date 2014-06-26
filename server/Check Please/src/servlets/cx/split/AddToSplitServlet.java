package servlets.cx.split;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.TableKey;
import modeltypes.UserConnection;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;

import utils.ParamWrapper;
import utils.PostServletBase;
import static utils.MyUtils.a;

public class AddToSplitServlet extends PostServletBase
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
		config.path = a("/", TableKey.getKind(), "tableKey", UserConnection.getKind(), "connectionID");
		config.exists = true;
		config.strs = a("itemID");
//		If for some reason the update has the old data, uncomment the following:
//		config.txnReq = false;
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException
	{
		UserConnection c = new UserConnection(p.getEntity());
		c.addItem(p.getStr(0));
		c.commit(ds);
		TableKey.sendSplitUpdate(c.getKey().getName(), new JSONArray(c.getItems()), c.getKey().getParent(), ds);
	}
}