var template = template || {};

template.cxSplit = function(items) {
	return	"<div id=\"split-page\">\r\n"+
			"\t<p>Tap on the items you will pay for or split with someone.</p>\r\n"+
			"\t<p>Once <strong>everyone</strong> is done picking their items, click the \"confirm\" button at the bottom.</p>\r\n"+
			"\t<ul class=\"items\">"+(items)+"</ul>\r\n"+
			"\t<a class=\"confirm\">Confirm</a>\r\n"+
			"</div>";
};