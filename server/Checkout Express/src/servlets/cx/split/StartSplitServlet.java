package servlets.cx.split;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import org.json.JSONException;

import kinds.MobileTickKey;

import com.google.appengine.api.datastore.DatastoreService;

import utils.ParamWrapper;
import utils.PostServletBase;
import static utils.MyUtils.a;

public class StartSplitServlet extends PostServletBase
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
		config.path = a("/", MobileTickKey.getKind(), "mobileKey");
		config.exists = true;
		config.keyNames = a("clientID");
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException
	{
		MobileTickKey m = new MobileTickKey(p.getEntity());
		m.startSplit();
		m.sendStartSplit(p.getKeyName(0), ds);
		m.commit(ds);
	}
}