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
			connectionID: models.connectionID(),
			clientID: saved.getClientID()
		});
		arguments.length = Math.max(arguments.length, 4);
		arguments[2] = arguments[2] || $.noop;
		arguments[3] = arguments[3] || function(code, _, msg) {
			models.error({	heading: code == 404 ?	"Bad Ajax" :
													code+" Error",
							symbol: String.fromCharCode(215),
							message: msg});
		};
		device.ajax.send.bind(device.ajax, "cx").apply(this, arguments);
	}

	server.sendHeartbeat = function(msg) {
		send("heartbeat", {message: msg});
	}

	server.logPos = function(viewName) {
		if(models.tableKey() != undefined)
			send("log_pos", {position: viewName});
	}

	server.doSplit = function() {
		var nToAdd = models.split().inNumWays - models.split().currNumWays;
		if(nToAdd != 0) {
			var trgt = models.split().trgt;
			var nWays = models.split().inNumWays;
			send("split", {itemID: trgt, nWays: nWays});
			models.split({});

			//Rather than waiting for a message from the server, we do the
			//split client side
			var itemIDs = {};
			var items = models.items();
			itemIDs[consts.statuses.CHECKED] = [];
			itemIDs[consts.statuses.UNCHECKED] = [];
			itemIDs[consts.statuses.PAID] = [];
			itemIDs[consts.statuses.TAKEN] = [];
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

			if(nToAdd < 0) {
				["UNCHECKED", "TAKEN", "CHECKED"].each(function(type) {
					var ids = itemIDs[consts.statuses[type]];
					for(; nToAdd && ids.length; nToAdd++)
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
									parseInt(models.funs.splitID(id)[1]));
						if(items[id].status != consts.statuses.PAID) {
							items[id].num = newFrac.n;
							items[id].denom = newFrac.d;
						}
					}
				var newItem = $.extend({}, items[trgt+":"+newIDNum]);
				newItem.status = consts.statuses.UNCHECKED;
				while(nToAdd--)
					items[trgt+":"+(++newIDNum)] = $.extend({}, newItem);
			}
			models.items(items);
		}
		
	}

	server.setAllCheck = function(checked) {
		send("check_all", {checked: checked});
		var oldSt = consts.statuses[(checked ? "UN" : "")+"CHECKED"];
		var newSt = consts.statuses[(checked ? "" : "UN")+"CHECKED"];
		for(var id in models.items())
			if(models.items()[id].status == oldSt)
				models.items()[id].status = newSt;
		models.items.alert();
	}

	server.toggleItemCheck = function(itemID) {
		var item = models.items()[itemID];
		if(item.status == consts.statuses.PAID)
			return;

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
		//Get data from models and validate that we're ready to pay
		var tip = parseInt(models.tip());
		if(isNaN(tip))
			return alert(text.ENTER_TIP_ALERT);
		var focus = models.cardFocus();
		var newCC;
		var password; //Password for saved card
		var card = models.cards()[focus];
		if(focus == -1) {
			newCC = models.newCardInfo();
			if(creditCards.validate(newCC) != null)
				return alert(text.SELECT_CARD_ALERT);
		} else if(card.reqPass) {
			password = models.passwords()[focus];
			if(!password)
				return alert(text.ENTER_CARD_PASS_ALERT);
		}

		models.loading({message: text.STARTING_PAYMENT_LOAD_MSG});

		//Prepare data to send to server
		var total = 0;
		for(var id in models.items()) {
			var item = models.items()[id];
			if(item.status == consts.statuses.CHECKED)
				total += (item.num / item.denom) * (item.price + item.tax +
										item.serviceCharge - item.discount);
		}
		var payInfo = {	total: money.round(total/100), tip: tip,
						protectCT: device.putSecretsInCookies()};
		var cmd;
		if(focus == -1) {
			cmd = "pay_new";
			payInfo.pan =	newCC.pan;
			payInfo.name =	newCC.name;
			payInfo.expr =	newCC.exprYear.slice(-2) + newCC.exprMonth;
			payInfo.cvv =	newCC.cvv;
			payInfo.zip =	newCC.zip;
			payInfo.save =	newCC.save;
			payInfo.cookieID = saved.newCCCookieID();
			if(newCC.reqPass)
				payInfo.password = newCC.password;
		} else {
			cmd = "pay_saved/"+saved.getCCCookieID(focus);
			payInfo.cardCT = card.ciphertext;
			if(password != null)
				payInfo.password = password;
		}

		//Pay!
		send(cmd, payInfo, function(data) {
			data = data.trim();
			if(/^\w+$/i.test(data)) {
				//Error Messages
				models.activeView(consts.views.CARDS);
				models.loading(undefined);
				if(data.startsWith("PASSWORD_")) {
					var $pass = $(".focus.saved.card .password input");
					$pass.val("");
					$pass.change();//Trigger listeners
					var focusTime = new Date().getTime()+consts.FOCUS_DELAY;
					if(data == "PASSWORD_NO_REQ")
						alert(text.NO_PADDWORD_REQ_ALERT);
					else if(data == "PASSWORD_REQ")
						alert(text.PASSWORD_REQ_ALERT);
					else
						alert(text.INCORRECT_PASSWORD_ALERT);
					setTimeout(function() {
						$pass.focus();
					}, Math.max(0, focusTime - new Date().getTime()));
				} else if(data.startsWith("KEY_")) {
					saved.deleteCCs();
					alert(text.LOST_ENCRYPT_KEY_ALERT);
				} else {
					saved.deleteCC(models.cardFocus());
					if(data == "NO_CIPHERTEXT")
						alert(text.NO_CIPHERTEXT_ALERT);
					else
						alert(text.CIPHERTEXT_CORRUPED_ALERT);
				}
			} else {
				//Successful payment
				data = JSON.parse(data);
				if(focus == -1) {
					if(newCC.save)
						saved.saveCC(newCC, data.cardCT, payInfo.cookieID);
				} else
					saved.logCCUse(focus);
				if(data.async)
					models.loading({message: text.PROCESSING_PAY_LOAD_MSG});
				else {
					models.done(data.done);
					models.validViews([consts.views.FEEDBACK]);
					models.loading(undefined);
				}
			}
		}, function(code, _, msg) {
			models.error({	heading: text.CANNOT_PAY_HDG,
							symbol: String.fromCharCode(215),
							message: (code == 404 ? "" : code + " ") + msg});
			models.loading(undefined);
		});
	}

	server.sendEmail = function(addr) {
		send("email", {email: addr});
		models.email(addr);
	}

	server.sendRating = function(rating) {
		send("rate", {rating: rating});
		models.feedback(rating);
	}
})();
