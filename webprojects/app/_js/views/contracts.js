/**	This file defines the views for all the contracts
 *
 *	@author sjelin
 */

mvc.views = mvc.views || {};
(function() {
	function cv(nextView, readonly) {
		this.nextView = nextView;
		this.readonly = readonly;
	};

	cv.prototype.build = function($trgt, oldView) {
		this.parentView = oldView;
		this.$view = $(templates.contract(templates[this.viewName](),
						this.readonly));
		this.$view.find("a.disagree").click(mvc.err.c("You cannot use this "+
							"service if you do not agree to the terms"));
		$trgt.append(this.$view);
	};
	cv.prototype.unbuild = function() {
		if(mvc.contract() == this.viewName)
			this.contract(null);
		this.$view.remove();
	};

	mvc.views.secFAQ = new cv(true, function() {return mvc.views.pay;});
	mvc.views.ccPolicy = new cv(function() {return mvc.views.pay;});
})();
