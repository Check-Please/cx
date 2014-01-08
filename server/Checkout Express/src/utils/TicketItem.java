package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kinds.TableKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import servlets.oz.Data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;

public class TicketItem {
	private String id;//Should be unique among all items ever ordered at that table, not necessarily all items everywhere
	private String name;

	//All prices stored in 100ths of a cent
	private long price;
	private long tax;
	private long serviceCharge;
	private long discount;
	private long tickFee;
	private long tickTax;
	private long tickSC;
	private long tickDiscount;

	private List<ItemModifier> mods;
	private long paidNum;//defaults to 0
	private long paidDenom;//defaults to 1

	public TicketItem(String id, String name, long price, long tax, long serviceCharge, long discount, List<ItemModifier> mods) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.tax = tax;
		this.serviceCharge = serviceCharge;
		this.discount = discount;
		this.tickFee = 0L;
		this.tickTax = 0L;
		this.tickSC = 0L;
		this.tickDiscount = 0L;
		this.mods = mods;
		this.paidNum = 0L;
		this.paidDenom = 1L;
	}

	public TicketItem(JSONObject json) throws JSONException
	{
		id = json.getString("id");
		name = json.getString("name");

		price = json.getLong("price");
		tax = json.getLong("tax");
		serviceCharge = json.has("serviceCharge") ? json.getLong("serviceCharge") : 0L;
		discount = json.has("discount") ? json.getLong("discount") : 0L;
		tickFee = json.has("tickFee") ? json.getLong("tickFee") : 0L;
		tickTax = json.has("tickTax") ? json.getLong("tickTax") : 0L;
		tickSC = json.has("tickSC") ? json.getLong("tickSC") : 0L;
		tickDiscount = json.has("tickDiscount") ? json.getLong("tickDiscount") : 0L;

		JSONArray jsonMods = json.getJSONArray("mods");
		mods = new ArrayList<ItemModifier>(jsonMods.length());
		for(int i = 0; i < jsonMods.length(); i++)
			mods.add(new ItemModifier(jsonMods.getJSONObject(i)));
		paidNum = json.getLong("paidNum");
		paidDenom = json.getLong("paidDenom");
	}


	public long getPrice() {
		return price;
	}
	public void setTickPrices(long tickFee, long tickTax, long tickSC, long tickDiscount)
	{
		this.tickFee = tickFee;
		this.tickTax = tickTax;
		this.tickSC = tickSC;
		this.tickDiscount = tickDiscount;
	}
	public String getID() {
		return id;
	}
	public List<ItemModifier> getMods() {
		return mods;
	}

	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("name", name);

		json.put("price", price);
		json.put("tax", tax);
		json.put("serviceCharge", serviceCharge);
		json.put("discount", discount);
		json.put("tickFee", tickFee);
		json.put("tickTax", tickTax);
		json.put("tickSC", tickSC);
		json.put("tickDiscount", tickDiscount);

		JSONArray jMods = new JSONArray();
		for(int i = 0; i < mods.size(); i++)
			jMods.put(mods.get(i).toJSON());
		json.put("mods", jMods);
		json.put("paidNum", paidNum);
		json.put("paidDenom", paidDenom);
		return json;
	}
	public String toString()
	{
		try {
			return this.toJSON().toString();
		} catch (JSONException e) {
			return null;
		}
	}
	public boolean equals(Object o)
	{
		if(!(o instanceof ItemModifier))
			return false;
		return this.toString().equals(o.toString());
	}
	public int hashCode()
	{
		return this.toString().hashCode();
	}

	public static List<TicketItem> getItems(String query, String restr, Map<String, Frac> paid, DatastoreService ds) throws JSONException, UnsupportedFeatureException, HttpErrMsg
	{
		JSONObject q = new JSONObject(query);
		String method = q.getString("method");
		if(method.equals("preloaded"))
			return Preloaded.getTicketItems(q.getLong("id"), paid);
		if(method.equals("oz")) {
			Data d = new Data(MyUtils.get_NoFail(KeyFactory.createKey(Data.getKind(), restr), ds));
			JSONObject ticket = new JSONObject();
			ticket.put("items", new JSONArray(d.getData().get(q.getInt("i"))));
			JSONArray tickets = new JSONArray();
			tickets.put(ticket);
			JSONObject order = new JSONObject();
			order.put("tickets", tickets);
			return OrderParser.parseOrder(order, paid);
		} else
			throw new IllegalArgumentException("Unknown system for query");
	}

	public static List<TicketItem> getItems(TableKey table, DatastoreService ds) throws JSONException, UnsupportedFeatureException, HttpErrMsg
	{
		return getItems(table.getQuery(), table.getRestrUsername(), table.getAccountedForPart(), ds);
	}
}
