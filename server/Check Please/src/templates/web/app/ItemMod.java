package templates.web.app;

public class ItemMod {
	public static String run(Object name, int price) {
		return	"<li class=\"mod\">"+
			"\t<span class=\"name\">"+(name)+"</span>"+
			"\t<span class=\"price\">"+(price)+"</span>"+
			"</li>";
	}
}