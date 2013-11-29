/*	The job of this module is to show a loading screen when shit is loading
 *
 *	@owner sjelin
 */

var loading = loading || {};

(function () {
	"use strict";

	function update()
	{
		var msg = mvc.loadMsg();
		if(msg == null)
			$("#loadMsg").hide();
		else {
			msg = msg.trim();
			if(msg.match(/[a-z0-9]\.*$/i))
				while(!msg.endsWith("..."))
					msg += ".";
			$("#loadMsg p").text(msg);
			$("#loadMsg").show();
		}
	}

	loading.init = function() {
		mvc.loadMsg.listen(update);
		update();
	}
})();
