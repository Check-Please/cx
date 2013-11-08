package templates;

public class CxCards {
	public static String run(Object side, Object cards) {
		return	"<!DOCTYPE html PUBLIC \"'-//W3C//DTD XHTML 1.0 Strict//EN\"'"+
			"\t\t\"'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"'>"+
			""+
			"<html xmlns=\"'http://www.w3.org/1999/xhtml\"'>"+
			"\t<head>"+
			"\t\t<title>Cards ("+(side)+")</title>"+
			"\t\t<link type=\"'text/css\"' rel=\"'Stylesheet\"' href=\"'merged/cxCard.css\"' media=\"'all\"' />"+
			"\t</head>"+
			"\t<body class=\"'"+(side)+"\"'>"+
			"\t\t"+(cards)+""+
			"\t</body>"+
			"</html>";
	}
}