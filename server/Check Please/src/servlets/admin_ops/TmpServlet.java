package servlets.admin_ops;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import org.json.JSONException;

import kinds.TableKey;

import utils.GetServletBase;
import utils.ParamWrapper;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;

public class TmpServlet extends GetServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 7597195762566891204L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.adminReq = true;
		config.txnReq = false;
	}

	/*NOTE:  Never EVER do this shit with a get request.  This is a HACK. */
	protected void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException
	{
		String [] x = {"CVQ", "DSB", "KJQ", "KTF", "LKV", "YBQ", "YNA"};
		for(String k : x)
			ds.delete(KeyFactory.createKey(TableKey.getKind(), k));
	}
}