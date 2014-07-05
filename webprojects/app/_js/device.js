/**	This module provides methods for doing things which may be done
 *	differently in the browser than they would be done on a native device.
 *	For instance, in the browser we can only generate random UUIDs, but on
 *	a native device we can often use a built in UUID generator.  In general,
 *	you can think of this module as an abstraction over any particular
 *	platform
 *
 *	This file does two things:
 *		(1) Provides JS-based implementations of each method for the browser
 *			to use or the native device to default upon
 *		(2) Provides a spec for the methods which any native implementations
 *			must follow
 *
 *	Not all methods should be overwritten on all platforms.  For instance, on
 *	iOS, accData() should be changed to use the iOS keychain feature, and
 *	makeUUID() should be changed to make UUIDs that aren't just random.
 *	However, once that is is done the getClientID() method will automatically
 *	use these new implementations and thereby actually do exactly what Apple
 *	recommends.  On some versions of android however, getClientID() might be
 *	better off returning some kind of device ID.
 *
 *	@author sjelin
 */

var device = device || {};

(function() {
	"use strict";

	/**	Gets information about what platform the user is on
	 *
	 *	@return	A string describing the platform
	 */
	device.getPlatform=op.id.c("{{PLATFORM}}: "+window.navigator.userAgent);

	/** Makes a UUID.  Should comply with the UUID spec
	 *
	 *	@return A string representation of a UUID
	 */
	device.makeUUID = function() {
		//Credit: http://stackoverflow.com/a/2117523/1432449
		return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
			function(c) {
    			var r = Math.random()*16|0;
				var v = c == 'x' ? r : (r&0x3|0x8);
    			return v.toString(16);
			}
		);
	};

	if({{TEST}} || {{NATIVE}}) {
		device.ajax = function(method, url) {
			if({{NATIVE}} && !url.match(/^[a-z]*:\/\//i))
				arguments[1] = "{{SERVER}}" + (url[0] =="/"?"":"/") + url;
			window.ajax.apply(this, $.makeArray(arguments));
		};
		$.extend(device.ajax, ajax);
	} else
		device.ajax = ajax;

	/**	Determines the information which will identify the table for the
	 *	server
	 *
	 *	@param	callback	A callback function. The first parameter is the
	 *						information.  If no information can be found,
	 *						null is passed as the first as the first
	 *						parameter, the second is an error code, and the
	 *						third is an error message.  So far, the
	 *						following codes are in use:
	 *							0 - No table info
	 *							1 - Bluetooth disabled
	 *							2 - Bluetooth not working
	 */
	device.getTableInfo = function(callback)
	{
		var q = window.location.search;
		if(q.length == 0)
			callback(null, 0);
		else {
			var i = q.indexOf('&');
			if(i == -1)
				callback(q.slice(1));
			else
				callback(q.slice(1, i));
		}
	};

	/**	Gets the ID for jsconsole
	 *
	 *	@return	The ID for jsconsole.  If jsconsole is not being used,
	 *			returns null
	 */
	device.getDebugID = function() {
		var paramName = "debugID";
		var q = window.location.search;
		var i = q.indexOf("&"+paramName+"=");
		if(i == -1)
			return null;
		else
			q = q.slice(i+1+paramName.length+1);
			var j = q.indexOf(j);
			if(j != -1)
				q = q.slice(0, j);
			return q;
	};

	/**	Gets the location of the user with no time restrictions on the speed
	 *	at which the callback is called.
	 *
	 *	@param	callback	The callback function.  Takes an object as its
	 *						sole parameter.  The object has the following:
	 *								latitude, longitude, accuracy
 	 *								code, message
	 *						The last two are used for errors, and code must
	 *						follow PositionError.code
	 */
	device.getPosInner = function(callback) {
		if(window.navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(function(pos) {
				callback(pos.coords);
			}, function(err) {
				callback(err);
			}, {enableHighAccuracy: true, maximumAge: 0});
		} else
			callback({code: 2, message:
				"No geolocation available.  Please upgrade your browser"});
	};

	/**	Gets the location of the user, calling the callback quickly.
	 *
	 *	Must call the callback function within about 1000 ms.  However, even
	 *	if the time expires this function should keep trying to get the
	 *	location in the background.
	 *
	 *	@param	callback	A callback function.  The parameters are:
	 *								latitude, longitude, accuracy[,
	 *									errCode, errMsg]
	 *						If no location should be found, the first four
	 *						will be undefined.  If a location is found, the
	 *						last two will be.  The error codes follow those
	 *					 	of PositionError.code
	 */
	device.getPos = function(callback)
	{
		var ret = {code: 3, message: "Still waiting for user decision"};
		var timeoutID = undefined;
		function callCallback() {
			clearTimeout(timeoutID);
			timeoutID = undefined;
			callback(	ret.latitude, ret.longitude, ret.accuracy,
						ret.code, ret.message);
		}
		device.getPosInner(function(resp) {
			ret = resp;
			if(timeoutID != null)
				callCallback();
		});
		setTimeout(callCallback, 1000);
	};

	/**	Loads data from the device into localStorage so that they can be
	 *	accesed without a callback function in the future.
	 *
	 *	@param	callback The funtion to be called once the data is loaded
	 */
	device.loadData = op.call;

	function buildStorageAtopLocalData(rewriteKey)
	{
		return function(k, v) {
			{{ASSERT: k.match(/^[_0-9a-z]*$/i)}};
			k = rewriteKey(k);
			if(arguments.length == 1) {
				//GET
				v = window.localStorage.getItem(k);
				return v == undefined ? undefined : JSON.parse(v);
			} else {
				//SET
				if(v !== undefined)
					window.localStorage.setItem(k, JSON.stringify(v));
				else
					window.localStorage.removeItem(k);
				return v;
			}
		};
	}

	/**	Local storage for data which pertains to account information.
	 *
	 *	Analogous to iOS' "keychain".  Data should (if possible) be encrypted
	 *	in some way, though this is not strictly necessary and should not be
	 *	relied on.  Even iOS' keychain is very much breakable.
	 *
	 *	Only small pieces of data should be stored here.  Storage may be slow
	 *	and may have a fairly small maximum size.
	 *
	 *	If the app is currently running natively, this information *cannot*
	 *	be stored in cookies, localStorage, and especially not globalStorage.
	 *	For security reasons, it will be assumed that if the app is running
	 *	natively then there is no way an attacker could gain access to this
	 *	data without gaining access to the device.
	 *
	 *	@param	key The key for the stored data
	 *	@param	data Optional.  If specified, overwrites the current value
	 *				 corresponding to the key.  Deleting entries can be done
	 *				 by explictly passing "undefined" for this parameter.
	 *
	 *	@return	The current value corresponding to the key.  Note that if the
	 *			"data" parameter was specified, this will always equal
	 *			whatever was passed in as that parameter.
	 */
	device.accData = buildStorageAtopLocalData(op.p.c("acc::"));

	/**	Store a secret in the most secure way possible.  Need not be
	 *	accessible client side.  Stores information in accData which can be
	 *	used by the server to determine the secret.
	 *
	 *	@param	key	The key to find the information for the server at
	 *	@param	secret The secret to store
	 */
	device.storeSecret = function(key, secret) {
		var uuid = device.makeUUID();
		device.accData(key, "cookie:"+uuid);
		window.document.cookie = uuid+"="+secret+"; secure; HttpOnly";
	}

	/**	Local storage for general purpose data
	 *
	 *	Analogous to iOS' "core data"
	 *
	 *	@param	key The key for the stored data
	 *	@param	data Optional.  If specified, overwrites the current value
	 *				 corresponding to the key.  Deleting entries can be done
	 *				 by explictly passing "undefined" for this parameter.
	 *
	 *	@return	The current value corresponding to the key.  Note that if the
	 *			"data" parameter was specified, this will always equal
	 *			whatever was passed in as that parameter.
	 */
	device.genData = buildStorageAtopLocalData(op.p.c("gen::"));
})();
