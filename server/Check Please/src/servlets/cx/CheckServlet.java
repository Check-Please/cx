package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.TableKey;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;

import utils.HttpErrMsg;
import utils.ParamWrapper;
import utils.PostServletBase;
import utils.TicketItem;
import static utils.MyUtils.a;

public class CheckServlet extends PostServletBase
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
		config.path = a("/", TableKey.getKind(), "tableKey");
		config.exists = true;
		config.keyNames = a("connectionID");
		config.strs = a("itemID");
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		TableKey table = new TableKey(p.getEntity());
		table.setOwner(p.getStr(0), p.getKeyName(0), TicketItem.getItems(table, ds), ds);
		table.commit(ds);
	}
}