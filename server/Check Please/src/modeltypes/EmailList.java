package modeltypes;

import java.util.ArrayList;
import java.util.List;

import utils.DSConverter;
import utils.HttpErrMsg;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class EmailList extends AbstractModelType
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "email_list"; }

	private List<String> emails;
	private List<String> zips;

	public EmailList(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public EmailList(Entity e) { super(e); }
	public EmailList(Key k) throws HttpErrMsg
	{
		setKey(k);
		emails = new ArrayList<String>();
		zips = new ArrayList<String>();
	}

	public void saveEmail(String email, String zip)
	{
		emails.add(email);
		zips.add(zip);
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		DSConverter.setList(e, "emails", emails);
		DSConverter.setList(e, "zips", zips);
		return e;
	}


	public void fromEntity(Entity e)
	{
		emails = DSConverter.getList(e, "emails");
		zips = DSConverter.getList(e, "zips");
	}
}
