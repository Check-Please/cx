package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.subtledata.api.LocationsApi;
import com.subtledata.client.ApiException;

public class SubtleUtils {
	public static String makeItemID(long dateOpened, int tickID, int itemID, int quantityIndex, int quantity)
	{
		return "SUBTLE:"+dateOpened+","+tickID+">"+itemID+">"+quantityIndex+"/"+quantity;
	}
	public static int getTickID(String itemID)
	{
		return Integer.parseInt(itemID.substring(itemID.indexOf(',')+1, itemID.indexOf('>')));
	}
	static DateFormat df;
	public static DateFormat getDateFormat()
	{
		if(df == null)
			df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
		return df;
	}

	private static final double zeroCents = 0.009;

	public static List<TicketItem> processTicket(JSONObject tick) throws UnsupportedFeatureException, JSONException, HttpErrMsg
	{
		return processTicket(tick, null);
	}

	public static List<TicketItem> processTicket(JSONObject tick, Map<String, Frac> paidMap) throws UnsupportedFeatureException, JSONException, HttpErrMsg
	{
		List<TicketItem> newItems = new ArrayList<TicketItem>();
		Boolean isOpen = null;
		try {
			isOpen = tick.getBoolean("ticket_open");
		} catch (JSONException e) {}
		JSONArray items =  tick.getJSONArray("items");
		if(((isOpen == null) || isOpen) && (items.length() > 0)) {
			int tickID = tick.getInt("ticket_id");
			Date dateOpened;
			try {
				dateOpened = getDateFormat().parse(tick.getString("date_opened"));
			} catch (ParseException e) {
				throw new HttpErrMsg(500, "Error in parsing date from subtle data ticket");
			}
			double subtotal = tick.getDouble("subtotal");
			double tax = tick.getDouble("tax");
			double serviceCharge = tick.getDouble("service_charge");
			double discount = tick.getDouble("discount");
			double remainingBalance = tick.getDouble("remaining_balance");
			double total = tick.getDouble("total");
			if(total - remainingBalance > zeroCents)
				throw new UnsupportedFeatureException(UnsupportedFeatureException.Type.SUBTLE_DATA__PARTIALLY_PAID);
			int itemCnt = 0;
			for(int i = 0; i < items.length(); i++)
				itemCnt += items.getJSONObject(i).getInt("quantity");
			for(int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i);
				if(item.getBoolean("voided"))
					throw new UnsupportedFeatureException(UnsupportedFeatureException.Type.SUBTLE_DATA__VOIDED_ITEMS);
				int itemID = item.getInt("ticket_item_id");
				Integer menuItemID = null;
				try {
					menuItemID = item.getInt("item_id");
				} catch(JSONException e) {}
				double price = item.getDouble("price");
				double portion = subtotal > zeroCents ? price/subtotal : 1.0 / itemCnt;
				String name = item.getString("name");
				int quantity = item.getInt("quantity");
				JSONArray rawMods = item.getJSONArray("item_modifiers");
				List<ItemModifier> mods = new ArrayList<ItemModifier>();
				for(int j = 0; j < rawMods.length(); j++) {
					JSONObject mod = rawMods.getJSONObject(j);
					Integer modID = null;
					try {
						modID = mod.getInt("modifier_id");
					} catch(JSONException e) {}
					mods.add(new ItemModifier(mod.getString("name"), ""+modID));
				}
				for(int j = 0; j < quantity; j++) {
					String id = SubtleUtils.makeItemID(dateOpened.getTime(), tickID, itemID, j, quantity);
					Frac paid = paidMap != null && paidMap.containsKey(id) ? paidMap.get(id) : Frac.ZERO;
					newItems.add(new TicketItem(id,
							name, dateOpened,
							MyUtils.toCentHundredths(price),
							MyUtils.toCentHundredths(tax*portion),
							MyUtils.toCentHundredths(serviceCharge*portion),
							MyUtils.toCentHundredths(discount*portion),
							mods,
							""+menuItemID,
							paid.getNum(),
							paid.getDenom()));
				}
			}
		}
		return newItems;
	}

	public static List<TicketItem> processTickets(JSONArray ticks) throws UnsupportedFeatureException, JSONException, HttpErrMsg
	{
		return processTickets(ticks, null);
	}

	public static List<TicketItem> processTickets(JSONArray ticks, Map<String, Frac> paid) throws UnsupportedFeatureException, JSONException, HttpErrMsg
	{
		List<TicketItem> ret = new ArrayList<TicketItem>(ticks.length());
		for(int i = 0; i < ticks.length(); i++)
			ret.addAll(processTicket(ticks.getJSONObject(i), paid));
		return ret;
	}

	public static List<TicketItem> processTickets(JSONObject ticks) throws UnsupportedFeatureException, JSONException, HttpErrMsg
	{
		return processTickets(ticks, null);
	}

	public static List<TicketItem> processTickets(JSONObject ticks, Map<String, Frac> paid) throws UnsupportedFeatureException, JSONException, HttpErrMsg
	{
		return processTickets(ticks.getJSONArray("open_tickets"), paid);
	}

	public static List<TicketItem> getTicketsByTableID(long loc, long id) throws UnsupportedFeatureException, JSONException, HttpErrMsg
	{
		return getTicketsByTableID(loc, id, null);
	}

	public static List<TicketItem> getTicketsByTableID(long loc, long id, Map<String, Frac> paid) throws UnsupportedFeatureException, JSONException, HttpErrMsg
	{
		try {
			return processTickets(LocationsApi.getTable((int) loc, (int) id), paid);
		} catch (ApiException e) {
			throw new HttpErrMsg(e);
		}
	}

	public static List<TicketItem> getTicketsByTableName(long loc, String name) throws UnsupportedFeatureException, JSONException, HttpErrMsg
	{
		return getTicketsByTableName(loc, name, null);
	}

	public static List<TicketItem> getTicketsByTableName(long loc, String name, Map<String, Frac> paid) throws UnsupportedFeatureException, JSONException, HttpErrMsg
	{
		try {
			JSONArray ticks = LocationsApi.getTickets((int) loc, false);
			List<TicketItem> ret = new ArrayList<TicketItem>();
			for(int i = 0; i < ticks.length(); i++) {
				JSONObject tick = ticks.getJSONObject(i);
				if(tick.getString("table_name").equals(name))
					ret.addAll(processTicket(tick, paid));
			}
			return ret;
		} catch (ApiException e) {
			throw new HttpErrMsg(e);
		}
	}

	public static String encodePassword(String pass) throws HttpErrMsg
	{
		pass = pass.replaceAll("[^0-9a-zA-Z]", "_");
		if(pass.length() < 8)
			pass += "H6je4Mh9".substring(pass.length());
		else if(pass.length() > 50)
			pass = pass.substring(0, 50);
		return pass;
	}

/*	//Assumes username is a UUID
	public static String makeSubtleEmail(String firstName, String lastName, String username)
	{
		char [] uname = username.toLowerCase().toCharArray();
		for(int i = 0; i < uname.length; i++)
			if(uname[i] != '-') {
				if(uname[i] <= '9')
					uname[i] += 'a'-'0';
				else
					uname[i] += 10;
			};
		return firstName.substring(0, 1).toLowerCase()+lastName.toLowerCase()+"-"+new String(uname)+"@chkex.com";
	}*/
}
