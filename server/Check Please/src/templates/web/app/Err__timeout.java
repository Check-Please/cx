package templates.web.app;

public class Err__timeout {
	public static String run() {
		return	"<div id=\"err\">"+
			"\t<h1>Sorry!</h1>"+
			"\t<p>Your session has timed out.  Please reload the page.</p>"+
			"</div>";
	}
}