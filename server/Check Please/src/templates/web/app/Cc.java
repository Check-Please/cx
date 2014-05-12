package templates.web.app;

public class Cc {
	public static String run(Object dbKey, Object partialNum, boolean noPass) {
		return	"<option db_key="+(dbKey)+(noPass ? " noPass" : "")+">"+(partialNum)+"</option>";
	}
}