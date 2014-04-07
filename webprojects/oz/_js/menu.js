/**	This module define the code for turning ticket items back and forth to
 *	short strings.
 *
 *	The actual meanings of the numbers in the strings is defined in the
 *	items.js and mods.js files.  This file only defines the algorithms for
 *	using the content defined in those files.
 */

var menu = menu || {};

(function() {
	"use strict";

	function parse(code, book)
	{
		var j = code.indexOf(":");
		var num = j == -1 ? code : code.substr(0, j);
		var ret = {};
		if(num[0] == "$") {
			ret.price = money.round((parseFloat(num.substr(1))||0) * 10000);
			ret.tax = money.round(ret.price * 0.07);
		} else {
			$.extend(ret, book[parseInt(code)]);
		}
		if(j != -1)
			ret.name = code.substr(j+1).trim();
		return ret;
	}

	menu.parse = function(raw) {
		raw = raw.trim();
		var tokens = [];
		var i = 0;
		while(i < raw.length) {
			while(raw[i].trim().length == 0)
				i++;
			if(raw[i] == "{") {
				var j = raw.indexOf("}", i);
				if(j == -1)
					i = raw.length;
				else {
					var token = raw.substr(i+1, j-i-1);
					var obj = null;
					for(var k = token.length; obj == null && k > 0; k--) try{
						obj = eval("(function(){var x={"+token.substr(0, k)+
									"}; return x;})()");
					} catch(e) {};
					if(obj == null)
						obj = new Object();
					tokens.push(obj);
					i = raw.indexOf(",", j)+1 || raw.length;
				}
			} else {
				j = raw.indexOf(",", i);
				if(j == -1)
					j = raw.length;	
				tokens.push(raw.substr(i, j-i).trim());
				i = j+1;
			}
		}
		if(tokens.length == 0)
			return null;

		var item = tokens[0];
		if(typeof item == "string")
			item = parse(item, menu.items);
		item.mods = [];
		for(var i = 1; i < tokens.length; i++) {
			var mod = tokens[i];
			if(typeof mod == "string")
				mod = parse(mod, menu.mods);
			if(mod != null)
				item.mods.push(mod);
		}
		return item;
	}

	function makeKey(x) {
		return x.name+(x.price?","+x.price:"")+(x.tax?"+"+x.tax:"")+
						(x.discount?"-"+x.discount:"");
	}
	var lookup = {};

	function jqEscape(s)
	{
		var $x = $("<x>");
		var $y = $("<y>");
		$y.attr("v", s);
		$x.append($y);
		var s = $x.html();
		return s.substr(6, s.length-12);
	}

	menu.unparse = function(item) {
		if((item == null) || (typeof item != "object"))
			return null;
		item = $.extend({}, item);
		var mods = item.mods;
		delete item.mods;
		delete item.id;
		delete item.orderDate;
		var str = lookup[makeKey(item)];
		if(str == null)
			str = jqEscape(JSON.stringify(item));
		if(mods != null)
			for(var i = 0; i < mods.length; i++) {
				var mod = lookup[makeKey(mods[i])];
				if(mod == null)
					mod = jqEscape(JSON.stringify(mods[i]));
				str += "," + mod;
			}
		return str;
	}

	for(var i in menu.items)
		if((typeof menu.items[i] == "object") && (menu.items[i] != null))
			lookup[makeKey(menu.items[i])] = i;
	for(var i in menu.mods)
		if((typeof menu.mods[i] == "object") && (menu.mods[i] != null))
			lookup[makeKey(menu.mods[i])] = i;
})();
