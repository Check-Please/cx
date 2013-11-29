var oldIE = oldIE || false;

function randStr()
{
	return Math.random().toString(36).substr(2); 
}

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

var money = {
	round: function(price)
	{
		"use strict";
		return Math.ceil(price-0.00001);
	},

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

if(!String.prototype.endsWith)
	String.prototype.endsWith = function(suffix) {
		return this.indexOf(suffix, this.length-suffix.length) !== -1;
	};

if(!Array.toArray)
	Array.toArray = function(x) {return Array.prototype.slice.call(x, 0);};

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

if(!window.parseBool)
	window.parseBool = function(s) {
		s = s.toUpperCase().charAt(0);
		return	((s == "T") || (s == "1")) ? true :
				((s == "F") || (s == "0")) ? false :
				null;
	}

if(!window.isEmail)
	window.isEmail = function(email) {
		//RFC822 spec,
		//Source: http://maximeparmentier.com/2012/04/09/javascript-rfc2822-email-validation/
		return /^([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x22([^\x0d\x22\x5c\x80-\xff]|\x5c[\x00-\x7f])*\x22)(\x2e([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x22([^\x0d\x22\x5c\x80-\xff]|\x5c[\x00-\x7f])*\x22))*\x40([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x5b([^\x0d\x5b-\x5d\x80-\xff]|\x5c[\x00-\x7f])*\x5d)(\x2e([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x5b([^\x0d\x5b-\x5d\x80-\xff]|\x5c[\x00-\x7f])*\x5d))*$/.test(email);
	}

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
