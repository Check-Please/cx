/**	This module handles drawing the basics of the drawing the views and
 *	attaching the controlers
 */

(function() {
	"use strict";

	/*	Updates the models on what the tickets are
	 *
	 *	Must be bound to the ticket's head in the DOM
	 */
	function updateItems()
	{
		var $tick = $(this);
		var items = [];
		$tick.find(".input .items .item").each(function() {
			var $item = $(this);
			var item = menu.parse($item.find("input").val());
			if(item != null) {
				item.id = $item.attr("itemID");
				item.orderDate = $item.attr("orderDate");
				items.push(item);
			}
		});
		models.setItems($tick.attr("tKey"), items);
	}

	/*	Adds an input box for a new ticket item
	 *
	 *	Must be bound to the ticket's head in the DOM
	 */
	function newItem() {
		var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
			function(c) {
    			var r = Math.random()*16|0;
				var v = c == 'x' ? r : (r&0x3|0x8);
    			return v.toString(16);
			}
		);
		$(this).find(".input .items").append($(templates.itemIn(uuid,
												new Date().getTime(), "")));
		updateItems.call(this);
	}

	/*	Deletes a ticket item
	 */
	function deleteItem() {
		var $tick = $(this).parents(".ticket");
		$(this).parent().remove();
		updateItems.call($tick[0]);
	}

	/*	Toggles if one payment has the focus
	 */
	function togglePayFocus() {
		var $this = $(this).find("a");
		models.togglePaymentFocus(
				$this.parents(".ticket").attr("tKey"),
				$this.parents(".payment").attr("cID"));
	}

	/*	Sets the status of a payment
	 */
	function setStatus() {
		var $this = $(this);
		var $payment = $this.parents(".payment");
		if($payment.hasClass("paid") || $payment.hasClass("failed"))
			return;
		var tKey = $this.parents(".ticket").attr("tKey");
		var cID = $payment.attr("cID")
		var sCode =	$this.hasClass("success") ?	models.STATUS_PAID :
					$this.hasClass("fail") ?	models.STATUS_FAIL :
												models.STATUS_NONE;
		var msg = $this.attr("msg");
		models.setPaymentStatus(tKey, cID, msg, sCode,
			sCode == models.STATUS_PAID ? "Success!" :
			msg == "limit"	? "Not within credit limit"		:
			msg == "invalid"? "Invalid credit information"	:
			msg == "reject"	? "Credit card rejected"		:
			msg == "clear"	? "Ticket already paid"			:
			msg == "tech"	? "Technical Difficulties"		:
			msg == "other"	? "Unknown failure cause"		:
			msg == "enqueue"	? "Entering queue"	:
			msg == "terminal"	? "Sending info"	:
			msg == "processing"	? "Card processing"	: "");
	}

	window.drawBase = function(tKeys) {
		var $ticks = $(".tickets");
		$ticks.empty();
		var $btns = $(".nav");
		$btns.empty();
		for(var i = 0; i < tKeys.length; i++) {
			var $tick = $(templates.ticket(tKeys[i]));
			var $items = $tick.find(".input .items");
			$items.on("keyup keydown keypress", ".item input",
										updateItems.bind($tick[0]));
			$items.on("click", ".item a", deleteItem);
			$tick.find(".new.item").click(newItem.bind($tick[0]));
			$tick.find(".input .clear").click(models.clear.c(tKeys[i]));
			$tick.find(".payments").on("click", ".payment", togglePayFocus);
			$tick.find(".payments").on("click", ".payment .messages", false);
			$tick.find(".payments").on("click", ".messages > a", setStatus);
			$ticks.append($tick);

			var $btn = $(templates.tickBtn(tKeys[i]));
			$btn.css("left", (100*(i+0.5)/tKeys.length)+"%");
			$btns.append($btn);
		}
		(onhashchange || $.noop)();
	}

	window.drawTick = function(tKey, items, payments) {
		var $tick = $(".ticket[tKey=\""+tKey+"\"]");

		//Input
		var $items = $tick.find(".input .items");
		var nCurrItems = 0;
		$items.children().each(function() {
			nCurrItems += menu.parse($(this).find("input").val())==null?0:1;
		});
		if(nCurrItems != items.length) {
			$items.empty();
			for(var i = 0; i < items.length; i++) {
				var item = items[i];
				var str = menu.unparse(item);
				if(str != null)
					$items.append($(templates.itemIn(item.id, item.orderDate,
																	str)));
			}
		}

		//Payments
		var notification = false;
		var payElems = {};//status -> array of payment elems
		var fPayment = null;
		for(var i = 0; i < payments.length; i++) {
			var p = payments[i];
			var sCode = p.statusCode;
			var statusString =	sCode == models.STATUS_FAIL ?	"failed" :
								sCode == models.STATUS_PAID ?	"paid" :
																"none";
			var lastFour = p.pan.slice(-4);
			while(lastFour.length < p.pan.length)
				lastFour = "X"+lastFour;
			var $payment = $(templates.payment(p.cID, statusString, p.msgKey,
								p.focus, p.notification, p.pan, p.name,
								p.exprMonth, p.exprYear, p.cvv, lastFour));
			(payElems[sCode] || (payElems[sCode] = [])).push($payment);
			notification = notification || p.notification;
			if(p.focus)
				fPayment = p;
		}
		var $payments = $tick.find(".payments ul");
		$payments.empty();
		$payments.append.apply($payments, payElems[models.STATUS_NONE]);
		$payments.append.apply($payments, payElems[models.STATUS_FAIL]);
		$payments.append.apply($payments, payElems[models.STATUS_PAID]);
		$(".nav a.tBtn[tKey=\""+tKey+"\"]")[(
			notification ? "add" : "remove")+"Class"]("notification");

		//Filter by payer with focus
		if(fPayment != null) {
			var payFracs = {};
			for(var i = 0; i < fPayment.itemsToPay.length; i++)
				payFracs[fPayment.itemsToPay[i]] = {
					num: fPayment.payFracNums[i],
					denom: fPayment.payFracDenoms[i]
				}
			items = $.extend(true, [], items);
			for(var i = items.length-1; i >= 0; i--) {
				var frac = payFracs[items[i].id];
				if(frac == null)
					items.splice(i, 1);
				else {
					items[i].paidNum = frac.denom-frac.num;
					items[i].paidDenom = frac.denom;
				}
			}
		}

		//Info
		var summary = {	subtotal: 0, tax: 0, service: 0, discount: 0,
						tip: (fPayment||{}).tip||0};
		$items = $tick.find(".info .items");
		$items.empty();
		for(var i = 0; i < items.length; i++) {
			var item = items[i];
			var d = (item.paidDenom) || 1;
			var n = d - (item.paidNum || 0);
			var name = (n != d ? "("+n+"/"+d+") " : "")+item.name;
			var price = ((item.price || 0) - (item.discount || 0)) * n / d;
			summary.subtotal += price;
			summary.tax += (item.tax || 0) * n / d;
			summary.service += (item.serviceCharge || 0) * n / d;
			$items.append($(templates.itemDesc(name, money.toStr(price/100),
				item.mods.map(function(x) {
					var p = ((x.price || 0) - (x.discount || 0)) * n / d;
					summary.subtotal += p;
					summary.tax += (x.tax || 0) * n / d;
					summary.service += (x.serviceCharge || 0) * n / d;
					return templates.itemMod(x.name,p?money.toStr(p/100):"");
				}).join("")
			)));
		}
		summary.total =	summary.subtotal + summary.tax +
						summary.service - summary.discount +
						(summary.tip || 0);
		for(var i in summary)
			$tick.find(".info ."+i+" span").text(
												money.toStr(summary[i]/100));
		$(".info h1 span").text(fPayment ? "("+fPayment.name+")" : "");
	}
})();
