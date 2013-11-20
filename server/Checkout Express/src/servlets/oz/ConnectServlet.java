package servlets.oz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.PostServletBase;
import utils.HttpErrMsg;
import utils.ParamWrapper;
import static utils.MyUtils.a;

import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;

public class ConnectServlet extends PostServletBase
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
		config.path = a(Data.getKind(), "restr");
		config.exists = true;
	}

	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg, JSONException
	{
		Data d = new Data(p.getEntity());
		JSONObject ret = new JSONObject();
		String clientID = UUID.randomUUID().toString();
		ret.put("cleintID", clientID);
		ret.put("token", ChannelServiceFactory.getChannelService().createChannel(clientID));
		d.setClient(clientID);
		ret.put("ticks", new JSONArray(d.getData().toString()));
		d.commit(ds);
		out.println(ret);
	}
}