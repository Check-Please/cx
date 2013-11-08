var template = template || {};

template.cxSplitItem = function(id, selected, others, name, price, mods) {
	return	"<li class=\"item "+(selected ? "selected":"")+" "+(others>0 ? "has-others":"")+"\">\r\n"+
			"\t<a itemID=\""+(id)+"\">\r\n"+
			"\t\t<span class=\"name\">"+(name)+"</span>\r\n"+
			"\t\t<span class=\"price\">"+(price)+"</span>\r\n"+
			"\t\t<ul class=\"mods\">"+(mods)+"</ul>\r\n"+
			"\t</a>\r\n"+
			"\t<div class=\"others\">"+
				(selected ? "Splitting with "+others+" other"+(others != 1 ? "s" : "") : others == 1 ? "Someone else is paying for this" : others+" others are splitting this")+
			"</div>\r\n"+
			"</li>";
};