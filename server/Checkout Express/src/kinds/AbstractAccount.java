package kinds;

import java.util.Date;
import java.util.UUID;

import utils.HttpErrMsg;
import utils.MyUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

public abstract class AbstractAccount extends AbstractKind
{
	private String passwordHASH;
	private String emailedCode;//Used for both email verification and password reset
	private Date exprDate;//null iff email has been verified
	private Date passwordResetExpr;
	public AbstractAccount(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public AbstractAccount(Entity e) { super(e); }
	public AbstractAccount(Key k, String password) throws HttpErrMsg
	{
		setKey(k);
		setPassword(password);
		emailedCode = UUID.randomUUID().toString();
		exprDate = new Date((new Date()).getTime() + MyUtils.week);
	}
	public void setPassword(String password) throws HttpErrMsg
	{
		this.passwordHASH = MyUtils.sha256(password);
	}
	public boolean checkPassword(String password) throws HttpErrMsg
	{
		return this.passwordHASH.equals(MyUtils.sha256(password));
	}
	public String makePasswordResetKey()
	{
		Date now = new Date();
		if(emailedCode == null || ((exprDate == null) &&
				(passwordResetExpr == null || (passwordResetExpr.compareTo(now) < 0))))
			emailedCode = UUID.randomUUID().toString();
		passwordResetExpr = new Date((new Date()).getTime() + MyUtils.week);
		return emailedCode;
	}
	public String getEmailCode()
	{
		return emailedCode;
	}
	public boolean checkPasswordResetKey(String possibleCode)
	{
		if(passwordResetExpr.compareTo(new Date()) < 0)
			return false;
		return possibleCode.equals(emailedCode);
	}
	public boolean verifyEmail(String possibleCode)
	{
		if(possibleCode.equals(emailedCode)) {
			exprDate = null;
			return true;
		} else
			return false;
	}
	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		e.setProperty("passwordHASH", new Text(passwordHASH));
		e.setProperty("emailedCode", emailedCode);
		if(passwordResetExpr != null)
			e.setProperty("passwordResetExpr", passwordResetExpr);
		if(exprDate != null)
			e.setProperty("exprDate", exprDate);
		return e;
	}
	public void fromEntity(Entity e)
	{
		passwordHASH = ((Text)e.getProperty("passwordHASH")).getValue();
		emailedCode = (String) e.getProperty("emailedCode");
		passwordResetExpr = (Date) e.getProperty("passwordResetExpr");
		exprDate = (Date) e.getProperty("exprDate");
	}

	public static AbstractAccount build(Entity e)
	{
		if(e.getKind().equals(User.getKind()))
			return new User(e);
		else if(e.getKind().equals(Restaurant.getKind()))
			return new Restaurant(e);
		else
			throw new IllegalArgumentException("Unknown account type");
	}
}
