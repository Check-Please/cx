package templates.web.app;

public class Err__reqUpdate {
	public static String run(Object howToUpdate) {
		return	"<div id=\"err\">"+
			"\t<h1>Update Required</h1>"+
			"\t<p>An update is required in order to communicate with the server.  Please "+(howToUpdate)+"</p>"+
			"</div>";
	}
}