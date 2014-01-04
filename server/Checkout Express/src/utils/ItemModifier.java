package utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ItemModifier {
	private String name;
	private long price;//stored in 100ths of a cent
	private long tax;//stored in 100ths of a cent
	private String type;//optional, useful for analysis
	private List<Boolean> choices;//null = has no choices
	public ItemModifier(String name)
	{
		init(name, 0L, 0L, null, null);
	}
	public ItemModifier(String name, String type)
	{
		init(name, 0L, 0L, type, null);
	}
	public ItemModifier(String name, long price, long tax)
	{
		init(name, price, tax, null, null);
	}
	public ItemModifier(String name, long price, long tax, String type)
	{
		init(name, price, tax, type, null);
	}
	public ItemModifier(String name, List<Boolean> choices)
	{
		init(name, 0L, 0L, null, choices);
	}
	public ItemModifier(String name, String type, List<Boolean> choices)
	{
		init(name, 0L, 0L, type, choices);
	}
	public ItemModifier(String name, long price, long tax, List<Boolean> choices)
	{
		init(name, price, tax, null, choices);
	}
	public ItemModifier(String name, long price, long tax, String type, List<Boolean> choices)
	{
		init(name, price, tax, type, choices);
	}
	private void init(String name, long price, long tax, String type, List<Boolean> choices)
	{
		this.name = name;
		this.price = price;
		this.tax = tax;
		this.type = type;
		this.choices = choices;
	}
	public ItemModifier(JSONObject json) throws JSONException
	{
		name = json.getString("name");
		price = json.has("price") ? json.getLong("price") : 0L;
		tax = json.has("tax") ? json.getLong("tax") : 0L;
		type = json.has("type") ? json.getString("type") : null;
		if(json.has("choices")) {
			JSONArray jsonChoices = json.getJSONArray("choices");
			choices = new ArrayList<Boolean>(jsonChoices.length());
			for(int i = 0; i < jsonChoices.length(); i++)
				choices.add(jsonChoices.getBoolean(i));
		} else
			choices = null;
	}
	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("price", price);
		json.put("tax", tax);
		if(type != null)
			json.put("type", type);
		if(choices != null)
			json.put("choices", choices);
		return json;
	}
	public boolean equals(Object o)
	{
		if(!(o instanceof ItemModifier))
			return false;
		ItemModifier that = (ItemModifier) o;
		return	this.name.equals(that.name) &&
				(this.price == that.price) &&
				(this.tax == that.tax) &&
				(this.type == null ? that.type == null :
					this.type.equals(that.type));
	}
	public int hashCode()
	{
		return	name.hashCode() + 31*(
				((int) price) + 31*(
				((int) tax) + 31*(
				(type == null ? 0 : type.hashCode()))));
	}
	public String toString()
	{
		try {
			return this.toJSON().toString();
		} catch (JSONException e) {
			return null;
		}
	}
}