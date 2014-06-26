package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.ClosedUserConnection;
import modeltypes.Restaurant;
import modeltypes.TableKey;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import utils.ParamWrapper;
import utils.PostServletBase;
import static utils.MyUtils.a;

public class RateServlet extends PostServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 742609711044231605L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.path = a("/", TableKey.getKind(), "tableKey");
		config.exists = true;
		config.keyNames = a("connectionID");
		config.longs = a("rating");
		config.txnXG = true;
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException
	{
		TableKey table = new TableKey(p.getEntity());
		try {
			ClosedUserConnection c = new ClosedUserConnection(
				KeyFactory.createKey(Restaurant.getKind(),
					table.getRestrUsername()).getChild(ClosedUserConnection.getKind(),
						p.getKeyName(0)), ds);
			c.setRating(p.getLong(0));
			c.commit(ds);
		} catch (EntityNotFoundException e) {
			// There's nothing we can do with their rating really
		}
	}
}