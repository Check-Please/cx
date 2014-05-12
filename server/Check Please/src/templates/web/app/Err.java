package templates.web.app;

public class Err {
	public static String run(Object msg) {
		return	"<div id=\"err\">"+
			"\t<h1>Sorry!</h1>"+
			"\t<p>Something went wrong.  We\'re getting the following error message:</p>"+
			"\t<p class=\"error\">"+(msg)+"</p>"+
			"\t<p>Please pay your bill through the waitstaff</p>"+
			"</div>";
	}
}