package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.TableKey;
import modeltypes.UserConnection;

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
		config.path = a("/", TableKey.getKind(), "tableKey", UserConnection.getKind(), "connectionID");
		config.exists = true;
		config.strs = a("position");
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException
	{
		UserConnection c = new UserConnection(p.getEntity());
		c.logPosition(p.getStr(0));
		c.commit(ds);
	}
}