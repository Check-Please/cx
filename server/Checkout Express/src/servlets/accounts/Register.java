package servlets.accounts;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import kinds.AbstractAccount;
import kinds.AbstractAuthKey;

import utils.Escaper;
import utils.HttpErrMsg;
import utils.PostServletBase;

@SuppressWarnings("serial")
public abstract class Register extends PostServletBase
{
	protected static interface AccountBuilder {
		public AbstractAccount build() throws JSONException, HttpErrMsg;
	};
	protected static void doPost(String username, String email, String serviceName, Key accKey, AccountBuilder builder, HttpSession sesh, String authKeyPN, AbstractAuthKey newAuth, PrintWriter out, DatastoreService ds) throws IOException, HttpErrMsg, JSONException
	{
		try {
			ds.get(KeyFactory.createKey(accKey.getKind(), username));
			throw new HttpErrMsg("Username in use");
		} catch (EntityNotFoundException e1) {}
		AbstractAccount newAcc = builder.build();
		newAcc.commit(ds);

		//Login
		newAuth.commit(ds);
		sesh.setAttribute(authKeyPN, newAuth.getKey().getName());

		//Send email
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody =	"Please confirm your email for " + serviceName +
        					" by clicking the following link:" +
        					"\thttp://www.chkex.com/verify?email="+
        					Escaper.url(email)+"&key="+
        					Escaper.url(newAcc.getEmailCode())+"\n\n"+
        					"Your account will expire in a week if you do " +
        					"not use the above link\n\n"+
        					"Best,\n"+
        					serviceName+" Team\n\n\n\n"+
        					"(this is an automatic email, do not reply)";
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("mail@checkoutxp.appspotmail.com", "Checkout Express Admin"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(email));
            msg.setSubject("Password Reset for "+serviceName);
            msg.setText(msgBody);
            Transport.send(msg);
    
        } catch (AddressException e) {
        	throw new HttpErrMsg("Problem with the email address");
        } catch (MessagingException e) {
        	throw new HttpErrMsg("Problem with the email message");
        }
	}
}
