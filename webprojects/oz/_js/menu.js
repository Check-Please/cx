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
			item = $.extend(true, {}, item);
			item.mods = [];
			for(var i = 1; i < raw.length; i++) {
				var mod = menu.mods[parseInt(raw[i])];
				if(mod != null)
					item.mods.push(mod);
			}
		}
		return item;
	}

	function makeKey(x) {
		return x.name+(x.price?","+x.price:"")+(x.tax?"+"+x.tax:"");
	}
	var lookup = {};

	menu.unparse = function(item) {
		var str = lookup[makeKey(item)];
		if((str != null) && (item.mods != null))
			for(var i = 0; i < item.mods.length; i++) {
				var m = lookup[makeKey(item.mods[i])];
				if(m != null)
					str += ","+m;
			}
		return str;
	}

	for(var i in menu.items)
		lookup[makeKey(menu.items[i])] = i;
	for(var i in menu.mods)
		lookup[makeKey(menu.mods[i])] = i;
})();
