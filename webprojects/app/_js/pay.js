/*	The MVC for the pay page
 *
 *	@see mvc.js
 *	@owner sjelin
 */

  ////////////////////
 /////  HEADER  /////
////////////////////

var mvc = mvc || {};

(function () {
	"use strict";

  /////////////////////////////////
 /////  CODE (IN NAMESPACE)  /////
/////////////////////////////////

	var complete;
	function completePayment()
	{
		return complete;
	}

	var $newCC = null;
	function update()
	{
		if($("#pay-page .cards").val() == $newCC.text()) {
			$("#pay-page").addClass("new-cc-needed");
			complete =	($("#pay-page .pan input").val().length >= 8) &&
						($("#pay-page .name input").val().length >= 2) &&
						($("#pay-page .expr-year input").val().length >= 2)&&
						($("#pay-page .cvv input").val().length >= 1) &&
						($("#pay-page .zip input").val().length >= 5);
		} else {
			$("#pay-page").removeClass("new-cc-needed");
			complete = true;
		}
		window.onresize();
		$("#pay-page .confirm")[(complete ? "remove" : "add") +
													"Class"]("disabled");
	}

	var oldKeyDown = null;
	function hideInfographic()
	{
		window.onkeydown = oldKeyDown;
		oldKeyDown = null;
		$("#pay-page .cvv").removeClass("help-needed");
	}
	function showInfographic()
	{
		oldKeyDown = window.onkeydown;
		window.onkeydown = hideInfographic;
		$("#pay-page .cvv").addClass("help-needed");
	}

	var cardMap;
	function populateCardsDropdown()
	{
		if($newCC == null)
			return;
		cardMap = {};
		var $cards = $newCC.parent();
		$cards.empty();
		var cards = mvc.cards();
		cards.sort(function(x,y) {return x.lastUse-y.lastUse;});
		//HACK: We do NOT notify because that would cause an infinite loop
		var initVal = null;
		for(var i = 0; i < cards.length; i++) {
			var info = cards[i];
			var pan = info.prefix;
			while(pan.length < info.len-4)
				pan += "X";
			pan += (info.lastFour<10 ? "   " : info.lastFour<100 ? "  " :
						info.lastFour < 1000 ? " " : "") + info.lastFour;
			cardMap[pan] = info.uuid;
			$cards.append($(template.cxCC(pan)));
			if(i == 0)
				initVal = pan;
		}
		$cards.append($newCC);
		if(initVal != null)
			$cards.val(initVal);
		update();
	}
	mvc.addCardsListener(populateCardsDropdown);
	mvc.addUsernameListener(function() {
		$("#pay-page .username").text(mvc.username());
	});
	function buildPay()
	{
		if($newCC == null) {
			$newCC = $("#pay-page .REPLACE-ME").siblings();
			populateCardsDropdown();
			$("#pay-page select").keydown(update);
			$("#pay-page input").keydown(update);
			$("#pay-page select").keyup(update);
			$("#pay-page input").keyup(update);
			$("#pay-page select").keypress(update);
			$("#pay-page input").keypress(update);
			$("#pay-page select").change(update);
			$("#pay-page input").change(update);
			$("#pay-page .cvv .explanation > a").click(showInfographic);
			$("#pay-page .cvv .infographic").click(hideInfographic);
			$("#pay-page .cvv .infographic > div").click(function(e)
					{ e.stopPropagation(); });
			$("#pay-page .cvv .infographic a").click(hideInfographic);
		}
		mvc.unbuild = function() {
			if(oldKeyDown != null)
				window.onkeydown = oldKeyDown;
		}
	}

	function pay(callback)
	{
		var $btn = $("#login-page .confirm");
		if($btn.hasClass("loading"))
			return;
		$btn.addClass("loading");
		function myPay(card) {
			var items = [];
			var nums = [];
			var denoms = [];
			var mvcItems = mvc.processedItems(true);
			for(var i = 0; i < mvcItems.length; i++) {
				var item = mvcItems[i];
				items.push(item.id);
				nums.push(item.num);
				denoms.push(item.denom);
			}
			ajax.send("cx", "pay", {
				mobileKey: mvc.key(),
				clientID: mvc.clientID(),
				cardUUID: card,
				items: items,
				nums: nums,
				denoms: denoms,
				total:	mvcItems.subtotal + mvcItems.tax -
						mvcItems.discount + mvcItems.serviceCharge,
				tip: mvc.tip()
			}, function(resp) {
				$btn.removeClass("loading");
				resp = JSON.parse(resp);
				mvc.done(resp.done);
				mvc.paid(true);
				callback();
			}, buildAjaxErrFun("pay"));
		}
		if($("#pay-page .cards").val() == $newCC.text()) {
			var pan = $("#pay-page .pan input").val().trim();
			ajax.send("user", "add_cc", {
				PAN: pan,
				name: $("#pay-page .name input").val().trim(),
				exprYear: $("#pay-page .expr-year input").val().trim(),
				exprMonth: parseInt($("#pay-page .expr-month select").val(
						).trim().substr(0,2).trim()),
				CVV: $("#pay-page .cvv input").val().trim(),
				zip: $("#pay-page .zip input").val().trim()
			}, function(cardUUID) {
				cardUUID = cardUUID.trim();
				var cards = mvc.cards();
				var prefixLen = cards.length && cards[0].prefix.length;
				cards.splice(0, 0, {
					prefix: pan.substr(0, prefixLen),
					lastFour: pan.substr(-4),
					uuid: cardUUID,
					len: pan.length
				});
				mvc.notifyCards();
				myPay(cardUUID);
			}, buildAjaxErrFun("add a credit card"));
		} else
			myPay(cardMap[$("#pay-page .cards").val()]);
	}

  ////////////////////
 /////  FOOTER  /////
////////////////////

	mvc.buildPay = buildPay;
	mvc.completePayment = completePayment;
	mvc.pay = pay;
})();
