/*	This is a very small and fragmented MVC.  Essentially, each page has its
 *	own MVC (except the page where we ask if the user wants to split the
 *	bill - one is not needed there).  The MVC for each page is responsible
 *	for all the dynamic elements, local state, and communication to the
 *	server.  Additionally, there is a small ammount of shared state,
 *	limited to the following:
 *		key - The code identifying the restaurant/table
 *		clientID - Used to tell the server who changed something
 *		items -	The items on the ticket.  Stored as an array of objects.
 *				Objects follow more or less the format in TicketItem.java
 *		split -	The way the items are split between the payers
 *				Map from clientIDs to arrays of item ids
 *		selection -	The items this user has picked.
 *					Map from item ids to booleans.  Null is false
 *		tip - How much the user has tipped
 *		username - The username of the user
 *		cards - The credit cards of the user.  Stored as an array of objects.
 *				Has the following fields:
 *					prefix, lastFour, uuid, len, lastUse
 *		paid - Whether or not this client has paid
 *		done - Whether or not the ticket has been paid in full
 *		err - The error message for the ticket (null if there are none)
 *
 *	The following functions can be used to interact with these values:
 *		{VAR_NAME}() - Get the value of a given variable
 *		{VAR_NAME}(val) - Set the value of a given variable
 *		add{VAR_NAME}Listener(l) - Adds a listener for a given variable
 *		notify{VAR_NAME}() - Calls all the listenrs for a given variable
 *	camelCase is used for all these funtion names.  Note that the setter will
 *	automatically call the listeners.  However, if because the values
 *	returned by the getter are not deep copies, it is posible to change a
 *	shared value without using a setter.  In such a case, the notify function
 *	should be called so that the listeners will be called.
 *
 *	This file is responsible for setting up this shared information, and thus
 *	it must be included first.
 *
 *	There are also two utility functions included in this file: mvc.init and
 *	mvc.processedItems
 *
 *	Each page's MVC should have a build function (e.g. buildSplit() or
 *	buildLogin()) which will be run when the page is navigated to.  The MVCs
 *	can then optionally set an unbuild function at mvc.unbuild, which will be
 *	run when the page is navigated away from.
 *
 *	The MVC does not handle navigation or incomming server communication.
 *	These tasks are handled by the nav and socket modules. These modules call
 *	the MVC, but the MVC does not call them.  During a build function, the
 *	MVC may attach some functions to DOM elements, but other than that it
 *	waits to be called by other code.
 *
 *	@owner sjelin
 */

var mvc = {};

(function () {
	"use strict";

	var names = ['key', 'clientID', 'items', 'split', 'selection', 'tip', 'username', 'cards', 'paid', 'done', 'err'];
	var vals = {};

	names.forEach(function(name) {
		function myNotify(old) {
			callAll(ls, arguments.length > 0 ? old : vals[name]);
		}

		var ls = [];
		var capName = name.charAt(0).toUpperCase()+name.substr(1);
		mvc[name] = function(v) {
			if(arguments.length == 0)
				return vals[name];
			var old = vals[name];
			vals[name] = v;
			myNotify(old);
			window.onresize();
		};
		mvc["add"+capName+"Listener"] = function(f) {ls.push(f);};
		mvc["notify"+capName] = op.call.c(myNotify);
	});

	mvc.init = function()
	{
		for(var i = 0; i < names.length; i++)
			vals[names[i]] = arguments[i];
	}

	mvc.processedSplit = function()
	{
		var rawSplit = mvc.split();
		if(rawSplit == null)
			return null;
		var split = {};
		for(var cid in rawSplit)
			for(var i = 0; i < rawSplit[cid].length; i++) {
				var iid = rawSplit[cid][i];
				split[iid] = (split[iid] || 0) + 1;
			}
		return split;
	}

	mvc.processedItems = function(useSplit, includePaidItems)
	{
		var ret = [];
		ret.subtotal = 0;
		ret.tax = 0;
		ret.discount = 0;
		ret.serviceCharge = 0;
		var items = mvc.items();
		var split = mvc.processedSplit();
		var selection = mvc.selection();
		for(var i = 0; i < items.length; i++) {
			var item = items[i];
			var id = item.id;
			if((!useSplit || (split == null) || selection[id]) &&
				   ((item.paidNum < item.paidDenom) || includePaidItems)) {
				var num = item.paidDenom-item.paidNum;
				var denom=item.paidDenom*(1+(useSplit&&split&&split[id]||0));
				var gcd = Math.gcd(num, denom);
				num /= gcd;
				denom /= gcd;
				var price = (item.price||0)*num/denom/100;
				var tax = (item.tax||0)*num/denom/100;
				var discount = (item.discount||0)*num/denom/100;
				var serviceCharge = (item.serviceCharge||0)*num/denom/100;
				ret.push({
					id: id,
					num: num,
					denom: denom,
					name: (denom==1 ? "" : "("+num+"/"+denom+") ")+item.name,
					price: money.round(price),
					tax: money.round(tax),
					discount: money.round(discount),
					serviceCharge: money.round(serviceCharge),
					mods: item.mods
				});
				for(var j = 0; j < item.mods.length; j++)
					price += (item.mods[j].price||0)*num/denom/100;
				ret.subtotal += price;
				ret.tax += tax;
				ret.discount += discount;
				ret.serviceCharge += serviceCharge;
			}
		}
		ret.subtotal = money.round(ret.subtotal);
		ret.tax = money.round(ret.tax);
		ret.discount = money.round(ret.discount);
		ret.serviceCharge = money.round(ret.serviceCharge);
		return ret;
	}
})();
