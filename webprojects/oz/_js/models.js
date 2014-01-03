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
 *						total - The total which the payer has been shown
 *						tip - How much the payer tipped
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
 *		drawTick(tKey, items, payments) 
 */

var models = models || {};

(function() {
	models.STATUS_FAIL = -1;
	models.STATUS_PAID = 1;
	models.STATUS_NONE = 0;

	var post = ajax.send.bind(ajax, "oz");
	var get = ajax.receive.bind(ajax, "oz");
	var restr = "sjelin";

	var tickets;
	var payments = {};

	/** Initializes the app, but does not clear any payment info in RAM
	 *
	 *	Sets up a new connection with the server, downloads the current
	 *	tickets, opens a socket, and calls drawBase() and drawTick()
	 */
	models.refresh = function(callback) {
		post("connect", {restr: restr}, function(data) {
			data = JSON.parse(data);
			openSocket(data.token);
			if(data.ticks instanceof Array) {
				tickets = {};
				for(var i = 0; i < data.ticks.length; i++)
					tickets["oz"+i] = data.ticks[i];
			} else
				tickets = data.ticks;
			drawBase(Object.keys(tickets));
			for(tKey in tickets) {
				if(payments[tKey] == undefined) {
					payments[tKey] =  [];
					payments[tKey].byCID = {};
				}
				drawTick(tKey, tickets[tKey], payments[tKey]);
			}
		}, buildAjaxErrFun("connect"));
	}

	/*	Opens a socket to the server & sets up the socket events
	 *
	 *	Once this is done, any payments sent to the server will automatically
	 *	be pushed to this console
	 */
	function openSocket(token)
	{
		var skt;
		var onOpen = $.noop;
		var onMessage = function(payment) {
			payment = JSON.parse(payment);
			payment.focus = false;
			patment.notification = true;
			payment.statusCode = models.STATUS_NONE;
			payment.statusMsg = "";
			payment.timeStamp = new Date();
			payments[payment.tKey].push(payment);
			payments[payment.tKey].byCID[payment.cID] = payment;
		};
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

	/**	Sets the items on a ticket and calls drawTick
	 *
	 *	@param	tKey The key of the ticket to set the items of
	 *	@param	items The items for the ticket
	 */
	models.setItems = function(tKey, items) {
		tickets[tKey] = items;
		post("tick", {restr: restr,	i: parseInt(tKey.substr(2)),
						tick: JSON.stringify(items)}, $.noop);
		drawTick(tKey, items, payments[tKey]);
	};

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
		drawTick(tKey, tickets[tKey], tPayments);
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
	models.setPaymentStatus = function(tKey, cID, statusCode, statusMsg) {
		var payment = payments[tKey].byCID[cID];
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
			case models.STATUS_PAID: post("success", params, $.noop); break;
			case models.STATUS_FAIL: post("failure", params, $.noop); break;
			case models.STATUS_NONE: post("update", params, $.noop); break;
		};
		drawTick(tKey, tickets[tKey], payments[tKey]);
	};
})();
