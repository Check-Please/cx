package utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import kinds.MobileTickKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import servlets.oz.Data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;

public class TicketItem {
	private String id;//Must be unique among all items ever ordered at that table, not necessarily all items everywhere
	private String name;
	private Date orderDate;
	//All prices stored in 100ths of a cent
	private long price;
	private long tax;
	private long serviceCharge;
	private long discount;
	private List<ItemModifier> mods;
	private String type;//optional, useful for analysis
	private long paidNum;//defaults to 0
	private long paidDenom;//defaults to 1

	public TicketItem(String id, String name, Date orderDate, long price, long tax, long serviceCharge, long discount, List<ItemModifier> mods)
	{
		init(id, name, orderDate, price, tax, serviceCharge, discount, mods, null, 0L, 1L);
	}
	public TicketItem(String id, String name, Date orderDate, long price, long tax, long serviceCharge, long discount, List<ItemModifier> mods, String type)
	{
		init(id, name, orderDate, price, tax, serviceCharge, discount, mods, type, 0L, 1L);
	}
	public TicketItem(String id, String name, Date orderDate, long price, long tax, long serviceCharge, long discount, List<ItemModifier> mods, long paidNum, long paidDenom)
	{
		init(id, name, orderDate, price, tax, serviceCharge, discount, mods, null, paidNum, paidDenom);
	}
	public TicketItem(String id, String name, Date orderDate, long price, long tax, long serviceCharge, long discount, List<ItemModifier> mods, String type, long paidNum, long paidDenom)
	{
		init(id, name, orderDate, price, tax, serviceCharge, discount, mods, type, paidNum, paidDenom);
	}
	public void init(String id, String name, Date orderDate, long price, long tax, long serviceCharge, long discount, List<ItemModifier> mods, String type, long paidNum, long paidDenom)
	{
		this.id = id;
		this.name = name;
		this.orderDate = orderDate;
		this.price = price;
		this.tax = tax;
		this.serviceCharge = serviceCharge;
		this.discount = discount;
		this.mods = mods;
		this.type = type;
		this.paidNum = paidNum;
		this.paidDenom = paidDenom;
	}
	public TicketItem(JSONObject json) throws JSONException
	{
		id = json.getString("id");
		name = json.getString("name");
		orderDate = new Date(json.getLong("orderDate"));
		price = json.getLong("price");
		tax = json.getLong("tax");
		serviceCharge = json.getLong("serviceCharge");
		discount = json.getLong("discount");
		JSONArray jsonMods = json.getJSONArray("mods");
		mods = new ArrayList<ItemModifier>(jsonMods.length());
		for(int i = 0; i < jsonMods.length(); i++)
			mods.add(new ItemModifier(jsonMods.getJSONObject(i)));
		type = json.has("type") ? json.getString("type") : null;
		paidNum = json.getLong("paidNum");
		paidDenom = json.getLong("paidDenom");
	}
	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("name", name);
		json.put("orderDate", orderDate.getTime());
		json.put("price", price);
		json.put("tax", tax);
		json.put("serviceCharge", serviceCharge);
		json.put("discount", discount);
		JSONArray jMods = new JSONArray();
		for(int i = 0; i < mods.size(); i++)
			jMods.put(mods.get(i).toJSON());
		json.put("mods", jMods);
		if(type != null)
			json.put("type", type);
		json.put("paidNum", paidNum);
		json.put("paidDenom", paidDenom);
		return json;
	}
	public String getID() {
		return id;
	}
	public String toString()
	{
		try {
			return this.toJSON().toString();
		} catch (JSONException e) {
			return null;
		}
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public boolean equals(Object o)
	{
		if(!(o instanceof TicketItem))
			return false;
		TicketItem that = (TicketItem) o;
		return	this.id.equals(that.id) &&
				this.name.equals(that.name) &&
				this.orderDate.equals(that.orderDate) &&
				(this.price == that.price) &&
				(this.tax == that.tax) &&
				(this.serviceCharge == that.serviceCharge) &&
				(this.discount == that.discount) &&
				this.mods.equals(that.mods) &&
				(this.type == null ? that.type == null :
					this.type.equals(that.type)) &&
				(this.paidNum == that.paidNum) &&
				(this.paidDenom == that.paidDenom);
	}
	public int hashCode()
	{
		return	(int) (id.hashCode() + 31*(
				name.hashCode() + 31*(
				orderDate.hashCode() + 31*(
				price + 31*(
				tax + 31*(
				serviceCharge + 31*(
				discount + 31*(
				mods.hashCode() + 31*(
				(type == null ? 0 : type.hashCode()) + 31*(
				paidNum + 31*(
				paidDenom)))))))))));
	}
	public static List<TicketItem> getItems(String query, String restr, Map<String, Frac> paid, DatastoreService ds) throws JSONException, UnsupportedFeatureException, HttpErrMsg
	{
		JSONObject q = new JSONObject(query);
		String method = q.getString("method");
		if(method.equals("preloaded"))
			return Preloaded.getTicketItems(q.getLong("id"), paid);
		if(method.equals("oz")) {
			Data d = new Data(MyUtils.get_NoFail(KeyFactory.createKey(Data.getKind(), restr), ds));
			return SubtleUtils.processTickets(new JSONObject(d.getData().get(q.getInt("i"))), paid);
		} else
			throw new IllegalArgumentException("Unknown system for query");
	}

	public static List<TicketItem> getItems(MobileTickKey mobile, DatastoreService ds) throws JSONException, UnsupportedFeatureException, HttpErrMsg
	{
		return getItems(mobile.getQuery(), mobile.getRestrUsername(), mobile.getPaidMap(), ds);
	}
}
