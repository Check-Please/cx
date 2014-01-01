/**	This module handles drawing the basics of the drawing the views and
 *	attaching the controlers
 */

(function() {

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
		$(this).append($(templates.itemIn(uuid, "")));
		updateItems.call(this);
	}

	/*	Deletes a ticket item
	 */
	function deleteItem() {
		$(this).parent().remove();
		upateItems.call($(this).parents(".ticket")[0]);
	}

	/*	Toggles if one payment has the focus
	 */
	function togglePayFocus() {
		var $this = $(this);
		models.togglePaymentFocus(
				$this.parents(".ticket").attr("tKey"),
				$this.parents(".payment").attr("cID"));
	}

	/*	Sets the status of a payment
	 */
	function setStatus() {
		var $this = $(this);
		var tKey = $this.parents(".ticket").attr("tKey");
		var cID = $this.parents(".payment").attr("cID")
		var sCode =	$this.hasClass("success") ?	models.STATUS_PAID :
					$this.hasClass("fail") ?	models.STATUS_FAIL :
												models.STATUS_NONE;
		var msg = $this.attr("msg");
		models.setPaymentStatus(tKey, cID, sCode,
			sCode == models.STATUS_PAID ? "Success!" :
			msg == "limit"	? "Not within credit limit"		:
			msg == "invalid"? "Invalid credit information"	:
			msg == "reject"	? "Credit card rejected"		:
			msg == "paid"	? "Ticket already paid"			:
			msg == "tech"	? "Technical Difficulties"		:
			msg == "other"	? "Unknown failure cause"		:
			msg == "enqueue"	? "Entering queue"	:
			msg == "terminal"	? "Sending info"	:
			msg == "processing"	? "Card processing"	: "");
	}

	var addNav = function($nav) {
		$nav.on("click", "a.tBtn", function() {
			location.hash = $(this).attr("tKey");
		});
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
										updateItems.bind($tick));
			$items.on("click", ".item a", deleteItem);
			$tick.find(".new.item").click(newItem.bind($tick));
			$tick.find(".payments").on("click", ".title", togglePayFocus);
			$tick.find(".payments").on("click", ".messages > a", setStatus);
			$ticks.append($tick);

			var $btn = $(templates.tickBtn(tKeys[i]));
			$btn.css("left", (100*(i+0.5)/tKeys.length)+"%");
			$btns.append($btn);
		}
		addNav($btns);
		addNav = $.noop;
	}

	window.drawTick = function(tKey, items, payments) {
		var $tick = $(".ticket[tKey="+tKey+"]");

		//Input
		var $items = $tick.find(".input .items");
		var nCurrItems = 0;
		$items.children().each(function() {
			nCurrItems += menu.parse($(this).find("input").val())==null?0:1;
		});
		if(nCurrItems == items.length) {
			$items.empty();
			for(var i = 0; i < items.length; i++)
				$items.append($(templates.itemIn(items[i].id,
												menu.unparse(items[i]))));
		}

		//Payments
		var payments = {};//status -> array of payment elems
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
			var $payment = $(template.payment(p.cID, statusString, p.focus,
								p.notification, p.pan, p.name,
								p.exprMonth, p.exprYear, p.cvv, lastFour));
			(payments[sCode] || (payments[sCode] = [])).push($payment);
			if(p.focus)
				fPayment = p;
		}
		var $payments = $tick.find(".payments ul");
		$payments.append.apply($payments, payments[modules.STATUS_NONE]);
		$payments.append.apply($payments, payments[modules.STATUS_FAIL]);
		$payments.append.apply($payments, payments[modules.STATUS_PAID]);

		//Filter by payer with focus
		if(fPayment != null) {
			var payFracs = {};
			for(var i = 0; i < fPayment.itemsToPay; i++)
				payFracs[fPayment.itemsToPay[i]] = {
					num: fPayment.payFracNums[i],
					denom: fPayment.payFracDenoms[i]
				}
			items = $.extend(true, {}, items);
			for(var i = items.length-1; i >= 0; i--) {
				frac = payFracs[items[i].id];
				if(frac == null)
					items.splice(i, 1);
				else {
					items[i].paidNum = frac.denom-frac.num;
					items[i].paidDenom = frac.denom;
				}
			}
		}

		//Info
		var summary = {	subtotal: 0, tax: 0, serviceCharge: 0, discount: 0,
						tip: (fPayment||{}).tip||0};
		$items = $tick.find(".info .items");
		for(var i = 0; i < items.length; i++) {
			var item = items[i];
			$items.append($(templates.itemDesc(item.name, item.price,
					item.mods.map(function(x) {
							return templates.map(x.name, x.price);
					})
			)));
			summary.subtotal += item.price;
			summary.tax += item.tax;
			summary.serviceCharge += item.serviceCharge;
			summary.discount += item.discount;
		}
		summary.total =	summary.subtotal + summary.tax +
						summary.serviceCharge - summary.discount +
						(summary.tip || 0);
		for(var i in summary)
			$tick.find(".info ."+i+" span").text(money.toStr(summary[i]));
	}
})();
