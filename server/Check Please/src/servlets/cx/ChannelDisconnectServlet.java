package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.ClosedUserConnection;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;

import utils.HttpErrMsg;
import utils.ParamWrapper;
import utils.PostServletBase;

public class ChannelDisconnectServlet extends PostServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = -6760144797550875144L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.txnXG = true;
	}

	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		CloseClientServlet.closeChannel(p.getChannelID(), ClosedUserConnection.CLOSE_CAUSE__DISCONNECTED, ds);
	}
}
