/**	The view for the error page
 *
 *	@author sjelin
 */

mvc.views = mvc.views || {};
(function() {
	mvc.views.error = {
		build: function($trgt) {
			var err = mvc.err();
			$trgt.append($((template["err__"+err] ||
												template.cxErr.c(err))()));
			ajax.send("cx", "close", {clientID: mvc.clientID(), error: err},
				$.noop);
		}
	};
})();
