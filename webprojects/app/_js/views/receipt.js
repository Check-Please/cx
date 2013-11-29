/*	The view for the receipt page
 *
 *	@owner sjelin
 */

mvc.views = mvc.views || {};

(function () {
	"use strict";

	var $view; //The element which corresponds to this view
	var total; //The total cost on the receipt
	var $tipPrct; //The element which shows what percentage the tip is
	function calcTipPercent()
	{
		$tipPrct = $tipPrct || $view.find(".tipPrct");
		var t = mvc.tip();
		$view.find(".tip .percent").removeClass("not-picked");
		if(t == null) {
			$tipPrct.text("");
			$view.find(".confirm").addClass("disabled");
		} else {
			$view.find(".tip .percent").addClass("not-picked");
			var prctName = "other";
			$view.find(".confirm").removeClass("disabled");
			if(t >= 1000000*total)
				$tipPrct.text("Thank you!");
			else if(t >= 10000*total)
				$tipPrct.text("("+Math.round(t/total)+"x)");
			else if(t >= 10*total)
				$tipPrct.text("("+Math.round(t*100/total)+"%)");
			else {
				var prct = Math.round(t*10000/total)/100;
				var moe = Math.min(1, Math.max(0.1, 100/total));
				if((prct > 17-moe) && (prct < 17+moe))
					prctName = "seventeen";
				else if((prct > 20-moe) && (prct < 20+moe))
					prctName = "twenty";
				else if((prct > 25-moe) && (prct < 25+moe))
					prctName = "twentyfive";
				$tipPrct.text("("+prct+"%)");
			}
			$view.find(".tip ."+prctName).removeClass("not-picked");
		}
	}

	var $container;//Contains the receipt items
	function render()
	{
		var items = mvc.processedItems(true);
		total = items.subtotal;
		$container.empty();
		for(var i = 0; i < items.length; i++) {
			var item = items[i];
			$container.append($(templates.receiptItem(item.name,
				money.toStr(item.price),
				item.mods.map(function(mod) {
					return templates.itemMod(mod.name, mod.price == 0 ? "":
						money.toStr(mod.price));}).join(""), "")));
		}
		var space=templates.receiptItem("","--------","","divider");
		$container.append($(space));
		$container.append($(templates.receiptItem("Subtotal",
				money.toStr(total), "", "subtotal")));
		if(items.discount != 0)
			$container.append($(templates.receiptItem("Discount",
				money.toStr(-items.discount), "", "discount")));
		if(items.serviceCharge != 0)
			$container.append($(templates.receiptItem("Service Charge",
				money.toStr(items.serviceCharge), "", "service-charge")));
		$container.append($(templates.receiptItem("Tax",
				money.toStr(items.tax), "", "tax")));
		$container.append($(space));
		total += items.tax-items.discount+items.serviceCharge;
		$container.append($(templates.receiptItem("Total",
				money.toStr(total), "", "total")));
		calcTipPercent();
	}

	var $tipBox;//The input dialogue which the top is typed into
	var oldTip = "";//What the tip was before the most recent change
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

	mvc.views.receipt = {
		build: function($trgt) {
			function setTipByPrct(prct) {
				$tipBox.val(money.toStr(money.round(total*prct/100), ""));
				updateTip();
			}
			if(!$view) {
				var re=/^(.*?),?([^\\,]+,?[a-z ]{2,},?\s*[0-9\\-]{0,10})$/i;
				var splitAddress = re.exec(mvc.restrAddress().trim())
				$view = $(templates.receipt(mvc.restrName(),mvc.restrStyle(),
											splitAddress[1], splitAddress[2],
											"<REPLACE_ME />"));
				$container = $view.find("REPLACE_ME").parent();
				render();
				$tipBox = $view.find(".tip input");
				$view.find(".tip .percent.seventeen").click(
						setTipByPrct.c(17));
				$view.find(".tip .percent.twenty").click(
						setTipByPrct.c(20));
				$view.find(".tip .percent.twentyfive").click(
						setTipByPrct.c(25));
				$view.find(".tip .percent.other").click(function() {
					$tipBox.val(money.toStr(Math.round(
								[200, total/3.3].max()), ""));
					updateTip();
				});
				$tipBox.keyup(updateTip);
				$tipBox.keydown(updateTip);
				$tipBox.keypress(updateTip);
				updateTip();
				mvc.items.listen(render);
				mvc.split.listen(render);
				mvc.selection.listen(render);
				mvc.tip.listen(calcTipPercent);
				$trgt.append($view);
			} else
				$view.show();
		},
		redirect: function() {
			if((mvc.split() != null) && !mvc.views.split.valid())
				return mvc.views.split;
		},
		unbuild: function() {$view.hide();},
		nextView: function() {
			if(mvc.tip() == null)
				alert("Please enter a tip!");
			else
				return mvc.views.pay;
		}
		
	}
})();
