/*	This model defines all the models.  Those are:
 *		inited - true iff the models have been initialized
 *		key - The table key
 *		restrName - The name of the restaurant
 *		restrAddress - The address of the restaurant
 *		restrStyle -	The name of a custom stylesheet for the restaurant,
 *						if one exists
 *		connectionID - Used to tell the server who changed something
 *		items -	The items on the ticket.  Stored as an array of objects.
 *				Objects follow more or less the format in TicketItem.java
 *		split -	The way the items are split between the payers
 *				Map from connectionIDs to arrays of item ids
 *		selection -	The items this user has picked.
 *					Map from item ids to booleans.  Null is false
 *		tip - How much the user has tipped
 *		contract -	The view for the contract to display to the user
 *					(null for no contract)
 *		loadMsg - A message to show while something loads (null for nothing)
 *		paid - Whether or not this client has paid
 *		done - Whether or not the ticket has been paid in full
 *		err - The error message for the ticket (null if there are none)
 *
 *	The following functions can be used to interact with these values:
 *		mvc.{VAR_NAME}() - Get the value of a given variable
 *		mvc.{VAR_NAME}(val) - Set the value of a given variable
 *		mvc.{VAR_NAME}.listen(l) - Adds a listener for a given variable
 *		mvc.{VAR_NAME}.notify() - Calls all listeners for a given variable
 *	camelCase is used for all these funtion names.  Note that the setter will
 *	automatically call the listeners.  However, if because the values
 *	returned by the getter are not deep copies, it is posible to change a
 *	shared value without using a setter.  In such a case, the notify function
 *	should be called so that the listeners will be called.
 *
 *	There are also three utility functions included in this file: mvc.init,
 *	mvc.errASAP, mvc.processedSplit, and mvc.processedItems
 *
 *	@owner sjelin
 */

var mvc = {};

(function () {
	"use strict";

	var inited = false;
	var initListeners = [];

	mvc.inited = function() {
		{{ASSERT: arguments.length == 0}};
		return inited;
	}
	mvc.inited.listen = function(fun) {
		initListeners.push(fun);
	}

	var vals;
	[	'key', 'restrName', 'restrAddress', 'restrStyle', 'connectionID',
		'items', 'split', 'selection', 'tip', 'contract', 'loadMsg', 'paid',
		'done', 'err'].forEach(function(name) {
		function myNotify(old) {
			{{ASSERT: inited && "Notify"}};
			old = arguments.length ? old : vals[name];
			ls.each(function(f) { f(old); });
		}

		var ls = [];
		mvc[name] = function(v) {
			if(!inited)
				console.log("NAME:"+name+", OLD:"+old+", VAL:"+v);
			{{ASSERT: inited && "Access"}};
			if(arguments.length > 0) {
				var old = vals[name];
				vals[name] = v;
				myNotify(old);
				window.onresize();
			}
			return vals[name];
		};
		mvc[name].notify = op.call.c(myNotify);
		mvc[name].listen = function(f) { ls.push(f); };
	});

	mvc.init = function(v)
	{
		{{ASSERT: !inited}};
		{{ASSERT: typeof v == "object"}};
		{{ASSERT: !v.hasOwnProperty("inited")}};
		vals = v;
		for(var view in mvc.views)
			mvc.views[view].viewName = view;
		inited = true;
		initListeners.each(function(f) {f(false);});
	};

	mvc.errASAP = function(v)
	{
		if(mvc.inited())
			mvc.err(v);
		else
			mvc.inited.listen(function() {mvc.err(v);});
	};

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
	};

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
	};
})();
