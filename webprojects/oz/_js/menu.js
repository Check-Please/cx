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

	menu.parse = function(raw) {
		raw = raw.split(",");
		var item = menu.items[parseInt(raw[0])];
		if(item != null) {
			item.mods = [];
			for(var i = 1; i < raw.length; i++) {
				var mod = menu.mods[parseInt(raw[i])];
				if(mod != null)
					item.mods.push(mod);
			}
		}
		return item;
	}

	menu.unparse = function(item) {
		var str = lookup(item);
		if((str != null) && (item.mods != null))
			for(var i = 0; i < item.mods.length; i++) {
				var m = lookup(item.mods[i]);
				if(m != null)
					str += ","+m;
			}
		return str;
	}

	function lookup(x) {
		x = $.extend(true, {}, x);
		delete x.id;
		delete x.orderDate;
		delete x.mods;
		delete x.choices;
		delete x.serviceCharge;
		delete x.discount;
		delete x.paidNum;
		delete x.paidDenom;
		return lookup[JSON.stringify[x]];
	}
	for(var i in menu.items)
		lookup[JSON.stringify(menu.items[i])] = i;
	for(var i in menu.mods)
		lookup[JSON.stringify(menu.mods[i])] = i;
})();
