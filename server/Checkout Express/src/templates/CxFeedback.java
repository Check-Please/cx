package templates;

public class CxFeedback {
	public static String run() {
		return	"<div id=\"'feedback-page\"'>"+
			"\t<h1>Thank you!</h1>"+
			"\t<div class=\"'feedback\"'>"+
			"\t\t<h3>Rate your experience:</h3>"+
			"\t\t<div class=\"'ratings\"'>"+
			"\t\t\t<a class=\"'bad\"'><img src=\"'img/thumbs-down.png\"' /></a>"+
			"\t\t\t<a class=\"'ok\"'>OK</a>"+
			"\t\t\t<a class=\"'just good\"'><img src=\"'img/thumbs-up.png\"' /></a>"+
			"\t\t\t<a class=\"'very good\"'><img src=\"'img/thumbs-up-up.png\"' /></a>"+
			"\t\t</div>"+
			"\t\t<!--p class=\"'comment-opener\"'>"+
			"\t\t\tClick <a>here</a> to leave a comment"+
			"\t\t\t<div id=\"'comment-wrapper\"'>"+
			"\t\t\t\t<div id=\"'comment\"'>"+
			"\t\t\t\t\t<div class=\"'bg\"'></div>"+
			"\t\t\t\t\t<div class=\"'ui\"'>"+
			"\t\t\t\t\t\t<h4>Please tell us how we did</h4>"+
			"\t\t\t\t\t\t<textarea></textarea>"+
			"\t\t\t\t\t\t<p class=\"'characters-remaining\"'></p>"+
			"\t\t\t\t\t\t<a class=\"'confirm\"'>Done</a>"+
			"\t\t\t\t\t</div>"+
			"\t\t\t\t</div>"+
			"\t\t\t</div>"+
			"\t\t</p-->"+
			"\t</div>"+
			"\t<div class=\"'self-promotion\"'>"+
			"\t\t<h3>Own a restaurant?</h3>"+
			"\t\t<p>Email <a href=\"'mailto:sjelin@chkex.com\"'>sjelin@chkex.com</a> to get this app at your restaurant</p>"+
			"\t</div>"+
			"</div>";
	}
}