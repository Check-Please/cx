package templates.web.oz;

public class ItemDesc {
	public static String run(Object name, Object price, Object mods) {
		return	"<li class=\"item\">"+
			"\t<span class=\"name\">"+(name)+"</span>"+
			"\t<span class=\"price\">"+(price)+"</span>"+
			"\t<ul class=\"mods\">"+(mods)+"</ul>"+
			"</li>";
	}
}