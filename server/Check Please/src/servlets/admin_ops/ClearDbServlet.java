package servlets.admin_ops;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpSession;

import utils.GetServletBase;
import utils.ParamWrapper;
import static utils.MyUtils.a;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

/*NOTE:  Never EVER do this shit with a get request.  This is a HACK. */
public class ClearDbServlet extends GetServletBase
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
		config.adminReq = true;
		config.txnReq = false;
		config.bools = a("sure");
	}

	protected void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException
	{
		if(!p.getBool(0))
			return;
		List<Entity> es;
		while(!(es = ds.prepare(new Query()).asList(FetchOptions.Builder.withLimit(1000))).isEmpty()) {
			for(Entity e : es)
				try {
					ds.delete(e.getKey());
				} catch(IllegalArgumentException ex) {}
		}
		out.println("Database cleared.");
	}
}