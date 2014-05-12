package templates.web.oz;

public class Ticket {
	public static String run(Object tKey) {
		return	"<div class=\"ticket\" tKey=\""+(tKey)+"\">"+
			"\t<div class=\"input\">"+
			"\t\t<h1>Input"+
			"\t\t\t<button class=\"clear\">Clear</button>"+
			"\t\t</h1>"+
			"\t\t<div class=\"items\"></div>"+
			"\t\t<div class=\"new item\">"+
			"\t\t\t<a>+</a> <label>New Item</label>"+
			"\t\t</div>"+
			"\t\t<button class=\"update\">Update Server</button>"+
			"\t</div>"+
			"\t<div class=\"info\">"+
			"\t\t<h1>Items<span></span></h1>"+
			"\t\t<ul class=\"items\"></ul>"+
			"\t\t<h2>Summary</h2>"+
			"\t\t<p class=\"subtotal\"><strong>Subtotal:</strong>\t<span></span></p>"+
			"\t\t<p class=\"tax\">\t\t<strong>Tax:</strong>\t\t<span></span></p>"+
			"\t\t<p class=\"service\">\t<strong>Service:</strong>\t<span></span></p>"+
			"\t\t<p class=\"discount\"><strong>Discount:</strong>\t<span></span></p>"+
			"\t\t<p class=\"tip\">\t\t<strong>Tip:</strong>\t\t<span></span></p>"+
			"\t\t<p class=\"total\">\t<strong>Total:</strong>\t\t<span></span></p>"+
			"\t</div>"+
			"\t<div class=\"payments\">"+
			"\t\t<h1>Payments</h1>"+
			"\t\t<ul></ul>"+
			"\t</div>"+
			"</div>";
	}
}