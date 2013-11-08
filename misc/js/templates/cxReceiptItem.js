var template = template || {};

template.cxReceiptItem = function(name, price, mods, custom_class) {
	return	"<li class=\"item "+(custom_class)+"\">\r\n"+
			"\t<span class=\"name\">"+(name)+"</span>\r\n"+
			"\t<span class=\"price\">"+(price)+"</span>\r\n"+
			"\t<ul class=\"mods\">"+(mods)+"</ul>\r\n"+
			"</li>";
};