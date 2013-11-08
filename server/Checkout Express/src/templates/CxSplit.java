package templates;

public class CxSplit {
	public static String run(Object items) {
		return	"<div id=\"'split-page\"'>"+
			"\t<p>Tap on the items you will pay for or split with someone.</p>"+
			"\t<p>Once <strong>everyone</strong> is done picking their items, click the \"'confirm\"' button at the bottom.</p>"+
			"\t<ul class=\"'items\"'>"+(items)+"</ul>"+
			"\t<a class=\"'confirm\"'>Confirm</a>"+
			"</div>";
	}
}