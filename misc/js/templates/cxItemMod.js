var template = template || {};

template.cxItemMod = function(name, price) {
	return	"<li class=\"mod\">\r\n"+
			"\t<span class=\"name\">"+(name)+"</span>\r\n"+
			"\t<span class=\"price\">"+(price)+"</span>\r\n"+
			"</li>";
};