var template = template || {};

template.cxCC = function(partialNum) {
	return	"<option>"+(partialNum)+"</option>";
};var template = template || {};

template.cxErr = function(msg) {
	return	"<div id=\"err-page\">\n"+
			"\t<h1>Sorry!</h1>\n"+
			"\t<p>Something went wrong.  We\'re getting the following error message:</p>\n"+
			"\t<p class=\"error\">"+(msg)+"</p>\n"+
			"\t<p>Please pay your bill through the waitstaff</p>\n"+
			"</div>";
};var template = template || {};

template.cxItemMod = function(name, price) {
	return	"<li class=\"mod\">\n"+
			"\t<span class=\"name\">"+(name)+"</span>\n"+
			"\t<span class=\"price\">"+(price)+"</span>\n"+
			"</li>";
};var template = template || {};

template.cxReceiptItem = function(name, price, mods, custom_class) {
	return	"<li class=\"item "+(custom_class)+"\">\n"+
			"\t<span class=\"name\">"+(name)+"</span>\n"+
			"\t<span class=\"price\">"+(price)+"</span>\n"+
			"\t<ul class=\"mods\">"+(mods)+"</ul>\n"+
			"</li>";
};var template = template || {};

template.cxSplitItem = function(id, selected, others, name, price, mods) {
	return	"<li class=\"item "+(selected ? "selected":"")+" "+(others>0 ? "has-others":"")+"\">\n"+
			"\t<a itemID=\""+(id)+"\">\n"+
			"\t\t<span class=\"name\">"+(name)+"</span>\n"+
			"\t\t<span class=\"price\">"+(price)+"</span>\n"+
			"\t\t<ul class=\"mods\">"+(mods)+"</ul>\n"+
			"\t</a>\n"+
			"\t<div class=\"others\">"+
				(selected ? "Splitting with "+others+" other"+(others != 1 ? "s" : "") : others == 1 ? "Someone else is paying for this" : others+" others are splitting this")+
			"</div>\n"+
			"</li>";
};var template = template || {};

template.cxReload = function() {
	return	"<div id=\"reload\">\n"+
			"\tPlease reload\n"+
			"</div>";
};/*	This is a very small and fragmented MVC.  Essentially, each page has its
 *	own MVC (except the page where we ask if the user wants to split the
 *	bill - one is not needed there).  The MVC for each page is responsible
 *	for all the dynamic elements, local state, and communication to the
 *	server.  Additionally, there is a small ammount of shared state,
 *	limited to the following:
 *		key - The code identifying the restaurant/table
 *		clientID - Used to tell the server who changed something
 *		items - The items on the ticket
 *		split - The way the items are split between the payers
 *		selection - The items this user has picked
 *		tip - How much the user has tipped
 *		username - The username of the user
 *		cards - The credit cards of the user
 *		paid - Whether or not this client has paid
 *		done - Whether or not the ticket has been paid in full
 *		err - The error message for the ticket (null if there are none)
 *
 *	The following functions can be used to interact with these values:
 *		{VAR_NAME}() - Get the value of a given variable
 *		{VAR_NAME}(val) - Set the value of a given variable
 *		add{VAR_NAME}Listener(l) - Adds a listener for a given variable
 *		notify{VAR_NAME}() - Calls all the listenrs for a given variable
 *	camelCase is used for all these funtion names.  Note that the setter will
 *	automatically call the listeners.  However, if because the values
 *	returned by the getter are not deep copies, it is posible to change a
 *	shared value without using a setter.  In such a case, the notify function
 *	should be called so that the listeners will be called.
 *
 *	This file is responsible for setting up this shared information, and thus
 *	it must be included first.
 *
 *	There are also two utility functions included in this file: mvc.init and
 *	mvc.processedItems
 *
 *	Each page's MVC should have a build function (e.g. buildSplit() or
 *	buildLogin()) which will be run when the page is navigated to.  The MVCs
 *	can then optionally set an unbuild function at mvc.unbuild, which will be
 *	run when the page is navigated away from.
 *
 *	The MVC does not handle navigation or incomming server communication.
 *	These tasks are handled by the nav and socket modules. These modules call
 *	the MVC, but the MVC does not call them.  During a build function, the
 *	MVC may attach some functions to DOM elements, but other than that it
 *	waits to be called by other code.
 *
 *	@owner sjelin
 */

