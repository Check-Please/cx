var oldIE = oldIE || false;

/**	Generates a random string
 *
 *	@return A random string
 */
function randStr()
{
	return Math.random().toString(36).substr(2); 
}

/**	Turns numbers into orginal strings (e.g. 1 -> "1st")
 *	@param	The number to get an orginal string for
 *	@return	The ordinal string for the number
 */
function ordinalNumStr(i)
{
	switch(Math.abs(i)%100) {
		case 11:
		case 12:
		case 13: return i+"th";
		default: switch(Math.abs(i)%10) {
			case 1: return i+"st";
			case 2: return i+"nd";
			case 3: return i+"rd";
			default: return i+"th";
		}
	}
}

/** Some functions for dealing with moeny
 */
var money = {
	/**	Rounds prices
	 *	@param  The price, in cents
	 *	@return The price, rounded to cents
	 */
	round: function(price)
	{
		"use strict";
		return Math.ceil(price-0.00001);
	},

	/**	Runs a price into a string (e.g. 123 -> "$1.23")
	 *	@param	price The price in cents
	 *	@param	currency Defaults to "$"
	 *	@param	noRound If the price shouldn't be rounded
	 *	@return The price string
	 */
	toStr: function(price, currency, noRound)
	{
		"use strict";
		if(!noRound)
			price = money.round(price);
		var absCents = Math.abs(price);
		return	(price < 0 ? "-" : "") +
				(currency != null ? currency : "$") +
				absCents/100.0 +
				(absCents % 100 == 0 ? ".00" :
					(absCents % 10 == 0 ? "0" : ""));
	}
};

/**	Tools for dealing with credit cards
 *
 *	I ignore the edge cases of JCB cards starting with 2131 or 1800,
 *	as I wasn't able to get much information on them other than that they are
 *	15 digits long.  I also ignore 13 digit Visa cards for the same reason.
 *	Finally, I label all cards processed like MasterCard as MasterCard, even
 *	if they are branded as Diners Club.
 */
creditCards = (function() {
	"use strict";
	var types = {
		VISA: "visa",
		MASTERCARD: "mastercard",
		AMERICAN_EXPRESS: "amex",
		DINERS_CLUB: "diners",
		DISCOVER: "discover",
		JCB: "jcb"
	}
	types.AMEX = types.AMERICAN_EXPRESS;
	types.DINERS = types.DINERS_CLUB;
	function format(pan, type, sep) {
		sep = sep == undefined ? "-" : sep;
		type = type == undefined ? creditCards.getType(pan) : type;
		var sepIndices =	type == types.AMEX || type == types.DINERS ?
							[4, 10] : [4, 8, 12];
		pan += "";//Make sure it's a string
		for(var i = sepIndices.length-1; i >= 0; i--) {
			var j = sepIndices[i];
			if(j < pan.length)
				pan = pan.slice(0,j)+sep+pan.slice(j);
		}
		return pan;
	}
	function getType(pan) {
		switch(pan.substr(0,1)) {
			case "3": switch(pan.substr(1,1)) {
				case "4":
				case "7": return types.AMEX;
				case "0":
				case "6":
				case "8": return types.DINERS;
				case "5": return types.JCB;
				default: return undefined;
			}
			case "4": return types.VISA;
			case "5": return types.MASTERCARD;
			case "6": return types.DISCOVER;
			default: return undefined;
		}
	}
	function getYear(year) {
		year = ""+year;
		if(year.length == 4)
			return year;
		else if(year.length == 3)
			return "2"+year;
		else if(year.length == 2) {
			year = parseInt(year);
			var curr = new Date().getYear() + 1900;
			var cent = curr - (curr % 100);
			var early = year+cent-100;
			var med = year+cent;
			if(Math.abs(early-curr) < Math.abs(med-curr))
				return early;
			var late = ear+cent+100;
			if(Math.abs(late-curr) < Math.abs(med-curr))
				return late;
			return med;
		}
	}
	function validate(pan, name, exprMonth, exprYear, cvv, zip)
	{
		if(pan.length == 0)
			return "Please enter credit card number";
		else if(pan.length < 8)
			return "Credit card number too short";
		else if(pan.length > 19)
			return "Credit card number too long";
		else if(!pan.match(/^\d+$/))
			return "Credit card number must be a number";
		else if(pan.match(/^(?:62|88|2014|2149)/) || //Luhn
				pan.split('').reduce(function(sum, d, n) {
					return sum + parseInt((n%2)?d:[0,2,4,6,8,1,3,5,7,9][d]);
				}, 0) % 10 == 0)
			return "Invalid credit card number";
		else if(name.length == 0)
			return "Please enter card holder name";
		else if(name.length == 1)
			return "Card holder name too short";
		else if(name.length > 26)
			return "Card holder name too long";
		else if(exprYear.length == 0)
			return "Please enter expiration year";
		else if(exprYear.length == 1)
			return "Expiration year too short";
		else if(exprYear.length > 4)
			return "Expiration year too long";
		else if(!exprYear.match(/^\d+$/))
			return "Expiration year must be a number";
		else if(exprMonth.length == 0)
			return "Please enter expiration month";
		else if(!exprMonth.match(/^\d+$/))
			return "Expiration month number must be a number";
		else if(parseInt(exprMonth) > 12 || parseInt(exprMonth) == 0)
			return "Expiration month invalid";
		else if(cvv.length == 0)
			return "Please enter card security code";
		else if(cvv.length < 3)
			return "Card security code too short";
		else if(cvv.length > 4)
			return "Card security code too long";
		else if(!cvv.match(/^\d+$/))
			return "Card security code must be a number";
		else if(zip.length == 0)
			return "Please enter billing zip code";
		else if(zip.length != 5)
			return "Zip code should be five digits";
		else if(!zip.match(/^\d+$/))
			return "Zip code number must be a number";
	}
	return {types: types, format: format, getType: getType,
			getYear: getYear, validate: validate};
})();

