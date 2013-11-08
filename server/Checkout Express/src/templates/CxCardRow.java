package templates;

public class CxCardRow {
	public static String run(Object card1, Object card2, Object card3) {
		return	"<div class=\"'vspace row\"'>"+
			"\t<div class=\"'hspace\"'>"+
			"\t\t<div class=\"'v marker\"'></div>"+
			"\t\t<div class=\"'h marker\"'></div>"+
			"\t</div>"+
			"\t<div class=\"'card\"'></div>"+
			"\t<div class=\"'hspace\"'>"+
			"\t\t<div class=\"'v marker\"'></div>"+
			"\t\t<div class=\"'h marker\"'></div>"+
			"\t</div>"+
			"\t<div class=\"'card\"'></div>"+
			"\t<div class=\"'hspace\"'>"+
			"\t\t<div class=\"'v marker\"'></div>"+
			"\t\t<div class=\"'h marker\"'></div>"+
			"\t</div>"+
			"\t<div class=\"'card\"'></div>"+
			"\t<div class=\"'hspace\"'>"+
			"\t\t<div class=\"'v marker\"'></div>"+
			"\t\t<div class=\"'h marker\"'></div>"+
			"\t</div>"+
			"</div>"+
			"<div class=\"'row\"'>"+
			"\t<div class=\"'hspace\"'></div>"+
			"\t"+(card1)+""+
			"\t<div class=\"'hspace\"'></div>"+
			"\t"+(card2)+""+
			"\t<div class=\"'hspace\"'></div>"+
			"\t"+(card3)+""+
			"\t<div class=\"'hspace\"'></div>"+
			"</div>"+
			"<div class=\"'vspace row\"'>"+
			"\t<div class=\"'hspace\"'>"+
			"\t\t<div class=\"'v marker\"'></div>"+
			"\t\t<div class=\"'h marker\"'></div>"+
			"\t</div>"+
			"\t<div class=\"'card\"'></div>"+
			"\t<div class=\"'hspace\"'>"+
			"\t\t<div class=\"'v marker\"'></div>"+
			"\t\t<div class=\"'h marker\"'></div>"+
			"\t</div>"+
			"\t<div class=\"'card\"'></div>"+
			"\t<div class=\"'hspace\"'>"+
			"\t\t<div class=\"'v marker\"'></div>"+
			"\t\t<div class=\"'h marker\"'></div>"+
			"\t</div>"+
			"\t<div class=\"'card\"'></div>"+
			"\t<div class=\"'hspace\"'>"+
			"\t\t<div class=\"'v marker\"'></div>"+
			"\t\t<div class=\"'h marker\"'></div>"+
			"\t</div>"+
			"</div>";
	}
}