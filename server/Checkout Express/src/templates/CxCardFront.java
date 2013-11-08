package templates;

public class CxCardFront {
	public static String run(Object code) {
		return	"<div class=\"'card front\"'>"+
			"\t<h2>Scan code or enter url to pay on your phone</h2>"+
			"\t<img src=\"'http://chart.googleapis.com/chart?chs=80x80&cht=qr&chld=|0&chl=http%3A//www.chkex.com/%3F"+(code)+"\"' />"+
			"\t<a href=\"'http://www.chkex.com/?"+(code)+"\"'>http://chkex.com?"+(code)+"</a>"+
			"</div>";
	}
}