package servlets.splash;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.EmailList;
import modeltypes.Globals;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import utils.HttpErrMsg;
import utils.ParamWrapper;
import utils.PostServletBase;
import static utils.MyUtils.a;

public class EmailListServlet extends PostServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 106525018859546022L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.strs = a("email", "zip");
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		final Key listKey = KeyFactory.createKey(EmailList.getKind(), Globals.defaultID);
		EmailList list;
		try {
			list = new EmailList(ds.get(listKey));
		} catch (EntityNotFoundException e) {
			list = new EmailList(listKey);
		}
		list.saveEmail(p.getStr(0), p.getStr(1));
		list.commit(ds);
	}
}