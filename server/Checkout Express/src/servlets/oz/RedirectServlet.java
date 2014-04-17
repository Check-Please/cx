package servlets.oz;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		resp.sendRedirect("http"+(isDev ? "" : "s")+"://"+req.getServerName()+(isDev ? ":"+req.getServerPort() : "")+"/?OZ"+n);
	}

}
