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
			if(err == "reqUpdate")
				$trgt.append($(templates.err__reqUpdate(
					!{{NATIVE}} ? "reload the page" :
					"{{PLATFORM}}"=="iOS" ? "check the appstore for the " +
											"latest version of this app" :
											"download the latest version " +
											"of this app")));
			else
				$trgt.append($((templates["err__"+err] ||
												templates.err.c(err))()));
			var reload = {{NATIVE}} && reloadsAllowed &&
									(err == "reload" || err == "timeout");
			if({{DEBUG}})
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
