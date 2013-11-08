package templates;

public class ResetPassword {
	public static String run() {
		return	"<!DOCTYPE html PUBLIC \"'-//W3C//DTD XHTML 1.0 Strict//EN\"'"+
			"\t\t\"'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"'>"+
			""+
			"<html xmlns=\"'http://www.w3.org/1999/xhtml\"'>"+
			"\t<head>"+
			"\t\t<title>Reset Password</title>"+
			"\t\t<meta name=\"'viewport\"' content=\"'width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\"'>"+
			"\t\t<meta name=\"'apple-mobile-web-app-capable\"' content=\"'yes\"'>"+
			"\t\t<meta name=\"'apple-mobile-web-app-status-bar-style\"' content=\"'black-translucent\"'>"+
			"\t\t<meta name=\"'HandheldFriendly\"' content=\"'true\"'>"+
			"\t\t<meta name=\"'MobileOptimized\"' content=\"'width\"'>"+
			"\t\t<link type=\"'text/css\"' rel=\"'Stylesheet\"' href=\"'merged/resetPassword.css\"' media=\"'all\"' />"+
			"\t\t<script type=\"'text/JavaScript\"' src=\"'merged/resetPassword.js\"'></script>"+
			"\t</head>"+
			"\t<body onload=\"'resetPassword.init()\"'>"+
			"\t\t<div id=\"'header\"'>"+
			"\t\t\t<img src=\"'img/cx.png\"'>"+
			"\t\t</div>"+
			"\t\t<div class=\"'password\"'>"+
			"\t\t\t<label>Password:</label>"+
			"\t\t\t<input type=\"'password\"' />"+
			"\t\t</div>"+
			"\t\t<div class=\"'confirm-password\"'>"+
			"\t\t\t<label>Confirm Password:</label>"+
			"\t\t\t<input type=\"'password\"' />"+
			"\t\t</div>"+
			"\t\t<a>Change Password</a>"+
			"\t</body>"+
			"</html>";
	}
}