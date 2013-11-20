package utils;

import java.io.IOException;
import java.util.ConcurrentModificationException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

@SuppressWarnings("serial")
public abstract class ServletBase extends HttpServlet
{
	private static final int MAX_RETRIES = 5;//We retry if there is a ConcurrentModificationException  during the commit
	protected static enum LoginType {
		USER
	}
	protected static enum ContentType {
		JSON, XML, HTML
	}
	protected static enum SecurityType {
		REJECT, REDIRECT
	}
	protected class Configuration {
		public ContentType contentType = null;
		public SecurityType securityType = null;

		public boolean adminReq = false;
		public boolean dsReq = true;
		public boolean txnReq = true;
		public boolean txnXG = false;
		public boolean readOnly = false;

		public boolean getReqHacks = false;//Allows a get request to not be readonly

		public String[] strs = null;
		public String[] strLists = {};
		public String[] str2DLists = null;
		public String[] longs = null;
		public String[] longLists = null;
		public String[] long2DLists = null;
		public String[] doubles = null;
		public String[] doubleLists = null;
		public String[] double2DLists = null;
		public String[] bools = null;
		public String[] boolLists = null;
		public String[] bool2DLists = null;
		public String[] keyIDs = null;
		public String[] keyIDLists = null;
		public String[] keyID2DLists = null;
		public String[] keyNames = null;
		public String[] keyNameLists = null;
		public String[] keyName2DLists = null;

		public String[] path = null;
		public Boolean exists = null;
		public String[] path2 = null;
		public Boolean exists2 = null;
		
		public Configuration()
		{
			customDefaults(this);
		}
	}
	protected void customDefaults(Configuration config) {};

	public void init() {
		if(getConfig() == null) {
			configure();
			consisChk(getConfig());
		}
	}
	protected abstract Configuration getConfig();
	protected abstract void configure();
	protected void consisChk(Configuration config)
	{
		if(config.exists != null && config.path == null)
			throw new IllegalStateException("Item has existence requirement, but there is no path");
		if(config.path2 != null && config.path == null)
			throw new IllegalStateException("Item has path2 but no path");
		if(config.exists2 != null && config.path2 == null)
			throw new IllegalStateException("Item has existence2 requirement, but there is no path2");
		if(config.txnReq && !config.dsReq)
			throw new IllegalStateException("Transaction required, but no database");
		if(config.txnXG && !config.txnReq)
			throw new IllegalStateException("Transaction must be cross-group, but isn't required");
	}

	private static String admin_pass_sha256 = "ec7aa91afe5d302190b1bac80c554d8fcd2bc1bbc5a4366193b1992d072fb56c";
	public static String admin_pass_key = "fVCvW8";

	public void do____Wrapper(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		try {
			Configuration config = getConfig();
			if((config.securityType != null) && !req.isSecure() && !MyUtils.isDevServer()) {
				switch(config.securityType) {
				case REDIRECT:
					StringBuffer requestURL = req.getRequestURL();
					String queryString = req.getQueryString();
					resp.sendRedirect((queryString == null ? requestURL : 
						requestURL.append('?').append(queryString)
							).toString().replaceFirst("http", "https"));
					break;
				case REJECT: throw new HttpErrMsg("SSL Required");
				}
				return;
			}
			Loader l = new Loader(req);
			DatastoreService ds = null;
			Transaction txn = null;

			if(config.contentType == null)
				resp.setContentType("text/plain");
			else switch(config.contentType) {
			case JSON:
				resp.setContentType("application/json");
				break;
			case XML:
				resp.setContentType("application/xml");
				break;
			case HTML:
				resp.setContentType("text/html");
				break;
			default:
				throw new IllegalStateException("Unknown content type");
			}

			if(config.adminReq) {
				if(req.getSession().getAttribute(admin_pass_key) == null)
					throw new HttpErrMsg("No admin login");
				if(!MyUtils.sha256(req.getSession().getAttribute(admin_pass_key).toString()).equals(admin_pass_sha256))
					throw new HttpErrMsg("Incorrect admin password");
			}
			int numRetries = 0;
			boolean retry;
			do {
				retry = false;

				//Load shit...
				if(config.strs != null)
					l.strs(config.strs);
				if(config.strLists != null)
					l.strLists(config.strLists);
				if(config.str2DLists != null)
					l.str2DLists(config.str2DLists);
				if(config.longs != null)
					l.longs(config.longs);
				if(config.longLists != null)
					l.longLists(config.longLists);
				if(config.long2DLists != null)
					l.long2DLists(config.long2DLists);
				if(config.doubles != null)
					l.doubles(config.doubles);
				if(config.doubleLists != null)
					l.doubleLists(config.doubleLists);
				if(config.double2DLists != null)
					l.double2DLists(config.double2DLists);
				if(config.bools != null)
					l.bools(config.bools);
				if(config.boolLists != null)
					l.boolLists(config.boolLists);
				if(config.bool2DLists != null)
					l.bool2DLists(config.bool2DLists);
				if(config.keyIDs != null)
					l.keyIDs(config.keyIDs);
				if(config.keyIDLists != null)
					l.keyIDLists(config.keyIDLists);
				if(config.keyID2DLists != null)
					l.keyID2DLists(config.keyID2DLists);
				if(config.keyNames != null)
					l.keyNames(config.keyNames);
				if(config.keyNameLists != null)
					l.keyNameLists(config.keyNameLists);
				if(config.keyName2DLists != null)
					l.keyName2DLists(config.keyName2DLists);

				try {
					if(config.dsReq) {
						ds = DatastoreServiceFactory.getDatastoreService();
						l.setDS(ds);
						if(config.txnReq)
							txn = ds.beginTransaction(TransactionOptions.Builder.withXG(config.txnXG));
					}
					if(config.path != null) {
						l.path(config.txnXG, config.path);
						if(config.exists != null) {
							if(config.exists)
								l.exists();
							else
								l.dne();
						}
					}
					if(config.path2 != null) {
						l.path(config.txnXG, config.path2);
						if(config.exists2 != null) {
							if(config.exists2)
								l.exists(1);
							else
								l.dne(1);
						}
					}
					doReq(l, req.getSession(), ds, resp);
					if(config.txnReq && !config.readOnly) try {
						txn.commit();
					} catch(ConcurrentModificationException e) {
						retry = numRetries++ < MAX_RETRIES;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					throw new HttpErrMsg(500, "JSON error");
				} finally {
					if((txn != null) && txn.isActive())
						if (txn.isActive())
							txn.rollback();//Do we special case config.readOnly?
				}
			} while(retry);
		} catch(HttpErrMsg e) {
			e.apply(resp);
		}
	}

	protected abstract void doReq(ParamWrapper p, HttpSession sesh, DatastoreService ds, HttpServletResponse resp) throws IOException, JSONException, HttpErrMsg;
}