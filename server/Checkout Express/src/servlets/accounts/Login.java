package servlets.accounts;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ConcurrentModificationException;

import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import kinds.AbstractAuthKey;
import kinds.AbstractAccount;

import utils.HttpErrMsg;
import utils.PostServletBase;

@SuppressWarnings("serial")
public abstract class Login extends PostServletBase
{
	protected static void doPost(String username, String password, Boolean recheck, HttpSession sesh, String authKeyPN, String accKind, AbstractAuthKey newAuth, PrintWriter out, DatastoreService ds) throws IOException, HttpErrMsg
	{
		String oldAuth = (String) sesh.getAttribute(authKeyPN);
		if((oldAuth != null) && ((recheck == null) || !recheck)) {
			try {
				if(ds.get(KeyFactory.createKey(newAuth.getKey().getKind(), oldAuth)).getProperty(AbstractAuthKey.usernamePN).equals(username))
					return;
			} catch (Exception e) {}
		}
		try {
			AbstractAccount acc = AbstractAccount.build(ds.get(KeyFactory.createKey(accKind, username)));
			if(acc.checkPassword(password)) {
				try {
					newAuth.commit(ds);
					sesh.setAttribute(authKeyPN, newAuth.getKey().getName());
				} catch(ConcurrentModificationException e) {
					throw new HttpErrMsg("ConcurrentModificationException: "+e.getMessage());
				} catch(DatastoreFailureException e) {
					throw new HttpErrMsg("Cannot create authentication key: "+e.getMessage());
				}
			} else
				throw new HttpErrMsg("Incorrect Password");
		} catch (EntityNotFoundException e) {
			throw new HttpErrMsg("Unknown username name");
		}
	}
}
