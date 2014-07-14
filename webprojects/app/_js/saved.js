/**	This module provides methods for accessing and changing saved data
 *
 *	A note about saved.quickCheckCCPass():
 *		Passwords are checked server side, not client side.  This function
 *		only exists to improve the UX.  Having a way to instantly check if
 *		a password is correct can allow the user to quickly be notified about
 *		a typo and correct it.  However, the issue is that this kind of quick
 *		check could be brute forced by an attacker.
 *
 *		Thus, this function, which has a substantial number of false
 *		positives (20% of all strings) was created.  Even if an attacker
 *		brute forces this function for information, they will not learn very
 *		much about the user's actual password (which again, is checked server
 *		side).  However, at the same time, if a user merely enters a typo,
 *		this check will generally detect it.  Thus, this check can be used to
 *		improve the UX without revealing much of the user's password.
 *
 *		It should be noted however, that this check does reveal SOME of the
 *		user's password (~2.32 bits of entropy).  In the case of this
 *		particular program, user passwords are optional and frankly a bit
 *		unnecessary (why the feature exists at all is another story).  As
 *		such, we are OK with revealing this information (we would actually be
 *		OK with revealing even more information, except for the issue of
 *		password reuse).  However, we do not advise that other software
 *		developers blindly copy this feature into their codebase.
 *
 *		It should also be noted that because there is some degree of client-
 *		side password checking, the user generally needs fewer server-side
 *		attempts before he/she enters the correct password.  As such, the
 *		existence of this client-side check allows us to be more restrictive
 *		server-side.  So on net, this leads to very little decrease in
 *		password strength on our system.  However, because of the issue of
 *		password reuse, the externalities of using techniques like this are
 *		unclear.
 *
 *	@author sjelin
 */

var saved = saved || {};

(function() {
	"use strict";

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
	saved.getClientID = function()
	{
		var id = device.accData("id");
		if(id == undefined) {
			id = device.makeUUID();
			device.accData("id", id);
		}
		return id;
	};

	var nameKey = "names";

	/**	Gets a list of names of people who have used the app on this device
	 *
	 *	@return	List of names sorted with the important first
	 */
	saved.getNames = function()
	{
		return device.accData(nameKey) || [];
	};

	/** Logs that a person with a given name has used the app
	 *
	 *	@param name The name to log
	 */
	saved.logName = function(name)
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

	var cardsID = "cards"

	/**	Gets a list of all unexpired credit cards on the device
	 *
	 *	@return	A list of credit cards, matching the format of models.cards
	 */
	saved.getCCs = function()
	{
		var cards = device.accData(cardsID) || [];
		var ret = [];
		var year = new Date().getYear() + 1900;
		var month = new Date().getMonth() + 1;
		for(var i = cards.length-1; i >= 0; i--)
			if(		(cards[i].exprYear > year) || (
					(cards[i].exprYear == year) &&
					(cards[i].exprMonth >= month)))
				ret.push({
					type: cards[i].type,
					lastFour: cards[i].lastFour,
					len: cards[i].len,
					reqPass: cards[i].reqPass
				});
			else
				cards.splice(i, 1);
		device.accData(cardsID, cards);
		return ret;
	}

	function strMod5(str)
	{
		var ret = 0;
		for(var i = 0; i < str.length; i++)
			ret = (ret + str.charCodeAt(i)) % 5;
		return ret;
	}

	/**	Save a credit card onto the device
	 *
	 *	@param	ccInfo	The information about the credit card from.  Matches
	 *					the format of models.newCardInfo
	 *	@param	ciphertext	Encrypted credit card information provided by the
	 *						server.  Need not be accessible client side in
	 *						the future.
	 */
	saved.saveCC = function(ccInfo, ciphertext)
	{
		var ciphertextKey = device.makeUUID().split("-").join("");
		var cards = device.accData(cardsID) || [];
		cards.push({
			lastFour: ccInfo.pan.slice(-4),
			len: ccInfo.pan.length,
			type: ccInfo.type,
			reqPass: ccInfo.reqPass,
			exprMonth: ccInfo.exprMonth,
			exprYear: creditCards.getYear(ccInfo.exprMonth),
			ciphertextKey: ciphertextKey,
			passMod5: strMod5(ccInfo.password)
		});
		device.accData(cardsID, cards);
		device.storeSecret(ciphertextKey, ciphertext);
		models.cards(saved.getCCs());
	}

	/**	Roughly checks a password.  False positives are very possible, though
	 *	false negatives are not.
	 *
	 *	@param	index The index of the card to check the password for
	 *	@param	pass The password to check
	 *
	 *	@return	true iff pass might be the correct password
	 */
	saved.quickCheckCCPass = function(index, pass)
	{
		var cards = device.accData(cardsID) || [];
		return cards[index].passMod5 == strMod5(pass);
	}


	/**	Get the ciphertext for the card at a given index.  Need not match
	 *	the ciphertext passed to saved.saveCC(), but must be comprehensible
	 *	to the sever
	 *
	 *	@param	index The index of the card to get the ciphertext for
	 */
	saved.getCardCiphertext = function(index)
	{
		return device.accData(device.accData(cardsID)[index].ciphertextKey);
	}

	/**	Logs that a credit card has just been used
	 *
	 *	@param	index The index of the card which was just used
	 */
	saved.logCCUse = function(index)
	{
		var cards = device.accData(cardsID) || [];
		cards.unshift(cards.splice(index, 1));
		device.accData(cardsID, cards);
		models.cards(saved.getCCs());
	}

	/**	Deletes a credit card
	 *
	 *	@param	index The index of the card to delete
	 */
	saved.deleteCC = function(index)
	{
		var cards = device.accData(cardsID) || [];
		cards.splice(index, 1);
		device.accData(cardsID, cards);
		models.cards(saved.getCCs());
	}

	/**	Deletes all saved cards
	 */
	saved.deleteCCs = function()
	{
		device.accData(cardsID, []);
		models.cards([]);
	}
})();
