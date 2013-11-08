var template = template || {};

template.cxCardBack = function(section, num) {
	return	"<div class=\"card back\">\r\n"+
			"\t<h2>"+(section)+"</h2>\r\n"+
			"\t<h3>#"+(num)+"</h3>\r\n"+
			"\t<h4>Turn card over to pay with your phone</h4>\r\n"+
			"</div>";
};