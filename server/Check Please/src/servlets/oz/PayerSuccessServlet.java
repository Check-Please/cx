package servlets.oz;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import kinds.TableKey;

import org.json.JSONException;

import utils.Frac;
import utils.MyUtils;
import utils.PostServletBase;
import utils.HttpErrMsg;
import utils.ParamWrapper;
import static utils.MyUtils.a;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;

public class PayerSuccessServlet extends PostServletBase
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
		config.strs = a("channelID");
		config.longs = a("i");
		config.strLists = a("items");
		config.longLists = a("nums", "denoms");
	}

	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg, JSONException
	{
		servlets.cx.PayServlet.paymentSuccessCallback(new TableKey(MyUtils.get_NoFail(KeyFactory.createKey(TableKey.getKind(), "OZ"+p.getLong(0)), ds)), null, p.getStr(0), p.getStrList(0), Frac.makeFracs(p.getLongList(0), p.getLongList(1)), ds);

	}
}