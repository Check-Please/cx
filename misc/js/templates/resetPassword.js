var template = template || {};

template.resetPassword = function() {
	return	"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\r\n"+
			"\t\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n"+
			"\r\n"+
			"<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n"+
			"\t<head>\r\n"+
			"\t\t<title>Reset Password</title>\r\n"+
			"\t\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\">\r\n"+
			"\t\t<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">\r\n"+
			"\t\t<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black-translucent\">\r\n"+
			"\t\t<meta name=\"HandheldFriendly\" content=\"true\">\r\n"+
			"\t\t<meta name=\"MobileOptimized\" content=\"width\">\r\n"+
			"\t\t<link type=\"text/css\" rel=\"Stylesheet\" href=\"merged/resetPassword.css\" media=\"all\" />\r\n"+
			"\t\t<script type=\"text/JavaScript\" src=\"merged/resetPassword.js\"></script>\r\n"+
			"\t</head>\r\n"+
			"\t<body onload=\"resetPassword.init()\">\r\n"+
			"\t\t<div id=\"header\">\r\n"+
			"\t\t\t<img src=\"img/cx.png\">\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<div class=\"password\">\r\n"+
			"\t\t\t<label>Password:</label>\r\n"+
			"\t\t\t<input type=\"password\" />\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<div class=\"confirm-password\">\r\n"+
			"\t\t\t<label>Confirm Password:</label>\r\n"+
			"\t\t\t<input type=\"password\" />\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<a>Change Password</a>\r\n"+
			"\t</body>\r\n"+
			"</html>";
};