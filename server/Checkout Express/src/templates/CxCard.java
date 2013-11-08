package templates;

public class CxCard {
	public static String run(Object title, Object img, Object front, Object back) {
		return	"<!DOCTYPE html PUBLIC \"'-//W3C//DTD XHTML 1.0 Strict//EN\"'"+
			"\t\t\"'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"'>"+
			""+
			"<html xmlns=\"'http://www.w3.org/1999/xhtml\"'>"+
			"\t<head>"+
			"\t\t<title>"+(title)+" Card</title>"+
			"\t\t<link type=\"'text/css\"' rel=\"'Stylesheet\"' href=\"'merged/cxCard.css\"' media=\"'all\"' />"+
			"\t</head>"+
			"\t<body class=\"'just-one\"'>"+
			"\t\t"+
				(img != null ? "<img class='title' src='cx_custom/"+img+"_receipt.png' />" : "<h1 class='title'>"+title+"</h1>")+
			""+
			"\t\t<h1>Front:</h1> "+(front)+""+
			"\t\t<h1>Back:</h1> "+(back)+""+
			"\t</body>"+
			"</html>";
	}
}