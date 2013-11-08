/*	The MVC for the receipt page
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

	var total;
	var $tipPrct
	function calcTipPercent()
	{
		$tipPrct = $tipPrct || $("#receipt-page .tipPrct");
		var t = mvc.tip();
		$("#receipt-page .tip .percent").removeClass("not-picked");
		if(t == null) {
			$tipPrct.text("");
			$("#receipt-page .confirm").addClass("disabled");
		} else {
			$("#receipt-page .tip .percent").addClass("not-picked");
			var prctName = "other";
			$("#receipt-page .confirm").removeClass("disabled");
			if(t >= 1000000*total)
				$tipPrct.text("Thank you!");
			else if(t >= 10000*total)
				$tipPrct.text("("+Math.round(t/total)+"x)");
			else if(t >= 10*total)
				$tipPrct.text("("+Math.round(t*100/total)+"%)");
			else {
				var prct = Math.round(t*10000/total)/100;
				var moe = Math.min(1, Math.max(0.1, 100/total));
				if((prct > 15-moe) && (prct < 15+moe))
					prctName = "fifteen";
				else if((prct > 17-moe) && (prct < 17+moe))
					prctName = "seventeen";
				else if((prct > 20-moe) && (prct < 20+moe))
					prctName = "twenty";
				$tipPrct.text("("+prct+"%)");
			}
			$("#receipt-page .tip ."+prctName).removeClass("not-picked");
		}
	}

	var $container = null;
	function render()
	{
		$container = $container || $("#receipt-page .REPLACE-ME").parent();
		var items = mvc.processedItems(true);
		total = items.subtotal;
		$container.empty();
		for(var i = 0; i < items.length; i++) {
			var item = items[i];
			$container.append($(template.cxReceiptItem(item.name,
				money.toStr(item.price),
				item.mods.map(function(mod) {
					return template.cxItemMod(mod.name, mod.price == 0 ? "" :
						money.toStr(mod.price));}).join(""), "")));
		}
		var space=template.cxReceiptItem("","--------","","divider");
		$container.append($(space));
		$container.append($(template.cxReceiptItem("Subtotal",
				money.toStr(total), "", "subtotal")));
		if(items.discount != 0)
			$container.append($(template.cxReceiptItem("Discount",
				money.toStr(-items.discount), "", "discount")));
		if(items.serviceCharge != 0)
			$container.append($(template.cxReceiptItem("Service Charge",
				money.toStr(items.serviceCharge), "", "service-charge")));
		$container.append($(template.cxReceiptItem("Tax",
				money.toStr(items.tax), "", "tax")));
		$container.append($(space));
		total += items.tax-items.discount+items.serviceCharge;
		$container.append($(template.cxReceiptItem("Total",
				money.toStr(total), "", "total")));
		calcTipPercent();
	}

	var $tipBox;
	var oldTip = "";
	function updateTip()
	{
		var newTip = $tipBox.val();
		if(newTip != oldTip) {
			if(newTip.match(/^[0-9]*\.?[0-9]{0,2}$/)) {
				oldTip = newTip;
				mvc.tip(newTip.length == 0 || newTip == "." ? null :
						Math.round(parseFloat(newTip)*100));
			} else
				$tipBox.val(oldTip);
		}
	}

	var inited = false;
	function buildReceipt()
	{
		function setTipByPrct(prct) {
			$tipBox.val(money.toStr(money.round(total*prct/100), ""));
			updateTip();
		}
		if(!inited) {
			render();
			$tipBox = $("#receipt-page .tip input");
			$("#receipt-page .tip .percent.fifteen").click(
					setTipByPrct.c(15));
			$("#receipt-page .tip .percent.seventeen").click(
					setTipByPrct.c(17));
			$("#receipt-page .tip .percent.twenty").click(
					setTipByPrct.c(20));
			$("#receipt-page .tip .percent.other").click(function() {
				$tipBox.val(money.toStr(Math.round([200,total/4].max()),""));
				updateTip();
			});
			$tipBox.keyup(updateTip);
			$tipBox.keydown(updateTip);
			$tipBox.keypress(updateTip);
			updateTip();
			inited = true;
		}
	}

	mvc.addItemsListener(render);
	mvc.addSplitListener(render);
	mvc.addSelectionListener(render);
	mvc.addTipListener(calcTipPercent);

  ////////////////////
 /////  FOOTER  /////
////////////////////

	mvc.buildReceipt = buildReceipt;
})();
