var NewCardView = Fluid.compileView({
	template: templates.cards.newCard,
	/*	@param	hasFocus If the user has chosen to input a new card
	 *	@param	type The type of the card (visa, etc) or undefined if unclear
	 *	@param	len The number of characters entered in for a PAN
	 *	@param	save If the card should be saved for future use
	 *	@param	reqPass If the saved card should require a password to use
	 */
	fill: function(hasFocus, type, len, save, reqPass) {
		return {focus: hasFocus ? "focus" : "",
				save: save ? "save" : "",
				reqPass: reqPass ? "reqPass" : "",
				type: type ? type : len >= 8 ? "unknown" : ""};
	},
	listeners: {
		".pan input":		models.newCardInfo.sub("pan"),
		".name input":		models.newCardInfo.sub("name"),
		".expire select":	models.newCardInfo.sub("exprMonth"),
		".expire input":	models.newCardInfo.sub("exprYear"),
		".cvv input":		models.newCardInfo.sub("cvv"),
		".zip input":		models.newCardInfo.sub("zip"),
		".save input":		models.newCardInfo.sub("save"),
		".reqPass input":	models.newCardInfo.sub("reqPass"),
		".password input":	models.newCardInfo.sub("password")
	},
	addControls: function($el) {
		$el.find("a.new").click(function() {models.cardFocus(-1);});
		var $saveIn = $el.find(".save input");
		var $rpIn = $el.find(".reqPass input");
		$el.find(".save").click(function(ev) {
			if(ev.target != $saveIn[0])
				$saveIn.click();
		});
		$el.find(".reqPass").click(function(ev) {
			if(ev.target != $rpIn[0])
				$rpIn.click();
		});
	}
});
