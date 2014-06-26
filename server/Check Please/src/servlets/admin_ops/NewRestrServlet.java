package servlets.admin_ops;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.*;
import utils.*;
import static utils.MyUtils.a;

import com.google.appengine.api.datastore.*;

public class NewRestrServlet extends GetServletBase {
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = -668194174579512772L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.strs = a("password", "name", "address", "email", "?style");
		config.keyNames = a("username");
		config.adminReq = true;
	}

	public void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg
	{
		Key key = KeyFactory.createKey(Restaurant.getKind(), p.getKeyName(0));
		try {
			ds.get(key);
			//At this point, an exception should have been thrown
			throw new HttpErrMsg("Username Taken");
		} catch (EntityNotFoundException e) {
			Restaurant restr = new Restaurant(key, p.getStr(0), p.getStr(1), p.getStr(2), p.getStr(3), p.getStr(4));
			restr.commit(ds);
			out.println("New Restraunt Added");
		}
	}
}