var mvc = {};

(function () {
	"use strict";

	var names = ['key', 'clientID', 'items', 'split', 'selection', 'tip', 'username', 'cards', 'paid', 'done', 'err'];
	var vals = {};

	names.forEach(function(name) {
		function myNotify(old) {
			callAll(ls, arguments.length > 0 ? old : vals[name]);
		}

		var ls = [];
		var capName = name.charAt(0).toUpperCase()+name.substr(1);
		mvc[name] = function(v) {
			if(arguments.length == 0)
				return vals[name];
			var old = vals[name];
			vals[name] = v;
			myNotify(old);
			window.onresize();
		};
		mvc["add"+capName+"Listener"] = function(f) {ls.push(f);};
		mvc["notify"+capName] = op.call.c(myNotify);
	});

	mvc.init = function()
	{
		for(var i = 0; i < names.length; i++)
			vals[names[i]] = arguments[i];
	}

	mvc.processedSplit = function()
	{
		var rawSplit = mvc.split();
		if(rawSplit == null)
			return null;
		var split = {};
		for(var cid in rawSplit)
			for(var i = 0; i < rawSplit[cid].length; i++) {
				var iid = rawSplit[cid][i];
				split[iid] = (split[iid] || 0) + 1;
			}
		return split;
	}

	mvc.processedItems = function(useSplit, includePaidItems)
	{
		var ret = [];
		ret.subtotal = 0;
		ret.tax = 0;
		ret.discount = 0;
		ret.serviceCharge = 0;
		var items = mvc.items();
		var split = mvc.processedSplit();
		var selection = mvc.selection();
		for(var i = 0; i < items.length; i++) {
			var item = items[i];
			var id = item.id;
			if((!useSplit || (split == null) || selection[id]) &&
				   ((item.paidNum < item.paidDenom) || includePaidItems)) {
				var num = item.paidDenom-item.paidNum;
				var denom=item.paidDenom*(1+(useSplit&&split&&split[id]||0));
				var gcd = Math.gcd(num, denom);
				num /= gcd;
				denom /= gcd;
				var price = (item.price||0)*num/denom/100;
				var tax = (item.tax||0)*num/denom/100;
				var discount = (item.discount||0)*num/denom/100;
				var serviceCharge = (item.serviceCharge||0)*num/denom/100;
				ret.push({
					id: id,
					num: num,
					denom: denom,
					name: (denom==1 ? "" : "("+num+"/"+denom+") ")+item.name,
					price: money.round(price),
					tax: money.round(tax),
					discount: money.round(discount),
					serviceCharge: money.round(serviceCharge),
					mods: item.mods
				});
				for(var j = 0; j < item.mods.length; j++)
					price += (item.mods[j].price||0)*num/denom/100;
				ret.subtotal += price;
				ret.tax += tax;
				ret.discount += discount;
				ret.serviceCharge += serviceCharge;
			}
		}
		ret.subtotal = money.round(ret.subtotal);
		ret.tax = money.round(ret.tax);
		ret.discount = money.round(ret.discount);
		ret.serviceCharge = money.round(ret.serviceCharge);
		return ret;
	}
})();
/*	The MVC for the split page
 *
 *	@see mvc.js
 *	@owner sjelin
 */

  ////////////////////
 /////  HEADER  /////
////////////////////

