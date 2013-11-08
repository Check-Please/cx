var template = template || {};

template.cxReceipt = function(restrName, logo, street_address, city_address, items) {
	return	"<div id=\"receipt-page\">\r\n"+
			"\t<div class=\'receipt "+(logo != null ? "has-logo" : "")+"\'>\r\n"+
			"\t\t<div class=\"header\"></div>\r\n"+
			"\t\t"+
				(logo == null ? "" : "<img class='title' src='cx_custom/"+logo+"_receipt.png' />")+
			"\r\n"+
			"\t\t<h2 class=\'title\'>"+(restrName)+"</h2>\r\n"+
			"\t\t<h3 class=\"street address\">"+(street_address)+"</h3>\r\n"+
			"\t\t"+
				(city_address == null ? "" : "<h3 class='city address'>"+city_address+"</h3>")+
			"\r\n"+
			"\t\t<ul class=\"items\">"+(items)+"</ul>\r\n"+
			"\t\t<div class=\"footer\"></div>\r\n"+
			"\t</div>\r\n"+
			"\t<div class=\"tip\">\r\n"+
			"\t\t<div class=\"buttons\">\r\n"+
			"\t\t\tTip:\r\n"+
			"\t\t\t<a class=\"fifteen percent\">15%</a>\r\n"+
			"\t\t\t<a class=\"seventeen percent\">17%</a>\r\n"+
			"\t\t\t<a class=\"twenty percent\">20%</a>\r\n"+
			"\t\t\t<a class=\"other percent\">Other</a>\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<div class=\"input\">\r\n"+
			"\t\t\tAmount: $<input type=\'text\' />\r\n"+
			"\t\t\t<span class=\"tipPrct\"></span>\r\n"+
			"\t\t</div>\r\n"+
			"\t</div>\r\n"+
			"\t<a class=\"confirm\">Confirm</a>\r\n"+
			"</div>";
};