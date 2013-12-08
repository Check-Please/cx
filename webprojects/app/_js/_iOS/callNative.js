/*	This module allows JS code to call some native iOS code.
 *
 *	The basic idea is this: We call a native function by creating an iFrame
 *	with a URL using a bogus protocal and containing information about what
 *	native code to call.  The iOS wrapper intercepts the bogus URL and uses
 *	the information to call the appropriate code.  When it's done, the iOS
 *	hands control back to JS by calling iOS.reenter() with a key that
 *	identifies the calling function.
 *
 *	Inspired by (and parts stolen from) Ram Kulkarni (ramkulkarni.com).
 *	http://ramkulkarni.com/temp/2013-01-12/iosBridge.js
 */

var iOS = iOS || {};
(function() {
	funMap = {};

	function getFunky()
	{
		var funky;
		do {
			funky = randStr();
		} while(funMap[funky] !== undefined);
		return funky;
	}

	iOS.call = function(name, args, success, fail)
	{
		args = args || [];

		var funky = getFunky();
		funMap[funky] = { success: success, fail: fail }

		var iFrame = createIFrame("js2ios://"+JSON.stringify({
			name: name,
			args: args,
			callbackKey: funky
		}));
		iFrame.parentNode.removeChild(iFrame);
	};

	function createIFrame(src)
	{
		var newFrameElm = document.createElement("IFRAME");
		newFrameElm.setAttribute("src",src);
		document.documentElement.appendChild(newFrameElm);
		return newFrameElm;
	}

	iOS.reenter = function(funky, success, arg)
	{
		var callback = funMap[funky][success ? "success" : "fail"] || $.noop;
		delete funMap[funky];
		if(arguments.length == 2)
			return callback();
		var keys = Object.keys(arg);
		if((keys.length == 1) && (keys[0] == "_"))
			arg = arg._;
		return callback(arg);
	};
})();
