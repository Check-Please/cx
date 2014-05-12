package templates.web.app;

public class Err__500 {
	public static String run() {
		return	"<div id=\"err\">"+
			"\t<h1>Sorry!</h1>"+
			"\t<p>We are unable to process your order at this time.  Please pay through the waitstaff</p>"+
			"</div>";
	}
}