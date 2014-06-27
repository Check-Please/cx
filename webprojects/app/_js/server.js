/**	This module provides methods for talking to the server
 *
 *	@author sjelin
 */

var server = server || {};

(function() {
	"use strict";

	var send = function() {
		$.extend(arguments[1], {
			tableKey: models.tableKey(),
			connectionID: models.connectionID()
		});
		arguments[2] = arguments[2] || $.noop;
		device.ajax.send.bind(device.ajax, "cx").apply(this, arguments);
	}

	server.logPos = function(viewName) {
		send("logPos", {viewName: viewName});
	}

	server.doSplit = function() {
		var diff = models.split().inNumWays - models.split().currNumWays;
		if(diff != 0) {
			var trgt = models.split().trgt;
			var nWays = models.split().inNumWays;
			send("split", {trgt: trgt, nWays: nWays});
			models.split(undefined);

			//Rather than waiting for a message from the server, we do the
			//split client side
			var itemIDs = {};
			var items = models.items();
			itemIDs[consts.statuses.CHECKED] = [];
			itemIDs[consts.statuses.UNCHECKED] = [];
			itemIDs[consts.statuses.PAID] = [];
			itemIDs[consts.statuses.SUBTOTAL] = [];
			for(var id in items)
				if(id.startsWith(trgt))
					itemIDs[items[id].status].push(id);

			var newFrac = {n: 1, d: 1};
			for(var i = 0; i < itemIDs[consts.statuses.PAID].length; i++) {
				var item = items[itemIDs[consts.statuses.PAID][i]];
				newFrac.n = newFrac.n*item.denom - item.num*newFrac.d;
				newFrac.d *= item.denom;
				var gcd = Math.gcd(newFrac.n, newFrac.d);
				newFrac.n /= gcd;
				newFrac.d /= gcd;
			}
			newFrac.d *= nWays;
			var gcd = Math.gcd(newFrac.n, newFrac.d);
			newFrac.n /= gcd;
			newFrac.d /= gcd;

			if(diff < 0) {
				["UNCHECKED", "TAKEN", "CHECKED"].each(function(type) {
					var ids = itemIDs[consts.statuses[type]];
					for(; diff && ids.length; diff++)
						delete items[ids.pop()];
					for(var i = 0; i < ids.length; i++) {
						items[ids[i]].num = newFrac.n;
						items[ids[i]].denom = newFrac.d;
					}
				});
			} else {
				var newIDNum = -1;
				for(var type in itemIDs)
					for(var i = 0; i < itemIDs[type].length; i++) {
						var id = itemIDs[type][i];
						newIDNum = Math.max(newIDNum,
											parseInt(id.split(":").pop()));
						items[id].num = newFrac.n;
						items[id].denom = newFrac.d;
					}
				var newItem = $.extend({}, items[trgt+":"+newIDNum]);
				newItem.status = consts.statuses.UNCHECKED;
				while(diff++)
					items[trgt+":"+(++newIDNum)] = $.extend({}, newItem);
			}
			models.items(items);
		}
		
	}

	server.setAllCheck = function(checked) {
		send("checkAll", {checked: checked, });
		var oldSt = consts.statuses[(checked ? "UN" : "")+"CHECKED"];
		var newSt = consts.statuses[(checked ? "" : "UN")+"CHECKED"];
		for(var id in models.items())
			if(models.items()[id].status == oldSt)
				models.items()[id].status = newSt;
		models.items.alert();
	}

	server.toggleItemCheck = function(itemID) {
		var item = models.items()[itemID];
		if(item.status == consts.statuses.CHECKED) {
			send("uncheck", {itemID: itemID});
			item.status = consts.statuses.UNCHECKED;
		} else {
			send("check", {itemID: itemID});
			item.status = consts.statuses.CHECKED;
		}
		models.items.alert();
	}

	server.pay = function() {
		models.loading({message: "Processing payment"});

		var payInfo = {items: models.items()};
		if(models.cardFocus() == -1) {
			payInfo.pan =	models.newCardInfo().pan;
			payInfo.name =	models.newCardInfo().name;
			payInfo.expr =	models.newCardInfo().exprYear.slice(-2) +
							models.newCardInfo().exprMonth;
			payInfo.cvv =	models.newCardInfo().cvv;
			payInfo.zip =	models.newCardInfo().zip;
			if(payInfo.reqPass)
				payInfo.password = models.newCardInfo().password;
		} else
			payInfo.cardCT = saved.getCardCiphertext(models.cardFocus());

		send("pay", payInfo, function(cardCT) {
			if((models.cardFocus() == -1) && (models.newCardInfo().save))
				saved.saveCC(models.newCardInfo(), cardCT);
			models.validViews([consts.views.FEEDBACK]);
			models.loading(undefined);
		}, function(code, _, msg) {
			models.error({	heading: "Couldn't pay",
							symbol: String.fromCharCode(215),
							message: (code == 404 ? "" : code + " ") + msg});
			models.loading(undefined);
		});
	}

	server.sendEmail = function(addr) {
		send("email", {addr: addr});
		models.email(addr);
	}

	server.sendRating = function(rating) {
		send("rate", {rating: rating});
		models.feedback(rating);
	}
})();
