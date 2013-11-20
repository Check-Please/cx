package utils;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class HttpErrMsg extends Exception
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 3857489497289421219L;

	private int code;
	private String msg;
	public static final HttpErrMsg ROLLBACK_DB = new HttpErrMsg(200, "This isn't actually an error, I just want the database to be rolled back.  If you are seeing this, something went very wrong.");

	public HttpErrMsg(String msg)
	{
		this.code = 404;
		this.msg = msg;
	}
	public HttpErrMsg(int code, String msg)
	{
		this.code = code;
		this.msg = msg;
	}
	public int getCode()
	{
		return code;
	}
	public String getMessage()
	{
		return msg;
	}
	public void apply(HttpServletResponse resp) throws IOException
	{
		if(this != ROLLBACK_DB) {//Note that I'm using actual equality here, not the equals() method
			resp.setStatus(code);
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().println(msg);
		}
	}
}
