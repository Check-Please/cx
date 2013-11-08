package servlets.admin_ops;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utils.ServletBase;

public class AdminLogoutServlet extends HttpServlet
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 8981336273178617925L;
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		doPost(req, resp);
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		req.getSession().removeAttribute(ServletBase.admin_pass_key);
	}
}