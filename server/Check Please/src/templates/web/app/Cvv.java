package templates.web.app;

public class Cvv {
	public static String run() {
		return	"<div class=\"cvv\">"+
			"\t<label>Security Code</label>"+
			"\t<input type=\"text\" maxlength=\"4\" />"+
			"\t<div class=\"explanation\">"+
			"\t\t<a>(what\'s this?)</a>"+
			"\t\t<div class=\"popup-bg\">"+
			"\t\t\t<div class=\"popup-wrapper\">"+
			"\t\t\t\t<p>Your security code can be found on your card</p>"+
			"\t\t\t\t<img src=\"img/app/CSC_en_US.png\" />"+
			"\t\t\t</div>"+
			"\t\t</div>"+
			"\t</div>"+
			"</div>";
	}
}