/*	The job of this module is to handle communications from the server.  The
 *	module should try to pass this information along as quickly as possible
 *	to another module (generally models.js) rather than doing any processing
 *	itself.
 *
 *	@owner sjelin
 */

var socket = socket || {};

(function () {
	"use strict";

	var skt = null;
	socket.open = function(token)
	{
    	skt = (new goog.appengine.Channel(token)).open({
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
		ITEMS_AND_REMOVE_SPLIT: function(items, splitID) {
			mvc.items(JSON.parse(items));
			if(mvc.split() != null) {
				delete mvc.split()[splitID];
				mvc.split.notify();
			}
		},
		SPLIT: function(splitID, splitVal) {
			var val = JSON.parse(splitVal);
			if(val == null) {
				if(mvc.split() != null)
					delete mvc.split()[splitID];
			} else {
				if(mvc.split() == null)
					mvc.split({});
				mvc.split()[splitID] = val;
			}
			mvc.split.notify();
		},
		START_SPLIT: function() {
			if(mvc.split() == null)
				mvc.split({});
			else
				mvc.split.notify();
		},
		ERR: function() {
			mvc.err(Array.prototype.join.call(arguments, "\n"));
		},
		DONE: function() {
			mvc.done(true);
		},
		/*	type = 0 for msg update, -1 for error, 1 for paid
		 */
		LOAD_UPDATE: function(type, msg) {
			switch(type) {
				case 0: mvc.loading(msg); break;
				case -1: if(msg!=null) alert(msg); mvc.loading(null); break;
				case 1: mvc.loading(null); mvc.paid(true); break;
			}
		}
	}

	function onMessage(msg)
	{
		msg = msg.data.trim().split("\n");
		var cmd = msg[0].trim().toUpperCase();
		if(cmds[cmd] == undefined)
			mvc.err("Unknown command \""+cmd+"\" from channel");
		else
			cmds[cmd].apply(this, msg.slice(1));
	}

	function onError(err)
	{
		if(err.code == 0)
			if(err.description == null || err.description.trim().length == 0)
				err.description =	"Either the server or your phone seems "+
				   					"to have turned off";
		mvc.err("Channel error #"+err.code+": "+err.description);
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
