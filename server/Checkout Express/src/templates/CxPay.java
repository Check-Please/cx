package templates;

public class CxPay {
	public static String run(Object username, Object cards) {
		return	"<div id=\"'pay-page\"'>"+
			"\t<h1>Payment Information <img class=\"'ssl\"' /></h1>"+
			"\t<div class=\"'login\"'>"+
			"\t\tLogged in as <span class=\"'username\"'>"+(username)+"</span>"+
			"\t\t<a>Change login</a>"+
			"\t</div>"+
			"\tPick Credit Card:"+
			"\t<select class=\"'cards\"'>"+
			"\t\t"+(cards)+""+
			"\t\t<option>New Credit Card</option>"+
			"\t</select>"+
			""+
			"\t<form class=\"'new-cc\"'>"+
			"\t\t<div class=\"'pan\"'>"+
			"\t\t\t<label>Credit Card Number</label>"+
			"\t\t\t<input type=\"'text\"' />"+
			"\t\t</div>"+
			"\t\t<div class=\"'name\"'>"+
			"\t\t\t<label>Name on card</label>"+
			"\t\t\t<input type=\"'text\"' class=\"'name\"'  maxlength=\"'26\"' />"+
			"\t\t</div>"+
			"\t\t<div class=\"'expr-month\"'>"+
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
			"\t\t<div class=\"'expr-year\"'>"+
			"\t\t\t<label>Year</label>"+
			"\t\t\t<input type=\"'text\"' maxlength=\"'4\"' />"+
			"\t\t</div>"+
			"\t\t<div class=\"'cvv\"'>"+
			"\t\t\t<label>Security Code</label>"+
			"\t\t\t<input type=\"'text\"' />"+
			"\t\t\t<div class=\"'explanation\"'>"+
			"\t\t\t\t<a>(what\'s this?)</a>"+
			"\t\t\t\t<div class=\"'popup\"'>"+
			"\t\t\t\t\t<div>"+
			"\t\t\t\t\t\t<a>&times;</a>"+
			"\t\t\t\t\t\tYour security code can be found on your card"+
			"\t\t\t\t\t\t<img src=\"'img/CSC_en_US.png\"' />"+
			"\t\t\t\t\t</div>"+
			"\t\t\t\t</div>"+
			"\t\t\t</div>"+
			"\t\t</div>"+
			"\t\t<div class=\"'zip\"'>"+
			"\t\t\t<label>Zip Code</label>"+
			"\t\t\t<input type=\"'text\"' maxlength=\"'5\"' />"+
			"\t\t</div>"+
			"\t</form>"+
			"\t<a class=\"'confirm\"'>Pay</a>"+
			"\t<p class=\"'security-notice\"'>Credit card storage and payments handled by SubtleData, Inc.</p>"+
			"</div>";
	}
}