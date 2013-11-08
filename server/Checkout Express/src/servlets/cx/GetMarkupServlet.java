 package servlets.cx;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;

import kinds.BasicPointer;
import kinds.BasicTickLog;
import kinds.MobileClient;
import kinds.MobileTickKey;
import kinds.Restaurant;
import kinds.UserAuthKey;

import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import utils.Escaper;
import utils.GetServletBase;
import utils.HttpErrMsg;
import utils.MyUtils;
import utils.ParamWrapper;
import utils.TicketItem;
import utils.UnsupportedFeatureException;
import static utils.MyUtils.a;

import servlets.users.GetUserCreditCardsServlet;
import templates.CxFooter;
import templates.CxPrivacyPolicy;
import templates.Cx;
import templates.CxHeader;
import templates.CxAskSplit;
import templates.CxSplit;
import templates.CxReceipt;
import templates.CxLogin;
import templates.CxPay;
import templates.CxFeedback;
import templates.CxNoJS;
import templates.CxTermsOfUse;


public class GetMarkupServlet extends GetServletBase
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 9151686588288617431L;

	private static Configuration config;
	protected Configuration getConfig()
	{
		return config;
	}
	protected void configure() {
		config = new Configuration();
		config.securityType = SecurityType.REDIRECT;
		config.contentType = ContentType.HTML;
		config.txnReq = true;
		config.txnXG = true;
		config.readOnly = false;
		config.getReqHacks = true;
		config.strs = a("?debugUUID");
	}

	private static String errPage(String msg)
	{
		return Cx.run("\""+Escaper.dq(msg)+"\"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	protected void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg
	{
		String mKey = p.getQueryString();
		String debugUUID = p.getStr(0);
		if(debugUUID != null)
			mKey = MyUtils.removeParam(mKey, "debugUUID");
		if((mKey == null) || (mKey.length() == 0)) {
			out.println(errPage("There is no information about what table you are at"));
		} else try {
			mKey = mKey.toUpperCase();
			MobileTickKey mobile = new MobileTickKey(KeyFactory.createKey(MobileTickKey.getKind(), mKey), ds);
			Restaurant restr = mobile.getRestr(ds);
			List<TicketItem> items = TicketItem.getItems(mobile);
			if(items == null || items.size() == 0) {
				out.println(errPage("EMPTY"));
				return;
			}
			if(mobile.clearOldMetadata(items, ds))
				mobile.commit(ds);
			String email = "";
			JSONArray cards = null;
			String authKey = (String) sesh.getAttribute(UserAuthKey.httpPN);
			if(authKey != null) try {
				Key k = KeyFactory.createKey(UserAuthKey.getKind(), authKey);
				UserAuthKey auth = new UserAuthKey(k, ds);
				email = auth.getAccountKey().getName();
				cards = GetUserCreditCardsServlet.getCards(auth.getAccountKey(), ds);
			} catch (EntityNotFoundException e) {}
			String clientID = UUID.randomUUID().toString();
			new BasicPointer(KeyFactory.createKey(BasicPointer.getKind(), clientID), mKey).commit(ds);
			String token = ChannelServiceFactory.getChannelService().createChannel(clientID);
			Key logKey = BasicTickLog.makeKey(restr.getKey().getName(), mKey, items);
			try {
				ds.get(logKey);
			} catch(EntityNotFoundException e) {
				new BasicTickLog(logKey, items).commit(ds);
			}
			new MobileClient(mobile.getKey().getChild(MobileClient.getKind(), clientID), logKey.getName()).commit(ds);
			String streetAddress = restr.getAddress().replaceAll("^\\s*(.*?),?[^\\,]+,?[a-zA-Z ]{2,},?\\s*[0-9\\-]{0,10}\\s*$", "$1");
			String cityAddress = restr.getAddress().substring(streetAddress.length());
			if(cityAddress.length() == 0)
				cityAddress = null;
			else if(streetAddress.length() == 0) {
				streetAddress = cityAddress;
				cityAddress = null;
			} else {
				if(cityAddress.startsWith(","))
					cityAddress = cityAddress.substring(1);
				cityAddress = cityAddress.trim();
			}
			out.println(Cx.run(null, restr.getName(), restr.getStyle(), email, token,
					mKey, clientID, items.toString(), mobile.getSplit(clientID, ds), cards,
					CxHeader.run(restr.getName(), restr.getStyle()),
					CxAskSplit.run(),
					CxSplit.run("<div class='REPLACE-ME'></div>"),
					CxReceipt.run(restr.getName(), restr.getStyle(), streetAddress, cityAddress, "<div class='REPLACE-ME'></div>"),
					CxLogin.run(true, CxTermsOfUse.run(), CxPrivacyPolicy.run()),
					CxPay.run(email, "<option class='REPLACE-ME'></option>"),
					CxFeedback.run(),
					CxFooter.run(),
					CxNoJS.run(),
					debugUUID));
		} catch (EntityNotFoundException e) {
			out.println(errPage("The table you have specified does not exist.  There is probably a typo in the URL you entered or a problem with the QR code you scanned"));
		} catch (JSONException e) {
			e.printStackTrace();
			out.println(errPage("There was internal problem with JSON.  We will try to solve this ASAP"));
		} catch (UnsupportedFeatureException e) {
			out.println(errPage(e.getMessage()));
		}
	}
}
