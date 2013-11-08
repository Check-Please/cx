package servlets.users;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import kinds.User;
import kinds.UserCC;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Key;
import com.subtledata.api.UsersApi;
import com.subtledata.client.ApiException;

import utils.HttpErrMsg;
import utils.MyUtils;
import utils.ParamWrapper;
import utils.PostServletBase;
import static utils.MyUtils.a;

public class AddCreditCardServlet extends PostServletBase
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
		config.securityType = SecurityType.REJECT;
		config.strs = a("PAN", "name");
		config.longs = a("CVV", "exprYear", "exprMonth", "zip");
	}
	protected void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, HttpErrMsg, NumberFormatException, JSONException
	{
		String err;

		String pan = p.getStr(0);
		if((err = UserCC.checkPAN(pan)) != null)
			throw new HttpErrMsg(err);
		String name = p.getStr(1);
		if(name.length() < UserCC.minNameLen)
			throw new HttpErrMsg("Name too short");
		if(name.length() > UserCC.maxNameLen)
			throw new HttpErrMsg("Name too short");
		int iin = Integer.parseInt(pan.substring(0, 6));
		int cvv = p.getLong(0).intValue();
		if((err = UserCC.checkCVV(iin, cvv)) != null)
			throw new HttpErrMsg(err);
		int exprYear = p.getLong(1).intValue()%100;
		int exprMonth = p.getLong(2).intValue();
		if(exprMonth < 1 || exprMonth > 12)
			throw new HttpErrMsg("Invalid Month");
		int zip = p.getLong(3).intValue();
		if(zip < 0 || zip > 99999)
			throw new HttpErrMsg("Invalid ZIP");

		//Add credit card to SubtleData
		User user = new User(MyUtils.get_NoFail(p.getAccountKey(), ds));
		JSONObject cardInfo = new JSONObject();
		cardInfo.put("name_on_card", name);
		cardInfo.put("expiration_year", 2000+exprYear);//TODO This code will break within the century #YOLO
		cardInfo.put("expiration_month", exprMonth);
		cardInfo.put("billing_zip", zip);
		cardInfo.put("card_number", pan);
		JSONObject subtleInfo;
		try {
			subtleInfo = UsersApi.createCardForUser(user.getSubtleID(), cardInfo);
		} catch (ApiException e) {
			throw new HttpErrMsg(e);
		}

		Key k = MyUtils.newKey(p.getAccountKey(), UserCC.getKind());
		(new UserCC(k, iin, Integer.parseInt(pan.substring(pan.length()-4)),
				name, exprYear, exprMonth, zip, subtleInfo.getInt("card_id"))).commit(ds);
		out.println(k.getName());
	}
}