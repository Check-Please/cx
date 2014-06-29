package utils;

import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Preloaded {
	public static List<TicketItem> getTicketItems(long id) throws JSONException, UnsupportedFeatureException, HttpErrMsg
	{
		Long d = new Date(0L).getTime();
		return OrderParser.parseOrder(id == 0L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 14000," +
				"\"items\": [{" +
					"\"name\": \"Burger\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 70000," +
					"\"quantity\": 2," +
				"},{" +
					"\"name\": \"Salad\"," +
					"\"id\": 2," +
					"\"mods\": [{" +
						"\"name\": \"No Dressing\"," +
					"}]," +
					"\"price\": 60000," +
				"}]" +
			"},{" +
				"\"id\": 2," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 00700," +
				"\"items\": [{" +
					"\"name\": \"Coke\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 10000," +
				"}]" +
			"}]" +
		"}") : id == 1L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 16800," +
				"\"items\": [{" +
					"\"name\": \"Wings\"," +
					"\"id\": 1," +
					"\"mods\": [{" +
						"\"name\": \"Hot Sauce\"," +
					"}]," +
					"\"price\": 105000," +
				"},{" +
					"\"name\": \"Provolone Wedges\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 75000," +
				"},{" +
					"\"name\": \"21st AMENDMENT Back in Black\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 30000," +
				"},{" +
					"\"name\": \"21st AMENDMENT Brew Free or Die\"," +
					"\"id\": 4," +
					"\"mods\": []," +
					"\"price\": 30000," +
				"}]" +
			"}]" +
		"}") : id == 2L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 45500," +
				"\"items\": [{" +
					"\"name\": \"Lobster risotto\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 150000," +
				"},{" +
					"\"name\": \"Oven roasted chicken breast\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 230000," +
				"},{" +
					"\"name\": \"Veal medallions\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 270000," +
				"}]" +
			"}]" +
		"}") : id == 3L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 14700," +
				"\"items\": [{" +
					"\"name\": \"Sweet Potato Tater Tots\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 50000," +
				"},{" +
					"\"name\": \"Shady Burger\"," +
					"\"id\": 2," +
					"\"mods\": [{" +
						"\"name\": \"No Cheese\"," +
					"}]," +
					"\"price\": 100000," +
				"},{" +
					"\"name\": \"Margherita Pizza\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 160000," +
				"}]" +
			"}]" +
			"}]" +
		"}") : id == 4L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 40600," +
				"\"items\": [{" +
					"\"name\": \"Steamed Mussels\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 120000," +
				"},{" +
					"\"name\": \"Ricotta Ravioli\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 170000," +
				"},{" +
					"\"name\": \"NY Strip Steak\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 290000," +
				"}]" +
			"}]" +
		"}") : id == 5L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 35700," +
				"\"items\": [{" +
					"\"name\": \"Crab Cake\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 140000," +
				"},{" +
					"\"name\": \"Haddock\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 220000," +
				"},{" +
					"\"name\": \"Fettuccini, Asparagus + Mushroom\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 150000," +
				"}]" +
			"}]" +
		"}") : id == 6L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 23500," +
				"\"items\": [{" +
					"\"name\": \"Banana Bread\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 75000," +
				"},{" +
					"\"name\": \"Walnut Carrot + Sumac Fritters\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 140000," +
				"},{" +
					"\"name\": \"Vietnamese Salad\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 120000," +
				"}]" +
			"}]" +
		"}") : id == 7L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 16800," +
				"\"items\": [{" +
					"\"name\": \"Zuppa del Giorno\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 39900," +
				"},{" +
					"\"name\": \"Potato Gnocchi\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 109900," +
				"},{" +
					"\"name\": \"Frittata del Giorno\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 89900," +
				"}]" +
			"}]" +
		"}") : id == 8L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 23800," +
				"\"items\": [{" +
					"\"name\": \"Stuffed Banana Peppers\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 80000," +
				"},{" +
					"\"name\": \"Margarita Pizza\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 100000," +
				"},{" +
					"\"name\": \"Steak Sandwich\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 110000," +
				"},{" +
					"\"name\": \"Blue Moon\"," +
					"\"id\": 4," +
					"\"mods\": []," +
					"\"price\": 50000," +
				"}]" +
			"}]" +
		"}") : id == 9L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 19600," +
				"\"items\": [{" +
					"\"name\": \"Stack'd Burger\"," +
					"\"id\": 1," +
					"\"mods\": [{" +
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
					"\"price\": 70000," +
				"},{" +
					"\"name\": \"Stack'd Burger\"," +
					"\"id\": 2," +
					"\"mods\": [{" +
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
					"\"price\": 70000," +
				"},{" +
					"\"name\": \"Regular Fries\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 10000," +
				"},{" +
					"\"name\": \"Vanilla Milkshake\"," +
					"\"id\": 4," +
					"\"mods\": []," +
					"\"price\": 50000," +
				"},{" +
					"\"name\": \"Irish Coffee\"," +
					"\"id\": 5," +
					"\"mods\": []," +
					"\"price\": 80000," +
				"}]" +
			"}]" +
		"}") : id == 10L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 11200," +
				"\"items\": [{" +
					"\"name\": \"Sopa de Pollo\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 45000," +
				"},{" +
					"\"name\": \"Pollo Tacos\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 105000," +
				"},{" +
					"\"name\": \"Fajitas\"," +
					"\"id\": 3," +
					"\"mods\": [{" +
						"\"name\": \"Chicken and Steak\"" +
					"}]," +
					"\"price\": 130000," +
				"}]" +
			"}]" +
		"}") : id == 11L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 18200," +
				"\"items\": [{" +
					"\"name\": \"BRGR fries\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 40000," +
				"},{" +
					"\"name\": \"The BRGR\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 130000," +
				"},{" +
					"\"name\": \"Fire In The Hole\"," +
					"\"id\": 3," +
					"\"mods\": [{" +
						"\"name\": \"No Cheese\"" +
					"}]," +
					"\"price\": 90000," +
				"}]" +
			"}]" +
		"}") : id == 12L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 30800," +
				"\"items\": [{" +
					"\"name\": \"Egg Rolls\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 20000," +
				"},{" +
					"\"name\": \"Thai Spicy Duck\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 190000," +
				"},{" +
					"\"name\": \"Pad Thai\"," +
					"\"id\": 3," +
					"\"mods\": [{" +
						"\"name\": \"Chicken\"" +
					"}]," +
					"\"price\": 130000," +
				"}]" +
			"}]" +
		"}") : id == 13L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 49700," +
				"\"items\": [{" +
					"\"name\": \"Lobster App\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 120000," +
				"},{" +
					"\"name\": \"Monkfish Osso Bucco\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 250000," +
				"},{" +
					"\"name\": \"Prime NY\"," +
					"\"id\": 3," +
					"\"mods\": [{" +
						"\"name\": \"Rare\"" +
					"}]," +
					"\"price\": 340000," +
				"}]" +
			"}]" +
		"}") : id == 14L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 23100," +
				"\"items\": [{" +
					"\"name\": \"Fish Tacos\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 120000," +
				"},{" +
					"\"name\": \"Fried Chicken Sandwich\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 110000," +
				"},{" +
					"\"name\": \"Tavern burer\"," +
					"\"id\": 3," +
					"\"mods\": [{" +
						"\"name\": \"Medium Rare\"" +
					"}]," +
					"\"price\": 100000," +
				"}]" +
			"}]" +
		"}") : id == 15L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 18200," +
				"\"items\": [{" +
					"\"name\": \"Chips and Salsa\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 30000," +
				"},{" +
					"\"name\": \"Bone-in Chicken Breast\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 80000," +
				"},{" +
					"\"name\": \"Marinated Chicken Breast\"," +
					"\"id\": 3," +
					"\"mods\": [{" +
						"\"name\": \"Mustard Dill Sauce\"" +
					"}]," +
					"\"price\": 150000," +
				"}]" +
			"}]" +
		"}") : id == 16L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 20300," +
				"\"items\": [{" +
					"\"name\": \"Chips on Chips\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 60000," +
				"},{" +
					"\"name\": \"Dead Head Sandwitch\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 90000," +
				"},{" +
					"\"name\": \"Margarita Pizza\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 140000," +
				"}]" +
			"}]" +
		"}") : id == 17L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 59500," +
				"\"items\": [{" +
					"\"name\": \"Tartare de Boeuf\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 150000," +
				"},{" +
					"\"name\": \"Jambe d'Agneau\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 320000," +
				"},{" +
					"\"name\": \"Steak/Frites\"," +
					"\"id\": 3," +
					"\"mods\": [{" +
						"\"name\": \"Peppercorn Sauce\"" +
					"},{"+
						"\"name\": \"Rare\"" +
					"}]," +
					"\"price\": 300000," +
				"},{" +
					"\"name\": \"Tarte Tatin\"," +
					"\"id\": 4," +
					"\"mods\": []," +
					"\"price\": 80000," +
				"}]" +
			"}]" +
		"}") : id == 18L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 17500," +
				"\"items\": [{" +
					"\"name\": \"Britney Spears\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 60000," +
				"},{" +
					"\"name\": \"Salmon Rushdies Return\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 100000," +
				"},{" +
					"\"name\": \"Royale with Cheese\"," +
					"\"id\": 3," +
					"\"mods\": [{" +
						"\"name\": \"Medium Rare\"" +
					"}]," +
					"\"price\": 90000," +
				"}]" +
			"}]" +
		"}") : id == 19L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 44100," +
				"\"items\": [{" +
					"\"name\": \"Soupe de poisson\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 90000," +
				"},{" +
					"\"name\": \"Boeuf Bourguignon\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 280000," +
				"},{" +
					"\"name\": \"Steak-Frites\"," +
					"\"id\": 3," +
					"\"mods\": [{" +
						"\"name\": \"Rare\"" +
					"},{"+
						"\"name\": \"Roquefort\"" +
					"}]," +
					"\"price\": 260000," +
				"}]" +
			"}]" +
		"}") : id == 20L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 35000," +
				"\"items\": [{" +
					"\"name\": \"Green Tomato Gazpacho\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 60000," +
				"},{" +
					"\"name\": \"Lamb Meatloaf\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 190000," +
				"},{" +
					"\"name\": \"Hanger Steak\"," +
					"\"id\": 3," +
					"\"mods\": [{" +
						"\"name\": \"Medium\"" +
					"}]," +
					"\"price\": 250000," +
				"}]" +
			"}]" +
		"}") : id == 21L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 35000," +
				"\"items\": [{" +
					"\"name\": \"Green Tomato Gazpacho\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 60000," +
				"},{" +
					"\"name\": \"Lamb Meatloaf\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 190000," +
				"},{" +
					"\"name\": \"Hanger Steak\"," +
					"\"id\": 3," +
					"\"mods\": [{" +
						"\"name\": \"Medium\"" +
					"}]," +
					"\"price\": 250000," +
				"}]" +
			"}]" +
		"}") : id == 22L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 37800," +
				"\"items\": [{" +
					"\"name\": \"Pork and Pistachio pate\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 90000," +
				"},{" +
					"\"name\": \"Swordfish\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 260000," +
				"},{" +
					"\"name\": \"Lazy Vareniki\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 190000," +
				"}]" +
			"}]" +
		"}") : id == 23L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 17200," +
				"\"items\": [{" +
					"\"name\": \"Dip Sticks\"," +
					"\"id\": 1," +
					"\"mods\": [{" +
						"\"name\": \"Mozzarella\"" +
					"}]," +
					"\"price\": 70000," +
				"},{" +
					"\"name\": \"Classic Maggie\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 85000," +
				"},{" +
					"\"name\": \"Sirloin Burger\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 90000," +
				"}]" +
			"}]" +
		"}") : id == 24L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 16100," +
				"\"items\": [{" +
					"\"name\": \"Marinated Olives\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 40000," +
				"},{" +
					"\"name\": \"Ricotta Pizza\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 130000," +
				"},{" +
					"\"name\": \"Chocolate Pot de Creme\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 60000," +
				"}]" +
			"}]" +
		"}") : id == 25L ? new JSONObject("{" +
			"\"tickets\": [{" +
				"\"id\": 1," +
				"\"dateOpened\": " + d + "," +
				"\"tax\": 16800," +
				"\"items\": [{" +
					"\"name\": \"Sukothai\"," +
					"\"id\": 1," +
					"\"mods\": []," +
					"\"price\": 60000," +
				"},{" +
					"\"name\": \"Street Noodle 1\"," +
					"\"id\": 2," +
					"\"mods\": []," +
					"\"price\": 90000," +
				"},{" +
					"\"name\": \"Pad Thai\"," +
					"\"id\": 3," +
					"\"mods\": []," +
					"\"price\": 90000," +
				"}]" +
			"}]" +
		"}") : null);
	}
}