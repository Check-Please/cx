 package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;

import utils.GetServletBase;
import utils.HttpErrMsg;
import utils.ParamWrapper;

import templates.CxCardBackTemplate;
import templates.CxCardBlankTemplate;
import templates.CxCardRowTemplate;
import templates.CxCardsTemplate;

public class GetCardBacksServlet extends GetServletBase
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
		if(p.getQueryString() == null || p.getQueryString().length() == 0)
			throw new HttpErrMsg("No table IDs given");
		StringBuffer cards = new StringBuffer();
		String [] mKeys = p.getQueryString().split(",");
		for(int i = 0; i < mKeys.length; i += 3)
			cards.append(CxCardRowTemplate.run(CxCardBackTemplate.run("Table", 2),
					i+1 >= mKeys.length ? CxCardBlankTemplate.run() :
						CxCardBackTemplate.run("Table", 2),
					i+2 >= mKeys.length ? CxCardBlankTemplate.run() :
						CxCardBackTemplate.run("Table", 2)));
		out.println(CxCardsTemplate.run("back", cards));
	}
}