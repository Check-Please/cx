(function() {
	window.onkeydown = function(x) {
		if((x.keyCode == 13) && (models.loading() == null)) {
			var view = models.split().trgt ? "split" : models.activeView();
			$("#"+view+"-view .confirm").click();
		}
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
