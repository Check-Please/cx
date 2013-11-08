var template = template || {};

template.cxPay = function(username, cards) {
	return	"<div id=\"pay-page\">\r\n"+
			"\t<h1>Payment Information <img class=\"ssl\" /></h1>\r\n"+
			"\t<div class=\"login\">\r\n"+
			"\t\tLogged in as <span class=\"username\">"+(username)+"</span>\r\n"+
			"\t\t<a>Change login</a>\r\n"+
			"\t</div>\r\n"+
			"\tPick Credit Card:\r\n"+
			"\t<select class=\"cards\">\r\n"+
			"\t\t"+(cards)+"\r\n"+
			"\t\t<option>New Credit Card</option>\r\n"+
			"\t</select>\r\n"+
			"\r\n"+
			"\t<form class=\"new-cc\">\r\n"+
			"\t\t<div class=\"pan\">\r\n"+
			"\t\t\t<label>Credit Card Number</label>\r\n"+
			"\t\t\t<input type=\"text\" />\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<div class=\"name\">\r\n"+
			"\t\t\t<label>Name on card</label>\r\n"+
			"\t\t\t<input type=\"text\" class=\"name\"  maxlength=\"26\" />\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<div class=\"expr-month\">\r\n"+
			"\t\t\t<label>Month</label>\r\n"+
			"\t\t\t<select>\r\n"+
			"\t\t\t\t<option>01 - January</option>\r\n"+
			"\t\t\t\t<option>02 - February</option>\r\n"+
			"\t\t\t\t<option>03 - March</option>\r\n"+
			"\t\t\t\t<option>04 - April</option>\r\n"+
			"\t\t\t\t<option>05 - May</option>\r\n"+
			"\t\t\t\t<option>06 - June</option>\r\n"+
			"\t\t\t\t<option>07 - July</option>\r\n"+
			"\t\t\t\t<option>08 - August</option>\r\n"+
			"\t\t\t\t<option>09 - September</option>\r\n"+
			"\t\t\t\t<option>10 - October</option>\r\n"+
			"\t\t\t\t<option>11 - November</option>\r\n"+
			"\t\t\t\t<option>12 - December</option>\r\n"+
			"\t\t\t</select>\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<div class=\"expr-year\">\r\n"+
			"\t\t\t<label>Year</label>\r\n"+
			"\t\t\t<input type=\"text\" maxlength=\"4\" />\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<div class=\"cvv\">\r\n"+
			"\t\t\t<label>Security Code</label>\r\n"+
			"\t\t\t<input type=\"text\" />\r\n"+
			"\t\t\t<div class=\"explanation\">\r\n"+
			"\t\t\t\t<a>(what\'s this?)</a>\r\n"+
			"\t\t\t\t<div class=\"popup\">\r\n"+
			"\t\t\t\t\t<div>\r\n"+
			"\t\t\t\t\t\t<a>&times;</a>\r\n"+
			"\t\t\t\t\t\tYour security code can be found on your card\r\n"+
			"\t\t\t\t\t\t<img src=\"img/CSC_en_US.png\" />\r\n"+
			"\t\t\t\t\t</div>\r\n"+
			"\t\t\t\t</div>\r\n"+
			"\t\t\t</div>\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<div class=\"zip\">\r\n"+
			"\t\t\t<label>Zip Code</label>\r\n"+
			"\t\t\t<input type=\"text\" maxlength=\"5\" />\r\n"+
			"\t\t</div>\r\n"+
			"\t</form>\r\n"+
			"\t<a class=\"confirm\">Pay</a>\r\n"+
			"\t<p class=\"security-notice\">Credit card storage and payments handled by SubtleData, Inc.</p>\r\n"+
			"</div>";
};