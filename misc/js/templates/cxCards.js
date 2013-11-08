var template = template || {};

template.cxCards = function(side, cards) {
	return	"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\r\n"+
			"\t\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n"+
			"\r\n"+
			"<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n"+
			"\t<head>\r\n"+
			"\t\t<title>Cards ("+(side)+")</title>\r\n"+
			"\t\t<link type=\"text/css\" rel=\"Stylesheet\" href=\"merged/cxCard.css\" media=\"all\" />\r\n"+
			"\t</head>\r\n"+
			"\t<body class=\""+(side)+"\">\r\n"+
			"\t\t"+(cards)+"\r\n"+
			"\t</body>\r\n"+
			"</html>";
};