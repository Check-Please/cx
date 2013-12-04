/**	The view for the error page
 *
 *	@author sjelin
 */

mvc.views = mvc.views || {};
(function() {
	var reloadsAllowed = false;
	mvc.views.error = {
		build: function($trgt) {
			var err = mvc.err();
			$trgt.append($((templates["err__"+err] ||
												templates.err.c(err))()));
			var reload;
			if(NATIVE)
				reload = reloadsAllowed&&(err=="reload" || err=="timeout");
			else
				reload = false;
			if(DEBUG)
				console.log("Error page:\n\n"+(new Error()).stack);
			if(mvc.connectionID() != null)
				device.ajax.send("cx", "close", {connectionID:
					mvc.connectionID(), error:err}, reload?undefined:$.noop);
			socket.close();
			if(reload)
				return window.location.reload(false);
		},
		redirect: function() {if(mvc.err()==null) return mvc.views.receipt;},
		allowReload: function() {reloadsAllowed = true;}
	};
})();
