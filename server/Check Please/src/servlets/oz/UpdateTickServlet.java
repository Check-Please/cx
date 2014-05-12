package servlets.oz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.json.JSONException;

import utils.PostServletBase;
import utils.HttpErrMsg;
import utils.ParamWrapper;
import static utils.MyUtils.a;

import com.google.appengine.api.datastore.DatastoreService;

public class UpdateTickServlet extends PostServletBase
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
		config.path = a(Data.getKind(), "restr");
		config.strs = a("tick");
		config.longs = a("i");
		config.exists = true;
	}

	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg, JSONException
	{
		Data d = new Data(p.getEntity());
		List<String> ticks = d.getData();
		ticks.set(p.getLong(0).intValue(), p.getStr(0));
		d.commit(ds);
	}
}