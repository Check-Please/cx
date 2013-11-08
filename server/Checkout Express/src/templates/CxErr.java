package templates;

public class CxErr {
	public static String run(Object msg) {
		return	"<div id=\"'err-page\"'>"+
			"\t<h1>Sorry!</h1>"+
			"\t<p>Something went wrong.  We\'re getting the following error message:</p>"+
			"\t<p class=\"'error\"'>"+(msg)+"</p>"+
			"\t<p>Please pay your bill through the waitstaff</p>"+
			"</div>";
	}
}