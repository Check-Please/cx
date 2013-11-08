var template = template || {};

template.cxCard = function(title, img, front, back) {
	return	"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\r\n"+
			"\t\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n"+
			"\r\n"+
			"<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n"+
			"\t<head>\r\n"+
			"\t\t<title>"+(title)+" Card</title>\r\n"+
			"\t\t<link type=\"text/css\" rel=\"Stylesheet\" href=\"merged/cxCard.css\" media=\"all\" />\r\n"+
			"\t</head>\r\n"+
			"\t<body class=\"just-one\">\r\n"+
			"\t\t"+
				(img != null ? "<img class='title' src='cx_custom/"+img+"_receipt.png' />" : "<h1 class='title'>"+title+"</h1>")+
			"\r\n"+
			"\t\t<h1>Front:</h1> "+(front)+"\r\n"+
			"\t\t<h1>Back:</h1> "+(back)+"\r\n"+
			"\t</body>\r\n"+
			"</html>";
};