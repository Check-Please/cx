package servlets.oz;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;

import utils.HttpErrMsg;
import utils.MyUtils;

public class RedirectServlet extends HttpServlet {

	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = -4423464483534263577L;
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String n = req.getServletPath();
		if(n.startsWith("/"))
			n = n.substring(1);
		boolean isDev = false;
		try {
			isDev = MyUtils.isDevServer();
		} catch (HttpErrMsg e) {}
		//SJELIN
		Data d = new Data(MyUtils.get_NoFail(KeyFactory.createKey(Data.getKind(), "sjelin"), DatastoreServiceFactory.getDatastoreService()));
		ChannelServiceFactory.getChannelService().sendMessage(new ChannelMessage(d.getClient(), "ALERT: New payer at OZ"+n));
		//END SJELIN
		resp.sendRedirect("http"+(isDev ? "" : "s")+"://"+req.getServerName()+(isDev ? ":"+req.getServerPort() : "")+"/?OZ"+n);
	}

}
