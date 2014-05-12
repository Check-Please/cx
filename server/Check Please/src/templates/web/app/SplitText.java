package templates.web.app;

public class SplitText {
	public static String run(boolean splitStarted) {
		return	""+
				(splitStarted ? "Click here to change the way the bill is being split" : "Click here to split the bill")+
			"";
	}
}