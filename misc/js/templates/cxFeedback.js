var template = template || {};

template.cxFeedback = function() {
	return	"<div id=\"feedback-page\">\r\n"+
			"\t<h1>Thank you!</h1>\r\n"+
			"\t<div class=\"feedback\">\r\n"+
			"\t\t<h3>Rate your experience:</h3>\r\n"+
			"\t\t<div class=\"ratings\">\r\n"+
			"\t\t\t<a class=\"bad\"><img src=\"img/thumbs-down.png\" /></a>\r\n"+
			"\t\t\t<a class=\"ok\">OK</a>\r\n"+
			"\t\t\t<a class=\"just good\"><img src=\"img/thumbs-up.png\" /></a>\r\n"+
			"\t\t\t<a class=\"very good\"><img src=\"img/thumbs-up-up.png\" /></a>\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<!--p class=\"comment-opener\">\r\n"+
			"\t\t\tClick <a>here</a> to leave a comment\r\n"+
			"\t\t\t<div id=\"comment-wrapper\">\r\n"+
			"\t\t\t\t<div id=\"comment\">\r\n"+
			"\t\t\t\t\t<div class=\"bg\"></div>\r\n"+
			"\t\t\t\t\t<div class=\"ui\">\r\n"+
			"\t\t\t\t\t\t<h4>Please tell us how we did</h4>\r\n"+
			"\t\t\t\t\t\t<textarea></textarea>\r\n"+
			"\t\t\t\t\t\t<p class=\"characters-remaining\"></p>\r\n"+
			"\t\t\t\t\t\t<a class=\"confirm\">Done</a>\r\n"+
			"\t\t\t\t\t</div>\r\n"+
			"\t\t\t\t</div>\r\n"+
			"\t\t\t</div>\r\n"+
			"\t\t</p-->\r\n"+
			"\t</div>\r\n"+
			"\t<div class=\"self-promotion\">\r\n"+
			"\t\t<h3>Own a restaurant?</h3>\r\n"+
			"\t\t<p>Email <a href=\"mailto:sjelin@chkex.com\">sjelin@chkex.com</a> to get this app at your restaurant</p>\r\n"+
			"\t</div>\r\n"+
			"</div>";
};