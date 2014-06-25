var CardsView = Fluid.compileView({
	template: templates.cards.body,
	/*	@param	cardFocusIndex	See documentation for models.cardFocus
	 *	@param	cardInfo	See documentation for models.cards
	 *	@param	passwords	See documentation for models.passwords
	 *	@param	newCardInfo	See documentation for models.newCardInfo 
	 */
	calc: function(cardFocusIndex, cardInfo, passwords, newCardInfo) {
		var ret = new Object();
		ret.newCard = new NewCardView(cardFocusIndex == -1, newCardInfo.type,
									newCardInfo.save, newCardInfo.reqPass);
		ret.savedCards = [];
		for(var i = 0; i < cardInfo.length; i++)
			ret.savedCards.push(new SavedCardView(i, i == cardFocusIndex,
					cardInfo[i].type, cardInfo[i].lastFour, cardInfo[i].len),
					cardInfo[i].reqPass);
		ret.confirm_disabled = (((cardFocusIndex == -1) &&
					(newCardInfo.pan.length > 0) &&
					(newCardInfo.name.length > 0) &&
					(newCardInfo.exprYear.length > 0) &&
					(newCardInfo.cvv.length > 0) &&
					(newCardInfo.zip.length > 0) &&
					(!newCardInfo.save || !newCardInfo.reqPass ||
						newCardInfo.password.length > 0)
			) || passwords[cardFocusIndex] ||
				!(cardInfo[cardFocusIndex]||{}).reqPass) ? "" : " disabled";
		return ret;
	},
	updateControls: function(_, $el, fIndex, cInfo, pass, newCC) {
		$el.find(".confirm")[0].onclick = function() {
			if(fIndex == -1) {
				var err = creditCards.validate(newCC.pan, newCC.name,
											newCC.exprMonth, newCC.exprYear,
											newCC.cvv, newCC.zip);
				if(err != undefined)
					return alert(err);
			} else if(cInfo[fIndex] == undefined)
				return alert("Please select a card");
			else if(cInfo[fIndex].reqPass) {
				if((pass[fIndex] || "").length == 0)
					return alert("Please enter card password");
				if(!saved.quickCheckCCPass(fIndex, pass[fIndex]))
					return alert("Incorrect password");
			}
			models.activeView.set(consts.views.PAY);
		}
	}
});