/**	Checks if a string starts with a prefix
 *
 *	E.g.	"Hello, World!".startsWith("Hello") == true
 *			"Hello, World!".startsWith("World!") == false
 */
if(!String.prototype.startsWith)
	String.prototype.startsWith = function(pfx) {
		return this.substr(0, pfx.length) == pfx;
	};

/**	Checks if a string starts with a prefix
 *
 *	E.g.	"Hello, World!".startsWith("Hello") == false
 *			"Hello, World!".startsWith("Wolrd!") == true
 */
if(!String.prototype.endsWith)
	String.prototype.endsWith = function(sfx) {
		return this.substr(-sfx.length) == sfx;
	};

/**	Finds the GCD of two numbers
 *
 *	@param	a
 *	@param	b
 *	@return	The GCD of a & b
 */
if(!Math.gcd)
	Math.gcd = function(a,b)
	{
		if(a < 0) {a = -a;};
		if(b < 0) {b = -b;};
		if(b > a)
			return Math.gcd(b, a);
		while (true) {
			if(a == 0)
				return b;
			b %= a;
			if(b == 0)
				return a;
			a %= b;
		}
		return b;
	}

/** Parses a bool from a string to an actual bool
 *
 *	@param	s A string which represent a bool
 *	@return	The bool
 */
if(!window.parseBool)
	window.parseBool = function(s) {
		s = s.toUpperCase().charAt(0);
		return	((s == "T") || (s == "1")) ? true :
				((s == "F") || (s == "0")) ? false :
				null;
	}

/**	Determines if a string is a valid email address
 *	@param	email The string maybe is an email
 *	@return	true iff the string was an email address
 */
if(!window.isEmail)
	window.isEmail = function(email) {
		//RFC822 spec,
		//Source: http://maximeparmentier.com/2012/04/09/javascript-rfc2822-email-validation/
		return /^([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x22([^\x0d\x22\x5c\x80-\xff]|\x5c[\x00-\x7f])*\x22)(\x2e([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x22([^\x0d\x22\x5c\x80-\xff]|\x5c[\x00-\x7f])*\x22))*\x40([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x5b([^\x0d\x5b-\x5d\x80-\xff]|\x5c[\x00-\x7f])*\x5d)(\x2e([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x5b([^\x0d\x5b-\x5d\x80-\xff]|\x5c[\x00-\x7f])*\x5d))*$/.test(email);
	}

/**	Takes the hashcode of a string
 */
if(!String.prototype.hashCode)
	String.prototype.hashCode = function(){
		var hash = 0;
		var l = this.length;
		for(var i = 0; i < l; i++) {
			hash  = (hash<<5)-hash+this.charCodeAt(i);
			hash |= 0; // Convert to 32bit integer
		}
		return hash;
	};
