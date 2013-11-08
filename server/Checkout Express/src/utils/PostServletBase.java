package utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;

import com.google.appengine.api.datastore.DatastoreService;


@SuppressWarnings("serial")
public abstract class PostServletBase extends ServletBase
{
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		do____Wrapper(req, resp);
	}
	protected void doReq(ParamWrapper p, HttpSession sesh, DatastoreService ds, HttpServletResponse resp) throws IOException, JSONException, HttpErrMsg
	{
		doPost(p, sesh, ds, resp.getWriter());
	}
	protected abstract void doPost(ParamWrapper p, HttpSession sesh, DatastoreService ds, PrintWriter out) throws IOException, JSONException, HttpErrMsg;
}