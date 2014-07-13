window.onload = function() {
	"use strict";

	// SSL
	if(!{{NATIVE}} && (window.location.hostname != "localhost") &&
					(window.location.protocol != "https:"))
		window.location.href = "https:" +
			window.location.href.substring(window.location.protocol.length);

	// ERROR MESSAGES
	var SERVER_ERROR = {
		0: {heading: "No Table Info", symbol: "?",
			message: "Couldn't find information about where you're sitting"},
		1: {heading: "Invalid Table", symbol: "!",
			message: "Couldn't find information about where you're sitting"},
		2: {heading: "Empty", symbol: String.fromCharCode(216),
			message: "There are no unpaid items at this table"},
		5: {heading: "Update", symbol: String.fromCharCode(10227), 
			message: "The app needs to be updated"},
		6: {heading: "Disabled", symbol: String.fromCharCode(215),
			message: "The app is currently disabled"},
		500:{heading:"Server Error", symbol: String.fromCharCode(215),
			message: "Something went wrong with on server"}
	};
	var GET_TABLE_ERRORS = {
		0: SERVER_ERROR[0],
		1: {heading: "Bluetooth disabled", symbol: String.fromCharCode(215),
			message: "Turn on bluetooth to use this app"},
		2: {heading: "Bluetooth error", symbol: String.fromCharCode(215),
			message: "Make sure bluetooth is enabled and working"}
	}

	// RENDER
	setTimeout(window.onresize, 0);//Must be async because of a browser bug
	window.onhashchange();
	Fluid.attachView($("#body"), BodyView, models.activeView, models.width,
		models.height, models.items, models.split, models.tipSlider,
		models.tip, models.cards, models.passwords, models.newCardInfo,
		models.cardFocus, models.email, models.feedback, models.loading,
		models.error, models.allowLandscape);

	models.loading({message: "Getting Order"});
	inParallel([device.getTableInfo, device.getPos, device.loadData],
	function(tInfo, pos) {
		if(tInfo[0] == null) {
			models.error(GET_TABLE_ERRORS[tInfo[1]] || {
				heading: "Can't find you", symbol: String.fromCharCode(9889),
				message:tInfo[2]||"Can't get data on where you're sitting"});
			return;
		}
		device.ajax.send("cx", "init", {
			isNative: {{NATIVE}},
			tableInfo: tInfo[0],
			clientID: saved.getClientID(),
			platform: device.getPlatform(),
			lat: pos[0],
			"long": pos[1],
			accuracy: pos[2],
			versionNum: {{VERSION_NUM}}
		}, function(data) {
			data = JSON.parse(data);
			if(data.errCode != null)
				models.error(SERVER_ERROR[data.errCode]||SERVER_ERROR[500]);
			else {
				socket.init(data.channelToken);
				models.tableKey(data.tKey);
				models.connectionID(data.connectionID);
				models.items(data.items);
				if(data.deleteCCs)
					saved.deleteCCs();
				else
					models.cards(saved.getCCs());
			}
			models.loading(undefined);
		}, function() {
			models.error({heading: "Couldn't load", symbol: "!",
				message: "Couldn't get your order from the server"});
		}, function(rs) {
			models.loading({message: "Getting Order",
							percent: rs*25, incrTo: (rs+1)*25-1});
		});
	});

	if(device.getDebugID() != null) {
		var $script = $("<script>");
		$script.attr("src",
				"http://jsconsole.com/remote.js?"+device.getDebugID());
		$("head").append($script);
	}
}
