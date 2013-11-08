package templates;

public class CxHeader {
	public static String run(Object name, Object logo) {
		return	"<div id=\"'header\"' "+(logo != null ? "class='has-logo'" : "")+">"+
			"\t"+(logo != null ? "<img src='cx_custom/"+logo+"_header.png' />" : "")+""+
			"\t<span>"+(name)+"</span>"+
			"</div>";
	}
}