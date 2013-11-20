/**	The view for the screen where we ask if the user wants to split the bill
 *
 *	@author sjelin
 */

mvc.views = mvc.views || {};
(function() {
	var $view = null;
	mvc.views.askSplit = {
		build: function($trgt) {
			if($view == null) {
				$view = $(templates.askSplit());
				$trgt.append($view);
			} else {
				$view.show();
			}
		},
		unbuild: function() { $view.hide() },
		redirect: function() {if(mvc.split()!=null) return mvc.views.split;}
	};
})();
