 package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kinds.BasicPointer;
import kinds.BasicTickLog;
import kinds.UserConnection;
import kinds.TableKey;
import kinds.Restaurant;

import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import utils.PostServletBase;
import utils.HttpErrMsg;
import utils.ParamWrapper;
import utils.TicketItem;
import utils.UnsupportedFeatureException;
import static utils.MyUtils.a;

public class InitServlet extends PostServletBase
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
		config.securityType = SecurityType.REJECT;
		config.contentType = ContentType.JSON;
		config.txnReq = true;
		config.txnXG = true;
		config.bools = a("isNative");
		config.strs = a("tableKey", "clientID", "platform");
		config.doubles = a("?lat", "?long", "?accuracy");
	}

	private static final int ERR__NO_TABLE_KEY = 0;
	private static final int ERR__INVALID_TABLE_KEY = 1;
	private static final int ERR__EMPTY_TICKET = 2;
	private static final int ERR__JSON = 3;
	private static final int ERR__UNSUPPORTED_FEATURE = 4;

	/*
	 * NOTE: Does not return
	 */
	private static void err(int code, String msg, PrintWriter out) throws JSONException, HttpErrMsg
	{
		JSONObject ret = new JSONObject();
		ret.put("errCode", code);
		if(msg != null)
			ret.put("errMsg", msg);
		out.println(ret);
		throw HttpErrMsg.ROLLBACK_DB;
	}

	private static void err(int code, PrintWriter out) throws JSONException, HttpErrMsg
	{
		err(code, null, out);
	}

	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg, JSONException
	{
		String tableKey = p.getStr(0);
		if(tableKey.length() == 0)
			err(ERR__NO_TABLE_KEY, out);
		else try {
			tableKey = tableKey.toUpperCase();
			TableKey table = new TableKey(KeyFactory.createKey(TableKey.getKind(), tableKey), ds);
			Restaurant restr = table.getRestr(ds);
			List<TicketItem> items = TicketItem.getItems(table, ds);
			if(items == null || items.size() == 0) {
				err(ERR__EMPTY_TICKET, out);
				return;
			}
			if(table.clearOldMetadata(items, ds))
				table.commit(ds);
			String connectionID = UUID.randomUUID().toString();
			new BasicPointer(KeyFactory.createKey(BasicPointer.getKind(), connectionID), tableKey).commit(ds);
			String token = ChannelServiceFactory.getChannelService().createChannel(connectionID);
			Key logKey = BasicTickLog.makeKey(restr.getKey().getName(), tableKey, items);
			try {
				ds.get(logKey);
			} catch(EntityNotFoundException e) {
				new BasicTickLog(logKey, items).commit(ds);
			}
			new UserConnection(table.getKey().getChild(UserConnection.getKind(), connectionID), logKey.getName()).commit(ds);

			JSONObject ret = new JSONObject();
			ret.put("restrName", restr.getName());
			ret.put("restrAddress", restr.getAddress());
			ret.put("restrStyle", restr.getStyle());
			ret.put("channelToken", token);
			ret.put("connectionID", connectionID);
			ret.put("items", new JSONArray(items.toString()));
			ret.put("split", table.getSplit(connectionID, ds));
		} catch (EntityNotFoundException e) {
			err(ERR__INVALID_TABLE_KEY, out);
		} catch (JSONException e) {
			e.printStackTrace();
			err(ERR__JSON, out);
		} catch (UnsupportedFeatureException e) {
			err(ERR__UNSUPPORTED_FEATURE, e.getMessage(), out);
		}
	}
}
