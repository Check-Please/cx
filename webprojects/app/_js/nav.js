/*	The job of this module is to handle the navigation, initialization, and
 *	some basic responsiveness.
 *
 *	@owner sjelin
 */

(function () {
	"use strict";

	var inited = false;

	function resize()
	{
		var $win = $(window);
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
	}

	window.onresize = resize;
	var hashchangeSupport = "onhashchange" in window;

	var currView = null;
	var mutex = false;
	/*	Goes to a specific view.  Really the core of the module
	 *
	 *	This function has a mutex so that any navigation it triggers (e.g.
	 *	during a build() or unbuild() call) is ignored.  However, this mutex
	 *	can be overridden via the stealMutex parameter.
	 */
	function goToView(view, stealMutex)
	{
		//Everything other than true is false
		stealMutex = stealMutex !== true;

		//Mutex code
		if(mutex)
			if(!stealMutex)
				return;
		if(DEBUG)
			assert(!mutex || stealMutex);
		mutex = true;
		try {

			//Navigate to special pages
			if((mvc.err() != null) && (view != mvc.views.error))
				return goToView(mvc.views.error, true);
			var isFeedback = view == mvc.views.feedback;
			if((mvc.paid() || mvc.done()) && !isFeedback)
				return goToView(mvc.views.feedback, true);

			//Handle redirection
			if(view.redirect) {
				var redirect = view.redirect();
				if(redirect != null)
					return goToView(redirect, true);
			}

			//Ignore duplicate navigation
			if(view == currView)
				return;

			//Spy
			if(inited && !isFeedback)
				ajax.send("cx", "log_pos", {position: pageName,
					mobileKey: mvc.key(), clientID: mvc.clientID()}, $.noop,
					buildAjaxErrFun("contact the server"));

			//Actually transition from old view to new view
			var $view = $("#view");
			if((currView != null) && (currView.unbuild != null))
				currView.unbuild($view, view);
			if(DEBUG) {
				assert($("#view > :visible").length == 0);
				DEBUG.elems = $("#view > *");
			}
			window.location.hash = "#"+view.viewName;
			$("body").removeClass(currView.viewName+"-page");
			$("body").addClass(view.viewName+"-page");
			if(view.build != null)
				view.build($view, currView);
			if(DEBUG) {
				DEBUG.elems.each(function() {
					assert($(this).parent("#view").length == 1);
				});
				delete DEBUG.elems;
			}
			currView = view;

			//Hack
			resize();

			//Release mutex
		} finally {
			mutex = false;
		}
	}

	window.onload = function() {
		inParallel([device.getTableKey, device.getPos], function(tKey, pos) {
			ajax.load("cx", "init", {
				isNative: device.isNative(),
				tableKey: tKey,
				clientID: device.getClientID(),
				platform: device.getPlatform(),
				lat: pos[0],
				"long": pos[1],
				accuracy: pos[2]
			}, function(data) {
				data = JSON.parse(data);
				if(data.errCode != null) {
					mvc.init({err:	data.errCode == 0 ? "NoKey" :
									data.errCode == 1 ? "InvalidKey" :
									data.errCode == 2 ? "Empty" :
														"500"});
					mvc.err.notify();
					return;
				}
				mvc.init({
					restrName: data.restrName,
					restrAddress: data.restrAddress,
					restrStyle: data.restrStyle,
					channelID: data.channelID,
					items: data.items,
					split: data.split,
					selection: {},
				});
				socket.open(data.channelToken);
				$("title").text("Pay your ticket at "+data.restrName);
				if(data.restrStyle == null) {
					var $name = $("<span>");
					$name.text(data.restrName);
					$("#header").append($name);
				} else {
					var $css = $("<link>");
					$css.attr("type", "text/css");
					$css.attr("rel", "Stylesheet");
					$css.attr("media", "all");
					$css.attr("href", "cx_custom/"+data.restrStyle+".css");
					$("head").append($css);
					var $img = $("<img>");
					$img.attr("src",
								"cx_custom/"+data.restrStyle+"_header.png");
					$("#header").append($img);
				}
				loading.init();
				window.onhashchange();
			}, buildAjaxErrFun("establish connection with the server"));
		});
		if(device.getDebugID() != null) {
			var $script = $("<script>");
			$script.attr("src",
					"http://jsconsole.com/remote.js?"+device.getDebugID());
			$("head").append($script);
		}

		mvc.split.listen(function(oldSplit) {
			if(!mvc.done() && (mvc.split() != null) &&
					((oldSplit == null) || !mvc.views.split.valid())) {
				mvc.tip(null);
				goToView(mvc.views.split);
			}
		});
		mvc.contract.listen(function() {
			var contract = mvc.contract();
			if(contract != null)
				goToView(mvc.views[contract]);
		});
		mvc.paid.listen(function() {
			if(mvc.paid())
				goToView(mvc.views.feedback);
		});
		mvc.done.listen(function() {
			if(mvc.done())
				goToView(mvc.views.feedback);
		});
		mvc.items.listen(function() {
			if(mvc.items().length == 0) {
				mvc.done(true);
				goToView(mvc.views.feedback);
			}
		});
		mvc.err.listen(function() {
			function putErr() {
				goToPage(mvc.views.error);
				ajax.send("cx", "close", {clientID:mvc.clientID(),error:err},
					$.noop);
			}
			if($("body").size() == 0)
				window.onload = putErr;
			else {
				putErr();
				socket.close();
			}
		});

		$("#view").on("click", "a.confirm", function() {
			nextView = currView.nextView && currView.nextView();
			if(nextView)
				goToView(nextView);
		});
		if(!hashchangeSupport) {
			$("#view").on("click", "a", function() {
				var href = $(this).attr("href");
				if(href && href[0] == "#")
					window.onhashchange();
			});
		}
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
		goToView(mvc.views[location.hash.slice(1)] || mvc.views.askSplit);
	}

})();
