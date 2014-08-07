$(document).ready(function() {
	"use strict";

	// SSL
	if(!{{NATIVE}} && (window.location.hostname != "localhost") &&
					(window.location.protocol != "https:"))
		window.location.href = "https:" +
			window.location.href.substring(window.location.protocol.length);

	// ERROR MESSAGES
	var SERVER_ERROR = {
		0: text.getError("NO_TABLE_INFO", "?"),
		1: text.getError("INVALID_TABLE", "!"),
		2: text.getError("EMPTY_TABLE"),
		5: text.getError("UPDATE_REQ", String.fromCharCode(10227)),
		6: text.getError("APP_DISABLED"),
		500: text.getError("500_ERROR")
	};
	var GET_TABLE_ERRORS = {
		0: SERVER_ERROR[0],
		1: text.getError("BLUETOOTH_DISABLED"),
		2: text.getError("BLUETOOTH_ERROR")
	}

	// RENDER
	setTimeout(window.onresize, 0);//Must be async because of a browser bug
	window.onhashchange();
	Fluid.attachView($("#body"), BodyView, models.activeView, models.width,
		models.height, models.items, models.split, models.tipSlider,
		models.tip, models.cards, models.passwords, models.newCardInfo,
		models.cardFocus, models.email, models.feedback, models.loading,
		models.error, models.done, models.allowLandscape);

	models.loading({message: text.GETTING_ORDER_LOAD_MSG});
	inParallel([device.getTableInfo, device.getPos, device.loadData],
	function(tInfo, pos) {
		if(tInfo[0] == null) {
			models.error(GET_TABLE_ERRORS[tInfo[1]] || {
				heading: text.NO_LOCATION_DATA_HDG,
				symbol: String.fromCharCode(9889),
				message: tInfo[2] || text.NO_LOCATION_DATA_MSG});
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
				else {
					models.cards(saved.getCCs());
					models.cardFocus(0);
				}
			}
			models.loading(undefined);
		}, function() {
			models.error(text.getError("LOADING_ORDER_ERROR", "!"));
		}, function(rs) {
			models.loading({message: text.GETTING_ORDER_LOAD_MSG,
							percent: rs*25, incrTo: (rs+1)*25-1});
		});
		models.cards(saved.getCCs());
	});

	if(device.getDebugID() != null) {
		var $script = $("<script>");
		$script.attr("src",
				"http://jsconsole.com/remote.js?"+device.getDebugID());
		$("head").append($script);
	}
});
