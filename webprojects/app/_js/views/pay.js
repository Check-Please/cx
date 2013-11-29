/*	The view for the pay page
 *
 *	@owner sjelin
 */

mvc.views = mvc.views || {};
(function () {
	"use strict";

	var invalidMsg;//The reason why the forn is invalid.  null for valid
	var $newCC;//The element of card dropdown for new credit cards
	var $view;//The object for the view

	function isNewCC()
	{
		return $view.find(".cards").val() == $newCC.text();
	}

	function luhnValid(pan)
	{
		return pan.split('').reduce(function(sum, d, n) {
			return sum + parseInt((n%2)? d : [0,2,4,6,8,1,3,5,7,9][d]);
		}, 0) % 10 == 0;
	}

	function update()
	{
		invalidMsg = null;
		if(isNewCC()) {
			$view.find(".unencrypted-cc").hide();
			$view.find(".encrypted-cc").hide();
			$view.find(".new-cc").show();

			if($view.find(".save input")[0].checked) {
				if(device.ccPassReq())
					$view.find(".new-cc .password").show();
				else {
					var pReq = false;
					if(device.ccPassAllowed())
						pReq = $view.find(".req-password").show()[0].checked;
					if(pReq) {
						$view.find(".new-cc .password").show();
						var warn = null;
						var password = $view.find(".password:visible").val();
						if(password.length < 8)
							warn =	"Your password should be at least "+
									"eight characters long";
						else if((password.length<12) &&
								password.match(/^\d+$/))
							warn =	"Your password should either be more "+
									"than just numbers or be very long";
						else if(password.match(/^[a-z]*$/i))
							warn =	"Your password should be more than just"+
									"a word";
						$view.find(".password span").toggle(warn != null
													).text(	warn || null);
					} else
						$view.find(".new-cc .password").hide();
				}	
			} else {
				$view.find(".req-password").hide();
				$view.find(".new-cc .password").hide();
			}
			var pan = $view.find(".pan input").val();
			var exprYear = $view.find(".expr-year input").val();
			var zip = $view.find(".zip input").val();
			if(pan.length < 8)
				invalidMsg = "Enter card number";
			else if(!pan.match(/^\d+$/))
				invalidMsg = "Your card number must be a number";
			else if(!pan.match(/^(?:62|88|2014|2149)/) && !luhnValid(pan))
				invalidMsg = "There is a typo in your card number";
			else if($view.find(".name input").val().length < 2)
				invalidMsg = "Enter name on card";
			else if((exprYear.length != 2) && (exprYear.length != 4))
				invalidMsg = "Enter expiration year";
			else if(!exprYear.match(/^\d+$/))
				invalidMsg = "The expiration year must be a number";
			else if(zip.length < 5)
				invalidMsg = "Enter zip code";
			else if(!zip.match(/^\d{5}(?:[-\s]\d{4})?/))
				invalidMsg = "The zip code is not correctly formatted";
			if((invalidMsg == null) && device.ccPassReq() &&
					($view.find(".password:visible").length > 0)) {
				var password = $view.find(".password:visible").val();
				if(password.length < 8)
					invalidMsg = "Password too short";
				else if(!password.match(/[0-9]/))
					invalidMsg = "Password should contain a number";
				else if(!password.match(/[a-z]/))
					invalidMsg ="Password should contain a lowercase letter";
				else if(!password.match(/[A-Z]/))
					invalidMsg ="Password should contain a uppercase letter";
				else if(password.match(/^[0-9a-z]$/i))
					invalidMsg =	"Password should be more than just "+
									"numbers and letters";
			}
		} else {
			var noPass = $view.find("option:selected").noPass != null;
			$view.find(".unencrypted-cc").toggle(noPass);
			$view.find(".encrypted-cc").toggle(!noPass);
			$view.find(".new-cc").hide();
		}
		if(invalidMsg==null) {
			var cvv = $view.find(".cvv input:visible").val();
			if((cvv != null) && (cvv.length < 3)) 
				invalidMsg = "Enter security code";
		}
		$view.find(".confirm")[(invalidMsg == null ? "remove" : "add")
						+ "Class"]("disabled");
		window.onresize();
	}

	var oldKeyDown = null;
	function hideInfographic()
	{
		window.onkeydown = oldKeyDown;
		oldKeyDown = null;
		$view.find(".cvv").removeClass("help-needed");
	}
	function showInfographic()
	{
		oldKeyDown = window.onkeydown;
		window.onkeydown = hideInfographic;
		$view.find(".cvv").addClass("help-needed");
	}

	/*	Deletes the currently selected card
	 */
	function deleteCard()
	{
		var $op = $view.find("option:selected");
		if(confirm("Delete card "+$op.text+"?")) {
			device.deleteCC($op.attr("db_key"));
			$op.remove();
			var $sel = $view.find(".cards");
			$sel[0].selectedIndex = $sel[0].options.length-1;
		}
	}

	mvc.views.pay = {
		build: function($trgt) {
			if(!$view) {
				$view = $(templates.pay(device.getCCs().map(function(cc) {
					var info = device.getCC(cc.key);
					if(info === undefined) {
						if(DEBUG)
							console.log("Warning: missing credit card data "+
										"with key "+cc.key);
						return "";
					} else
						return templates.cc(cc.key, cc.preview, !!info);
				}).join(""), device.ccPassAllowed(), !device.ccPassReq(),
						templates.cvv()));
				$newCC = $view.find(".cards option").filter(function() {
					return $(this).attr("db_key") == null;
				});
				$view.find("select").keydown(update);
				$view.find("input").keydown(update);
				$view.find("select").keyup(update);
				$view.find("input").keyup(update);
				$view.find("select").keypress(update);
				$view.find("input").keypress(update);
				$view.find("select").change(update);
				$view.find("input").change(update);
				$view.find(".cvv .explanation > a").click(showInfographic);
				$view.find(".cvv .popup-rapper").click(hideInfographic);
				$view.find(".confirm").click(cBtn);
				$view.find("a.delete").click(deleteCard);
				update();
				$trgt.append($view);
			} else
				$view.show();
		},
		unbuild:  function() {
			$view.hide();
			if(oldKeyDown != null)
				window.onkeydown = oldKeyDown;
		},
		redirect: function() {
			if(!mvc.views.split.valid())
				return mvc.views.split;
			else if(mvc.tip() == null)
				return mvc.views.receipt;
		},
		onResize: function() {
			//Use different images for the SSL logo when possible
			var $ssl = $view.find("img.ssl");
			var sslH = $ssl.height();
			$ssl.attr("src", "img/app/ssl_" + (sslH >= 55 ? "58" :
										sslH >= 37 ?  "52" : "22") + ".png");
		}
	}

	/*	The function called when the user pressed the confirm button
	 */
	function cBtn()
	{
		if(invalidMsg != null) {
			alert(invalidMsg);
			return;
		}
		var $btn = $view.find(".confirm");
		if($btn.hasClass("loading"))
			return;
		$btn.addClass("loading");

		if(isNewCC()) {
			var pan = $view.find(".pan input").val();
			var name = $view.find(".name input").val();
			var expr =	$view.find(".expr-year input").val().slice(-2) +
						$view.find(".expr-month select").val().slice(0,2);
			var zip = $view.find(".zip input").val();
			var cvv = $view.find(".cvv input:visible").val();
			if($view.find(".save input")[0].checked)
				setTimeout(device.storeCC.c(pan, name, expr, zip,
					$view.find(".password input:visible").val()) || null, 0);
			pay(pan, name, expr, zip, cvv);
		} else setTimeout(function() {
			var cc = device.getCC($view.find(":selected").attr("db_key"),
					$view.find(".password input:visible").val() || null);
			if(cc === undefined) {
				alert(	"Credit card data missing.  You should probably "+
						"delete this card.");
				$btn.removeClass("loading");
			} else if(cc == null) {
				alert("Incorrect password");
				$btn.removeClass("loading");
			} else
				pay(cc.pan, cc.name, cc.expr, cc.zip,
					$view.find(".cvv input:visible").val());
		}, 0);
	}

	/*	Pays.  When finished paying, removes the "loading" class
	 */
	function pay(pan, name, expr, zip, cvv)
	{
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
			tableKey: mvc.key(),
			connectionID: mvc.connectionID(),
			pan: pan,
			name: name,
			expr: expr,
			zip: zip,
			cvv: cvv,
			items: items,
			nums: nums,
			denoms: denoms,
			total:	mvcItems.subtotal + mvcItems.tax -
					mvcItems.discount + mvcItems.serviceCharge,
			tip: mvc.tip()
		}, function(resp) {
			resp = JSON.parse(resp);
			if(resp.loadMsg == null) {
				mvc.done(resp.done);
				mvc.paid(true);
			} else {
				mvc.loading(resp.loadMsg);
			}
			$(".loading").removeClass("loading");
		}, buildAjaxErrFun("pay"));
	}
})();
