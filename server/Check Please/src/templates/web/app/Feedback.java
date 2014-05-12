package templates.web.app;

public class Feedback {
	public static String run() {
		return	"<div id=\"feedback\">"+
			"\t<h1>Thank you!</h1>"+
			"\t<div class=\"feedback\">"+
			"\t\t<h3>Rate your experience:</h3>"+
			"\t\t<div class=\"ratings\">"+
			"\t\t\t<a class=\"bad\"><img src=\"img/app/thumbs-down.png\" /></a>"+
			"\t\t\t<a class=\"ok\">OK</a>"+
			"\t\t\t<a class=\"just good\"><img src=\"img/app/thumbs-up.png\" /></a>"+
			"\t\t\t<a class=\"very good\"><img src=\"img/app/thumbs-up-up.png\" /></a>"+
			"\t\t</div>"+
			"\t</div>"+
			"\t<div class=\"self-promotion\">"+
			"\t\t<h3>Own a restaurant?</h3>"+
			"\t\t<p>Email <a href=\"mailto:sjelin@chkex.com\">sjelin@chkex.com</a> to get this app at your restaurant</p>"+
			"\t</div>"+
			"</div>";
	}
}