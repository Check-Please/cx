package utils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Preloaded {
	public static List<TicketItem> getTicketItems(long id, Map<String, Frac> paid) throws JSONException, UnsupportedFeatureException, HttpErrMsg
	{
		String d = SubtleUtils.getDateFormat().format(new Date(0L));
		return SubtleUtils.processTickets(id == 0L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 20.00," +
				"\"tax\": 1.40," +
				"\"service_charge\": 1.00," +
				"\"discount\": 1.00," +
				"\"remaining_balance\": 21.40," +
				"\"total\": 21.40," +
				"\"items\": [{" +
					"\"name\": \"Burger\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 7.00," +
					"\"quantity\": 2," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Salad\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": [{" +
						"\"name\": \"No Dressing\"," +
					"}]," +
					"\"price\": 6.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"},{" +
				"\"ticket_id\": 2," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 1.00," +
				"\"tax\": 0.07," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 1.07," +
				"\"total\": 1.07," +
				"\"items\": [{" +
					"\"name\": \"Coke\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 1.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 1L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 24.00," +
				"\"tax\": 1.68," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 25.68," +
				"\"total\": 25.68," +
				"\"items\": [{" +
					"\"name\": \"Wings\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Hot Sauce\"," +
					"}]," +
					"\"price\": 10.50," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Provolone Wedges\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 7.50," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"21st AMENDMENT Back in Black\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 3.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"21st AMENDMENT Brew Free or Die\"," +
					"\"ticket_item_id\": 4," +
					"\"item_modifiers\": []," +
					"\"price\": 3.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 2L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 65.00," +
				"\"tax\": 4.55," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 69.55," +
				"\"total\": 69.55," +
				"\"items\": [{" +
					"\"name\": \"Lobster risotto\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 15.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Oven roasted chicken breast\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 23.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Veal medallions\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 27.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 3L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 21.00," +
				"\"tax\": 1.47," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 22.47," +
				"\"total\": 22.47," +
				"\"items\": [{" +
					"\"name\": \"Sweet Potato Tater Tots\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 5.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Shady Burger\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": [{" +
						"\"name\": \"No Cheese\"," +
					"}]," +
					"\"price\": 10.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Margherita Pizza\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 16.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
			"}]" +
		"}") : id == 4L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 58.00," +
				"\"tax\": 4.06," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 62.06," +
				"\"total\": 62.06," +
				"\"items\": [{" +
					"\"name\": \"Steamed Mussels\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 12.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Ricotta Ravioli\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 17.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"NY Strip Steak\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 29.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 5L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 51.00," +
				"\"tax\": 3.57," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 54.57," +
				"\"total\": 54.57," +
				"\"items\": [{" +
					"\"name\": \"Crab Cake\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 14.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Haddock\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 22.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Fettuccini, Asparagus + Mushroom\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 15.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 6L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 33.50," +
				"\"tax\": 2.35," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 35.85," +
				"\"total\": 35.85," +
				"\"items\": [{" +
					"\"name\": \"Banana Bread\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 7.50," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Walnut Carrot + Sumac Fritters\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 14.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Vietnamese Salad\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 12.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 7L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 23.97," +
				"\"tax\": 1.68," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 25.65," +
				"\"total\": 25.65," +
				"\"items\": [{" +
					"\"name\": \"Zuppa del Giorno\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 3.99," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Potato Gnocchi\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 10.99," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Frittata del Giorno\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 8.99," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 8L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 34.00," +
				"\"tax\": 2.38," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 36.38," +
				"\"total\": 36.38," +
				"\"items\": [{" +
					"\"name\": \"Stuffed Banana Peppers\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 8.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Margarita Pizza\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 10.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Steak Sandwich\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 11.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Blue Moon\"," +
					"\"ticket_item_id\": 4," +
					"\"item_modifiers\": []," +
					"\"price\": 5.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 9L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 28.00," +
				"\"tax\": 1.96," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 29.96," +
				"\"total\": 29.96," +
				"\"items\": [{" +
					"\"name\": \"Stack'd Burger\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Kaiser Bun\"" +
					"},{" +
						"\"name\": \"American Cheese\"" +
					"},{" +
						"\"name\": \"A1 Steak Sauce\"" +
					"},{" +
						"\"name\": \"Iceberg Lettuce\"" +
					"},{" +
						"\"name\": \"Tomato\"" +
					"}]," +
					"\"price\": 7.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Stack'd Burger\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Sesame\"" +
					"},{" +
						"\"name\": \"Ketchup\"" +
					"},{" +
						"\"name\": \"Grilled Mushrooms\"" +
					"},{" +
						"\"name\": \"Grilled Onions\"" +
					"},{" +
						"\"name\": \"Jalapenos\"" +
					"},{" +
						"\"name\": \"Romaine Lettuce\"" +
					"},{" +
						"\"name\": \"Fried Egg\"" +
					"}]," +
					"\"price\": 7.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Regular Fries\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 1.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Vanilla Milkshake\"," +
					"\"ticket_item_id\": 4," +
					"\"item_modifiers\": []," +
					"\"price\": 5.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Irish Coffee\"," +
					"\"ticket_item_id\": 5," +
					"\"item_modifiers\": []," +
					"\"price\": 8.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 10L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 16.00," +
				"\"tax\": 1.12," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 17.12," +
				"\"total\": 17.12," +
				"\"items\": [{" +
					"\"name\": \"Sopa de Pollo\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 4.50," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Pollo Tacos\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 10.50," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Fajitas\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Chicken and Steak\"" +
					"}]," +
					"\"price\": 13.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 11L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 26.00," +
				"\"tax\": 1.82," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 27.82," +
				"\"total\": 27.82," +
				"\"items\": [{" +
					"\"name\": \"BRGR fries\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 4.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"The BRGR\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 13.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Fire In The Hole\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": [{" +
						"\"name\": \"No Cheese\"" +
					"}]," +
					"\"price\": 9.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 12L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 44.00," +
				"\"tax\": 3.08," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 47.08," +
				"\"total\": 47.08," +
				"\"items\": [{" +
					"\"name\": \"Egg Rolls\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 2.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Thai Spicy Duck\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 19.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Pad Thai\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Chicken\"" +
					"}]," +
					"\"price\": 13.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 13L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 71.00," +
				"\"tax\": 4.97," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 75.97," +
				"\"total\": 75.97," +
				"\"items\": [{" +
					"\"name\": \"Lobster App\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 12.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Monkfish Osso Bucco\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 25.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Prime NY\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Rare\"" +
					"}]," +
					"\"price\": 34.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 14L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 33.00," +
				"\"tax\": 2.31," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 35.31," +
				"\"total\": 35.31," +
				"\"items\": [{" +
					"\"name\": \"Fish Tacos\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 12.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Fried Chicken Sandwich\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 11.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Tavern burer\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Medium Rare\"" +
					"}]," +
					"\"price\": 10.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 15L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 26.00," +
				"\"tax\": 1.82," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 27.82," +
				"\"total\": 27.82," +
				"\"items\": [{" +
					"\"name\": \"Chips and Salsa\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 3.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Bone-in Chicken Breast\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 8.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Marinated Chicken Breast\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Mustard Dill Sauce\"" +
					"}]," +
					"\"price\": 15.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 16L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 29.00," +
				"\"tax\": 2.03," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 31.03," +
				"\"total\": 31.03," +
				"\"items\": [{" +
					"\"name\": \"Chips on Chips\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 6.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Dead Head Sandwitch\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 9.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Margarita Pizza\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 14.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 17L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 85.00," +
				"\"tax\": 5.95," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 90.95," +
				"\"total\": 90.95," +
				"\"items\": [{" +
					"\"name\": \"Tartare de Boeuf\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 15.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Jambe d’Agneau\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 32.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Steak/Frites\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Peppercorn Sauce\"" +
					"},{"+
						"\"name\": \"Rare\"" +
					"}]," +
					"\"price\": 30.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Tarte Tatin\"," +
					"\"ticket_item_id\": 4," +
					"\"item_modifiers\": []," +
					"\"price\": 8.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 18L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 25.00," +
				"\"tax\": 1.75," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 26.75," +
				"\"total\": 26.75," +
				"\"items\": [{" +
					"\"name\": \"Britney Spears\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 6.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Salmon Rushdies Return\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 10.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Royale with Cheese\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Medium Rare\"" +
					"}]," +
					"\"price\": 9.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 19L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 63.00," +
				"\"tax\": 4.41," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 67.41," +
				"\"total\": 67.41," +
				"\"items\": [{" +
					"\"name\": \"Soupe de poisson\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 9.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Boeuf Bourguignon\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 28.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Steak-Frites\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Rare\"" +
					"},{"+
						"\"name\": \"Roquefort\"" +
					"}]," +
					"\"price\": 26.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 20L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 50.00," +
				"\"tax\": 3.50," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 53.50," +
				"\"total\": 53.50," +
				"\"items\": [{" +
					"\"name\": \"Green Tomato Gazpacho\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 6.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Lamb Meatloaf\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 19.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Hanger Steak\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Medium\"" +
					"}]," +
					"\"price\": 25.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 21L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 50.00," +
				"\"tax\": 3.50," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 53.50," +
				"\"total\": 53.50," +
				"\"items\": [{" +
					"\"name\": \"Green Tomato Gazpacho\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 6.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Lamb Meatloaf\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 19.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Hanger Steak\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Medium\"" +
					"}]," +
					"\"price\": 25.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 22L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 54.00," +
				"\"tax\": 3.78," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 53.78," +
				"\"total\": 53.78," +
				"\"items\": [{" +
					"\"name\": \"Pork and Pistachio pate\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 9.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Swordfish\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 26.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Lazy Vareniki\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 19.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 23L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 24.50," +
				"\"tax\": 1.72," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 26.22," +
				"\"total\": 26.22," +
				"\"items\": [{" +
					"\"name\": \"Dip Sticks\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": [{" +
						"\"name\": \"Mozzarella\"" +
					"}]," +
					"\"price\": 7.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Classic Maggie\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 8.50," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Sirloin Burger\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 9.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 24L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 23.00," +
				"\"tax\": 1.61," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 24.61," +
				"\"total\": 24.61," +
				"\"items\": [{" +
					"\"name\": \"Marinated Olives\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 4.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Ricotta Pizza\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 13.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Chocolate Pot de Creme\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 6.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : id == 25L ? new JSONObject("{" +
			"\"open_tickets\": [{" +
				"\"ticket_id\": 1," +
				"\"date_opened\": \"" + d + "\"," +
				"\"subtotal\": 24.00," +
				"\"tax\": 1.68," +
				"\"service_charge\": 0.00," +
				"\"discount\": 0.00," +
				"\"remaining_balance\": 25.68," +
				"\"total\": 25.68," +
				"\"items\": [{" +
					"\"name\": \"Sukothai\"," +
					"\"ticket_item_id\": 1," +
					"\"item_modifiers\": []," +
					"\"price\": 6.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Street Noodle 1\"," +
					"\"ticket_item_id\": 2," +
					"\"item_modifiers\": []," +
					"\"price\": 9.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"},{" +
					"\"name\": \"Pad Thai\"," +
					"\"ticket_item_id\": 3," +
					"\"item_modifiers\": []," +
					"\"price\": 9.00," +
					"\"quantity\": 1," +
					"\"voided\": false" +
				"}]" +
			"}]" +
		"}") : null, paid);
	}
}
