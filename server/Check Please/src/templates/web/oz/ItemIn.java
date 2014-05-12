package templates.web.oz;

public class ItemIn {
	public static String run(Object id, int orderDate, Object shorthand) {
		return	"<div class=\"item\" itemID=\""+(id)+"\" orderDate="+(orderDate)+">"+
			"\t<a>&times;</a> <input type=\"text\" value=\""+(shorthand)+"\" />"+
			"</div>";
	}
}