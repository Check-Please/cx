package templates.web.oz;

public class TickBtn {
	public static String run(Object tKey) {
		return	"<a class=\"tBtn\" tKey=\""+(tKey)+"\" href=\"#tick-"+(tKey)+"\">"+(tKey)+"</a>";
	}
}