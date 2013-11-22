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

	/** Determines if the app is running natively or in the browser
	 *
	 *	@return	true iff the app is running natively
	 */
	device.isNative = op.id.c(false);

	/**	Determines if credit cards should be allowed to be stored without
	 *	passwords on this device
	 *
	 *	@return	true iff credit cards should be able to be stored without
	 *			passwords on this device
	 */
	device.ccWOPass = op.id.c(false);

	/**	Gets information about what platform the user is on
	 *
	 *	@return	A string describing the platform
	 */
	device.getPlatform = op.id.c(navigator.userAgent);

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

	/**	Determines the key for the table
	 *
	 *	@param	callback A callback function. The first parameter is the key.
	 *					 If no key can be found, null is passed as the first
	 *					 as the first parameter, and the second is an error
	 *					 message.
	 */
	device.getTableKey = function(callback)
	{
		var q = window.location.search;
		if(q.length == 0)
			callback(null, "No table specified in the URL.");
		var i = q.indexOf('&');
		if(i == -1)
			callback(q.slice(1));
		else
			callback(q.slice(1, i));
	};

	/**	Gets the ID for jsconsole
	 *
	 *	@return	The ID for jsconsole.  If jsconsole is not being used,
	 *			returns null
	 */
	device.getDebugID = function()
	{
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
	}

	/**	Gets the location of the user.
	 *
	 *	Must call the callback function within about 1000 ms
	 *
	 *	@param	callback	A callback function.  The parameters are:
	 *							latitude, longitude, accuracy, errCode,errMsg
	 *						If no location should be found, the first three
	 *						will be undefined.  If a location is found, the
	 *						last two will be.  The error codes follow those
	 *					 	of PositionError.code
	 */
	device.getPos = function(callback)
	{
		if(navigator.geolocation) {
			var ret = {code: 3, message: "Still waiting for user decision"};
			navigator.geolocation.getCurrentPosition(function(pos) {
				ret = pos.coords;
			}, function(err) {
				ret = err;
			}, {enableHighAccuracy: true, maximumAge: 0});
			setTimeout(function() {
				callback(	ret.latitude, ret.longitude, ret.accuracy,
							ret.code, ret.message);
			}, 1000);
		} else
			callback(undefined, undefined, undefined, 2,
				"No geolocation available.  Please upgrade your browser");
	};

	/**	Gets some sort of hash identifying the device/user
	 *
	 *	The purpose of this ID is to help the server track users even without
	 *	them registering an account.  What's more, once a user registers an
	 *	account the ID can be used to import all of the previously being
	 *	tracked data into their account.  As such, these IDs should have two
	 *	properties:
	 *		(1) Different users will generate different IDs
	 *		(2) The same user will consistantly generate the same ID
	 *	The first property here is more important than the second, since
	 *	losing data the user didn't even know we were tracking is just a
	 *	small inconvenience, but misassigning data could lead to seriosuly
	 *	erroneous results.
	 *
	 *	@return Some kind of string identifying the device
	 */
	device.getClientID = function()
	{
		var id = device.accData("id");
		if(id == undefined) {
			id = device.makeUUID();
			device.accData("id", id);
		}
		return id;
	};

	function buildStorageAtopLocalData(rewriteKey)
	{
		return function(k, v) {
			k = rewriteKey(k);
			if(arguments.length == 1) {
				//GET
				v = localStorage.getItem(k);
				if(v != undefined)
					return JSON.parse(v);
				else
					return undefined;
			} else {
				//SET
				if(v !== undefined)
					localStorage.setItem(k, JSON.stringify(v));
				else
					localStorage.removeItem(k);
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


	var nameKey = "names";

	/**	Gets a list of names of people who have used the app on this device
	 *
	 *	@return	List of names sorted with the important first
	 */
	device.getNames = function()
	{
		return device.accData(nameKey) || [];
	};

	/** Logs that a person with a given name has used the app
	 *
	 *	@param name The name to log
	 */
	device.logName = function(name)
	{
		names = device.accData(nameKey) || [];
		i = names.indexOf(name);
		if(i != 0) {
			if(i > 0)
				names.splice(i, 1);
			names.unshift(name);
			device.accData(nameKey, names);
		}
	};

	/** Gets a list of credit cards.
	 *
	 *	@return A list of lists of objects. The list is sorted by importance.
	 *			Each object has the following properties:
	 *			
	 *				preview - The PAN with most of the numbers replaced
	 *						  with "X".  E.g. XXXXXXXXXXXX1234
	 *				key - The database key for the credit card.
	 */
	device.getCCs = function()
	{
		return device.accData("CCs") || [];
	};

	/**	Encrypts some plaintext using a password.
	 *
	 *	Should probably use something like PBKDF2 in order to make an
	 *	encryption key and then use AES for the actual encryption.  iOS was
	 *	using 10,000 iterations in their PBKDF2 in 2010, so I wouldn't use
	 *	anything less than 20,000 iterations.  This was written in 2013
	 *	though, so depending on how far in the future you are you may want
	 *	more.  If that doesn't make sense to you, please ask sjelin, who will
	 *	hopefully have read a book on cryptography by now!
	 *
	 *	@param	plaintext The text to encrypt
	 *	@param	password	The password to encrypt with.
	 *						null means no password.  The fuction should
	 *						return very quickly if null is used. 
	 *	@return	The ciphertext
	 */
	device.encrypt = function(plaintext, password) {
		var iter = 50000;
		if(password == null) {
			if(DEBUG)
				assert(device.ccWOPass());
			password = "password";
			iter = 101;
			
		}
		return sjcl.encrypt(password, plaintext, {iter: iter});
	}

	/**	Decrypts some ciphertext which was encrypted with device.encrypt()
	 *
	 *	@param	ciphertext The text to decrypt
	 *	@param	password	The password to decrypt with.
	 *						null means no password.  The fuction should
	 *						return very quickly if null is used. 
	 *	@return	The plaintext if the password was correct, else null
	 */
	device.decrypt = function(ciphertext, password) {
		var iter = 50000;
		if(password == null) {
			if(DEBUG)
				assert(device.ccWOPass());
			password = "password";
			iter = 101;
			
		}
		try {
			return sjcl.decrypt(password, ciphertext, {iter: iter});
		} catch(e) {
			return null;
		}
	}


	/** Adds a card to the list of credit cards
	 *
	 *	@param	pan	The principle account number
	 *	@param	name The name on the card
	 *	@param	expr The experation date for the card
	 *	@param	zip The zip code for the address of the card holder
	 *	@param	password	The password to encrypt the card data with
	 *						null means no password.  The fuction should
	 *						return very quickly if null is used. 
	 */
	device.storeCC = function(pan, name, expr, zip, password)
	{
		var k;
		do {
			k = "card::"+randStr();
		} while(device.accData(k) !== undefined);
		preview = pan.slice(-4);
		while(preview.length < pan.length)
			preview = "X"+preview;
		var ccList = device.accData("CCs") || [];
		ccList.unshift({preview: preview, key: k});
		device.accData("CCs", ccList);
		device.accData(k, device.encrypt(JSON.stringify({
			pan: pan,
			name: name,
			expr: expr,
			zip: zip
		}), password));
	};

	/**	Gets a credit card from storage
	 *
	 *	@param	key The database key for the card information
	 *	@param	password	The password to decrypt the card data with
	 *						null means no password.  The fuction should
	 *						return very quickly if null is used. 
	 *	@return	An object containing the credit card information.
	 *			The fields are pan, name, expr, & zip.  Returns undefined for
	 *			invalid key and null for incorrect password
	 */
	device.getCC = function(key, password)
	{
		//Load and decrypt
		var cipher = device.accData(key);
		if(cipher == undefined)
			return undefined;
		var stringy = device.decrypt(cipher, password);
		if(stringy == null)
			return null;

		//Update order of CC list
		var ccList = device.accData("CCs");
		for(var i = 0; ccList[i].key != key; i++)
			;
		ccList.unshift(ccList.splice(i, 1));
		device.accData("CCs", ccList);

		return JSON.parse(stringy);
	};

	/**	Deletes a credit card from storage
	 *
	 *	@param	key The database key for the card information
	 */
	device.deleteCC = function(key)
	{
		device.accData(key, undefined);
		var ccList = device.accData("CCs");
		for(var i = 0; ccList[i].key != key; i++)
			;
		ccList.splice(i, 1);
		device.accData("CCs", ccList);
	};
})()
