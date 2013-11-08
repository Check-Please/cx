package templates;

public class CxCardBack {
	public static String run(Object section, int num) {
		return	"<div class=\"'card back\"'>"+
			"\t<h2>"+(section)+"</h2>"+
			"\t<h3>#"+(num)+"</h3>"+
			"\t<h4>Turn card over to pay with your phone</h4>"+
			"</div>";
	}
}