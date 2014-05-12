package templates.web.app;

public class SplitItem {
	public static String run(Object id, boolean selected, int others, Object name, Object price, Object mods) {
		return	"<li class=\"item"+(selected ? " selected":"")+(others>0 ? " has-others":"")+"\">"+
			"\t<a itemID=\""+(id)+"\">"+
			"\t\t<span class=\"name\">"+(name)+"</span>"+
			"\t\t<span class=\"price\">"+(price)+"</span>"+
			"\t\t<ul class=\"mods\">"+(mods)+"</ul>"+
			"\t</a>"+
			"\t<div class=\"others\">"+
			"\t\t"+(selected ?
			"\t\t\tSplitting with "+(others)+" other"+((others != 1 ? "s" : ""))+
			"\t\t": others == 1 ?
			"\t\t\t1 other person is paying for this"+
			"\t\t":
			"\t\t\t"+(others)+" other people are splitting this"+
			"\t\t")+
			"\t</div>"+
			"</li>";
	}
}