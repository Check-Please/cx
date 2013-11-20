/*	The job of this module is to show a loading screen when shit is loading
 *
 *	@owner sjelin
 */

(function () {
	"use strict";

	function update()
	{
		var msg = mvc.loadMsg();
		if(msg == null)
			$("#loading").hide();
		else {
			$("#loading p").text(msg);
			$("#loading").show();
		}
	}

	loading.init = function() {
		mvc.loadMsg.listen(update);
		update();
	}
})()
