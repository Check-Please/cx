package servlets.admin_ops;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.ConnectionToTablePointer;
import modeltypes.TableKey;
import utils.GetServletBase;
import utils.ParamWrapper;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class CleanServlet extends GetServletBase
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
	}

	/*NOTE:  Never EVER do this shit with a get request.  This is a HACK. */
	protected void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException
	{
		TableKey m;
		try {
			m = new TableKey(KeyFactory.createKey(TableKey.getKind(), "IKA"),ds);
			m.clearTickMetadata(ds);
			m.commit(ds);
			for(Entity e : ds.prepare(new Query(ConnectionToTablePointer.getKind())).asIterable(FetchOptions.Builder.withLimit(1000)))
				ds.delete(e.getKey());
		} catch (EntityNotFoundException e) {}
	}
}