package templates.web.app;

public class ReceiptItem {
	public static String run(Object name, Object price, Object mods, Object custom_class) {
		return	"<li class=\"item "+(custom_class)+"\">"+
			"\t<span class=\"name\">"+(name)+"</span>"+
			"\t<span class=\"price\">"+(price)+"</span>"+
			"\t<ul class=\"mods\">"+(mods)+"</ul>"+
			"</li>";
	}
}