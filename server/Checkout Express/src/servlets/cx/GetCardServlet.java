 package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import kinds.MobileTickKey;
import kinds.Restaurant;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import utils.GetServletBase;
import utils.HttpErrMsg;
import utils.ParamWrapper;

import templates.CxCardBack;
import templates.CxCardFront;
import templates.CxCard;

public class GetCardServlet extends GetServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 9151686588288617431L;

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
		if((mKey == null) || (mKey.length() == 0)) {
			throw new HttpErrMsg(404, "No Such Restaurant");
		} else try {
			mKey = mKey.toUpperCase();
			MobileTickKey mobile = new MobileTickKey(KeyFactory.createKey(MobileTickKey.getKind(), mKey), ds);
			Restaurant restr = mobile.getRestr(ds);
			out.println(CxCard.run(restr.getName(), restr.getStyle(), CxCardFront.run(mKey), CxCardBack.run("Table", 2)));
		} catch (EntityNotFoundException e) {
			throw new HttpErrMsg(404, "No Such Restaurant");
		}
	}
}
