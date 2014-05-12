package servlets.oz;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import org.json.JSONException;

import utils.GetServletBase;
import utils.HttpErrMsg;
import utils.MyUtils;
import utils.ParamWrapper;
import static utils.MyUtils.a;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;

/*NOTE:  Never EVER do this shit with a get request.  This is a HACK. */
public class EnableServlet extends GetServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 1088486516450223638L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.adminReq = true;
		config.txnReq = false;
		config.bools = a("?enable");
		config.readOnly = false;
		config.getReqHacks = true;
	}

	protected void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		Data d = new Data(MyUtils.get_NoFail(KeyFactory.createKey(Data.getKind(), "sjelin"), ds));
		Boolean enabled = p.getBool(0);
		d.enable(enabled == null ? true : enabled);
		d.commit(ds);	
	}
}