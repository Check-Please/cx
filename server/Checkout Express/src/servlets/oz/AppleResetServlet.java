package servlets.oz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.json.JSONException;

import kinds.TableKey;
import utils.GetServletBase;
import utils.HttpErrMsg;
import utils.MyUtils;
import utils.ParamWrapper;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;

/*NOTE:  Never EVER do this shit with a get request.  This is a HACK. */
public class AppleResetServlet extends GetServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 7863745795076143117L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.txnReq = false;
		config.readOnly = false;
		config.getReqHacks = true;
	}

	public static String appleKey = "OZ0";
	private static String order = "[{\"name\":\"Burger\",\"price\":70000,\"tax\":4900,\"mods\":[],\"id\":\"d1d5d7c0-72d5-44bb-af79-c5c0a5fdfd33\",\"orderDate\":\"1394290996524\"},{\"name\":\"Salad\",\"price\":60000,\"tax\":4200,\"mods\":[],\"id\":\"f614a273-9d17-4d59-810e-b720964e5cb0\",\"orderDate\":\"1394291068607\"},{\"name\":\"Coke\",\"price\":10000,\"tax\":700,\"mods\":[],\"id\":\"622db719-332b-4615-b9b5-6215c083b270\",\"orderDate\":\"1394291071958\"},{\"name\":\"Pepsi\",\"price\":10000,\"tax\":700,\"mods\":[],\"id\":\"5dd79cb0-2d97-4edb-9518-6ae4bea95407\",\"orderDate\":\"1394291100779\"}]";

	protected void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg, JSONException
	{
		Data d = new Data(MyUtils.get_NoFail(KeyFactory.createKey(Data.getKind(), "sjelin"), ds));
		List<String> ticks = d.getData();
		ticks.set(0, order);
		d.commit(ds);
		
		TableKey t = new TableKey(MyUtils.get_NoFail(KeyFactory.createKey(TableKey.getKind(), "OZ0"), ds));
		if(t.clearTickMetadata(ds))
			t.commit(ds);
		
		out.println("Ticket Reset");
	}
}