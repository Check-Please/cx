package kinds;

import utils.HttpErrMsg;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class Restaurant extends AbstractAccount
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "restaurant"; }

	String name;
	String address;
	String email;
	String style;

	public Restaurant(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public Restaurant(Entity e) { super(e); }
	public Restaurant(Key k, String password, String name, String address, String email) throws HttpErrMsg
	{
		super(k, password);
		init(name, address, email, null);
	}
	public Restaurant(Key k, String password, String name, String address, String email, String style) throws HttpErrMsg
	{
		super(k, password);
		init(name, address, email, style);
	}

	private void init(String name, String address, String email, String style)
	{
		this.name = name;
		this.address = address;
		this.email = email;
		this.style = style;
	}

	public String getName()
	{
		return name;
	}

	public String getAddress()
	{
		return address;
	}

	public String getStyle()
	{
		return style;
	}

	public Entity toEntity()
	{
		Entity e = super.toEntity();
		e.setProperty("name", name);
		e.setProperty("address", address);
		e.setProperty("email", email);
		e.setProperty("style", style);
		return e;
	}


	public void fromEntity(Entity e)
	{
		super.fromEntity(e);
		name = (String) e.getProperty("name");
		address = (String) e.getProperty("address");
		email = (String) e.getProperty("email");
		style = (String) e.getProperty("style");
	}
}
