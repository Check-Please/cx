package servlets.accounts;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import kinds.AbstractAccount;

import utils.HttpErrMsg;
import utils.PostServletBase;

@SuppressWarnings("serial")
public abstract class ResetPassword extends PostServletBase
{
	protected static void doPost(String username, String code, String newPassword, String kind, PrintWriter out, DatastoreService ds) throws IOException, HttpErrMsg
	{
		AbstractAccount acc;
		try {
			acc = AbstractAccount.build(ds.get(KeyFactory.createKey(kind, username)));
		} catch (EntityNotFoundException e1) {
			throw new HttpErrMsg("Unknown username");
		}
		if(acc.checkPasswordResetKey(code))
			acc.setPassword(newPassword);
		else
			throw new HttpErrMsg("Incorrect password reset key");
	}
}
