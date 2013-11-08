package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import kinds.MobileClient;
import kinds.MobileTickKey;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;

import utils.ParamWrapper;
import utils.PostServletBase;
import static utils.MyUtils.a;

public class LogPositionServlet extends PostServletBase
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
		config.path = a("/", MobileTickKey.getKind(), "mobileKey", MobileClient.getKind(), "clientID");
		config.exists = true;
		config.longs = a("position");
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException
	{
		MobileClient c = new MobileClient(p.getEntity());
		c.logPosition(p.getLong(0));
		c.commit(ds);
	}
}