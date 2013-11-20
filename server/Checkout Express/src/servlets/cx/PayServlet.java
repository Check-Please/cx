package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;
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

import kinds.BasicPointer;
import kinds.ClosedMobileClient;
import kinds.MobileClient;
import kinds.MobileTickKey;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;

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
		config.path = a("/", MobileTickKey.getKind(), "mobileKey");
		config.exists = true;
		config.exists2 = true;
		config.strs = a("pan", "name", "expr", "zip", "?cvv");
		config.strLists = a("items");
		config.longLists = a("nums", "denoms");
		config.longs = a("total", "tip", "pan", "expr", "zip", "?cvv");//NOTE: total does not include tip
		config.keyNames = a("clientID");
	}
	//pre: pan, cvv ~= /^\d+$/
	private static void validateInput(String pan, String expr, List<String> itemsToPay, List<Frac> payFracs) throws HttpErrMsg
	{
		if(pan.length() < 8)
			throw new HttpErrMsg("The card number is too short");
		if(!pan.matches("(?:62|88|2014|2149)\\d+")) {
			//Luhn Algorithm
			int sum = 0;
			for(int i = 0; i < pan.length(); i++) {
				int d = Integer.parseInt(pan.substring(i, 1));
				sum += (i == 0) ? (d*2)%9 : d;
			}
			if(sum != 0)
				throw new HttpErrMsg("The card number is incorrect");
		}
		if(expr.length() != 4)
			throw new HttpErrMsg("The expration date must be in YYMM format");

		Calendar date = new GregorianCalendar();
		//TODO: The following will have Y2.1K bugs and I'm not totally sure
		//how to deal with them
		int exprYear = Integer.parseInt(expr.substring(0, 2));
		int exprMonth = Integer.parseInt(expr.substring(2, 4));
		if((date.get(Calendar.YEAR) % 100 > exprYear) ||
				((date.get(Calendar.YEAR) % 100 == exprYear) &&
					(date.get(Calendar.MONTH)+1 > exprMonth)))
			throw new HttpErrMsg("You are trying to pay with a credit card which has expired");
		if(payFracs.size() != itemsToPay.size())
			throw new HttpErrMsg("The number of fractions does not match the number of items");
	}
	private static void updatePaymentInfo(Map<String, Frac> paidMap, Set<String> itemsOnTicket, List<String> itemsToPay, List<Frac> payFracs) throws HttpErrMsg
	{
		for(int i = 0; i < itemsToPay.size(); i++) {
			String itemID = itemsToPay.get(i);
			if(!itemsOnTicket.contains(itemID))
				throw new HttpErrMsg("You are trying to pay for an item which isn't on the ticket anymore");
			Frac oldPaid = paidMap.get(itemID);
			if(oldPaid == null)
				oldPaid = Frac.ZERO;
			Frac currentPay = payFracs.get(i);
			Frac newPaid = oldPaid.add(currentPay);
			if(newPaid.compareTo(Frac.ONE) > 0)
				throw new HttpErrMsg("You are trying to pay for " + 
								currentPay + " of an item that only has " +
								Frac.ONE.sub(oldPaid) + " left");
			paidMap.put(itemID, newPaid);
		}
	}
	private static boolean isPaid(Set<String> itemsOnTicket, Map<String, Frac> paidMap)
	{
		for(String id : itemsOnTicket) {
			Frac paid = paidMap.get(id);
			if(paid == null || !paid.equals(Frac.ONE))
				return false;
		}
		return true;
	}
	private static void pay(JSONObject query, String restr, String pan, String name, String expr, String zip, String cvv, List<TicketItem> items, long total, long tip, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		if(query.getString("method").equals("oz")) {
			Data d = new Data(MyUtils.get_NoFail(KeyFactory.createKey(Data.getKind(), restr), ds));
			JSONObject msg = new JSONObject();
			msg.put("items", new JSONArray(items.toString()));
			msg.put("total", total);
			msg.put("tip", tip);
			msg.put("pan", pan);
			msg.put("name", name);
			msg.put("expr", expr);
			msg.put("zip", zip);
			if(cvv != null)
				msg.put("cvv", cvv);
			ChannelServiceFactory.getChannelService().sendMessage(new ChannelMessage(d.getClient(), msg.toString()));
		}
	}
	private static void updateDS(MobileTickKey mobile, JSONObject query, Set<String> itemsOnTicket, String clientID, boolean ticketPaid, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		ds.delete(KeyFactory.createKey(BasicPointer.getKind(), clientID));
		MobileClient mc = new MobileClient(MyUtils.get_NoFail(mobile.getKey().getChild(MobileClient.getKind(), clientID), ds));
		new ClosedMobileClient(mobile.getRestrUsername(), mc, ClosedMobileClient.CLOSE_CAUSE__PAID).commit(ds);
		mc.rmv(ds);
		if(ticketPaid) {
			if(mobile.clearTickMetadata(ds))
				mobile.commit(ds);
		} else {
			try {
				mobile.sendItemsUpdateAndRemoveSplit(clientID, ds);
			} catch (UnsupportedFeatureException e) {
				mobile.sendErrMsg(e.getMessage(), ds);
			}
			mobile.commit(ds);
		}
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg
	{
		//Get params
		MobileTickKey mobile = new MobileTickKey(p.getEntity());
		String pan = p.getStr(0);
		String name = p.getStr(1);
		String expr = p.getStr(2);
		String zip = p.getStr(3);
		String cvv = p.getStr(4);
		List<String> itemsToPay = p.getStrList(0);
		List<Frac> payFracs;
		try {
			payFracs = Frac.makeFracs(p.getLongList(0), p.getLongList(1));
		} catch(IllegalArgumentException ex) {
			throw new HttpErrMsg(ex.getMessage());
		}
		long total = p.getLong(0);
		long tip = p.getLong(1);
		String clientID = p.getKeyName(0);
		validateInput(pan, expr, itemsToPay, payFracs);

		//Get/set internal payment info
		List<TicketItem> items;
		try {
			items = TicketItem.getItems(mobile, ds);
		} catch (UnsupportedFeatureException e) {
			throw new HttpErrMsg(e.getMessage());
		}
		Set<String> itemsOnTicket = new HashSet<String>();
		for(TicketItem item : items)
			itemsOnTicket.add(item.getID());
		Map<String, Frac> paidMap = mobile.getPaidMap();
		updatePaymentInfo(paidMap, itemsOnTicket, itemsToPay, payFracs);
		boolean ticketPaid = isPaid(itemsOnTicket, paidMap);

		//Pay & update DS
		JSONObject query = new JSONObject(mobile.getQuery());
		pay(query, mobile.getRestrUsername(), pan, name, expr, zip, cvv, items, total, tip, ds);
		updateDS(mobile, query, itemsOnTicket, clientID, ticketPaid, ds);

		//Return
		JSONObject ret = new JSONObject();
		ret.put("done", ticketPaid);
		out.println(ret);
	}
}