package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import kinds.TableKey;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import templates.CxFacts;
import utils.GetServletBase;
import utils.HttpErrMsg;
import utils.ParamWrapper;

public class FactSheetServlet extends GetServletBase
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
		config.contentType = ContentType.HTML;
	}

	protected void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg
	{
		String mKey = p.getQueryString();
		String name;
		if((mKey == null) || (mKey.length() == 0)) {
			mKey = "IKA";
			name = null;
		} else try {
			mKey = mKey.toUpperCase();
			TableKey table = new TableKey(KeyFactory.createKey(TableKey.getKind(), mKey), ds);
			name = table.getRestr(ds).getName();
		} catch (EntityNotFoundException e) {
			throw new HttpErrMsg(404, "No Such Restaurant");
		}
		out.println(CxFacts.run(name, mKey));
	}
}