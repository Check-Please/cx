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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import kinds.AbstractAccount;

import utils.Escaper;
import utils.HttpErrMsg;
import utils.PostServletBase;

@SuppressWarnings("serial")
public abstract class EmailPasswordReset extends PostServletBase
{
	protected static void doPost(String username, String email, String kind, String serviceName, PrintWriter out, DatastoreService ds) throws IOException, HttpErrMsg
	{
		AbstractAccount acc;
		try {
			acc = AbstractAccount.build(ds.get(KeyFactory.createKey(kind, username)));
		} catch (EntityNotFoundException e1) {
			throw new HttpErrMsg("Unknown username");
		}

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody =	"Someone has requested a password reset for " +
        					"your " + serviceName + " account.  If you " +
        					"would like to reset your password, please " +
        					"follow the URL below.  If this email was sent " +
        					"to you in error, please ignore it.\n\n" +
        					"\thttp://www.chkex.com/reset?email="+
        					Escaper.url(email)+"&key="+
        					Escaper.url(acc.makePasswordResetKey())+"\n\n"+
        					"This URL will expire in one week if it is not" +
        					"used.\n\n"+
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
        	throw new HttpErrMsg("Problem with the address");
        } catch (MessagingException e) {
        	throw new HttpErrMsg("Problem with the message");
        }
        acc.commit(ds);
	}
}
