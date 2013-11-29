/**	The view for the error page
 *
 *	@author sjelin
 */

mvc.views = mvc.views || {};
(function() {
	mvc.views.error = {
		build: function($trgt) {
			var err = mvc.err();
			$trgt.append($((templates["err__"+err] ||
												templates.err.c(err))()));
			if(DEBUG)
				console.log("Error page:\n\n"+(new Error()).stack);
			if(mvc.connectionID() != null)
				ajax.send("cx", "close", {connectionID: mvc.connectionID(),
										error: err}, $.noop);
			socket.close();
		},
		redirect: function() {if(mvc.err()==null) return mvc.views.askSplit;}
	};
})();
