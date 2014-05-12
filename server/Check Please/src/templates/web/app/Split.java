package templates.web.app;

public class Split {
	public static String run(Object items) {
		return	"<div id=\"split\">"+
			"\t<p>Tap on the items you want to pay for.  If multiple people tap on the same item, its cost will be shared evenly.</p>"+
			"\t<p>Once <strong>everyone</strong> is done picking their items, click the \"confirm\" button at the bottom.</p>"+
			"\t<ul class=\"items\">"+(items)+"</ul>"+
			"\t<a class=\"confirm\">Split</a>"+
			"\t<a class=\"cancel\">Cancel</a>"+
			"</div>";
	}
}