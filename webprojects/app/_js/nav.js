/*	The job of this module is to handle the navigation, initialization, and
 *	some basic responsiveness.
 *
 *	@owner sjelin
 */

(function () {
	"use strict";

	var inited = false;
	var currView = null;
	var hashchangeSupport = "onhashchange" in window;
	var unloading = false;

	window.onresize = function()
	{
		var $win = $(window);
		var fSz = $win.width()*0.0354;
		var $body = $("body");
		if($body.size() == 0)
			return;
		$("html").css("font-size", $win.width()+"px");
		$(".popup-bg").css("font-size",
					Math.min($win.width(), $win.height())+"px");
		//Use different logo images for each small size
		var $logo = $("#footer img");
		var logoH = fSz * (window.devicePixelRatio || 1) * 2.5;
		var verySmall = logoH < 19;
		if(logoH < 21.5)
			logoH = 20;
		else if(logoH < 60) {
			var x = logoH % 5;
			logoH = Math.round(logoH - x + (x < 1.5 ? 0 : x < 4 ? 3 : 5));
		}
		$logo.attr("src", "img/app/cx" + (logoH>60 ? "":"_"+logoH) + ".png");
		if(verySmall || logoH > 60)
			$logo.removeAttr("style");
		else
			$logo.css("height",
					Math.round(logoH/(window.devicePixelRatio || 1))+"px");

		//The following is inefficient but works 100% of the time
		//All the min and max stuff is to deal with browser inconsistency
		$body.removeClass("tall");
		if(Math.max($body.get(0).scrollHeight,
					$("html").get(0).scrollHeight) <=
				Math.min($body.get(0).clientHeight,
					$("html").get(0).clientHeight))
			$body.addClass("tall");

		if(currView && currView.onResize)
			currView.onResize(Math.floor(fSz));
	};

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
		stealMutex = stealMutex === true;

		//Mutex code
		if(mutex)
			if(!stealMutex)
				return;
		if(_DEBUG_)
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
			if(inited && !isFeedback) {
				device.ajax.send("cx", "log_pos", {position: view.viewName,
					tableKey: mvc.key(), connectionID: mvc.connectionID()},
					$.noop, function() {
						if((mvc.err() == null) && !unloading)
							mvc.err("reload");
					});
			}

			//Actually transition from old view to new view
			var $view = $("#view");
			if((currView != null) && (currView.unbuild != null))
				currView.unbuild($view, view);
			if(_DEBUG_) {
				assert($("#view > :visible").length == 0);
				window.DEBUG_VAR = $("#view > *");
			}
			window.location.hash = "#"+view.viewName;
			if(currView != null)
				$("body").removeClass(currView.viewName);
			$("body").addClass(view.viewName);
			if(view.build != null)
				view.build($view, currView);
			if(_DEBUG_) {
				window.DEBUG_VAR.each(function() {
					assert($(this).parent("#view").length == 1);
				});
				window.DEBUG_VAR = undefined;
				delete window.DEBUG_VAR;
			}
			currView = view;

			//Hack
			window.onresize();

			//Release mutex
		} finally {
			mutex = false;
		}

		//We ignored nativation due to errors via the mutex.  Here we are
		//going to make that navigation happen
		mvc.err.notify();
	}

	window.onload = function() {
		if(!_NATIVE_) {
			if((window.location.hostname != "localhost") &&
						(window.location.protocol != "https:"))
				window.location.href = "https:" +
					window.location.href.substring(location.protocol.length);
			if(navigator.userAgent.indexOf("iPhone") != -1)
				$("body").addClass("platform-iOS");
		} else
			$("body").addClass("platform-_PLATFORM_");
		setTimeout(window.onresize, 0);
		inParallel([device.getTableKey, device.getPos], function(tKey, pos) {
			tKey = tKey[0] || "";
			if(!_NATIVE_) {
				if(tKey == "")
					return(window.location = "http://"+window.location.host
														+ "/website.html");
			}
			device.ajax.send("cx", "init", {
				isNative: _NATIVE_,
				tableKey: tKey,
				clientID: device.getClientID(),
				platform: device.getPlatform(),
				lat: pos[0],
				"long": pos[1],
				accuracy: pos[2]
			}, function(data) {
				data = JSON.parse(data);
				if(data.errCode != null) {
					mvc.init({err:	data.errCode == 0 ? "noKey" :
									data.errCode == 1 ? "invalidKey" :
									data.errCode == 2 ? "empty" :
														"500"});
					loading.init();
					mvc.err.notify();
					return;
				}
				if(data.deleteCCs)
					device.deleteCards();
				mvc.init({
					key: tKey,
					restrName: data.restrName,
					restrAddress: data.restrAddress,
					restrStyle: data.restrStyle,
					connectionID: data.connectionID,
					items: data.items,
					split: data.split,
					selection: {},
				});
				socket.init(data.channelToken);
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
				inited = true;
				window.onhashchange();
				mvc.views.error.allowReload();
			}, mvc.err.c("Couldn't contact the server"));
		});
		if(device.getDebugID() != null) {
			var $script = $("<script>");
			$script.attr("src",
					"http://jsconsole.com/remote.js?"+device.getDebugID());
			$("head").append($script);
		}

		mvc.split.listen(function() {
			if(!mvc.done() && !mvc.paid() && (mvc.loadMsg() == null)) {
				if(mvc.split() == null) {
					mvc.selection({});
					if(currView == mvc.views.split)
						goToView(mvc.views.receipt);
				} else if(!mvc.views.split.valid())
					goToView(mvc.views.split);
			}
		});
		mvc.contract.listen(function() {
			var contract = mvc.contract();
			if(contract != null)
				goToView(mvc.views[contract]);
		});
		mvc.paid.listen(function() {
			if(mvc.paid()) {
				mvc.loadMsg(null);
				goToView(mvc.views.feedback);
			}
		});
		mvc.done.listen(function() {
			if(mvc.done()) {
				mvc.loadMsg(null);
				goToView(mvc.views.feedback);
			}
		});
		mvc.items.listen(function() {
			if(mvc.items().length == 0) {
				mvc.loadMsg(null);
				mvc.done(true);
				goToView(mvc.views.feedback);
			}
		});
		mvc.err.listen(function() {
			if(mvc.err() != null) {
				mvc.loadMsg(null);
				if($("body").size() == 0)
					window.onload = goToView.c(mvc.views.error);
				else
					goToView(mvc.views.error);
			}
		});

		$("#view").on("click", "a.confirm", function() {
			var nextView = currView.nextView && currView.nextView();
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
			if((x.keyCode == 13) && (mvc.loadMsg() == null))
				$(".confirm:visible").click();
		}
	};

	window.onunload = window.onbeforeunload = function() {
		if(inited && (mvc.err() == null) && !unloading) {
			unloading = true;
			device.ajax.send("cx","close",{connectionID:mvc.connectionID()});
		}
	};

	window.onhashchange = function() {
		goToView(mvc.views[location.hash.slice(1)] || mvc.views.receipt);
	}

})();
