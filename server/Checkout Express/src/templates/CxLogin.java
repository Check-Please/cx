package templates;

public class CxLogin {
	public static String run(boolean login, Object terms_of_use, Object privacy_policy) {
		return	"<div id=\"'login-page\"'>"+
			"\t<h1>Login or Register</h1>"+
			"\t<form class=\"'lor\"'>"+
			"\t\t<span class=\"'login\"'>"+
			"\t\t\t<input type=\"'radio\"' name=\"'lor\"' checked=\"'on\"' /> Login"+
			"\t\t</span>"+
			"\t\t<span class=\"'register\"'>"+
			"\t\t\t<input type=\"'radio\"' name=\"'lor\"' /> Register"+
			"\t\t</span>"+
			"\t</form>"+
			""+
			"\t<form class=\"'info "+(login ? "login" : "register")+"\"'>"+
			"\t\t<div class=\"'email\"'>"+
			"\t\t\t<label>Email:</label>"+
			"\t\t\t<input type=\"'text\"' />"+
			"\t\t</div>"+
			"\t\t<div class=\"'password\"'>"+
			"\t\t\t<label>Password:</label>"+
			"\t\t\t<input type=\"'password\"' />"+
			"\t\t</div>"+
			"\t\t<a class=\"'forgot-password\"'>Forgot your password?</a>"+
			"\t\t<div class=\"'confirm-password\"'>"+
			"\t\t\t<label>Confirm Password:</label>"+
			"\t\t\t<input type=\"'password\"' />"+
			"\t\t</div>"+
			"\t\t<div class=\"'agreements\"'>"+
			"\t\t\tBy registering, you agree to our <a class=\"'tou\"'>Terms of Use</a> and <a class=\"'pp\"'>Privacy Policy</a>"+
			"\t\t\t<div class=\"'tou popup\"'>"+
			"\t\t\t\t<div>"+
			"\t\t\t\t\t<div class=\"'text\"'>"+(terms_of_use)+"</div>"+
			"\t\t\t\t\t<a class=\"'agree\"'>I Agree</a>"+
			"\t\t\t\t\t<a class=\"'disagree\"'>I do not agree</a>"+
			"\t\t\t\t</div>"+
			"\t\t\t</div>"+
			"\t\t\t<div class=\"'pp popup\"'>"+
			"\t\t\t\t<div>"+
			"\t\t\t\t\t<div class=\"'text\"'>"+(privacy_policy)+"</div>"+
			"\t\t\t\t\t<a class=\"'agree\"'>I Agree</a>"+
			"\t\t\t\t\t<a class=\"'disagree\"'>I do not agree</a>"+
			"\t\t\t\t</div>"+
			"\t\t\t</div>"+
			"\t\t</div>"+
			"\t</form>"+
			""+
			"\t<a class=\"'confirm\"'>"+(login ? "Login" : "Register")+"</a>"+
			"</div>";
	}
}