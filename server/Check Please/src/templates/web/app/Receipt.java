package templates.web.app;

public class Receipt {
	public static String run(Object restrName, Object logo, Object street_address, Object city_address, Object items, Object splitText, Object server) {
		return	"<div id=\"receipt\">"+
			"\t<div class=\'receipt "+(logo != null ? "has-logo" : "")+"\'>"+
			"\t\t<div class=\"header\"></div>"+
			"\t\t"+(logo == null ? "" :
			"\t\t\t<img class=\'title\' src=\'"+(server)+"/cx/receiptImg.png?"+(logo)+"\' />"+
			"\t\t")+
			"\t\t<h2 class=\'title\'>"+(restrName)+"</h2>"+
			"\t\t<h3 class=\"street address\">"+(street_address)+"</h3>"+
			"\t\t"+(city_address == null ? "" :
			"\t\t\t<h3 class=\'city address\'>"+(city_address)+"</h3>"+
			"\t\t")+
			"\t\t<ul class=\"items\">"+(items)+"</ul>"+
			"\t\t<div class=\"footer\"></div>"+
			"\t</div>"+
			"\t<div><a class=\"split\" href=\"#split\">"+(splitText)+"</a></div>"+
			"\t<div class=\"tip\">"+
			"\t\t<div class=\"buttons\">"+
			"\t\t\tTip:"+
			"\t\t\t<a class=\"seventeen percent\">17%</a>"+
			"\t\t\t<a class=\"twenty percent\">20%</a>"+
			"\t\t\t<a class=\"twentyfive percent\">25%</a>"+
			"\t\t\t<a class=\"other percent\">Other</a>"+
			"\t\t</div>"+
			"\t\t<div class=\"input\">"+
			"\t\t\tAmount: $<input type=\'text\' />"+
			"\t\t\t<span class=\"tipPrct\"></span>"+
			"\t\t</div>"+
			"\t</div>"+
			"\t<a class=\"confirm\">Confirm</a>"+
			"</div>";
	}
}