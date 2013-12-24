/**	This file defines the views for all the contracts
 *
 *	@author sjelin
 */

mvc.views = mvc.views || {};
(function() {
	function cv(name, nextView, readonly) {
		this.title = name;
		this.nextView = nextView;
		this.readonly = readonly === true;
	};

	cv.prototype.build = function($trgt) {
		this.$view = $(templates.contract(templates[this.viewName](),
						this.readonly));
		this.$view.find("a.disagree").click(mvc.err.c("You cannot use this "+
							"service if you do not agree to the terms"));
		$trgt.append(this.$view);
	};
	cv.prototype.unbuild = function() {
		if(mvc.contract() == this.viewName)
			mvc.contract(null);
		this.$view.remove();
	};
	cv.prototype.redirect = function() {
		if(mvc.contract() != this.viewName)
			return this.nextView();
	};
	cv.prototype.onResize = function(fSz) {
		this.$view.find(".content").height(this.$view.height()-8*fSz);
		var lens = this.$view.find(".buttons a").map(function() {
			return $(this).outerWidth(true);
		});
		var len = fSz;
		for(var i = 0; i < lens.length; i++)
			len += lens[i];
		this.$view.find(".buttons").width(Math.ceil(len)+1+"px");
	}

	var nextViewFun = function() {
		if(window.history && window.history.back)
			window.history.back();
		else
			return mvc.views.pay;
	};
	mvc.views.secFAQ = new cv("Security FAQ", nextViewFun, true);
	mvc.views.ccPolicy = new cv("Credit Card Policy", nextViewFun);
})();
