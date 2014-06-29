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
			models.error({heading:"Server Error", symbol:"!", message:msg});
		},
		PAYMENT_ERROR: function(msg) {
			models.error({	heading: "Payment Error", message: msg,
							symbol: String.fromCharCode(215)});
		},
		PAYMENT_SUCCESS: function() {
			models.validViews([consts.views.FEEDBACK]);
			models.loading(undefined);
		},
		PAYMENT_UPDATE: function(msg) {
			models.loading({message: msg});
		}
	}

	function onMessage(msg)
	{
		msg = msg.data.trim().split("\n");
		var cmd = msg[0].trim().toUpperCase();
		if(cmds[cmd] == undefined)
			models.error({heading: "Unknown command", symbol: "!",
				message: "Unknown command \""+cmd+"\" from server"});
		else
			cmds[cmd].apply(this, msg.slice(1));
	}

	function onError(err)
	{
		if(err.code == 401)
			models.error({heading: "Inactive", symbol: "",
				message: "Please reload"});
		else
			models.error({heading: "Unknown Error", symbol: "?",
				message: "Please reload"});
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
