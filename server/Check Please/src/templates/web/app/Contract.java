package templates.web.app;

public class Contract {
	public static String run(Object content, boolean readonly) {
		return	"<div class=\"contract\">"+
			"\t<div class=\"content\">"+(content)+"</div>"+
			"\t<div class=\"buttons\">"+
			"\t\t"+(readonly ?
			"\t\t\t<a class=\"confirm\">Done</a>"+
			"\t\t":
			"\t\t\t<a class=\"confirm\">Agree</a>"+
			"\t\t\t<a class=\"disagree\">Disagree</a>"+
			"\t\t")+
			"\t</div>"+
			"</div>";
	}
}