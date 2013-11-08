package utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;

@SuppressWarnings("serial")
public abstract class GetServletBase extends ServletBase
{
	protected void customDefaults(Configuration config)
	{
		config.txnReq = false;
		config.readOnly = true;
	}
	protected void consisChk(Configuration config)
	{
		super.consisChk(config);
		if(!config.readOnly && !config.getReqHacks)
			throw new IllegalStateException("Get requests cannot have side effects");
	}
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		do____Wrapper(req, resp);
	}
	protected void doReq(ParamWrapper p, HttpSession sesh, DatastoreService ds, HttpServletResponse resp) throws IOException, JSONException, HttpErrMsg
	{
		resp.setHeader("Expires", "-1");
		doGet(p, sesh, ds, resp.getWriter());
	}
	protected abstract void doGet(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg;
}