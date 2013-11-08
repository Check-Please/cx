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
