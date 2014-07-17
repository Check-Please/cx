var SavedCardView = Fluid.compileView({
	template: templates.cards.savedCard,
	/*	@param	index The index of the card in the card list
	 *	@param	pos The position of the item on the receipt
	 *	@param	hasFocus If this is the card the user has currently selected
	 *	@param	cardType The brand of the card (visa, mastercard, etc.)
	 *	@param	lastFour The last four digits of the credit card
	 *	@param	cardLen The number of digits in the card
	 *	@param	reqPass If the card requires a password to use
	 */
	fill: function(index,pos,hasFocus,cardType,lastFour,cardLen,reqPass) {
		return {focus: hasFocus ? "focus" : "",
				reqPass: reqPass ? "reqPass" : "",
				type: cardType ? cardType : "unknown",
				position: pos,
				pan: creditCards.format("XXXXXXXXXXXXXXXXXXX".substr(0,
						cardLen-4) + lastFour, cardType, "  ")};
	},
	listeners: function(i) {return {
							".password input": models.passwords.sub(i)};},
	addControls: function($el, index) {
		$el.find("a.pan").click(function() {models.cardFocus(index);});
		$el.find(".delete a").click(saved.deleteCC.c(index));
	}
});
