package servlets.admin_ops;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import modeltypes.*;
import utils.*;
import static utils.MyUtils.a;

import com.google.appengine.api.datastore.*;

public class NewTableKeyServlet extends GetServletBase {
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
		config.keyNames = a("restr");
		config.strs = a("query", "?code");
		config.adminReq = true;
		config.txnReq = true;
		config.txnXG = true;
		config.readOnly = false;
		config.getReqHacks = true;
	}

	public void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg
	{
		try {
			ds.get(KeyFactory.createKey(Restaurant.getKind(), p.getKeyName(0)));
		} catch (EntityNotFoundException e) {
			throw new HttpErrMsg("No such restaurant");
		}
		String code = p.getStr(1);
		if(code == null) {
			Globals g = Globals.getGlobals(ds);
			code =  g.getNextTicketKey(ds);
			g.commit(ds);
		}
		(new TableKey(KeyFactory.createKey(TableKey.getKind(), code), p.getKeyName(0), p.getStr(0))).commit(ds);
	}
}
