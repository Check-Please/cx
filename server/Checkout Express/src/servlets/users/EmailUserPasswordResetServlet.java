package servlets.users;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;

import kinds.User;

import servlets.accounts.EmailPasswordReset;
import utils.HttpErrMsg;
import utils.ParamWrapper;
import static utils.MyUtils.a;

public class EmailUserPasswordResetServlet extends EmailPasswordReset
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
		config.keyNames = a("email");
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg {
		doPost(p.getKeyName(0), p.getKeyName(0), User.getKind(), "Checkout Express", out, ds);
	}
}
