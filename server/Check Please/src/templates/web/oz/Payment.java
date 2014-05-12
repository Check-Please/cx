package templates.web.oz;

public class Payment {
	public static String run(Object cID, Object status, Object msgKey, boolean focus, boolean notification, Object pan, Object name, Object exprMonth, Object exprYear, Object cvv, Object xxxx_lastFour) {
		return	"<li class=\'payment "+(status)+(focus ? " focus" : "")+(notification ? " notification" : "")+(msgKey != null ? " "+msgKey : "")+"\' cID=\""+(cID)+"\">"+
			"\t<a class=\'title\'>"+(name)+" ("+(xxxx_lastFour)+")</a>"+
			"\t<div class=\"card-info\">"+
			"\t\t<p class=\"name\"><strong>Name:</strong> "+(name)+"</p>"+
			"\t\t<p class=\"pan\" ><strong>PAN: </strong> "+(pan)+"</p>\t\t"+
			"\t\t<p class=\"expr\"><strong>Expr:</strong> "+(exprMonth)+"/"+(exprYear)+"</p>"+
			"\t\t<p class=\"cvv\" ><strong>CVV: </strong> "+(cvv)+"</p>"+
			"\t</div>"+
			"\t<div class=\"messages\">"+
			"\t\t<a class=\"success\">Success</a>"+
			"\t\t<br />"+
			
			"\t\t<a class=\"fail\" msg=\"limit\"\t\t>Limit</a>"+
			"\t\t<a class=\"fail\" msg=\"invalid\"\t>Invalid</a>"+
			"\t\t<a class=\"fail\" msg=\"reject\"\t>Reject</a>"+
			
			"\t\t<a class=\"fail\" msg=\"clear\"\t>Paid</a>"+
			"\t\t<a class=\"fail\" msg=\"tech\"\t>Tech</a>"+
			"\t\t<a class=\"fail\" msg=\"other\"\t>Other</a>"+
			
			"\t\t<a class=\"update\" msg=\"enqueue\"\t\t>Seen</a>"+
			"\t\t<a class=\"update\" msg=\"terminal\"\t>Terminal</a>"+
			"\t\t<a class=\"update\" msg=\"processing\"\t>Processing</a>"+
			"\t</div>"+
			"</li>";
	}
}