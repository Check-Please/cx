 package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kinds.BluetoothUUIDToRestaurantPointer;
import kinds.Client;
import kinds.ConnectionToTablePointer;
import kinds.BasicTickLog;
import kinds.Globals;
import kinds.UserConnection;
import kinds.TableKey;
import kinds.Restaurant;

import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import utils.MyUtils;
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
		config.longs = a("versionNum");
		config.bools = a("isNative");
		config.strs = a("tableInfo", "platform");
		config.path = a(Client.getKind(), "clientID");
		config.doubles = a("?lat", "?long", "?accuracy");
	}

	private static final int ERR__NO_TABLE_KEY = 0;
	private static final int ERR__INVALID_TABLE_KEY = 1;
	private static final int ERR__EMPTY_TICKET = 2;
	private static final int ERR__JSON = 3;
	private static final int ERR__UNSUPPORTED_FEATURE = 4;
	private static final int ERR__UPDATE_REQUIRED = 5;

	private static String deduceTableKey(JSONObject info, DatastoreService ds) throws JSONException {
		JSONArray rawUUIDs = info.getJSONArray("uuids");
		JSONArray rawRSSIs = info.getJSONArray("rssis");
		if(rawUUIDs.length() != rawRSSIs.length())
			throw new JSONException("Number of UUIDs should equal the number of RSSIs");
		Map<String, List<String>> restaurantUUIDs = new HashMap<String, List<String>>();
		Map<String, List<Double>> restaurantRSSIs = new HashMap<String, List<Double>>();
		for(int i = 0; i < rawUUIDs.length(); i++) {
			String uuid = rawUUIDs.getString(i);
			Double rssi = rawRSSIs.getDouble(i);
			try {
				String restr = new BluetoothUUIDToRestaurantPointer(KeyFactory.createKey(BluetoothUUIDToRestaurantPointer.getKind(), uuid), ds).getKeyName();
				if(restaurantUUIDs.containsKey(restr)) {
					restaurantUUIDs.get(restr).add(uuid);
					restaurantRSSIs.get(restr).add(rssi);
				} else {
					List<String> uuids = new ArrayList<String>();
					uuids.add(uuid);
					restaurantUUIDs.put(restr, uuids);
					List<Double> rssis = new ArrayList<Double>();
					rssis.add(rssi);
					restaurantRSSIs.put(restr, rssis);
				}
			} catch (EntityNotFoundException e) {}
		}
		String strongestRestr = null;
		double strongestRSSI = Double.MIN_VALUE;
		for(String restr : restaurantUUIDs.keySet()) {
			List<Double> rssis = restaurantRSSIs.get(restr);
			double avgRSSI = 0;
			for(Double rssi : rssis)
				avgRSSI += rssi;
			avgRSSI /= rssis.size();
			if(avgRSSI > strongestRSSI) {
				strongestRSSI = avgRSSI;
				strongestRestr = restr;
			}
		}
		if(strongestRestr == null)
			return null;
		
		return new Restaurant(MyUtils.get_NoFail(KeyFactory.createKey(Restaurant.getKind(), strongestRestr), ds)).findTable(restaurantUUIDs.get(strongestRestr), restaurantRSSIs.get(strongestRestr));
	}

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
		Long vNum = p.getLong(0);
		if(vNum < Globals.minSupportedVersion)
			err(ERR__UPDATE_REQUIRED, out);
		String tableInfo = p.getStr(0);
		if(tableInfo.length() == 0)
			err(ERR__NO_TABLE_KEY, out);
		else try {
			String tableKey;
			try {
				JSONObject json = new JSONObject(tableInfo);
				try {
					tableKey = deduceTableKey(json, ds);
				} catch(JSONException e) {
					err(ERR__INVALID_TABLE_KEY, out);
					return;
				}
				if(tableKey == null) {
					tableKey = "IKA";
				}
			} catch(JSONException e) {
				tableKey = tableInfo.toUpperCase();
			}
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
			new ConnectionToTablePointer(KeyFactory.createKey(ConnectionToTablePointer.getKind(), connectionID), tableKey).commit(ds);
			String token = ChannelServiceFactory.getChannelService().createChannel(connectionID);
			Key logKey = BasicTickLog.makeKey(restr.getKey().getName(), tableKey, items);
			try {
				ds.get(logKey);
			} catch(EntityNotFoundException e) {
				new BasicTickLog(logKey, items).commit(ds);
			}
			Client c;
			try {
				c = new Client(p.getKey(), ds);
			} catch(EntityNotFoundException e) {
				c = new Client(p.getKey());
			}
			c.logConnection(connectionID);

			boolean newClientKey = false;
			if(!c.hasPrivateKey()) {
				c.setKey();
				newClientKey = true;
			}
			c.commit(ds);
			new UserConnection(table.getKey().getChild(UserConnection.getKind(), connectionID), logKey.getName(), p.getStr(1)).commit(ds);

			JSONObject ret = new JSONObject();
			ret.put("tKey", tableKey);
			ret.put("restrName", restr.getName());
			ret.put("restrAddress", restr.getAddress());
			ret.put("restrStyle", restr.getStyle());
			ret.put("channelToken", token);
			ret.put("connectionID", connectionID);
			ret.put("items", new JSONArray(items.toString()));
			ret.put("split", table.getSplit(connectionID, ds));
			if(newClientKey) {
				ret.put("deleteCCs", true);
			}
			out.println(ret);
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
