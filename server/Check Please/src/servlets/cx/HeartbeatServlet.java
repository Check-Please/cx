package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;


import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;

import utils.ParamWrapper;
import utils.PostServletBase;

public class HeartbeatServlet extends PostServletBase
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
		config.txnReq = false;
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException
	{
		//Not really using this right now D=
	}
}