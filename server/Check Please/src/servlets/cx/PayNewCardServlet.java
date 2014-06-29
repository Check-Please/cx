package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import modeltypes.Client;
import modeltypes.ClosedUserConnection;
import modeltypes.ConnectionToTablePointer;
import modeltypes.Restaurant;
import modeltypes.TableKey;
import modeltypes.TableKey.cIDStatuses;
import modeltypes.UserConnection;

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
		config.txnXG = true;
		config.path = a("/", TableKey.getKind(), "tableKey");
		config.exists = true;
		config.bools = a("save");
		config.strs = a("pan", "name", "expr", "cvv", "zip", "?password");
		config.longs = a("total", "tip");//NOTE: total does not include tip
		config.keyNames = a("connectionID", "clientID");
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
		//TODO: The following will have Y2.1K bugs
		int exprYear = Integer.parseInt(expr.substring(0, 2));
		int exprMonth = Integer.parseInt(expr.substring(2, 4));
		if((exprMonth == 0) || (exprMonth > 12))
			throw new HttpErrMsg("No such month");
		if((date.get(Calendar.YEAR) % 100 > exprYear) ||
				((date.get(Calendar.YEAR) % 100 == exprYear) &&
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
		String name = p.getStr(1);
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
			ret.put("cardCT", c.encrypt(info.toString(), p.getStr(5)));
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

	/*	Closes the connection without sending a split update
	 *
	 *	@param	table The table key
	 *	@param	connectionID The ID for the connection of the user who made this payment
	 *	@param	tip	The tip the client paid
	 *	@param	ds The datastore
	 */
	private static void closeConnection(TableKey table, String connectionID, long tip, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		ds.delete(KeyFactory.createKey(ConnectionToTablePointer.getKind(), connectionID));
		UserConnection uc = new UserConnection(MyUtils.get_NoFail(table.getKey().getChild(UserConnection.getKind(), connectionID), ds));
		ClosedUserConnection cuc = new ClosedUserConnection(table.getRestrUsername(), uc, ClosedUserConnection.CLOSE_CAUSE__PAID);
		cuc.setTip(tip);
		cuc.commit(ds);
		uc.rmv(ds);
	}

	/*	Reopens a connection
	 *
	 *	This is called in case the client was closed for an asyncronous payment which later failed.
	 *
	 *	@param	table The table key
	 *	@param	connectionID The ID for the connection of the user who made the failed payment
	 *	@param	ds The datastore
	 */
	private static void reopenConnection(TableKey table, String connectionID, DatastoreService ds)
	{
		new ConnectionToTablePointer(KeyFactory.createKey(ConnectionToTablePointer.getKind(), connectionID), table.getKey().getName()).commit(ds);
		ClosedUserConnection cuc = new ClosedUserConnection(MyUtils.get_NoFail(KeyFactory.createKey(Restaurant.getKind(), table.getRestrUsername()).getChild(ClosedUserConnection.getKind(), connectionID), ds));
		new UserConnection(table, cuc).commit(ds);
		cuc.rmv(ds);
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
		if(total != table.getTotalToPay(items, connectionID))
			throw new HttpErrMsg("Please reload the app");

		//Pay
		JSONObject query = new JSONObject(table.getQuery());
		JSONObject ret = new JSONObject();
		config.FORBID_RETRIES = true;
		boolean sync = payInner(query, table.getRestrUsername(), table.getKey().getName(), connectionID, pan, name, expr, zip, cvv, items, table.getPayFracs(connectionID), total, tip, ds);

		closeConnection(table, connectionID, tip, ds);
		if(sync) {
			table.setConnectionStatus(connectionID, cIDStatuses.PAID, ds);
			ret.put("done", table.isPaid());
		} else {
			table.setConnectionStatus(connectionID, cIDStatuses.PROCESSING, ds);
		}

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
		table.setConnectionStatus(connectionID, cIDStatuses.PAID, ds);
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
		reopenConnection(table, connectionID, ds);
		ChannelServiceFactory.getChannelService().sendMessage(new ChannelMessage(connectionID, "PAYMENT_ERROR\n"+msg));
		table.setConnectionStatus(connectionID, cIDStatuses.INPUTTING, ds);
		table.commit(ds);
	}

}