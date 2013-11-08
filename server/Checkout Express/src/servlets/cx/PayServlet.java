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

import org.json.JSONException;
import org.json.JSONObject;

import kinds.BasicPointer;
import kinds.ClosedMobileClient;
import kinds.MobileClient;
import kinds.MobileTickKey;
import kinds.User;
import kinds.UserCC;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;
import com.subtledata.api.LocationsApi;
import com.subtledata.client.ApiException;

import utils.Frac;
import utils.HttpErrMsg;
import utils.MyUtils;
import utils.ParamWrapper;
import utils.PostServletBase;
import utils.SubtleUtils;
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
		config.loginType = LoginType.USER;
		config.contentType = ContentType.JSON;
		config.txnXG = true;
		config.path = a("/", MobileTickKey.getKind(), "mobileKey");
		config.exists = true;
		config.path2 = a(UserCC.getKind(), "cardUUID");
		config.exists2 = true;
		config.strLists = a("items");
		config.longLists = a("nums", "denoms");
		config.longs = a("total", "tip");//NOTE: total does not include tip
		config.keyNames = a("clientID");
	}
	private static void validateInput(UserCC cc, List<String> itemsToPay, List<Frac> payFracs) throws HttpErrMsg
	{
		Calendar date = new GregorianCalendar();
		//TODO: The following will have Y2.1K bugs and I'm not totally sure
		//how to deal with them
		if((date.get(Calendar.YEAR) % 100 > cc.getExprYear()) ||
				((date.get(Calendar.YEAR) % 100 == cc.getExprYear()) &&
					(date.get(Calendar.MONTH)+1 > cc.getExprMonth())))
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
	private static void pay(JSONObject query, List<TicketItem> items, User user, UserCC cc, long total, long tip) throws JSONException, HttpErrMsg
	{
		if(query.getString("method").equals("subtle_data")) {
			//Note: rather than dividing the payments among the tickets,
			//we just put the entire payment on the first one.  Not only
			//is this easier, but it saves the restaurant money because it
			//lowers the number of transactions.
			if(items.size() == 0)
				throw new HttpErrMsg("You cannot pay for an empty ticket");
			else {
				JSONObject paymentInfo = new JSONObject();
				paymentInfo.put("tip_amount", tip/100.0);
				paymentInfo.put("amount_before_tip", total/100.0);
				paymentInfo.put("user_id", user.getSubtleID());
				paymentInfo.put("card_id", cc.getSubtleID());
				try {
					LocationsApi.addPaymentToTicket((int)query.getLong("loc"), SubtleUtils.getTickID(items.get(0).getID()), paymentInfo);
				} catch (ApiException e) {
					throw new HttpErrMsg(e);
				}
			}
		}
	}
	private static void updateDS(MobileTickKey mobile, JSONObject query, Set<String> itemsOnTicket, String clientID, UserCC cc, boolean ticketPaid, DatastoreService ds) throws JSONException, HttpErrMsg
	{
		cc.updateLastUse();
		cc.commit(ds);
		ds.delete(KeyFactory.createKey(BasicPointer.getKind(), clientID));
		MobileClient mc = new MobileClient(MyUtils.get_NoFail(mobile.getKey().getChild(MobileClient.getKind(), clientID), ds));
		new ClosedMobileClient(mobile.getRestrUsername(), mc, ClosedMobileClient.CloseCause.PAID).commit(ds);
		mc.rmv(ds);
		if(ticketPaid) {
			if(mobile.clearTickMetadata(ds))
				mobile.commit(ds);
			//Delete Ticket
			if(query.getString("method").equals("subtle_data")) {
				Set<Integer> subtleIDs = new HashSet<Integer>();
				for(String id : itemsOnTicket)
					subtleIDs.add(SubtleUtils.getTickID(id));
				for(Integer id : subtleIDs)
					try {
						LocationsApi.voidTicket((int)query.getLong("loc"), id, 0);
					} catch (ApiException e) {
						throw new HttpErrMsg(e);
					}
			}
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
		UserCC cc = new UserCC(p.getEntity(1));
		List<String> itemsToPay = p.getStrList(0);
		List<Frac> payFracs;
		try {
			payFracs = Frac.makeFracs(p.getLongList(0), p.getLongList(1));
		} catch(IllegalArgumentException ex) {
			throw new HttpErrMsg(ex.getMessage());
		}
		long total = p.getLong(0);
		long tip = p.getLong(1);
		User user = new User(MyUtils.get_NoFail(p.getAccountKey(), ds));
		String clientID = p.getKeyName(0);
		validateInput(cc, itemsToPay, payFracs);

		//Get/set internal payment info
		List<TicketItem> items;
		try {
			items = TicketItem.getItems(mobile);
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
		pay(query, items, user, cc, total, tip);
		updateDS(mobile, query, itemsOnTicket, clientID, cc, ticketPaid, ds);

		//Return
		JSONObject ret = new JSONObject();
		ret.put("done", ticketPaid);
		out.println(ret);
	}
}