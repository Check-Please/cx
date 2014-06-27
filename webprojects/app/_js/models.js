/**	In this file we set up the models and add listeners which coordinate
 *	the values of the models with eachother and things like the window's
 *	width.
 *
 *	All the documentation for the models is also in this file
 *
 *	The current list of models is:
 *		tableKey, connectionID, validViews, activeView, width, height, items,
 *		split, tipSlider, tip, cards, passwords, newCardInfo, cardFocus,
 *		email, feedback, loading, error
 *
 *	@owner	sjelin	
 */

var models = models || {};

(function(models) {
	"use strict";

	//=======================================================================
	//  Session Information
	//=======================================================================

	/**	The key used to identify this table */
	models.tableKey = Fluid.newModel(undefined);

	/**	The ID used to identify this instance of the app to the server */
	models.connectionID = Fluid.newModel(undefined);

	//=======================================================================
	//  Routing Information
	//=======================================================================

	/**	A list of possible views to nagivate to given the current state.
	 *	Values come from consts.views */
	models.validViews = Fluid.newModel([	consts.views.RECEIPT,
											consts.views.PAY,
											consts.views.CARDS]);

	/**	The view currently being shown.  Should be an element of
	 *	models.validViews.  If it isn't, the first value in models.validViews
	 *	is used instead */
	models.activeView = Fluid.newModel("");

	window.onhashchange = function() {
		var hash = (window.location.hash || "").slice(1);
		if(models.validViews.get().indexOf(hash) == -1)
			hash = models.validViews.get()[0];
		if(models.activeView.get() != hash)
			models.activeView.set(hash);
	}
	models.activeView.listen(function() {
		server.logPos(models.activeView());
		var hash = "#"+models.activeView();
		if(hash != window.location.hash)
			window.location.hash = hash;
		
	});
	models.validViews.listen(window.onhashchange);

	//=======================================================================
	//  Responsiveness
	//=======================================================================

	/**	The width and height of the device */
	models.width = Fluid.newModel(1);
	models.height = Fluid.newModel(1);
	window.onresize = function() {
		models.width.set($(window).width());
		models.height.set($(window).height());
	}

	//=======================================================================
	//  Receipt View Info
	//=======================================================================

	/**	The items on the ticket.  Map from ids to objects.
	 *
	 *	IDs uniquely identify the item within the ticket.  The format is:
	 *		ID:num
	 *	"ID" indentifies a something that was ordered.  If that thing is
	 *	split between multiple users, then multiple ticket items are created,
	 *	each with the same value for "ID", but with a different value for
	 *	"num".  "num" is a non-negative integer, and generally (but not in
	 *	general) when an item is split n ways the values for "num" will be
	 *	0, 1, ..., n-1.  Note that this is true even if an item hasn't been
	 *	split.  Then it is treated as being split one way.
	 *
	 *	Each element has the following properties:
	 *		name: The name of the item
	 *		status: The status of the item
	 *		num: The numerator for this item
	 *		denom:	The denominator for this item.  If falsy or equal to num,
	 *				the fraction is assume to be equal to 1
	 *		price:	The base price for the item
	 *		discount:	The amount discounted off the price of the item.
	 *		serviceCharge:	The service charge for the item.
	 *		tax: The tax on the item
	 *		mods:	Modifiers for the item.  Array of objects. 
	 *				Each element has the following:
	 *					name, price, discount, serviceCharge, tax
	 *				These properties have basically the same meaning that
	 *				they do in their parents
	 *	Prices are all in 100ths of a cent, with 0 as the only valid falsy
	 *	value.  The prices also represent the cost of the item if the
	 *	fraction was 1.
	 */ 
	models.items = Fluid.newModel({});

	/*	Information about the element currently being split.  If nothing is
	 *	currently being split, undefined.  Otherwise, an object with the
	 *	following properties:
	 *		trgt - The ID of the item being split
	 *		name - The name of the item being split
	 *		currNumWays - The number of ways the item is currently split
	 *		inNumWays -	The number of ways the user has currently inputted
	 */
	models.split = Fluid.newModel(undefined);

	function updateSplit() {
		var split = models.split.get();
		var items = models.items.get();
		if(split != null) {
			var n = Object.keys(items).filer(function(id) {
				if(!id.startsWith(split.trgt+":"))
					return false;
				split.name = split.name || items[id].name;
				return items[id].status != consts.statuses.PAID;
			});
			if(split.currNumWays != n) {
				split.inNumWays = (split.currNumWays = n) + "";
				models.split.alert();
			}
		}
	}
	models.items.listen(updateSplit);
	models.split.listen(updateSplit);

	//=======================================================================
	//  Pay View Info
	//=======================================================================

	/**	The tip ammount picked from the slider.  Must be a value in
	 *	consts.tipSlider
	 */
	models.tipSlider = Fluid.newModel(consts.tipSlider.MED_TIP);

	/** The amount tipped in cents.  undefined if no tip entered */
	models.tip = Fluid.newModel(0);

	var updateTip = function() {
		var prct = undefined;
		switch(models.tipSlider.get()) {
			case consts.tipSlider.SMALL_TIP:	prct = 17; break;
			case consts.tipSlider.MED_TIP:		prct = 20; break;
			case consts.tipSlider.LARGE_TIP:	prct = 25; break;
		}
		if(prct != undefined) {
			var tippable = 0;
			var items = models.items.get();
			for(var i in items) {
				var item = items[i];
				if(item.type == consts.statuses.CHECKED) {
					tippable += (item.price + item.tax) *
								(item.denom ? (item.num||0)/item.denom : 1);
					for(var j = 0; j < item.mods.length; j++)
						tippable += item.mods[j].price + item.mods[j].tax;
				}
			}
			models.tip.set(money.round(tippable * prct / 10000));
		}
	};
	models.tipSlider.listen(updateTip);
	models.items.listen(updateTip);

	//=======================================================================
	//  Cards View Info
	//=======================================================================

	/**	Information about saved credit cards.  Array of objects, each with
	 *	the following properties:
	 *		type: The brand of the card (visa, etc.)
	 *		lastFour: The last four digits of the card
	 *		len: The length of the card number
	 *		reqPass:	If a password is required to use this card
	 */
	models.cards = Fluid.newModel([]);

	/**	A list of passwords that have been entered for each saved card */
	models.passwords = Fluid.newModel([]);

	/** Contains the following properties:
	 *		pan: The card number
	 *		type: The type of the card (Visa, mastercard, etc)
	 *		name: The name on the card
	 *		exprMonth: The month the card expires in
	 *		exprYear: The year the card expires in
	 *		cvv: The security code on the back of the card
	 *		zip: The zip code in the billing address of the card
	 *		save, reqPass, password
	 */
	models.newCardInfo = Fluid.newModel({
		pan: "", type: undefined, name: "", exprMonth: "", exprYear: "",
		cvv: "", zip: "", save: false, reqPass: false, password: ""
	});
	//Ensure correct type
	models.newCardInfo.listen(function() {
		var info = models.newCardInfo.get();
		info.type = creditCards.getType(info.pan);
		info.exprMonth = info.exprMonth.length == 1 ?
						"0"+info.exprMonth : info.exprMonth.substr(0,2);
		//No need to alert() because this is the first listener
	});

	/**	Which card is currently selected.  -1 means the user is entering a
	 *	new card  */
	models.cardFocus = Fluid.newModel(-1);

	//=======================================================================
	//  Feedback View Info
	//=======================================================================

	/**	The email address which the receipt was sent to.  undefined if no
	 *	receipt has been sent yet
	 */
	models.email = Fluid.newModel(undefined);

	/**	The feedback the user gave on their meal.  Must be a value from
	 *	consts.feedback.  undefined if no beedback has been given yet
	 */
	models.feedback = Fluid.newModel(undefined);

	//=======================================================================
	//  Other View Info
	//=======================================================================

	/**	Information for the loading screen to show the user.  undefined if no
	 *	loading screen needs to be shown.  Otherwise, an object with:
	 *		message: What is currently being done
	 *		percent: The percent done.  undefined if unknown
	 *		incrTo: Sometimes it is desirable to have the percentage
	 *				automatically increment.  If this is desired, this
	 *				property should be set to the maximum percentage which
	 *				can be incremented to.  If not, this property should not
	 *				be specified.
	 */
	models.loading = Fluid.newModel(undefined);
	models.loading.listen(function() {
		var loading = models.loading();
		if(loading && (loading.incrTo < loading.percent)) {
			var current = JSON.stringify(loading);
			setTimeout(function() {
				if(JSON.stringify(models.loading()) == current) {
					models.loading().incrTo++;
					models.loading.alert();
				}
			}, 2000);
		}
	});

	/**	Information about the what has gone wrong.  undefined if no error has
	 *	yet occured.  Object has the following properties:
	 *		heading: A one or two word description of what is wrong
	 *		message: A short error message
	 *		symbol: A single character which invokes the feeling of the error
	 */
	models.error = Fluid.newModel(undefined);

	window.onerror = function() {
		alert("JavaScript error from "+url+":\n\n"+err+" (Line #"+num+")");
	}
	models.error.listen(function() {
		if(models.error.get() != undefined)
			models.validViews.set([consts.views.ERROR]);
	});
})(models);
