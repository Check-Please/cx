package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kinds.Client;
import kinds.ConnectionToTablePointer;
import kinds.ClosedUserConnection;
import kinds.Restaurant;
import kinds.UserConnection;
import kinds.TableKey;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import servlets.oz.AppleResetServlet;
import servlets.oz.Data;
import utils.Frac;
import utils.HttpErrMsg;
import utils.MyUtils;
import utils.ParamWrapper;
import utils.PostServletBase;
import utils.TicketItem;
import utils.UnsupportedFeatureException;
import static utils.MyUtils.a;

public class PayServlet extends PostServletBase
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
		config.strs = a("ccInfo", "?cvv");
		config.strLists = a("items");
		config.longLists = a("nums", "denoms");
		config.longs = a("total", "tip");//NOTE: total does not include tip
		config.keyNames = a("connectionID", "?clientID");
	}

	/**	Runs some basic checks on credit card information.
	 *
	 *	@param	pan Checked for length, that is it a number, and that it follows Luhn's Algorithm
	 *	@param	name Checked for length
	 *	@param	expr Checked for length, that it is a number, that the month is valid, and that it hasn't expired
	 *	@param	zip Checked for length and that it is a number
	 *	@throws	HttpErrMsg if something is wrong
	 */
	public static void basicValidation(String pan, String name, String expr, String zip) throws HttpErrMsg
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
		//TODO: The following will have Y2.1K bugs and I'm not totally sure
		//how to deal with them
		int exprYear = Integer.parseInt(expr.substring(0, 2));
		int exprMonth = Integer.parseInt(expr.substring(2, 4));
		if((exprMonth == 0) || (exprMonth > 12))
			throw new HttpErrMsg("No such month");
		if((date.get(Calendar.YEAR) % 100 > exprYear) ||
				((date.get(Calendar.YEAR) % 100 == exprYear) &&
					(date.get(Calendar.MONTH)+1 > exprMonth)))
			throw new HttpErrMsg("This card has expired");
		if(zip.length() != 5)
			throw new HttpErrMsg("The zip code should be five digits");
		if(!zip.matches("\\d+"))
			throw new HttpErrMsg("Zip code is not a number");
	}

	/*	Validates various parts of the input.
	 *
	 *	@param	pan Checked for length, that is it a number, and that it follows Luhn's Algorithm
	 *	@param	name Checked for length
	 *	@param	expr Checked for length, that it is a number, that the month is valid, and that it hasn't expired
	 *	@param	zip Checked for length and that it is a number
	 *	@param	cvv Checked for length and that it is a number
	 *	@param	itemsToPay Checked for matching length against payFracs
	 *	@param	payFracs Checked for matching length against itemsToPay.
	 *	@throws	HttpErrMsg if the input wasn't valid
	 */
	private static void validateInput(String pan, String name, String expr, String zip, String cvv, List<String> itemsToPay, List<Frac> payFracs) throws HttpErrMsg
	{
		basicValidation(pan, name, expr, zip);
		if(cvv != null) {
			if((cvv.length() < 3) || (cvv.length() > 4))
				throw new HttpErrMsg("Security code should be either 3 and 4 digits");
			if(!cvv.matches("\\d+"))
				throw new HttpErrMsg("Security code is not a number");
		}
		if(payFracs.size() != itemsToPay.size())
			throw new HttpErrMsg("The number of fractions does not match the number of items");
	}

	/*	Validates that a payment is only targeting items on a ticket and isn't overpaying anything
	 *
	 *	@param	paidPart A map from item IDs to what fraction has already been paid
	 *	@param	outstandingPayments A map from item IDs to the fraction which has outstanding payments associated with it
	 *	@param	itemsOnTicket The set of all item IDs for items on the ticks
	 *	@param	itemsToPay A list of the items which will be paid, at least in part, this time
	 *	@param	payFracs A list of what fraction of each corresponding item will be paid
	 *	@throws	HttpErrMsg if an item to be paid isn't on the ticket or would be over paid
	 */
	private static void validatePaymentFracs(Map<String, Frac> paidPart, Map<String, Frac> outstandingPayments, Set<String> itemsOnTicket, List<String> itemsToPay, List<Frac> payFracs) throws HttpErrMsg
	{
		for(int i = 0; i < itemsToPay.size(); i++) {
			String itemID = itemsToPay.get(i);
			if(!itemsOnTicket.contains(itemID))
				throw new HttpErrMsg("You are trying to pay for an item which isn't on the ticket anymore");
			Frac oldPaid = paidPart.get(itemID);
			if(oldPaid == null)
				oldPaid = Frac.ZERO;
			if(outstandingPayments.containsKey(itemID))
				oldPaid.add(outstandingPayments.get(itemID));
			Frac currentPay = payFracs.get(i);
			Frac newPaid = oldPaid.add(currentPay);
			if(newPaid.compareTo(Frac.ONE) > 0)
				throw new HttpErrMsg("You are trying to pay for " + 
								currentPay + " of an item that only has " +
								Frac.ONE.sub(oldPaid) + " left");
		}
	}

	private static enum MAP_SIGN {MAP_ADD, MAP_SUB};
	private static void addOrSubToMap(Map<String, Frac> m, List<String> keys, List<Frac> vals, MAP_SIGN sign)
	{
		for(int i = 0; i < keys.size(); i++) {
			String k = keys.get(i);
			Frac old = m.get(k);
			if(old == null)
				old = Frac.ZERO;
			old = old.add(vals.get(i).mult(sign == MAP_SIGN.MAP_ADD ? 1 : -1));
			m.put(k, old);
		}
	}

	/*	Figures out if the ticket has been paid in full
	 *
	 *	@param	itemsOnTicket The set item IDs for all the items on the ticket
	 *	@param	paidMap A map from item IDs to the fraction which is already paid
	 *	@return	true iff the ticket has been paid in full
	 */
	private static boolean isPaid(Set<String> itemsOnTicket, Map<String, Frac> paidMap)
	{
		for(String id : itemsOnTicket) {
			Frac paid = paidMap.get(id);
			if(paid == null || !paid.equals(Frac.ONE))
				return false;
		}
		return true;
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
	 *	@param	itemsToPay A list of the items which will be paid, at least in part, this time
	 *	@param	payFracs A list of what fraction of each corresponding item will be paid
	 *	@param	items The items on the ticket
	 *	@param	total The total being paid (not including tip)
	 *	@param	tip The tip being paid
	 *	@param	ds The datastore
	 *
	 *	@throws	HttpErrMsg If for some reason the payment cannot happen
	 *	@return true iff the payment is synchronous (i.e. true = the ticket has now been paid,
	 *			false = the ticket as merely been set up to be paid later)
	 */
	private static boolean pay(JSONObject query, String restr, String tKey, String connectionID, String pan, String name, String expr, String zip, String cvv, List<String> itemsToPay, List<Frac> payFracs, List<TicketItem> items, long total, long tip, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		String method = query.getString("method");
		System.out.println(tKey);
		if(method.equals("preloaded") || (method.equals("oz") && (tKey.toUpperCase().equals(AppleResetServlet.appleKey))))
			return true;
		else if(method.equals("oz")) {
			Data d = new Data(MyUtils.get_NoFail(KeyFactory.createKey(Data.getKind(), restr), ds));
			JSONObject msg = new JSONObject();
			msg.put("tKey", tKey);
			msg.put("cID", connectionID);
			msg.put("itemsToPay", new JSONArray(itemsToPay.toString()));
			msg.put("payFracNums", new JSONArray(Frac.getNums(payFracs).toString()));
			msg.put("payFracDenoms", new JSONArray(Frac.getDenoms(payFracs).toString()));
			msg.put("total", total);
			msg.put("tip", tip);
			msg.put("pan", pan);
			msg.put("name", name);
			msg.put("expr", expr);
			msg.put("zip", zip);
			if(cvv != null)
				msg.put("cvv", cvv);
			ChannelServiceFactory.getChannelService().sendMessage(new ChannelMessage(d.getClient(), msg.toString()));
			return false;
		} else
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

	/*	Finalizes the metadata in the ticket and sends updates.  This entails clearing the meta-
	 *	data if the ticket was just paid in full, sending channel messages if not, and actually
	 *	commiting to the datastore
	 *
	 *	@param	table The table key
	 *	@param	connectionID The ID for the connection of the user who made this payment
	 *	@param	ticketPaid Whether or not the ticket has just been paid in full
	 *	@param	ds The datastore
	 */
	private void finalizeMetadata(TableKey table, String connectionID, boolean ticketPaid, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		if(ticketPaid) {
			if(table.clearTickMetadata(ds))
				table.commit(ds);
		} else {
			try {
				table.sendItemsUpdateAndRemoveSplit(connectionID, ds);
			} catch (UnsupportedFeatureException e) {
				table.sendErrMsg(e.getMessage(), ds);
			}
			table.commit(ds);
		}
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		//Get params
		TableKey table = new TableKey(p.getEntity());
		String rawCC = p.getStr(0);
		JSONObject cc;
		try {
			cc = new JSONObject(rawCC);
		} catch(JSONException e) {
			String cID = p.getKeyName(1);
			if(cID == null)
				throw new HttpErrMsg("Cannot decrypt credit information without client ID");
			try {
				rawCC = new Client(KeyFactory.createKey(Client.getKind(), cID), ds).decrypt(rawCC);
			} catch (EntityNotFoundException e1) {
				throw new HttpErrMsg("Invalid client ID");
			}
			if(rawCC == null)
				throw new HttpErrMsg("We could not decrypt your credit card information.  You should delete the saved information and then re-enter it all");
			cc = new JSONObject(rawCC);
		}
		String pan = cc.getString("pan");
		String name = cc.getString("name");
		String expr = cc.getString("expr");
		String zip = cc.getString("zip");
		String cvv = p.getStr(1);
		List<String> itemsToPay = p.getStrList(0);
		List<Frac> payFracs;
		try {
			payFracs = Frac.makeFracs(p.getLongList(0), p.getLongList(1));
		} catch(IllegalArgumentException ex) {
			throw new HttpErrMsg(ex.getMessage());
		}
		long total = p.getLong(0);
		long tip = p.getLong(1);
		String connectionID = p.getKeyName(0);
		validateInput(pan, name, expr, zip, cvv, itemsToPay, payFracs);


		//Get/set internal payment info
		List<TicketItem> items;
		try {
			items = TicketItem.getItems(table, ds);
		} catch (UnsupportedFeatureException e) {
			throw new HttpErrMsg(e.getMessage());
		}
		Set<String> itemsOnTicket = new HashSet<String>();
		for(TicketItem item : items)
			itemsOnTicket.add(item.getID());
		Map<String, Frac> paidPart = table.getPaidPart();
		Map<String, Frac> outstandingPayments = table.getOutstandingPart();
		validatePaymentFracs(paidPart, outstandingPayments, itemsOnTicket, itemsToPay, payFracs);

		//Pay
		JSONObject query = new JSONObject(table.getQuery());
		JSONObject ret = new JSONObject();
		config.FORBID_RETRIES = true;
		boolean sync = pay(query, table.getRestrUsername(), table.getKey().getName(), connectionID, pan, name, expr, zip, cvv, itemsToPay, payFracs, items, total, tip, ds);

		closeConnection(table, connectionID, tip, ds);
		boolean ticketPaid;
		if(sync) {
			addOrSubToMap(paidPart, itemsToPay, payFracs, MAP_SIGN.MAP_ADD);
			ticketPaid = isPaid(itemsOnTicket, paidPart);
			if(ticketPaid && (table.getKey().getName().equals(AppleResetServlet.appleKey))) {
				Data d = new Data(MyUtils.get_NoFail(KeyFactory.createKey(Data.getKind(), "sjelin"), ds));
				List<String> ticks = d.getData();
				ticks.set(0, "[]");
				d.commit(ds);
			}
			ret.put("done", ticketPaid);
		} else {
			addOrSubToMap(outstandingPayments, itemsToPay, payFracs, MAP_SIGN.MAP_ADD);
			ticketPaid = false;
			ret.put("loadMsg", query.get("method").equals("oz") ? "Accessing terminal" :
							"Processing credit card");
		}
		finalizeMetadata(table, connectionID, ticketPaid, ds);

		//Return
		out.println(ret);
	}

	/**	If a payment was asyncronus, this function should be called when the payment is
	 *	completed.
	 *
	 *	The client is told to stop waiting and its connection is closed, the ticket's metadata is
	 *	updated, and everyone is happy :D
	 *
	 *	@param	table The table where the payment occured
	 *	@param	itemsOnTicket	A set of item IDs for the ticket.  Can be null, in which case it
	 *							will be calculated using the datastore
	 *	@param	connectionID The ID for the connection from which the payment was made
	 *	@param	itemsPaid A list of IDs of items which were in part paid
	 *	@param	ammountPaid The fraction which the corresponding item was paid by
	 *	@param	ds The datastore
	 */
	public static void paymentSuccessCallback(TableKey table, Set<String> itemsOnTicket, String connectionID, List<String> itemsPaid, List<Frac> ammountPaid, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		//Tell the client that they've paid
		ChannelServiceFactory.getChannelService().sendMessage(new ChannelMessage(connectionID, "load_update\n1"));

		//Manage internal stuff
		Map<String, Frac> paidPart = table.getPaidPart();
		addOrSubToMap(paidPart, itemsPaid, ammountPaid, MAP_SIGN.MAP_ADD);
		if(itemsOnTicket == null) {
			List<TicketItem> items;
			try {
				items = TicketItem.getItems(table, ds);
			} catch (UnsupportedFeatureException e) {
				table.sendErrMsg(e.getMessage(), ds);
				items = new ArrayList<TicketItem>();
			}
			itemsOnTicket = new HashSet<String>();
			for(TicketItem item : items)
				itemsOnTicket.add(item.getID());
		}
		boolean ticketPaid = isPaid(itemsOnTicket, paidPart);
		if(ticketPaid)
			table.clearTickMetadata(ds);
		else
			addOrSubToMap(table.getOutstandingPart(), itemsPaid, ammountPaid, MAP_SIGN.MAP_SUB);
		table.commit(ds);
	}

	/**	If a payment was asyncronus, this function should be called when the payment fails to
	 *	be completed
	 *
	 *	The client is informed and the outstanding payment ammount is reduced
	 *
	 *	@param	table The table where the payment occured
	 *	@param	connectionID The ID for the connection from which the payment was made
	 *	@param	itemsPaid A list of IDs of items which were in part paid
	 *	@param	ammountPaid The fraction which the corresponding item was paid by
	 *	@param	ds The datastore
	 *	@param	msg A message for the client telling them why their payment failed
	 */
	public static void paymentFailureCallback(TableKey table, String connectionID, List<String> itemsPaid, List<Frac> ammountPaid, DatastoreService ds, String msg) throws JSONException, HttpErrMsg
	{
		//Manage internal stuff
		reopenConnection(table, connectionID, ds);
		addOrSubToMap(table.getOutstandingPart(), itemsPaid, ammountPaid, MAP_SIGN.MAP_SUB);
		try {
			table.sendItemsUpdateAndRestoreSplit(connectionID, itemsPaid, ds);
		} catch (UnsupportedFeatureException e) {
			table.sendErrMsg(e.getMessage(), ds);
		}
		table.commit(ds);

		//Tell the client that they they haven't paid
		ChannelServiceFactory.getChannelService().sendMessage(new ChannelMessage(connectionID, "load_update\n-1"+(msg == null ? "" : "\n"+msg)));
	}
}