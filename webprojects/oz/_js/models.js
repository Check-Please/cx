/**	This file handels the models for what is basically the most basic MVC
 *	ever.  We keep track of the variables for each ticket:
 *		items - The items on the tables.
 *		payments -	The way the items are split between the payers.
 *
 *	The following methods are used to access these variables:
 *		models.refresh()
 *		models.setItems(tKey, items)
 *		models.setPaymentFocus(tKey, cID, focus)
 *		models.setPaymentStatus(tKey, cID, statusCode, statusMsg)
 *
 *	The following constants are also defined:
 *		models.STATUS_FAIL
 *		models.STATUS_PAID
 *		models.STATUS_NONE
 *
 *	All communications with the server are handled automatically in this
 *	file.  Additionally, the following functions are sometimes called:
 *		drawBase(tKeys)
 *		drawTick(tKey, items, payments) 
 */

models = models || {};

(function() {
	models.STATUS_FAIL = -1;
	models.STATUS_PAID = 1;
	models.STATUS_NONE = 0;

	var post = ajax.send.c("oz");
	var get = ajax.recieve.c("oz");
	var restr = "sjelin";

	var tickets;
	var payments = {};

	models.refresh = function(callback) {
		post("connect", {restr: restr}, function(data) {
			data = JSON.parse(data);
			openSocket(data.token);
			if(data.ticks instanceof Array) {
				tickets = {};
				for(var i = 0; i < n; i++)
					tickets["oz"+i] = tickets[i];
			} else
				tickets = data.ticks;
			drawBase(Object.keys(tickets));
			for(tKey in tickets) {
				payments[tKey] = payments[tKey] || {};
				drawTick(tKey, tickets[tKey], payments[tKey]);
			}
		});
	}

	function openSocket(token)
	{
		skt = (new goog.appengine.Channel(token)).open({
			'onopen': onOpen,
			'onmessage': onMessage,
			'onerror': onError,
			'onclose': onClose
		});
		skt.onopen = $.noop;
		skt.onmessage = function(payment) {
			payment = JSON.parse(payment);
			payment.focus = false;
			patment.notification = true;
			payment.statusCode = models.STATUS_NONE;i
			payment.statusMsg = "";
			payment.timeStamp = new Date();
			payments[payment.tKey][payment.cID] = payment;
		};
		skt.onerror = function(err) {
			alert("SOCKET ERROR!  Please restart.\n\n"+JSON.stringify(err));
			var s = skt;
			skt = null;
			s.close();
		};
		skt.onclose = function() {
			if(skt != null) {
				alert("Socket closed!  Please restart.");
				var s = skt;
				skt = null;
				s.close();
			}
		};
	}

	models.setItems = function(tKey, items) {
		tickets[tKey] = items;
		send("tick", {restr: restr,	i: parseInt(tKey.substr(2)),
						tick: JSON.stringify(items)}, $.noop);
		drawTick(tKey, items, payments[tKey]);
	};

	models.setPaymentFocus = function(tKey, cID, focus) {
		var tPayments = payments[tKey];
		if(focus) {
			tPayments[cID].notification = false;
			for(var i in tPayments)
				tPayments[i].focus = i == cID;
		} else
			tPayments[cID].focus = false;
		drawTick(tKey, tickets[tKey], tPayments);
	};

	models.setPaymentStatus = function(tKey, cID, statusCode, statusMsg) {
		var payment = payments[tKey][cID];
		payment.statusCode = statusCode;
		payment.statusMsg = statusMsg;
		if(statusCode != 0)
			payment.focus = false;
		var items = tickets[tKey];
		for(var i = 0; i < items.length; i++)
			items[i] = JSON.stringify(items[i]); 
		params = {
			channelID: payment.cID,
			msg: statusMsg,
			i: parseInt(tKey.substr(2)),
			items: items,
			nums: payment.payFracNums,
			denoms: payment.payFracDenoms
		};
		switch(statusCode) {
			case models.STATUS_PAID: send("success", params, $.noop); break;
			case models.STATUS_FAIL: send("failure", params, $.noop); break;
			case models.STATUS_NONE: send("update", params, $.noop); break;
		};
		drawTick(tKey, tickets[tKey], payments[tKey]);
	};
})();
