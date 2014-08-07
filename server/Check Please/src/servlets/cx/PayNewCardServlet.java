package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import modeltypes.Client;
import modeltypes.Globals;
import modeltypes.TableKey;
import modeltypes.TableKey.ConnectionStatus;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import utils.Frac;
import utils.HttpErrMsg;
import utils.MyUtils;
import utils.ParamWrapper;
import utils.PostServletBase;
import utils.TicketItem;
import utils.UnsupportedFeatureException;
import static utils.MyUtils.a;

public class PayNewCardServlet extends PostServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 742609711044231605L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.contentType = ContentType.JSON;
		config.securityType = SecurityType.REJECT;
		config.txnXG = true;
		config.path = a("/", TableKey.getKind(), "tableKey");
		config.exists = true;
		config.bools = a("save", "?protectCT");
		config.strs = a("pan", "name", "expr", "cvv", "zip", "?password", "cookieID");
		config.longs = a("total", "tip");//NOTE: total does not include tip, both are in cents
		config.keyNames = a("connectionID", "clientID");
		config.FORBID_RETRIES = true;
	}

	private static int getYear(String yearStr) {                                                                                                                                   
    	if(yearStr.length() == 3)
    		return Integer.parseInt("2"+yearStr);
	    else if(yearStr.length() == 2) {
	    	int year = Integer.parseInt(yearStr);
	    	int curr = Calendar.getInstance().get(Calendar.YEAR);
           	int cent = curr - (curr % 100);
           	int early = year+cent-100;
           	int med = year+cent;
           	if(Math.abs(early-curr) < Math.abs(med-curr))
           		return early;
           	int late = early+cent+100;
           	if(Math.abs(late-curr) < Math.abs(med-curr))
           		return late;
           	return med;
	    }
	    else
	    	return Integer.parseInt(yearStr);
    }                                                                                                                                                          

	private void validate(String pan, String name, String expr, String cvv, String zip) throws HttpErrMsg
	{
		if(pan.length() < 8)
			throw new HttpErrMsg("The card number is too short");
		if(pan.length() > 19)
			throw new HttpErrMsg("The card number is too long");
		if(!pan.matches("\\d+"))
			throw new HttpErrMsg("Card number is not a number");
		if(!pan.matches("(?:62|88|2014|2149)\\d+")) {
			//Luhn Algorithm
			int sum = 0;
			for(int i = 0; i < pan.length(); i++) {
				int d = Integer.parseInt(pan.substring(i, i+1));
				sum += (i%2 == 0) && (d != 9) ? (d*2)%9 : d;
			}
			if(sum%10 != 0)
				throw new HttpErrMsg("The card number is incorrect");
		}
		if(name.length() < 2)
			throw new HttpErrMsg("The name on the card is too short");			
		if(name.length() > 26)
			throw new HttpErrMsg("The name on the card is too long");			
		if(expr.length() != 4)
			throw new HttpErrMsg("The expration date must be in YYMM format");
		if(!expr.matches("\\d+"))
			throw new HttpErrMsg("Expiration date is not a number");
		Calendar date = new GregorianCalendar();
		int exprYear = getYear(expr.substring(0, 2));
		int exprMonth = Integer.parseInt(expr.substring(2, 4));
		if((exprMonth == 0) || (exprMonth > 12))
			throw new HttpErrMsg("No such month");
		if((date.get(Calendar.YEAR) > exprYear) ||
				((date.get(Calendar.YEAR) == exprYear) &&
					(date.get(Calendar.MONTH)+1 > exprMonth)))
			throw new HttpErrMsg("This card has expired");
		if((cvv.length() < 3) || (cvv.length() > 4))
			throw new HttpErrMsg("The card security code should be three or four digits");
		if(!cvv.matches("\\d+"))
			throw new HttpErrMsg("The card security code must be a number");
		if(zip.length() != 5)
			throw new HttpErrMsg("The zip code should be five digits");
		if(!zip.matches("\\d+"))
			throw new HttpErrMsg("Zip code is not a number");

	}

	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		String pan = p.getStr(0);
		String name = p.getStr(1).toUpperCase();
		String expr = p.getStr(2);
		String cvv = p.getStr(3);
		String zip = p.getStr(4);
		validate(pan, name, expr, cvv, zip);

		JSONObject ret = pay(pan, name, expr, cvv, zip, p.getLong(0), p.getLong(1),
				new TableKey(p.getEntity()), p.getKeyName(0), ds);
		
		if(p.getBool(0)) {
			JSONObject info = new JSONObject();
			info.put("pan", pan);
			info.put("name", name);
			info.put("expr", expr);
			info.put("zip", zip);
			if(p.getStr(5) != null)
				info.put("password", MyUtils.protectPassword(p.getStr(5)));
			Client c;
			try {
				c = new Client(KeyFactory.createKey(Client.getKind(), p.getKeyName(1)), ds);
			} catch (EntityNotFoundException e) {
				throw new HttpErrMsg("Invalid client ID");
			}
			if(!c.hasPrivateKey()) {
				c.setKey();
				c.commit(ds);
			}
			String ciphertext = c.encrypt(info.toString());
			if(p.getBool(1) != null && p.getBool(1)) {
				int exprYear = getYear(expr.substring(0, 2));
				int exprMonth = Integer.parseInt(expr.substring(2, 4));
				Calendar date = new GregorianCalendar();
				date.set(exprYear, exprMonth, 2);//Use a 1 day buffer to deal with time zones
				p.saveCookie(	Globals.CARD_CT_COOKIE,
								ciphertext.substring(0, Globals.COOKIE_CARD_CT_LEN),
								"/cx/pay_saved/"+p.getStr(6), date.getTime());
				ret.put("cardCT", Globals.COOKIED_CARD_CT_PREFIX+ciphertext.substring(Globals.COOKIE_CARD_CT_LEN));
			} else
				ret.put("cardCT", ciphertext);
		}

		out.println(ret.toString());
	}


	/*	Pays the ticket
	 *
	 *	@param	query The query to find the ticket with
	 *	@param	restr The username for the restaurant
	 *	@param	tKey The key for the table
	 *	@param	connectionID The connection of the payer
	 *	@param	pan The principle account number
	 *	@param	name The name on the card
	 *	@param	expr The expiration date on the card
	 *	@param	zip The zip code for the card
	 *	@param	cvv The security code for the card
	 *	@param	items The items on the ticket
	 *	@param	payFracs
	 *	@param	total The total being paid (not including tip)
	 *	@param	tip The tip being paid
	 *	@param	ds The datastore
	 *
	 *	@throws	HttpErrMsg If for some reason the payment cannot happen
	 *	@return true iff the payment is synchronous (i.e. true = the ticket has now been paid,
	 *			false = the ticket as merely been set up to be paid later)
	 */
	private static boolean payInner(JSONObject query, String restr, String tKey, String connectionID, String pan, String name, String expr, String zip, String cvv, List<TicketItem> items, Map<String, Frac> payFracs, long total, long tip, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		String method = query.getString("method");
		if(method.equals("preloaded"))
			return true;
		else
			throw new HttpErrMsg("Unknown query method");
	}

	/**	Pays
	 *
	 *	@param	pan The card number
	 *	@param	name The name on the card
	 *	@param	expr The expiration date of the card
	 *	@param	cvv The CVV of the card (can be null)
	 *	@param	zip The zip code for the card
	 *	@param	total The total (excluding tip) being paid in cents
	 *	@param	tip The tip in cents
	 *	@param	table The table being paid
	 *	@param	connectionID Who is paying
	 *	@param	ds The datastore to get info from
	 *
	 *	@return A JSON object with info about the result of the payment
	 *	@throws JSONException If something goes wrong with the JSON
	 *	@throws HttpErrMsg If something else goes wrong
	 */
	public static JSONObject pay(String pan, String name, String expr, String cvv, String zip, Long total, Long tip, TableKey table, String connectionID, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		//Get/set internal payment info
		List<TicketItem> items;
		try {
			items = TicketItem.getItems(table, ds);
		} catch (UnsupportedFeatureException e) {
			throw new HttpErrMsg(e.getMessage());
		}
		if(total.longValue() != table.getTotalToPay(items, connectionID))
			throw new HttpErrMsg("Please reload the app");

		//Pay
		JSONObject query = new JSONObject(table.getQuery());
		JSONObject ret = new JSONObject();
		boolean sync = payInner(query, table.getRestrUsername(), table.getKey().getName(), connectionID, pan, name, expr, zip, cvv, items, table.getPayFracs(connectionID), total, tip, ds);
		ret.put("async", !sync);

		if(sync) {
			table.setConnectionStatus(connectionID, ConnectionStatus.PAID, TicketItem.getItems(table, ds), ds);
			boolean paid = table.isPaid();
			if(paid)
				table.clearMetadata();
			ret.put("done", paid);
		} else {
			table.setConnectionStatus(connectionID, ConnectionStatus.PROCESSING, TicketItem.getItems(table, ds), ds);
		}
		table.commit(ds);

		return ret;
	}

	/**	If a payment was asyncronus, this function should be called when the payment is
	 *	completed.
	 *
	 *	The client is told to stop waiting and its connection is closed, the ticket's metadata is
	 *	updated, and everyone is happy :D
	 *
	 *	@param	table The table where the payment occured
	 *	@param	connectionID The ID for the connection from which the payment was made
	 *	@param	ds The datastore
	 */
	public static void paymentSuccessCallback(TableKey table, String connectionID, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		ChannelServiceFactory.getChannelService().sendMessage(new ChannelMessage(connectionID, "PAYMENT_SUCCESS"));
		table.setConnectionStatus(connectionID, ConnectionStatus.PAID, TicketItem.getItems(table, ds), ds);
		if(table.isPaid())
			table.clearMetadata();
		table.commit(ds);
	}

	/**	If a payment was asyncronus, this function should be called when the payment fails to
	 *	be completed
	 *
	 *	The client is informed and the outstanding payment ammount is reduced
	 *
	 *	@param	table The table where the payment occured
	 *	@param	connectionID The ID for the connection from which the payment was made
	 *	@param	msg A message for the client telling them why their payment failed
	 *	@param	ds The datastore
	 */
	public static void paymentFailureCallback(TableKey table, String connectionID, String msg, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		ChannelServiceFactory.getChannelService().sendMessage(new ChannelMessage(connectionID, "PAYMENT_ERROR\n"+msg));
		table.setConnectionStatus(connectionID, ConnectionStatus.INPUTTING, TicketItem.getItems(table, ds), ds);
		table.commit(ds);
	}

}