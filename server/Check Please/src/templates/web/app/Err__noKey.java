package templates.web.app;

public class Err__noKey {
	public static String run(Object prereq) {
		return	"<div id=\"err\">"+
			"\t<h1>Sorry!</h1>"+
			"\t<p>We can\'t figure out where you\'re sitting.  Please make sure "+(prereq)+".  If that is not the problem, please pay through the waitstaff</p>"+
			"</div>";
	}
}