(function () {
	"use strict";

  /////////////////////////////////
 /////  CODE (IN NAMESPACE)  /////
/////////////////////////////////

	var items = null;
	var elems = null;

	function send(cmd, params, f1, f2, f3) {
		params = params || {};
		params.mobileKey = mvc.key();
		params.clientID = mvc.clientID();
		ajax.send("cx/split", cmd, params, f1, f2, f3);
	};

	function toggleSelection()
	{
		var id = $(this).attr("itemID");
		var selection = mvc.selection();
		send((selection[id]=!selection[id]) ? 'add' : 'remove', {itemID:id});
		mvc.notifySelection();
	}

	var inited = false;
	function buildSplit()
	{
		if(!inited) {
			inited = true;
			$("#split-page .items").on("click", "a", toggleSelection);
			buildItems();
		}
		if(mvc.split() == null) {
			mvc.split({});
			send('start');
		}
	}

	function updateItem(id)
	{
		var info = items[id];
		var on = mvc.selection() != null && !!mvc.selection()[id];
		var others = mvc.split() == null ? 0 : (mvc.processedSplit()[id]||0);
		var mods = info.mods.map(function(mod) {
			return template.cxItemMod(mod.name,
				mod.price ?  money.toStr(mod.price) : "");
		}).join("");
		var $item = $(template.cxSplitItem(id, on, others, info.name, 
					money.toStr(info.price), mods));
		if(elems[id] != null)
			elems[id].replaceWith($item);
		elems[id] = $item;
	}

	function buildItems()
	{
		var mvcItems = mvc.processedItems();
		if(mvcItems.length == 0)
			return;//The order has been paid (hopefully)

		var $trgt = null;
		if(elems == null)
			$trgt = $("#split-page .REPLACE-ME");
		else for(var id in elems)
			if($trgt == null)
				$trgt = elems[id];
			else
				elems[id].remove();
		elems = {};

		var ids = [];
		items = {};
		for(var i = 0; i < mvcItems.length; i++) {
			var id = mvcItems[i].id;
			items[id] = mvcItems[i];
			updateItem(id);
			ids.push(id);
		}
		$trgt.replaceWith(ids.map(function(id){return elems[id].get(0);}));
		updateConfirm();
	}

	function update()
	{
		for(var id in items)
			updateItem(id);
		updateConfirm();
	}

	function validSplit()
	{
		if(items == null)
			return mvc.processedSplit() == null;
		var sel = mvc.selection() || {};
		var sp = mvc.processedSplit() || {};
		for(var id in items)
			if(!sel[id] && !sp[id])
				return false;
		return true;
	}

	function updateConfirm()
	{
		$("#split-page .confirm")[(validSplit()?"remove":"add")+"Class"](
				"disabled");
	}

	mvc.addItemsListener(buildItems);
	mvc.addSplitListener(update);
	mvc.addSelectionListener(update);

  ////////////////////
 /////  FOOTER  /////
////////////////////

	mvc.buildSplit = buildSplit;
	mvc.validSplit = validSplit;
})();
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
/*	The MVC for the login page
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


	var $loginCb = null;
	var $registerCb = null;
	var complete;

	function validate()
	{
		complete = ($("#login-page .info .email input").val().length != 0) &&
			($("#login-page .info .password input").val().length != 0) &&
			(!$registerCb[0].checked ||
				($("#login-page .info .confirm-password input"
					).val().length != 0));
		$("#login-page .confirm")[(complete ? "remove" : "add")
					+ "Class"]("disabled");
	}

	function completeLogin()
	{
		return complete;
	}

	function refresh()
	{
		var register = $registerCb[0].checked;
		$("#login-page .info").addClass(register ? "register" : "login");
		$("#login-page .info").removeClass(register ? "login":"register");
		$("#login-page .confirm").text(register ? "Register" : "Login");
		window.onresize();
		validate();
	}

	var oldKeyDown = null;
	function buildLogin()
	{
		function setCbs(login) {
			$loginCb[0].checked = login;
			$registerCb[0].checked = !login;
			refresh();
		}
		if($loginCb == null) {
			$loginCb = $("#login-page .lor .login input");
			$registerCb = $("#login-page .lor .register input");
			$("#login-page .lor .login").click(setCbs.c(true));
			$("#login-page .lor .register").click(setCbs.c(false));
			$("#login-page .info input").keydown(validate);
			$("#login-page .info input").keyup(validate);
			$("#login-page .info input").keypress(validate);
			$("#login-page .agreements > a").click(function() {
				oldKeyDown = window.onkeydown;
				window.onkeydown = function(x) {
					if(x.keyCode == 13)
						$("#login-page .agreements .agree:visible").click();
				}
				$("#login-page .agreements .popup." + 
					$(this).attr("class")).addClass("display");
			});
			$("#login-page .agreements .agree").click(function() {
				window.onkeydown = oldKeyDown;
				oldKeyDown = null;
				$(this).parent().parent().removeClass("display");
			});
			$("#login-page .agreements .disagree").click(function() {
				window.onkeydown = oldKeyDown;
				oldKeyDown = null;
				mvc.err("You must agree to both the Terms of Use and the " +
						"Privacy Policy in order to use this app.");
			});
		}
		mvc.unbuild = function() {
			if(oldKeyDown != null)
				window.onkeydown = oldKeyDown;
		}
		refresh();
	}

	function login(callback)
	{
		var $btn = $("#login-page .confirm");
		if($btn.hasClass("loading"))
			return;
		var email = $("#login-page .info .email input").val();
		var password = $("#login-page .info .password input").val();
		function send(cmd) {
			$btn.addClass("loading");
			ajax.send('user', cmd, {
				username: email,
				password: password
			},  function(resp) {
				$btn.removeClass("loading");
				mvc.username(email);
				mvc.cards(JSON.parse(resp));
				callback();
			}, buildAjaxErrFun(cmd));
		}
		if(!isEmail(email))
			alert("The email address you entered is invalid");
		else if($registerCb[0].checked) {
			if(password.length < 6)
				alert("Password is too short");
			else if(/^[0-9]*$/.test(password) && password.length < 30)
				alert("Password is not strong enough.  Either make it very long or add symbols/letters");
			else if(/^[a-zA-Z]*$/.test(password) && password.length < 20)
				alert("Password is not strong enough.  Either make it very long or add symbols/numbers");
			else if(/^[a-zA-Z0-9]*$/.test(password) && password.length<16)
				alert("Password is not strong enough.  Either make it longer or add symbols");
			else if(password != 
					$("#login-page .info .confirm-password input").val())
				alert("Passwords do not match");
			else
				send('register');
		} else
			send('login');
	}

  ////////////////////
 /////  FOOTER  /////
////////////////////

	mvc.buildLogin = buildLogin;
	mvc.completeLogin = completeLogin;
	mvc.login = login;
})();
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
/*	The MVC for the feedback page
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

	var inited = false;
	function buildFeedback()
	{
		if(!inited) {
			inited = true;
			$("#feedback-page .ratings").on("click", "a", function() {
				var $this = $(this);
				var rating;
				if($this.hasClass("bad"))
					rating = -1;
				else if($this.hasClass("ok"))
					rating = 0;
				else if($this.hasClass("just"))
					rating = 1;
				else
					rating = 2;
				$("#feedback-page .ratings a").addClass("not-picked");
				$this.removeClass("not-picked");
				ajax.send("cx", "rate", {
					mobileKey: mvc.key(),
					clientID: mvc.clientID(),
					rating: rating
				}, $.noop, buildAjaxErrFun("send rating"));
			});
		}
		$("#feedback-page")[(mvc.done()?"add":"remove")+"Class"]("not-done");
	}
	mvc.addDoneListener(buildFeedback);

  ////////////////////
 /////  FOOTER  /////
////////////////////

	mvc.buildFeedback = buildFeedback;
})();
/*	The job of this module is to handle communications from the server.  The
 *	module should try to pass this information along as quickly as possible
 *	to another module (generally the MVC) rather than doing any processing
 *	itself.
 *
 *	@owner sjelin
 */

  ////////////////////
 /////  HEADER  /////
