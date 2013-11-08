package utils;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.subtledata.client.ApiException;

public class HttpErrMsg extends Exception
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = 3857489497289421219L;

	private int code;
	private String msg;

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
	public HttpErrMsg(ApiException e)
	{
		this.code = e.getCode();
		this.msg = "SUBTLE DATA ERROR: "+e.getMessage();
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
		resp.setStatus(code);
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().println(msg);
	}
}
