/**	This module provides methods for receiving messages from the server
 *
 *	@author sjelin
 */

var socket = socket || {};

(function() {
	"use strict";

	var skt = null;
	socket.init = function(token) {
    	skt = (new window.goog.appengine.Channel(token)).open({
			'onopen': onOpen,
			'onmessage': onMessage,
			'onerror': onError,
			'onclose': onClose
		});
    	skt.onopen = onOpen;
    	skt.onmessage = onMessage;
    	skt.onerror = onError;
    	skt.onclose = onClose;
	}

	function onOpen() {}

	var cmds = {
		SET_ITEMS: function(items) {
			models.items(JSON.parse(items));
		},
		ERROR: function(msg) {
			models.error({	heading: text["500_ERROR_HDG"], symbol:"!",
							message: msg});
		},
		PAYMENT_ERROR: function(msg) {
			models.error({heading: text.PAY_ERROR_HDG, message: msg});
		},
		PAYMENT_SUCCESS: function() {
			models.validViews([consts.views.FEEDBACK]);
			models.loading(undefined);
		},
		PAYMENT_UPDATE: function(msg) {
			models.loading({message: msg});
		},
		HEARTBEAT: function(msg) {
			server.sendHeartbeat(msg);
		},
		PAID: function() {
			models.validViews([consts.views.FEEDBACK]);
			models.loading(undefined);
		},
		CLEARED: function() {
			models.error(text.getError("SOCKET_CLEAR"));
		}
	}

	function onMessage(msg)
	{
		msg = msg.data.trim().split("\n");
		var cmd = msg[0].trim().toUpperCase();
		if(cmds[cmd] == undefined)
			models.error({heading: text.SOCKET_UNKNOWN_CMD_HDG, symbol: "!",
				message: "Unknown command \""+cmd+"\" from server"});
		else
			cmds[cmd].apply(this, msg.slice(1));
	}

	function onError(err)
	{
		if(err.code == 401)
			models.error(text.getError("SOCKET_INACTIVE"));
		else
			models.error(text.getError("SOCKET_ERROR"));
	}

	function onClose() {}

	socket.close = function()
	{
		if(skt != null) {
			skt.close();
			skt = null;
		}
	}
})();
