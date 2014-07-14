var CardsView = Fluid.compileView({
	template: templates.cards.body,
	/*	@param	cardFocusIndex	See documentation for models.cardFocus
	 *	@param	cardInfo	See documentation for models.cards
	 *	@param	passwords	See documentation for models.passwords
	 *	@param	newCardInfo	See documentation for models.newCardInfo 
	 *	@param	emHeight Ten times the height of the window in ems
	 */
	fill: function(cardFocusIndex,cardInfo,passwords,newCardInfo,emHeight) {
		var ret = {cards_height: 0};
		ret.newCard = new NewCardView(cardFocusIndex == -1, newCardInfo.type,
									newCardInfo.save, newCardInfo.reqPass);
		ret.savedCards = [];
		for(var i = 0; i < cardInfo.length; i++) {
			var focus = i == cardFocusIndex;
			ret.savedCards.push(new SavedCardView(i, ret.cards_height, focus,
					cardInfo[i].type, cardInfo[i].lastFour, cardInfo[i].len),
					cardInfo[i].reqPass);

			//Height of card
			ret.cards_height++;
			if(focus) {
				ret.cards_height++;
				if(cardInfo[i].reqPass)
					ret.cards_height++;
			}
		}

		//Final height stuff
		ret.cards_height++;
		if(cardFocusIndex == -1) {
			ret.cards_height += 6;
			if(newCardInfo.save) {
				ret.cards_height++;
				if(newCardInfo.reqPass)
					ret.cards_height++;
			}
		}
		if(ret.cards_height + 4.225 < emHeight/10)
			ret.height_class = "tall";
		else
			ret.height_class = "";

		//See if confirm button should be disabled
		ret.confirm_disabled = (((cardFocusIndex == -1) &&
					(newCardInfo.pan.length > 0) &&
					(newCardInfo.name.length > 0) &&
					(newCardInfo.exprYear.length > 0) &&
					(newCardInfo.cvv.length > 0) &&
					(newCardInfo.zip.length > 0) &&
					(!newCardInfo.save || !newCardInfo.reqPass ||
						newCardInfo.password.length > 0)
			) || passwords[cardFocusIndex] ||
				!(cardInfo[cardFocusIndex]||{reqPass:true}).reqPass) ?
					"" : "disabled";

		return ret;
	},
	updateControls: function(_, $el, fIndex, cInfo, pass, newCC) {
		$el.find(".confirm")[0].onclick = function() {
			if(fIndex == -1) {
				var err = creditCards.validate(newCC);
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
