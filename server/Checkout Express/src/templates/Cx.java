package templates;

public class Cx {
	public static String run(Object errMsg, Object title, Object style, Object email, Object token, Object mobileKey, Object clientID, Object items, Object split, Object cards, Object header, Object askSplit, Object splitPage, Object receipt, Object login, Object pay, Object feedback, Object footer, Object noJS, Object debugUUID) {
		return	"<!DOCTYPE html PUBLIC \"'-//W3C//DTD XHTML 1.0 Strict//EN\"'"+
			"\t\t\"'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"'>"+
			""+
			"<html xmlns=\"'http://www.w3.org/1999/xhtml\"'>"+
			"\t<head>"+
			"\t\t<title>Pay Ticket - "+(title)+"</title>"+
			"\t\t<meta charset=\"'UTF-8\"' />"+
			"\t\t<meta name=\"'viewport\"' content=\"'width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\"' />"+
			"\t\t<meta name=\"'apple-mobile-web-app-capable\"' content=\"'yes\"' />"+
			"\t\t<meta name=\"'apple-mobile-web-app-status-bar-style\"' content=\"'black-translucent\"' />"+
			"\t\t<meta name=\"'HandheldFriendly\"' content=\"'true\"' />"+
			"\t\t<meta name=\"'MobileOptimized\"' content=\"'width\"' />"+
			"\t\t<link type=\"'text/css\"' rel=\"'Stylesheet\"' href=\"'merged/cx.css\"' media=\"'all\"' />"+
			"\t\t"+(style != null ? "<link type='text/css' rel='Stylesheet' href='cx_custom/"+style+".css' media='all' />" : "")+""+
			"\t\t<!--[if gte IE 9]><!-->"+
			"\t\t\t<script src=\"'merged/jquery-2.0.3.min.js\"'></script>"+
			"\t\t<!--<![endif]-->"+
			"\t\t<!--[if lt IE 9]>"+
			"\t\t\t<script src=\"'merged/jquery-1.10.2.min.js\"'></script>"+
			"\t\t\t<script src=\"'http://ie7-js.googlecode.com/svn/version/2.1(beta4)/IE9.js\"'></script>"+
			"\t\t\t<script src=\"'merged/oldIE.js\"'></script>"+
			"\t\t<![endif]-->"+
			"\t\t<script type=\"'text/JavaScript\"' src=\"'merged/base.js\"'></script>"+
			"\t\t<script type=\"'text/javascript\"' src=\"'/_ah/channel/jsapi\"'></script>"+
			"\t\t<script type=\"'text/JavaScript\"' src=\"'merged/cx.js\"'></script>"+
			"\t\t"+(debugUUID == null ? "" : "<script src='http://jsconsole.com/remote.js?"+debugUUID+"'></script>")+""+
			"\t\t<script type=\"'text/javascript\"'>"+
			"\t\t\tif("+(errMsg)+" == null) {"+
			"\t\t\t\tsocket.init(\"'"+(token)+"\"');"+
			"\t\t\t\tmvc.init(\"'"+(mobileKey)+"\"', \"'"+(clientID)+"\"', "+(items)+", "+(split)+", {}, null, \"'"+(email)+"\"', "+(cards)+", false, false);"+
			"\t\t\t} else"+
			"\t\t\t\tmvc.err("+(errMsg)+");"+
			"\t\t</script>"+
			"\t</head>"+
			"\t<body>"+
			"\t\t"+(header)+""+
			"\t\t"+(askSplit)+""+
			"\t\t"+(splitPage)+""+
			"\t\t"+(receipt)+""+
			"\t\t"+(login)+""+
			"\t\t"+(pay)+""+
			"\t\t"+(feedback)+""+
			"\t\t"+(footer)+""+
			"\t\t"+(noJS)+""+
			"\t</body>"+
			"</html>";
	}
}