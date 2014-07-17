(function() {
	window.onkeydown = function(x) {
		if((x.keyCode == 13) && (models.loading() == null))
			$("#"+models.activeView()+"-view .confirm").click();
	};

	var unloading = false;
	window.onunload = window.onbeforeunload = function() {
		if(!unloading) {
			unloading = true;
			device.ajax.send("cx", "close", {
				tableKey: models.tableKey(),
				connectionID: models.connectionID(),
				clientID: saved.getClientID()
			});
		}
	};
})();
