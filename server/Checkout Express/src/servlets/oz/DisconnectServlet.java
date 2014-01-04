package servlets.oz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import kinds.TableKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Frac;
import utils.MyUtils;
import utils.PostServletBase;
import utils.HttpErrMsg;
import utils.ParamWrapper;
import static utils.MyUtils.a;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;

public class DisconnectServlet extends PostServletBase
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
		config.strs = a("payments");
	}

	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg, JSONException
	{
		JSONObject payments = new JSONObject(p.getStr(0));
		for(Iterator<?> i = payments.keys(); i.hasNext();) {
			Object o = i.next();
			if(o instanceof String) {
				String tKey = (String) o;
				JSONArray payers = payments.getJSONArray(tKey);
				TableKey table = new TableKey(MyUtils.get_NoFail(KeyFactory.createKey(TableKey.getKind(), tKey), ds));
				for(int j = 0; j < payers.length(); j++) {
					JSONObject payer = payers.getJSONObject(j);
					if(payer.getLong("statusCode") == 0L) {
						String connectionID = payer.getString("cID");
						JSONArray itemsPaidRaw = payer.getJSONArray("itemsToPay");
						JSONArray payNumsRaw = payer.getJSONArray("payFracNums");
						JSONArray payDenomsRaw = payer.getJSONArray("payFracDenoms");
						List<String> itemsPaid = new ArrayList<String>(itemsPaidRaw.length());
						List<Frac> ammountPaid = new ArrayList<Frac>(itemsPaidRaw.length());
						for(int k = 0; k < itemsPaidRaw.length(); k++) {
							itemsPaid.add(itemsPaidRaw.getString(k));
							ammountPaid.add(new Frac(payNumsRaw.getLong(k), payDenomsRaw.getLong(k)));
						}
						servlets.cx.PayServlet.paymentFailureCallback(table, connectionID, itemsPaid, ammountPaid, ds, "Lost connection");
					}
				}
			}
		}
		servlets.cx.PayServlet.paymentFailureCallback(new TableKey(MyUtils.get_NoFail(KeyFactory.createKey(TableKey.getKind(), "OZ"+p.getLong(0)), ds)), p.getStr(0), p.getStrList(0), Frac.makeFracs(p.getLongList(0), p.getLongList(1)), ds, p.getStr(1));
	}
}