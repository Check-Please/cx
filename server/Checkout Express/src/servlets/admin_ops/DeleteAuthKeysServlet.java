package servlets.admin_ops;

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpSession;

import kinds.AbstractAuthKey;
import kinds.UserAuthKey;
import utils.GetServletBase;
import utils.ParamWrapper;
import static utils.MyUtils.a;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

/*NOTE:  Never EVER do this shit with a get request.  This is a HACK. */
public class DeleteAuthKeysServlet extends GetServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 7597195762566891204L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.bools = a("?force");
		config.adminReq = true;
		config.txnReq = false;
	}

	protected void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out)
	{
		Boolean force = p.getBool(0);
		if(force == null)
			force = false;
		Date date = new Date();
		for(Entity auth : ds.prepare(new Query(UserAuthKey.getKind())).asIterable(FetchOptions.Builder.withLimit(1000))) {
			if(force || !(AbstractAuthKey.build(auth).getExprDate().after(date)))
				ds.delete(auth.getKey());
		}
		out.println((force ? "All" : "Expired")+" authkeys deleted.");
	}
}