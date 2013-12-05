/*	The job of this module is to handle the navigation
 *
 *	@see README.md
 *
 *	@owner sjelin
 */

(function () {
	"use strict";

	var currView = null;
	var hashchangeSupport = "onhashchange" in window;
	var unloading = false;

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
			if((currView != null) && !isFeedback) {
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

	window.nav = {init : function() {
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
	}};
	window.onkeydown = function(x) {
		if((x.keyCode == 13) && (mvc.loadMsg() == null))
			$(".confirm:visible").click();
	};

	window.onunload = window.onbeforeunload = function() {
		if((currView != null) && (mvc.err() == null) && !unloading) {
			unloading = true;
			device.ajax.send("cx","close",{connectionID:mvc.connectionID()});
		}
	};

	window.onhashchange = function() {
		goToView(mvc.views[location.hash.slice(1)] || mvc.views.receipt);
	}

})();
