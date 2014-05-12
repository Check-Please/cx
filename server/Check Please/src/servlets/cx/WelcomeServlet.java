package servlets.cx;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WelcomeServlet extends HttpServlet {

	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = -4423464483534263577L;
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String q = req.getQueryString();
		if((q == null) || (q.length() == 0))
			resp.sendRedirect("/website.html");
		else {
			RequestDispatcher dispatcher = req.getRequestDispatcher("/app.html");
			dispatcher.forward(req, resp);
		}
	}

}
