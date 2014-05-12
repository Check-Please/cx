package templates.web.app;

public class Err__empty {
	public static String run() {
		return	"<div id=\"err\">"+
			"\t<h1>Sorry!</h1>"+
			"\t<p>We can\'t find any unpaid items on your table.  If you\'ve pushed two tables together, you may have to check the other table.  Otherwise, please pay through the waitstaff</p>"+
			"</div>";
	}
}