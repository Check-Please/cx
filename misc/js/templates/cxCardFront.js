var template = template || {};

template.cxCardFront = function(code) {
	return	"<div class=\"card front\">\r\n"+
			"\t<h2>Scan code or enter url to pay on your phone</h2>\r\n"+
			"\t<img src=\"http://chart.googleapis.com/chart?chs=80x80&cht=qr&chld=|0&chl=http%3A//www.chkex.com/%3F"+(code)+"\" />\r\n"+
			"\t<a href=\"http://www.chkex.com/?"+(code)+"\">http://chkex.com?"+(code)+"</a>\r\n"+
			"</div>";
};