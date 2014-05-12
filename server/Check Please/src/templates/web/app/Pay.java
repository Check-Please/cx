package templates.web.app;

public class Pay {
	public static String run(Object cards, boolean allowPassword, boolean allowNoPassword, Object cvv) {
		return	"<div id=\"pay\">"+
			"\t<h1>Payment Information <img class=\"ssl\" /></h1>"+
			"\tPick Credit Card:"+
			"\t<select class=\"cards\">"+
			"\t\t"+(cards)+
			"\t\t<option>New Credit Card</option>"+
			"\t</select>"+
			"\t<a class=\"delete\">Delete Card</a>"+
			
			"\t<form class=\"unencrypted-cc\">"+
			"\t\t"+(cvv)+
			"\t</form>"+
			
			"\t<form class=\"encrypted-cc\">"+
			"\t\t<div class=\"password\">"+
			"\t\t\t<label>Password</label>"+
			"\t\t\t<input type=\"password\" />"+
			"\t\t</div>"+
			"\t\t"+(cvv)+
			"\t</form>"+
			
			"\t<form class=\"new-cc\">"+
			"\t\t<div class=\"pan\">"+
			"\t\t\t<label>Credit Card Number</label>"+
			"\t\t\t<input type=\"text\"  maxlength=\"19\" />"+
			"\t\t</div>"+
			"\t\t<div class=\"name\">"+
			"\t\t\t<label>Name on card</label>"+
			"\t\t\t<input type=\"text\" class=\"name\"  maxlength=\"26\" />"+
			"\t\t</div>"+
			"\t\t<div class=\"expr-month\">"+
			"\t\t\t<label>Month</label>"+
			"\t\t\t<select>"+
			"\t\t\t\t<option>01 - January</option>"+
			"\t\t\t\t<option>02 - February</option>"+
			"\t\t\t\t<option>03 - March</option>"+
			"\t\t\t\t<option>04 - April</option>"+
			"\t\t\t\t<option>05 - May</option>"+
			"\t\t\t\t<option>06 - June</option>"+
			"\t\t\t\t<option>07 - July</option>"+
			"\t\t\t\t<option>08 - August</option>"+
			"\t\t\t\t<option>09 - September</option>"+
			"\t\t\t\t<option>10 - October</option>"+
			"\t\t\t\t<option>11 - November</option>"+
			"\t\t\t\t<option>12 - December</option>"+
			"\t\t\t</select>"+
			"\t\t</div>"+
			"\t\t<div class=\"expr-year\">"+
			"\t\t\t<label>Year</label>"+
			"\t\t\t<input type=\"text\" maxlength=\"4\" />"+
			"\t\t</div>"+
			"\t\t"+(cvv)+
			"\t\t<div class=\"zip\">"+
			"\t\t\t<label>Zip Code</label>"+
			"\t\t\t<input type=\"text\" maxlength=\"5\" />"+
			"\t\t</div>"+
			"\t\t<div class=\"save\">"+
			"\t\t\t<span>"+
			"\t\t\t\t<input type=\"checkbox\" />"+
			"\t\t\t\tSave your credit card for future use"+
			"\t\t\t</span>"+
			"\t\t\t<a>(Security FAQ)</a>"+
			"\t\t</div>"+
			"\t\t"+(allowPassword ?
			"\t\t\t"+(allowNoPassword ?
			"\t\t\t\t<div class=\"req-password\">"+
			"\t\t\t\t\t<span>"+
			"\t\t\t\t\t\t<input type=\"checkbox\" />"+
			"\t\t\t\t\t\tRequire a password to access credit card"+
			"\t\t\t\t\t</span>"+
			"\t\t\t\t</div>"+
			"\t\t\t":"")+
			"\t\t\t<div class=\"password\">"+
			"\t\t\t\t<label>Password</label>"+
			"\t\t\t\t<input type=\"password\" />"+
			"\t\t\t\t<span></span>"+
			"\t\t\t</div>"+
			"\t\t":"")+
			"\t</form>"+
			"\t<p class=\"ccPolicy\">My clicking \"Pay\", you agree to our <a>Credit Card Policy</a></p>"+
			"\t<a class=\"confirm\">Pay</a>"+
			"</div>";
	}
}