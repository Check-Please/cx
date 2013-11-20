var oldIE = oldIE || false;

function serializeXML(xml)
{
	"use strict";
	if (window.XMLSerializer)
		return (new XMLSerializer()).serializeToString(xml);
	else
		return xml.xml;
}

jQuery.fn.innerXML = function()
{
	"use strict";
	var sXML = serializeXML($(this).get(0));
	var startI = sXML.indexOf(">")+1;
	var endI = sXML.lastIndexOf("<");
	return sXML.substr(startI, endI-startI);
}

function ajax(method, url, rawData, callback, failFunc, boringUpdate)
{
	"use strict";

	//Format data
	function addArray(array, name, target)
	{
		for(var i = 0; i < array.length; i++) {
			var val = array[i];
			if(Array.isArray(val))
				addArray(val, name+"_"+i, target);
			else if(val != null)
				target[name+"_"+i] = val;
		}
		target[name+"_"] = array.length;
	}

	var data = undefined;
	if(rawData != null) {
		data = {};
		for(var key in rawData) {
			var val = rawData[key];
			if(val != null) {
				if(Array.isArray(val))
					addArray(val, key, data);
				else
					data[key] = val;
			}
		}
	}

	//Actually make request
	var xmlhttp = window.XMLHttpRequest ? new XMLHttpRequest() :
						new ActiveXObject("Microsoft.XMLHTTP");
	if((callback!=null) || (failFunc!=null) || (boringUpdate!=null)) {
		//There is an odd bug in ajax (for only chrome?) where the ready
		//states are not always 1,2,3,4.  Instead, they are AT LEAST 1,2,3,4
		//on the 1st, 2nd, 3rd, 4th call, respectively.  I suspect this is
		//some race condition.  Normally 4 only shows up on the forth call
		//though (why? I don't know). However, when using breakpoints (in
		//only chrome?), this is not the case, and I've gotten 4 to show up
		//up to three times.  This is a problem, because it means the
		//callback function gets called multiple times.  Even though break
		//points shouldn't exist in production, it makes debugging annoying,
		//and since the bug is not understood some sort of solution needs to
		//be in place.  In this case, what we're doing is having an extra
		//variable keep track of if we've seen state 4 yet, and sending extra
		//state 4s onto the boring update function.
		var stateFourSeen = false;
		xmlhttp.onreadystatechange = function ()
		{
			if((xmlhttp.readyState == 4) && !stateFourSeen) {
				stateFourSeen = true;
				if(xmlhttp.status == 200) {
					if(callback != null)
						callback(xmlhttp.responseText, xmlhttp);
				} else if(failFunc != null) {
					var msg = xmlhttp.statusText;
					if(msg.substr(0,3) == "404")
						msg = msg.substr(3);
					msg = msg.trim();
					failFunc(xmlhttp.status, xmlhttp.statusText,
							xmlhttp.responseText, xmlhttp);
				}
			} else if(boringUpdate != null) {
				boringUpdate(xmlhttp.readyState, xmlhttp);
			}
		}
	}
	method = method.toUpperCase();
	if((method == "GET") && (data != null)) {
		url += "?"+$.param(data);
		data = null;
	}
	xmlhttp.open(method, url, callback != null);
	if(data != null) {
		data = data instanceof Object ? $.param(data) : data;
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");
	}
	xmlhttp.send(data);
	return xmlhttp;
}
ajax.get = ajax.c("GET");
ajax.post = ajax.c("POST");
ajax.receive = function(base, cmd, data, callback, failFun, boringUpdate)
{
	return ajax.get("/"+base+"/"+cmd,data,callback,failFun,boringUpdate);
}
ajax.send = function(base, cmd, data, callback, failFun, boringUpdate)
{
	return ajax.post("/"+base+"/"+cmd,data,callback,failFun,boringUpdate);
}

/**	Runs some asynchronous functions in sequence.
 *	
 *	@param	funs {Function[]} The functions to be executed.  Each function
 *			must take a callback function as it's first parameter and a
 *			failure function as it's second.  Each function should call its
 *			callback function exactly once.  If a function does not call its
 *			callback function then the squence of calls ends, and the final
 *			final callback function is never called.  If a function calls its
 *			callback function multiple times, then all future function will
 *			be called multiple times.
 *	@param	callback {Function} A callback final function, to be called upon
 *			the success of all the functions passed into this function
 *	@param	failfun {Function} The failure function.  Called if any of the
 *			functions fails.  Gets passed the index of the function which
 *			failed.
 */
function inSequence(funs, callback, failfun)
{
	function myARun(i) {
		if(i == funs.length)
			callback();
		else
			funs[i](myARun.c(i+1), failfun.c(i));
	}
	myARun(0);
}
/**	Runs some asynchronous functions in parallel.
 *	
 *	@param	funs {Function[]} The functions to be executed.  Each function
 *			must take a callback function as it's first parameter and a
 *			failure function as it's second.  Each function should call its
 *			callback function exactly once.  If a function does not call its
 *			callback function then the squence of calls ends, and the final
 *			final callback function is never called.  If a function calls its
 *			callback function multiple times, then all future function will
 *			be called multiple times.
 *	@param	callback {Function} A callback final function, to be called upon
 *			the success of all the functions passed into this function
 *	@param	failfun {Function} The failure function.  Called if any of the
 *			functions fails.  Gets passed the index of the function which
 *			failed.
 */
function inParallel(funs, callback, failFun)
{
	if(funs.length == 0) {
		callback([]);
	} else {
		var rets = new Array(funs.length);
		var finished = Array.tabulate(funs.length, op.id.c(false));
		function success(i) {
			rets[i] = Array.prototype.slice.call(arguments, 1);
			finished[i] = true;
			if(finished.reduce(op.and))
				callback.apply(this, rets);
		}
		for(var i = 0; i < funs.length; i++)
			funs[i](success.c(i), failFun.c(i));
	}
}

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

function buildAjaxErrFun(cmd, dontStopLoading)
{
	return function(code, _, msg) {
		if(!dontStopLoading)
			$(".loading").removeClass("loading");
		alert("Could not "+cmd+".  Reason:\n"+(code==404?"":code+" ")+msg);
	};
}

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
