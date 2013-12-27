/**	This file manages the location
 */

$(document).ready(models.refresh);

window.onhashchange = function() {
	var cmd = (location.hash||"").slice(1);
	if(cmd.startsWith("tick-")) {
		var tKey = cmd.substr(5);
	} else
		drawClear();
	if(cmd == "refresh" || cmd == "reload" || cmd == "reconnect")
		models.refresh();
}
