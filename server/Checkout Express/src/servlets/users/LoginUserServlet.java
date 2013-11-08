package servlets.users;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;

import kinds.UserAuthKey;
import kinds.User;

import servlets.accounts.Login;
import utils.HttpErrMsg;
import utils.MyUtils;
import utils.ParamWrapper;
import static utils.MyUtils.a;

public class LoginUserServlet extends Login
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = -563333843333883836L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.txnReq = false;
		config.securityType = SecurityType.REJECT;
		config.keyNames = a("username");
		config.strs = a("password");
		config.bools = a("?recheckPass", "?noCCs");
	}

	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		String username = p.getKeyName(0);
		doPost(username, p.getStr(0), p.getBool(0), sesh, UserAuthKey.httpPN, User.getKind(), new UserAuthKey(MyUtils.newKey(null, UserAuthKey.getKind()), username), out, ds);
		if(p.getBool(1) == null || !p.getBool(1))
			out.println(GetUserCreditCardsServlet.getCards(
					KeyFactory.createKey(User.getKind(), username), ds));
	}
	
	
}
