/**	This file handels the models for what is basically the most basic MVC
 *	ever.  We keep track of the variables for each ticket:
 *		items - The items on the tables.  Follow the TicketItem.java format
 *		payments -	The way the items are split between the payers.  Each
 *					payment has the following elements:
 *						tKey - The key for the table
 *						cID - The connection ID of the payer
 *						itemsToPay - The items being targeted by this payment
 *						payFracNums - Numerators of the fractions to pay
 *						payFracDenoms - Denominators of the fractions to pay
 *						total - The total which the payer was shown (cents)
 *						tip - How much the payer tipped (in cents)
 *						pan - Card information
 *						name - Card information
 *						expr - Card information
 *						zip - Card information
 *						cvv - Card information (optional)
 *						focus - True iff this payment has the focus
 *						notification - True iff payment hasn't been viewed
 *						timeStamp - When the payment came in
 *						statusCode - The status of the payment
 *						statusMsg - The message for the payer
 *
 *	The following methods are used to access these variables:
 *		models.refresh()
 *		models.setItems(tKey, items)
 *		models.sendItemsUpdate()
 *		models.togglePaymentFocus(tKey, cID)
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
 *		drawTick(tKey, items, payments, needsUpdate) 
 */

var models = models || {};

(function() {
	"use strict";

	models.STATUS_FAIL = -1;
	models.STATUS_PAID = 1;
	models.STATUS_NONE = 0;

	var post = ajax.send.bind(ajax, "oz");
	var restr = "sjelin";

	var tickets;
	var needsUpdate;
	var payments = {};

	/** Initializes the app, but does not clear any payment info in RAM
	 *
	 *	Sets up a new connection with the server, downloads the current
	 *	tickets, opens a socket, and calls drawBase() and drawTick()
	 */
	models.refresh = function() {
		post("connect", {restr: restr}, function(data) {
			//Load Data
			data = JSON.parse(data);
			openSocket(data.token);
			if(data.ticks instanceof Array) {
				tickets = {};
				for(var i = 0; i < data.ticks.length; i++)
					tickets["OZ"+i] = data.ticks[i];
			} else
				tickets = data.ticks;
			needsUpdate = {};

			//Process data
			drawBase(Object.keys(tickets));
			for(var tKey in tickets) {
				var items = tickets[tKey];
				var payers = payments[tKey];
				if(payers == undefined) {
					payments[tKey] = (payers = []);
					payers.byCID = {};
				}

				//Draw
				drawTick(tKey, items, payers, false);
			}
		}, buildAjaxErrFun("connect"));
	}

	function addPayment(payment) {
		alert("New payment!");
		payment.focus = false;
		payment.notification = true;
		payment.msgKey = null;
		payment.statusCode = models.STATUS_NONE;
		payment.statusMsg = "";
		payment.timeStamp = new Date();
		payment.exprYear = "20"+payment.expr.slice(0,2);
		payment.exprMonth = payment.expr.slice(2);
		var tKey = payment.tKey;
		var cID = payment.cID;
		var pays = payments[tKey];
		if(pays.byCID[cID] != null)
			for(var i = 0; i < pays.length; i++)
				if(pays[i].cID == cID)
					pays.splice(i,1);
		pays.push(payment);
		pays.byCID[cID] = payment;
		drawTick(tKey, tickets[tKey], pays, needsUpdate[tKey]);
	}
	if({{DEBUG}})
		window.addPayment = addPayment;

	var skt = null;
	/*	Opens a socket to the server & sets up the socket events
	 *
	 *	Once this is done, any payments sent to the server will automatically
	 *	be pushed to this console
	 */
	function openSocket(token)
	{
		var onOpen = $.noop;
		var onMessage = function(msg) {
			if(msg.data.substr(0,6) == "ALERT:")
				alert(msg.data.substr(6).trim());
			else
				addPayment(JSON.parse(msg.data));
		}
		var onError = function(err) {
			alert("SOCKET ERROR!  Please restart.\n\n"+JSON.stringify(err));
			var s = skt;
			skt = null;
			s.close();
		};
		var onClose = function() {
			if(skt != null) {
				alert("Socket closed!  Please restart.");
				var s = skt;
				skt = null;
				s.close();
			}
		};
		closeSocket();
		skt = (new goog.appengine.Channel(token)).open({
			'onopen': onOpen,
			'onmessage': onMessage,
			'onerror': onError,
			'onclose': onClose
		});
		skt.onopen = onOpen;
		skt.onmessage = onMessage;
		skt.onerror = onError;
		skt.onclose = onClose;
	}

	/**	Closes the socket without error messages
	 */
	function closeSocket()
	{
		if(skt != null) {
			var s = skt;
			skt = null;
			s.close();
		}
	}

/*	window.onbeforeunload = function() {
		closeSocket();
		post("disconnect", {payments: JSON.stringify(payments)});
	}*/

	/**	Sets the items on a ticket and calls drawTick
	 *
	 *	@param	tKey The key of the ticket to set the items of
	 *	@param	items The items for the ticket
	 */
	models.setItems = function(tKey, items) {
		tickets[tKey] = items;
		needsUpdate[tKey] = true;
		drawTick(tKey, items, payments[tKey], true);
	};

	/**	Updates the server on the status of the items
	 */
	models.sendItemsUpdate = function(tKey)
	{
		post("tick", {restr: restr,	i: parseInt(tKey.substr(2)),
						tick: JSON.stringify(tickets[tKey])}, $.noop);
		needsUpdate[tKey] = false;
		drawTick(tKey, tickets[tKey], payments[tKey], false);
	}

	/**	Gives/takes the focus to/from particular payment and calls drawTick
	 *
	 *	@param	tKey The ticket which the payment is in
	 *	@param	cID The connection ID of the payer
	 */
	models.togglePaymentFocus = function(tKey, cID) {
		var tPayments = payments[tKey];
		var focus = !tPayments.byCID[cID].focus;
		if(focus) {
			tPayments.byCID[cID].notification = false;
			for(var i in tPayments.byCID)
				tPayments.byCID[i].focus = i == cID;
		} else
			tPayments.byCID[cID].focus = false;
		drawTick(tKey, tickets[tKey], tPayments, needsUpdate[tKey]);
	};

	/**	Sets the status a payment and updates the payer on that status.
	 *
	 *	Also calls drawTick
	 *
	 *	@param	tKey The key of the table to which the payment belongs
	 *	@param	cID The connection ID of the payer
	 *	@param	statusCode The status code of the update
	 *	@param	statusMsg The message for the update
	 */
	models.setPaymentStatus = function(tKey,cID,msgKey,statusCode,statusMsg){
		var payment = payments[tKey].byCID[cID];
		payment.statusCode = statusCode;
		payment.statusMsg = statusMsg;
		payment.msgKey = msgKey;
		if(statusCode != 0)
			payment.focus = false;
		var params = {
			channelID: payment.cID,
			msg: statusMsg,
			i: parseInt(tKey.substr(2)),
			items: payment.itemsToPay,
			nums: payment.payFracNums,
			denoms: payment.payFracDenoms
		};
		switch(statusCode) {
			case models.STATUS_PAID: post("success", params, $.noop); break;
			case models.STATUS_FAIL: post("failure", params, $.noop); break;
			case models.STATUS_NONE: post("update", params, $.noop); break;
		};
		drawTick(tKey, tickets[tKey], payments[tKey], needsUpdate[tKey]);
	};

	/**	Removes all items/payments from a particular table
	 *
	 *	If there are outstanding payments, the user gets a confirm dialogue.
	 *	If the user confirms, all outstanding payments fail, getting the
	 *	message that they timed out
	 *
	 *	@param	tKey The key for the table to clear
	 */
	models.clear = function(tKey) {
		var payers = payments[tKey];
		var confirmed = false;
		for(var i = 0; i < payers.length; i++) {
			var payment = payers[i];
			if(payment.statusCode == models.STATUS_NONE) {
				if(!confirmed) {
					confirmed = confirm("There are some outstanding "+
										"payments on this table.  Are you "+
										"sure you want to clear?");
					if(!confirmed)
						return;
				}
				post("failure", {
					channelID: payment.cID,
					msg: "Payment timmed out",
					i: parseInt(tKey.substr(2)),
					items: payment.itemsToPay,
					nums: payment.payFracNums,
					denoms: payment.payFracDenoms
				}, $.noop);
			}
		}
		payments[tKey] = [];
		payments[tKey].byCID = {};
		models.setItems(tKey, []);
	}
})();
