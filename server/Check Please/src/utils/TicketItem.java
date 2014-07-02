package utils;

import java.util.ArrayList;
import java.util.List;

import modeltypes.TableKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;

public class TicketItem {
	private String id;//Should be unique among all items ever ordered at that table, not necessarily all items everywhere
	private String name;

	//All prices stored in 100ths of a cent
	private long price;
	private long tax;
	private long serviceCharge;
	private long discount;

	private List<ItemModifier> mods;

	public TicketItem(String id, String name, long price, long tax, long serviceCharge, long discount, List<ItemModifier> mods) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.tax = tax;
		this.serviceCharge = serviceCharge;
		this.discount = discount;
		this.mods = mods;
	}

	public TicketItem(JSONObject json) throws JSONException
	{
		id = json.getString("id");
		name = json.getString("name");

		price = json.getLong("price");
		tax = json.getLong("tax");
		serviceCharge = json.has("serviceCharge") ? json.getLong("serviceCharge") : 0L;
		discount = json.has("discount") ? json.getLong("discount") : 0L;

		JSONArray jsonMods = json.getJSONArray("mods");
		mods = new ArrayList<ItemModifier>(jsonMods.length());
		for(int i = 0; i < jsonMods.length(); i++)
			mods.add(new ItemModifier(jsonMods.getJSONObject(i)));
	}


	public long getPrice() {
		return price;
	}
	public long getNetPrice() {
		return price+tax+serviceCharge-discount;
	}
	public String getID() {
		return id;
	}
	public void addTax(Long moreTax) {
		tax += moreTax;
	}
	public void addSC(Long moreSC) {
		serviceCharge += moreSC;
	}
	public void addDiscount(Long moreDiscount) {
		discount += moreDiscount;
	}
	public List<ItemModifier> getMods() {
		return mods;
	}

	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("name", name);

		json.put("price", price);
		json.put("tax", tax);
		json.put("serviceCharge", serviceCharge);
		json.put("discount", discount);

		JSONArray jMods = new JSONArray();
		for(int i = 0; i < mods.size(); i++)
			jMods.put(mods.get(i).toJSON());
		json.put("mods", jMods);
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

	public static HttpErrMsg currentlyDisabled = new HttpErrMsg(404, "Checkout Express has been temporarily disabled.");
	public static List<TicketItem> getItems(String query, String restr, DatastoreService ds) throws JSONException, UnsupportedFeatureException, HttpErrMsg
	{
		JSONObject q = new JSONObject(query);
		String method = q.getString("method");
		if(method.equals("preloaded"))
			return Preloaded.getTicketItems(q.getLong("id"));
		else
			throw new IllegalArgumentException("Unknown system for query");
	}

	public static List<TicketItem> getItems(TableKey table, DatastoreService ds) throws JSONException, UnsupportedFeatureException, HttpErrMsg
	{
		return getItems(table.getQuery(), table.getRestrUsername(), ds);
	}
}
