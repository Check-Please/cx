package servlets.oz;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.Restaurant;
import modeltypes.TableKey;

import org.json.JSONException;
import org.json.JSONObject;

import utils.GetServletBase;
import utils.HttpErrMsg;
import utils.ParamWrapper;
import static utils.MyUtils.a;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;

/*NOTE:  Never EVER do this shit with a get request.  This is a HACK. */
public class InitServlet extends GetServletBase
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
		config.keyNames = a("restr");
		config.path = a(Restaurant.getKind(), "restr");
		config.exists = true;
		config.longs = a("?len");
		config.readOnly = false;
		config.getReqHacks = true;
	}

	protected void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg, JSONException
	{
		Long len = p.getLong(0);
		if(len == null)
			len = 6L;
		String restr = p.getKeyName(0);
		(new Data(KeyFactory.createKey(Data.getKind(), restr), len.intValue())).commit(ds);
		JSONObject query = new JSONObject();
		query.put("method", "oz");
		query.put("restr", restr);
		for(int i = 1; i <= len; i++) {
			query.put("i", i);
			(new TableKey(KeyFactory.createKey(TableKey.getKind(), "OZ"+i), restr, query.toString())).commit(ds);
		}
	}
}