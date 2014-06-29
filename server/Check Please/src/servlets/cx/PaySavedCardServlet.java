package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import modeltypes.Client;
import modeltypes.TableKey;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;

import utils.HttpErrMsg;
import utils.ParamWrapper;
import utils.PostServletBase;
import static utils.MyUtils.a;

public class PaySavedCardServlet extends PostServletBase
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
		config.contentType = ContentType.JSON;
		config.txnXG = true;
		config.path = a("/", TableKey.getKind(), "tableKey");
		config.exists = true;
		config.path2 = a("/", Client.getKind(), "clientID");
		config.exists2 = true;
		config.strs = a("cardCT");
		config.longs = a("total", "tip");//NOTE: total does not include tip
		config.keyNames = a("connectionID");
	}

	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		String cardCT = p.getStr(0);
		if(cardCT.startsWith("cookie:")) {
			cardCT = cardCT.substring(7);
			Cookie [] cookies = p.getCookies();
			for(int i = 0; i < cookies.length; i++)
				if(cookies[i].getName() == cardCT) {
					cardCT = cookies[i].getValue();
					break;
				}
		}
		JSONObject card = new JSONObject(new Client(p.getEntity(1)).decrypt(cardCT));
		out.println(PayNewCardServlet.pay(card.getString("pan"), card.getString("name"),
				card.getString("expr"), null, card.getString("zip"), p.getLong(0), p.getLong(1),
				new TableKey(p.getEntity()), p.getKeyName(0), ds));
	}
}