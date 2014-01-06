package utils;

import org.json.JSONException;
import org.json.JSONObject;


public class ItemModifier {
	private String name;

	//All prices stored in 100ths of a cent
	private long price;//Defaults to 0
	private long tax;//Defaults to 0
	private long serviceCharge;//Defaults to 0
	private long discount;//Defaults to 0

	public ItemModifier(String name, long price, long tax, long serviceCharge, long discount) {
		this.name = name;
		this.price = price;
		this.tax = tax;
		this.serviceCharge = serviceCharge;
		this.discount = discount;
	}

	public ItemModifier(JSONObject json) throws JSONException
	{
		name = json.getString("name");
		
		price = json.has("price") ? json.getLong("price") : 0L;
		tax = json.has("tax") ? json.getLong("tax") : 0L;
		serviceCharge = json.has("serviceCharge") ? json.getLong("serviceCharge") : 0L;
		discount = json.has("discount") ? json.getLong("discount") : 0L;
	}

	public long getPrice() {
		return price;
	}

	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("price", price);
		json.put("tax", tax);
		json.put("serviceCharge", serviceCharge);
		json.put("serviceCharge", discount);
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
}