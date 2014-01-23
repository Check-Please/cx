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
	socket.init = function(token) {
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
		ITEMS_AND_RESTORE_SPLIT: function(items, splitID, splitItems)
		{
			mvc.items(JSON.parse(items));
			splitItems = JSON.parse(splitItems);
			if(mvc.split() != null) {
				mvc.split()[splitID] = splitItems;
				mvc.split.notify();
			} else
				mvc.split({splitID: splitItems});
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
		CANCEL_SPLIT: function() {
			if(mvc.split() == null)
				mvc.split.notify();
			else
				mvc.split(null);
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
			switch(parseInt(type)) {
				case 0: mvc.loadMsg(msg); break;
				case -1: if(msg!=null) alert(msg); mvc.loadMsg(null); break;
				case 1: mvc.loadMsg(null); mvc.paid(true); break;
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
		if(err.code == 401)
			mvc.err("timeout");
		else
			mvc.err("reload");
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
