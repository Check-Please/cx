package servlets.users;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

import kinds.UserCC;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

import utils.MyUtils;
import utils.ParamWrapper;
import utils.GetServletBase;

import org.json.*;

public class GetUserCreditCardsServlet extends GetServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = -5979417163312276007L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.loginType = LoginType.USER;
		config.contentType = ContentType.JSON;
		config.securityType = SecurityType.REJECT;
	}

	public static JSONArray getCards(Key accKey, DatastoreService ds) throws JSONException
	{
		Iterable<Entity> cards = ds.prepare(new Query(UserCC.getKind(), accKey)).asList(FetchOptions.Builder.withDefaults());

		//Figure out how many of the first digits of the credit card we need to show
		int numFirstDigits = 0;
		boolean needsMoreDigits;
		do {
			needsMoreDigits = false;
			Set<String> combosSeen = new HashSet<String>();
			for(Entity e : cards) {
				UserCC cc = new UserCC(e);
				String combo = MyUtils.ensureNDigits(cc.getFirstSix(), numFirstDigits)+","+cc.getLastFour();
				if(combosSeen.contains(combo)) {
					needsMoreDigits = true;
					numFirstDigits++;
					break;
				} else
					combosSeen.add(combo);
			}
		} while(needsMoreDigits && numFirstDigits < 6);
		
		JSONArray ret = new JSONArray();
		for(Entity e : cards) {
			UserCC cc = new UserCC(e);
			JSONObject info = new JSONObject();
			info.put("prefix", MyUtils.ensureNDigits(cc.getFirstSix(), numFirstDigits));
			info.put("lastFour", cc.getLastFour());
			info.put("uuid", cc.getKey().getName());
			info.put("len", cc.getPANLen());
			info.put("lastUse", cc.getLastUse().getTime());
			ret.put(info);
		}
		return ret;
	}

	protected void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException
	{
		out.println(getCards(p.getAccountKey(), ds));
	}
}