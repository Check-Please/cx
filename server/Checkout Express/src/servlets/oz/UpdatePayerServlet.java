package servlets.oz;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import org.json.JSONException;

import utils.PostServletBase;
import utils.HttpErrMsg;
import utils.ParamWrapper;
import static utils.MyUtils.a;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;

public class UpdatePayerServlet extends PostServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 1088486516450223638L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.adminReq = true;
		config.txnReq = false;
		config.strs = a("channelID", "msg");
	}

	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg, JSONException
	{
		ChannelServiceFactory.getChannelService().sendMessage(new ChannelMessage(p.getStr(0),
				"load_update\n0\n"+p.getStr(1)));
	}
}