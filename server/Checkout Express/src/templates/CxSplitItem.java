package templates;

public class CxSplitItem {
	public static String run(Object id, boolean selected, int others, Object name, Object price, Object mods) {
		return	"<li class=\"'item "+(selected ? "selected":"")+" "+(others>0 ? "has-others":"")+"\"'>"+
			"\t<a itemID=\"'"+(id)+"\"'>"+
			"\t\t<span class=\"'name\"'>"+(name)+"</span>"+
			"\t\t<span class=\"'price\"'>"+(price)+"</span>"+
			"\t\t<ul class=\"'mods\"'>"+(mods)+"</ul>"+
			"\t</a>"+
			"\t<div class=\"'others\"'>"+
				(selected ? "Splitting with "+others+" other"+(others != 1 ? "s" : "") : others == 1 ? "Someone else is paying for this" : others+" others are splitting this" )+
			"</div>"+
			"</li>";
	}
}