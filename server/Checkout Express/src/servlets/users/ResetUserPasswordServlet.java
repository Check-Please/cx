package servlets.users;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;
import kinds.User;

import servlets.accounts.ResetPassword;
import utils.HttpErrMsg;
import utils.ParamWrapper;
import static utils.MyUtils.a;

public class ResetUserPasswordServlet extends ResetPassword
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 5616322953450053194L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.securityType = SecurityType.REJECT;
		config.keyNames = a("email");
		config.strs = a("key", "newPassword");
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg
	{
		doPost(p.getKeyName(0), p.getStr(0), p.getStr(1), User.getKind(), out, ds);
	}
}
