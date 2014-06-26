package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.CardHashToClientPointer;
import modeltypes.Client;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;

import utils.HttpErrMsg;
import utils.MyUtils;
import utils.ParamWrapper;
import utils.PostServletBase;
import static utils.MyUtils.a;

public class EncryptCCServlet extends PostServletBase
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
		config.strs = a("pan", "name", "expr", "zip");
		config.path = a(Client.getKind(), "clientID");
		config.exists = true;
		config.keyNames = a("clientID");
		config.txnXG = true;
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		String pan = p.getStr(0);
		String name = p.getStr(1);
		String expr = p.getStr(2);
		String zip = p.getStr(3);
		PayServlet.basicValidation(pan, name, expr, zip);
		new CardHashToClientPointer(KeyFactory.createKey(CardHashToClientPointer.getKind(),
					MyUtils.sha256(pan.substring(pan.length()-4)+name.toUpperCase()+expr+zip)),
				p.getKeyName(0)).commit(ds);
		JSONObject info = new JSONObject();
		info.put("pan", pan);
		info.put("name", name);
		info.put("expr", expr);
		info.put("zip", zip);
		out.println(new Client(p.getEntity()).encrypt(info.toString()));
	}
}