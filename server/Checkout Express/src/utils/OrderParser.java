package utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderParser {
	private static ItemModifier parseMod(JSONObject mod) throws JSONException
	{
		long price = mod.has("price") ? mod.getLong("price") : 0L;
		long tax = mod.has("tax") ? mod.getLong("tax") : 0L;
		long serviceCharge = mod.has("serviceCharge") ? mod.getLong("serviceCharge") : 0L;
		long discount = mod.has("discount") ? mod.getLong("discount") : 0L; 
		return new ItemModifier(mod.getString("name"), price, tax, serviceCharge, discount);
	}
	private static List<TicketItem> parseItem(JSONObject item, int itemIndex, Object tickID, Long seatNum, Long date, Map<String, Frac> paid) throws JSONException
	{
		String name = item.getString("name");

		String id;
		boolean isUUID = false;
		try {
			id = ""+item.getLong("id");
		} catch(JSONException e) {
			try {
				id = item.getString("id");
				isUUID = true;
			} catch(JSONException f) {
				id = ""+itemIndex;
			}
		}
		if(!isUUID) {
			id += ":"+tickID;
			if(!(tickID instanceof String)) {
				if(item.has("dateAdded"))
					date = item.getLong("dateAdded");
				if(date != null)
					id = id+","+date;
				else if(seatNum != null)
					id += "-"+seatNum+","+name;
			}
		}

		long price = item.has("price") ? item.getLong("price") : 0L;
		long tax = item.has("tax") ? item.getLong("tax") : 0L;
		long serviceCharge = item.has("serviceCharge") ? item.getLong("serviceCharge") : 0L;
		long discount = item.has("discount") ? item.getLong("discount") : 0L;

		JSONArray rawMods = item.getJSONArray("mods");
		List<ItemModifier> mods = new ArrayList<ItemModifier>(rawMods.length());
		for(int i = 0; i < rawMods.length(); i++)
			mods.add(parseMod(rawMods.getJSONObject(i)));

		Frac paidFrac = paid.containsKey(id) ? paid.get(id) : Frac.ZERO;

		List<TicketItem> items = new ArrayList<TicketItem>();
		if(item.has("quantity")) {
			int q = item.getInt("quantity");
			for(int i = 0; i < q; i++)
				items.add(new TicketItem(id+","+i, name, price, tax, serviceCharge, discount, mods, paidFrac));
		} else
			items.add(new TicketItem(id, name, price, tax, serviceCharge, discount, mods, paidFrac));
		
		return items;
	}
	private static List<TicketItem> parseTicket(JSONObject tick, int tickIndex, Map<String, Frac> paid) throws JSONException
	{
		Object tickID;
		try {
			tickID = tick.getLong("id");
		} catch(JSONException e) {
			try {
				tickID = tick.getString("id");
			} catch(JSONException f) {
				tickID = new Long(tickIndex);
			}
		}
		Long seatNum = tick.has("seatNum") ? tick.getLong("seatNum") : null;
		Long dateOpened = tick.has("dateOpened") ? tick.getLong("dateOpened") : null;
		long fee = tick.has("fee") ? tick.getLong("fee") : 0L;
		long tax = tick.has("tax") ? tick.getLong("tax") : 0L;
		long serviceCharge = tick.has("serviceCharge") ? tick.getLong("serviceCharge") : 0L;
		long discount = tick.has("discount") ? tick.getLong("discount") : 0L;
		
		JSONArray rawItems = tick.getJSONArray("items");
		List<TicketItem> items = new ArrayList<TicketItem>(rawItems.length());
		for(int i = 0; i < rawItems.length(); i++)
			items.addAll(parseItem(rawItems.getJSONObject(i), i, tickID, seatNum, dateOpened, paid));
		
		long [] prices = new long[items.size()];
		for(int i = 0; i < prices.length; i++) {
			TicketItem item = items.get(i);
			prices[i] = item.getPrice();
			for(ItemModifier mod : item.getMods())
				prices[i] += mod.getPrice();
		}

		long subtotal = 0;
		for(long p : prices)
			subtotal += p;

		if(subtotal == 0) {
			subtotal = prices.length;
			for(int i = 0; i < prices.length; i++)
				prices[i] = 1;
		}

		for(int i = 0; i < prices.length; i++)
			items.get(i).setTickPrices(
					(fee*prices[i]+subtotal-1)/subtotal,
					(tax*prices[i]+subtotal-1)/subtotal,
					(serviceCharge*prices[i]+subtotal-1)/subtotal,
					(discount*prices[i])/subtotal);
		
		return items;
	}
	public static List<TicketItem> parseOrder(JSONObject order, Map<String, Frac> paid) throws JSONException
	{
		List<TicketItem> items = new ArrayList<TicketItem>();
		JSONArray ticks = order.getJSONArray("tickets");
		for(int i = 0; i < ticks.length(); i++)
			items.addAll(parseTicket(ticks.getJSONObject(i), i, paid));
		return items;
	}
}
