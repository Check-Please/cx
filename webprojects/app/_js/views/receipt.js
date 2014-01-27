/*	The view for the receipt page
 *
 *	@owner sjelin
 */

mvc.views = mvc.views || {};

(function () {
	"use strict";

	var $view; //The element which corresponds to this view
	var tipable; //The ammount of money which is tipable
	var $tipPrct; //The element which shows what percentage the tip is
	var tipPercent=20; //The desired % for the tip.  null if tip not set by %
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
			if(t >= 1000000*tipable)
				$tipPrct.text("Thank you!");
			else if(t >= 10000*tipable)
				$tipPrct.text("("+Math.round(t/tipable)+"x)");
			else if(t >= 10*tipable)
				$tipPrct.text("("+Math.round(t*100/tipable)+"%)");
			else {
				var prct = Math.round(t*10000/tipable)/100;
				var moe = Math.min(1, Math.max(0.1, 100/tipable));
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
		tipable = items.tipable;
		$container.empty();
		for(var i = 0; i < items.length; i++) {
			var item = items[i];
			$container.append($(templates.receiptItem(item.name,
				money.toStr(item.price - item.discount),
				item.mods.map(function(mod) {
					return templates.itemMod(mod.name, mod.price == 0 ? "":
						money.toStr(mod.price-mod.discount));}).join(""), "")
			));
		}
		var space=templates.receiptItem("","--------","","divider");
		$container.append($(space));
		$container.append($(templates.receiptItem("Subtotal",
				money.toStr(items.subtotal), "", "subtotal")));
		if(items.fee != 0)
			$container.append($(templates.receiptItem("Fee",
				money.toStr(items.fee), "", "fee")));
		if(items.discount != 0)
			$container.append($(templates.receiptItem("Discount",
				money.toStr(-items.discount), "", "discount")));
		if(items.serviceCharge != 0)
			$container.append($(templates.receiptItem("Service Charge",
				money.toStr(items.serviceCharge), "", "service-charge")));
		$container.append($(templates.receiptItem("Tax",
				money.toStr(items.tax), "", "tax")));
		$container.append($(space));
		$container.append($(templates.receiptItem("Total",
				money.toStr(items.total), "", "total")));
		calcTipPercent();
	}

	var $tipBox;//The input dialogue which the top is typed into
	var oldTip = "";//What the tip was before the most recent change
	function setTipStr(tipStr)
	{
		if(tipStr != $tipBox.val())
			$tipBox.val(tipStr);
		if(tipStr != oldTip) {
			oldTip = tipStr;
			mvc.tip((tipStr.length == 0) || (tipStr == ".") ? null :
							Math.round(parseFloat(tipStr)*100));
		}
	}
	function parseTip()
	{
		tipPercent = null;
		var newTip = $tipBox.val();
		setTipStr(newTip.match(/^[0-9]*\.?[0-9]{0,2}$/) ? newTip : oldTip);
	}
	function setTipByPrct(prct) {
		tipPercent = prct;
		var tip = money.toStr(money.round(tipable*prct/100), "");
		setTipStr(tip);
	}

	var $splitBtn;
	mvc.views.receipt = {
		title: "Receipt",
		build: function($trgt, oldView) {
			if((oldView == mvc.views.split) && !mvc.views.split.valid()) {
				device.ajax.send("cx/split", "cancel", {
					tableKey: mvc.key(),
					connectionID: mvc.connectionID()
				}, $.noop);
				mvc.split(null);
			}
			if(!$view) {
				var re= /^(.*?),?([^\\,]+,?[a-z ]{2,},?\s*[0-9\\-]{0,10})$/i;
				var splitAddress = re.exec(mvc.restrAddress().trim())
				$view = $(templates.receipt(mvc.restrName(),mvc.receiptImg(),
								splitAddress[1], splitAddress[2],
								"<REPLACE_ME />","<R_ME_2 />","{{SERVER}}"));
				$container = $view.find("REPLACE_ME").parent();
				$splitBtn = $view.find("R_ME_2").parent();
				$splitBtn.text(templates.splitText(mvc.split() != null));
				render();
				$tipBox = $view.find(".tip input");
				$view.find(".tip .percent.seventeen").click(
						setTipByPrct.c(17));
				$view.find(".tip .percent.twenty").click(
						setTipByPrct.c(20));
				$view.find(".tip .percent.twentyfive").click(
						setTipByPrct.c(25));
				$view.find(".tip .percent.other").click(function() {
					$tipBox.val("");
					$tipBox.focus();
					parseTip();
				});
				$tipBox.keyup(parseTip);
				$tipBox.keydown(parseTip);
				$tipBox.keypress(parseTip);
				mvc.items.listen(render);
				mvc.split.listen(render);
				mvc.selection.listen(render);
				mvc.tip.listen(calcTipPercent);
				$trgt.append($view);
			} else {
				$splitBtn.text(templates.splitText(mvc.split() != null));
				$view.show();
			}
			if(tipPercent != null)
				setTipByPrct(tipPercent);
			else
				parseTip();
		},
		redirect: function(prevView) {
			if((mvc.split() != null) && (prevView != mvc.views.split) &&
					!mvc.views.split.valid())
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
