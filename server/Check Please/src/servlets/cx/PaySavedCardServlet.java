package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import modeltypes.Client;
import modeltypes.Globals;
import modeltypes.TableKey;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;

import utils.HttpErrMsg;
import utils.MyUtils;
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
		config.securityType = SecurityType.REJECT;
		config.txnXG = true;
		config.path = a("/", TableKey.getKind(), "tableKey");
		config.exists = true;
		config.path2 = a("/", Client.getKind(), "clientID");
		config.exists2 = true;
		config.strs = a("cardCT", "?password");
		config.longs = a("total", "tip");//NOTE: total does not include tip, both are in cents
		config.keyNames = a("connectionID");
		config.FORBID_RETRIES = true;
	}

	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		String cardCT = p.getStr(0);
		String error = null;
		if(cardCT.startsWith(Globals.COOKIED_CARD_CT_PREFIX)) {
			for(Cookie c : p.getCookies())
				//NOTE:  I'd love to check the path and if we're using an HttpOnly cookie,
				//but getPath() always returns null and HttpOnly cookies aren't directly supported
				//in App Engine at the moment
				if(Globals.CARD_CT_COOKIE.equals(c.getName())) {
					cardCT = c.getValue() + cardCT.substring(Globals.COOKIED_CARD_CT_PREFIX.length());
					break;
				}
			if(cardCT.startsWith(Globals.COOKIED_CARD_CT_PREFIX))
				error = "NO_CIPHERTEXT";
		}
		
		JSONObject card = null;
		if(error == null)
			try {
				String plaintext = new Client(p.getEntity(1)).decrypt(cardCT);
				if(plaintext == null) {
					error = "KEY_INCORRECT";
				} else {
					card = new JSONObject(plaintext);
					if(p.getStr(1) == null) {
						if(card.has("password"))
							error = "PASSWORD_REQ";
					} else if(card.has("password")) {
						if(!MyUtils.checkProtectedPassword(p.getStr(1), card.getString("password")))
							error = "PASSWORD_INCORRECT";
					} else
						error = "PASSWORD_NO_REQ";
				}
			} catch(JSONException ex) {
				error = "PLAINTEXT_MALFORMATTED";
			}
		if(error != null)
			out.println(error);
		else
			out.println(PayNewCardServlet.pay(card.getString("pan"), card.getString("name"),
				card.getString("expr"), null, card.getString("zip"), p.getLong(0), p.getLong(1),
				new TableKey(p.getEntity()), p.getKeyName(0), ds));
	}
}