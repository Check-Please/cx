var template = template || {};

template.cxLogin = function(login, terms_of_use, privacy_policy) {
	return	"<div id=\"login-page\">\r\n"+
			"\t<h1>Login or Register</h1>\r\n"+
			"\t<form class=\"lor\">\r\n"+
			"\t\t<span class=\"login\">\r\n"+
			"\t\t\t<input type=\"radio\" name=\"lor\" checked=\"on\" /> Login\r\n"+
			"\t\t</span>\r\n"+
			"\t\t<span class=\"register\">\r\n"+
			"\t\t\t<input type=\"radio\" name=\"lor\" /> Register\r\n"+
			"\t\t</span>\r\n"+
			"\t</form>\r\n"+
			"\r\n"+
			"\t<form class=\"info "+(login ? "login" : "register")+"\">\r\n"+
			"\t\t<div class=\"email\">\r\n"+
			"\t\t\t<label>Email:</label>\r\n"+
			"\t\t\t<input type=\"text\" />\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<div class=\"password\">\r\n"+
			"\t\t\t<label>Password:</label>\r\n"+
			"\t\t\t<input type=\"password\" />\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<a class=\"forgot-password\">Forgot your password?</a>\r\n"+
			"\t\t<div class=\"confirm-password\">\r\n"+
			"\t\t\t<label>Confirm Password:</label>\r\n"+
			"\t\t\t<input type=\"password\" />\r\n"+
			"\t\t</div>\r\n"+
			"\t\t<div class=\"agreements\">\r\n"+
			"\t\t\tBy registering, you agree to our <a class=\"tou\">Terms of Use</a> and <a class=\"pp\">Privacy Policy</a>\r\n"+
			"\t\t\t<div class=\"tou popup\">\r\n"+
			"\t\t\t\t<div>\r\n"+
			"\t\t\t\t\t<div class=\"text\">"+(terms_of_use)+"</div>\r\n"+
			"\t\t\t\t\t<a class=\"agree\">I Agree</a>\r\n"+
			"\t\t\t\t\t<a class=\"disagree\">I do not agree</a>\r\n"+
			"\t\t\t\t</div>\r\n"+
			"\t\t\t</div>\r\n"+
			"\t\t\t<div class=\"pp popup\">\r\n"+
			"\t\t\t\t<div>\r\n"+
			"\t\t\t\t\t<div class=\"text\">"+(privacy_policy)+"</div>\r\n"+
			"\t\t\t\t\t<a class=\"agree\">I Agree</a>\r\n"+
			"\t\t\t\t\t<a class=\"disagree\">I do not agree</a>\r\n"+
			"\t\t\t\t</div>\r\n"+
			"\t\t\t</div>\r\n"+
			"\t\t</div>\r\n"+
			"\t</form>\r\n"+
			"\r\n"+
			"\t<a class=\"confirm\">"+(login ? "Login" : "Register")+"</a>\r\n"+
			"</div>";
};