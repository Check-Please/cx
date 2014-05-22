package cx;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.utils.SystemProperty;

public class ExpandServlet extends HttpServlet {
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 0;
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String path = req.getRequestURI();
		String args = req.getQueryString();
		if(path.startsWith("/"))
			path = path.substring(1);
		boolean tick = (path.length() == 0) && (args.length() > 0);
		boolean isDev = SystemProperty.environment.value() == SystemProperty.Environment.Value.Development;
		resp.sendRedirect("http"+(isDev ? "://localhost:8888" : "s://www.getthecheck.com")+(tick ? "/app.html" : "/"+path)+(args.length() > 0 ? "?"+args : ""));
	}
} 