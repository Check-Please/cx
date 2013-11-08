var template = template || {};

template.cx = function(errMsg, title, style, email, token, mobileKey, clientID, items, split, cards, header, askSplit, splitPage, receipt, login, pay, feedback, footer, noJS, debugUUID) {
	return	"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\r\n"+
			"\t\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n"+
			"\r\n"+
			"<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n"+
			"\t<head>\r\n"+
			"\t\t<title>Pay Ticket - "+(title)+"</title>\r\n"+
			"\t\t<meta charset=\"UTF-8\" />\r\n"+
			"\t\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\" />\r\n"+
			"\t\t<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />\r\n"+
			"\t\t<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black-translucent\" />\r\n"+
			"\t\t<meta name=\"HandheldFriendly\" content=\"true\" />\r\n"+
			"\t\t<meta name=\"MobileOptimized\" content=\"width\" />\r\n"+
			"\t\t<link type=\"text/css\" rel=\"Stylesheet\" href=\"merged/cx.css\" media=\"all\" />\r\n"+
			"\t\t"+(style != null ? "<link type='text/css' rel='Stylesheet' href='cx_custom/"+style+".css' media='all' />" : "")+"\r\n"+
			"\t\t<!--[if gte IE 9]><!-->\r\n"+
			"\t\t\t<script src=\"merged/jquery-2.0.3.min.js\"></script>\r\n"+
			"\t\t<!--<![endif]-->\r\n"+
			"\t\t<!--[if lt IE 9]>\r\n"+
			"\t\t\t<script src=\"merged/jquery-1.10.2.min.js\"></script>\r\n"+
			"\t\t\t<script src=\"http://ie7-js.googlecode.com/svn/version/2.1(beta4)/IE9.js\"></script>\r\n"+
			"\t\t\t<script src=\"merged/oldIE.js\"></script>\r\n"+
			"\t\t<![endif]-->\r\n"+
			"\t\t<script type=\"text/JavaScript\" src=\"merged/base.js\"></script>\r\n"+
			"\t\t<script type=\"text/javascript\" src=\"/_ah/channel/jsapi\"></script>\r\n"+
			"\t\t<script type=\"text/JavaScript\" src=\"merged/cx.js\"></script>\r\n"+
			"\t\t"+(debugUUID == null ? "" : "<script src='http://jsconsole.com/remote.js?"+debugUUID+"'></script>")+"\r\n"+
			"\t\t<script type=\"text/javascript\">\r\n"+
			"\t\t\tif("+(errMsg)+" == null) {\r\n"+
			"\t\t\t\tsocket.init(\""+(token)+"\");\r\n"+
			"\t\t\t\tmvc.init(\""+(mobileKey)+"\", \""+(clientID)+"\", "+(items)+", "+(split)+", {}, null, \""+(email)+"\", "+(cards)+", false, false);\r\n"+
			"\t\t\t} else\r\n"+
			"\t\t\t\tmvc.err("+(errMsg)+");\r\n"+
			"\t\t</script>\r\n"+
			"\t</head>\r\n"+
			"\t<body>\r\n"+
			"\t\t"+(header)+"\r\n"+
			"\t\t"+(askSplit)+"\r\n"+
			"\t\t"+(splitPage)+"\r\n"+
			"\t\t"+(receipt)+"\r\n"+
			"\t\t"+(login)+"\r\n"+
			"\t\t"+(pay)+"\r\n"+
			"\t\t"+(feedback)+"\r\n"+
			"\t\t"+(footer)+"\r\n"+
			"\t\t"+(noJS)+"\r\n"+
			"\t</body>\r\n"+
			"</html>";
};