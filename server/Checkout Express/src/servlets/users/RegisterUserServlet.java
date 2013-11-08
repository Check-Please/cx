package servlets.users;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.subtledata.api.UsersApi;
import com.subtledata.client.ApiException;

import kinds.AbstractAccount;
import kinds.User;
import kinds.UserAuthKey;

import servlets.accounts.Register;
import utils.HttpErrMsg;
import utils.MyUtils;
import utils.ParamWrapper;
import utils.SubtleUtils;
import static utils.MyUtils.a;

public class RegisterUserServlet extends Register
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = -5172969013436858581L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.keyNames = a("username");
		config.strs = a("password");
		config.txnXG = true;
		config.bools = a("?noCCs");
		config.securityType = SecurityType.REJECT;
	}
	protected void doPost(final ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		final String username = p.getKeyName(0);
		final Key k = KeyFactory.createKey(User.getKind(), username);
		doPost(username, username, "Checkout Express", k, new AccountBuilder() {
				public AbstractAccount build() throws JSONException, HttpErrMsg
				{
					String uuid = UUID.randomUUID().toString();
					JSONObject userInfo = new JSONObject();
					userInfo.put("user_name", uuid);
					userInfo.put("password", SubtleUtils.encodePassword(p.getStr(0)));
					userInfo.put("first_name", "---");
					userInfo.put("last_name", "---");
					userInfo.put("email_address", "customer@chkex.com");
					try {
						JSONObject subtleInfo = UsersApi.createUser(userInfo);
						return new User(k, p.getStr(0), uuid, subtleInfo.getInt("user_id"));
					} catch (ApiException e) {
						throw new HttpErrMsg(e);
					}
				}
			}, sesh, UserAuthKey.httpPN, new UserAuthKey(MyUtils.newKey(null, UserAuthKey.getKind()), username), out, ds);
		if(p.getBool(0) == null || !p.getBool(0))
			out.println(GetUserCreditCardsServlet.getCards(
					KeyFactory.createKey(User.getKind(), username), ds));

	}
}
