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

	//Are you ready for the most fucked up thing you've ever seen?
	//Because what follows is not for the faint of heart.  I'm serious.
	//
	//
	//
	//You've been warned.
	if({{NATIVE}}) device.ajax.get("_ah/channel/jsapi", {}, function(code) {
		code = code.replace(/\/_ah/g, "{{SERVER}}/_ah");//I WARNED YOU
		var $script=$("<script>");//This is going exactly where you think
		$script.attr("type", "text/JavaScript");
		$script[0].innerHTML = code;
		$("head").append($script);
	}, mvc.errASAP.c("Couldn't load communications with the server"));
	//The web case was handled in index.html

	//You think it's over don't you?
	//Don't you?
	//Hahaha, that's rich.

	socket.init = function(token, timeWaited) {
		timeWaited = timeWaited || 0;
		if(window.goog && goog.appengine && goog.appengine.Channel)
			open(token);
		else {
			if(timeWaited > 9000)
				mvc.errASAP("Couldn't load communications with the server");
			else {
				var wait = Math.ceil(timeWaited/10+20);
				setTimeout(socket.init, wait, token, timeWaited+wait)
			}
		}
	}
	//Ok, you an open your eyes now.

	var skt = null;
	function open(token)
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