////////////////////

var socket = socket || {};

(function () {
	"use strict";

  /////////////////////////////////
 /////  CODE (IN NAMESPACE)  /////
/////////////////////////////////

	var token;
	function init(t)
	{
		token = t;
	}

	var skt = null;
	function load()
	{
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

	function onOpen() {}

	var cmds = {
		ITEMS_AND_REMOVE_SPLIT: function(items, splitID) {
			mvc.items(JSON.parse(items));
			if(mvc.split() != null) {
				delete mvc.split()[splitID];
				mvc.notifySplit();
			}
		},
		SPLIT: function(splitID, splitVal) {
			var val = JSON.parse(splitVal);
			if(val == null) {
				if(mvc.split() != null)
					delete mvc.split()[splitID];
			} else {
				if(mvc.split() == null)
					mvc.split({});
				mvc.split()[splitID] = val;
			}
			mvc.notifySplit();
		},
		START_SPLIT: function() {
			if(mvc.split() == null)
				mvc.split({});
			else
				mvc.notifySplit();
		},
		ERR: function() {
			mvc.err(Array.prototype.join.call(arguments, "\n"));
		},
		DONE: function() {
			mvc.done(true);
		}
	}

	function onMessage(msg)
	{
		msg = msg.data.trim().split("\n");
		var cmd = msg[0].trim().toUpperCase();
		if(cmds[cmd] == undefined)
			mvc.err("Unknown command \""+cmd+"\" from channel");
		else
			cmds[cmd].apply(this, msg.slice(1));
	}

	function onError(err)
	{
		if(err.code == 0)
			if(err.description == null || err.description.trim().length == 0)
				err.description =	"Either the server or your phone seems "+
				   					"to have turned off";
		mvc.err("Channel error #"+err.code+": "+err.description);
	}

	function onClose() {}

	function close()
	{
		if(skt != null) {
			skt.close();
			skt = null;
		}
	}

  ////////////////////
 /////  FOOTER  /////
////////////////////

	socket.init = init;
	socket.load = load;
	socket.close = close;
})();
/*	The job of this module is to handle the navigation and basic
 *	functionality (i.e. responsiveness) of the app.  Is job is *not* to deal
 *	with any server communication, dynamic elements, or local state.  Any
 *	communication to the server, storage of information, or updating of
 *	dynamic elements should be handled by the MVC (see the "mvc" file).  Any
 *	communication from the server is handled by the socket module, which
 *	generally passes its results directly along to the MVC.
 *
 *	@owner sjelin
 */

  ////////////////////
 /////  HEADER  /////
////////////////////

(function () {
	"use strict";

  /////////////////////////////////
 /////  CODE (IN NAMESPACE)  /////
/////////////////////////////////

	var inited = false;

	function resize()
	{
		var $win = $(window);
		var h = $win.height();
		var fSz = Math.floor($win.width()*.92/26);
		var $body = $("body");
		if($body.size() == 0)
			return;
		$body.css("font-size", fSz+"px");
		//Use different logo images for each small size
		var $logo = $("#footer img");
		var logoH = fSz < 8 || fSz > 24 ? null : Math.ceil(fSz*2.5);
		$logo.attr("src", "img/cx" + (fSz>24 ? "":"_"+(logoH||20)) + ".png");
		if(logoH == null)
			$logo.removeAttr("style");
		else
			$logo.css("height", logoH+"px");

		//Use different images for the SSL logo when possible
		var $ssl = $("#pay-page img.ssl");
		var sslH = $ssl.height();
		$ssl.attr("src", "img/ssl_" + (sslH >= 55 ? "58" : sslH >= 37 ?
					"52" : "22") + ".png");

		//The following is inefficient but works 100% of the time
		//All the min and max stuff is to deal with browser inconsistency
		$body.removeClass("tall");
		if(Math.max($body.get(0).scrollHeight,
					$("html").get(0).scrollHeight) <=
				Math.min($body.get(0).clientHeight,
					$("html").get(0).clientHeight))
			$body.addClass("tall");
		//Popups have a special font size
		$(".popup").css("font-size",
				Math.min($win.width()/28.4, $win.height()/24)+"px");
	}

	window.onresize = resize;

	var pageNames = ["ask-split","split","receipt","login","pay","feedback"];
	var feedbackIndex = pageNames.indexOf("feedback");
	function goToPage(i, buildFun)
	{
		if((mvc.paid() || mvc.done()) && (i != feedbackIndex))
			return goToFeedback();
		if(window.location.hash != "#"+pageNames[i])
			window.location.hash = "#"+pageNames[i];
		if($("body").hasClass(pageNames[i]+"-page"))
			return;
		if(inited && (i != feedbackIndex))
			ajax.send("cx", "log_pos", {position: i, mobileKey: mvc.key(),
				clientID: mvc.clientID}, $.noop,
				buildAjaxErrFun("contact the server"));
		if(mvc.unbuild != null) {
			mvc.unbuild();
			mvc.unbuild = null;
		}
		for(var j = 0; j < pageNames.length; j++)
			if(i == j)
				$("body").addClass(pageNames[j]+"-page");
			else
				$("body").removeClass(pageNames[j]+"-page");
		if(buildFun != null)
			buildFun.apply(mvc);
		resize();
	}

	function goToStart()
	{
		if(mvc.split() != null)
			goToSplit();
		else
			goToPage(0);
	}

	function goToSplit()
	{
		goToPage(1, mvc.buildSplit);
	}

	function goToReceipt()
	{
		if((mvc.split() != null) && !mvc.validSplit())
			goToStart()
		else
			goToPage(2, mvc.buildReceipt);
	}

	function goToLogin()
	{
		if((mvc.tip() == null) || ((mvc.split()!=null) && !mvc.validSplit()))
			goToStart();
		else
			goToPage(3, mvc.buildLogin);
	}

	function goToPay()
	{
		if((mvc.tip() == null) || (mvc.username().length == 0) ||
				((mvc.split() != null) && !mvc.validSplit()))
			goToStart();
		else
			goToPage(4, mvc.buildPay);
	}

	function goToFeedback()
	{
		if(!mvc.paid() && !mvc.done())
			goToStart();
		else
			goToPage(feedbackIndex, mvc.buildFeedback);
	}

	window.onload = function() {
		$("body").addClass("has-js");
		socket.load();
		window.onhashchange();

		//Navigation related functions are attached here.  Other functions
		//are attached by the MVC
		$("#ask-split-page a.yes").click(goToSplit);
		$("#ask-split-page a.no").click(goToReceipt);
		$("#split-page a.confirm").click(function() {
			if(mvc.validSplit())
				goToReceipt();
			else {
				if((mvc.selection()==null)||$.isEmptyObject(mvc.selection()))
					alert("Tap on the items you which to pay for");
				else
					alert("Before you can proceed, everyone must select the items they are paying for.  This means that your friends need to take out their phone and scan the code or enter the URL just as you have done");
			}
		});
		$("#receipt-page a.confirm").click(function() {
			if(mvc.tip() != null) {
				if(mvc.username().length == 0)
					goToLogin();
				else
					goToPay();
			} else
				alert("Please enter a tip");
		});
		$("#login-page a.confirm").click(function() {
			if(mvc.completeLogin())
				mvc.login(goToPay);
			else
				alert("The login/register information you entered is incomplete");
		});
		$("#pay-page .login a").click(goToLogin);
		$("#pay-page a.confirm").click(function() {
			if(mvc.completePayment())
				mvc.pay(goToFeedback);
			else
				alert("The payment information you entered is incomplete");
		});
		window.onkeydown = function(x) {
			if(x.keyCode == 13)
				$(".confirm:visible").click();
		}
		inited = true;
	};

	var unloading = false;
	window.onunload = window.onbeforeunload = function() {
		if((mvc.err() == null) && !unloading) {
			unloading = true;
			ajax.send("cx", "close", {clientID: mvc.clientID()});
		}
	};

	window.onhashchange = function() {
		var hash = window.location.hash;
		if(hash.length > 0)
			hash = hash.slice(1);
		switch(pageNames.indexOf(hash)) {
			case 1: goToSplit(); break;
			case 2: goToReceipt(); break;
			case 3: goToLogin(); break;
			case 4: goToPay(); break;
			case 5: goToFeedback(); break;
			default: goToStart(); break;
		}
	}

	mvc.addSplitListener(function(oldSplit) {
		if(!mvc.done() && (mvc.split() != null) &&
				((oldSplit == null) || !mvc.validSplit())) {
			mvc.tip(null);
			goToSplit();
		}
	});
	mvc.addPaidListener(function () {
		if(mvc.paid())
			goToFeedback();
	});
	mvc.addDoneListener(function () {
		if(mvc.done())
			goToFeedback();
	});
	mvc.addItemsListener(function() {
		if(mvc.items().length == 0) {
			mvc.done(true);
			goToFeedback();
		}
	});
	mvc.addErrListener(function() {
		function putErr() {
			$("body").addClass("error-page");
			var err = mvc.err();
			$("body").html(template["cxErr__"+err] != null ?
							template["cxErr__"+err]() :
							template.cxErr(err));
			document.title = "Checkout Express - Error: "+err;
			window.onhashchange = function() {
				if(window.location.hash.length > 0)
					window.location.hash = "";
			};
			window.onhashchange();
			ajax.send("cx", "close", {clientID: mvc.clientID(), error: err},
				$.noop);
		}
		if($("body").size() == 0)
			window.onload = putErr;
		else {
			putErr();
			socket.close();
		}
	});
})();
