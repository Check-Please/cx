var BodyView = Fluid.compileView({
	template: templates.body,
	/*	See models.js for a description of what the parameters mean
	 */
	fill: function(	activeView, width, height, items, split, tipSlider, tip,
					cards, passwords, newCardInfo, cardFocus, email,
					feedback, loading, err, done, allowLandscape) {
		////////////////////////////////////////////////
		////		Compute Basic Style Info		////
		////////////////////////////////////////////////
		if((activeView == consts.views.RECEIPT) && (split.trgt != null))
			activeView += " "+consts.views.SPLIT;
		else if((err == null) && (loading != null))
			activeView += " "+consts.views.LOADING;
		var heightClasses = "";
		var fS = Math.round(width/8);
		var emHeight = Math.floor(height/fS*10);
		for(var i = 30, max = Math.min(emHeight, 160); i <= max; i++)
			heightClasses += " h" + i;
		for(var i = Math.max(emHeight+1, 30); i <= 160; i++)
			heightClasses += " h_" + i;
		heightClasses = heightClasses.trim();
	
		//Now watch as I ignore the MVC which I myself painstakingly build!
		var noTrans =	$("#body").css("font-size") != (fS+"px") ||
						!$("#body").hasClass("h"+emHeight) ||
						$("#body").hasClass("h"+(emHeight+1)) ?
						"noTrans" : "";

		var views;
		if((err == null) && (allowLandscape || (height > width))) {
			////////////////////////////////////////////
			////		Compute Receipt View		////
			////////////////////////////////////////////
			var totals = {	subtotal: 0, discount: 0, serviceCharge: 0,
							tax: 0, tippable: 0, totalDiscount: 0};
			function updateTotals(item, frac) {
				totals.subtotal += frac * (item.price - item.discount);
				totals.serviceCharge += frac * item.serviceCharge;
				totals.tax += frac * item.tax;
				totals.tippable += frac * (item.price + item.tax);
				totals.totalDiscount += frac * item.discount;
			}
			var elems = [];
			for(var id in items) {
				var item = items[id];
				var frac = item.denom ? (item.num || 0) / item.denom : 1;
				var mods = [];
				for(var j = 0; j < item.mods.length; j++) {
					var mod = item.mods[j];
					if(item.status == consts.statuses.CHECKED)
						updateTotals(mod, frac);
					mods.push({
						name: mod.name,
						price: money.round(frac*(mod.price-mod.discount)/100)
					});
				}
				if(item.status == consts.statuses.CHECKED)
					updateTotals(item, frac);
				elems.push({
					name: item.name,
					id: id,
					status: item.status,
					num: item.num,
					denom: item.denom,
					price: money.round(frac*(item.price-item.discount)/100),
					mods: mods
				});
			}

			totals.total =	totals.subtotal + totals.discount + totals.tax +
							totals.serviceCharge;
			var summaries = {};
			summaries[consts.summaries.SUBTOTAL] =
									money.round(totals.subtotal/100);
			summaries[consts.summaries.DISCOUNT] =
									money.round(totals.discount/100);
			summaries[consts.summaries.S_C] =
									money.round(totals.serviceCharge/100);
			summaries[consts.summaries.TAX] =
									money.round(totals.tax/100);
			summaries[consts.summaries.TOTAL] =
									money.round(totals.total/100);
			views = [new ReceiptView(elems, summaries, emHeight),
				new SplitView((split || {}).name, (split || {}).inNumWays)];

			////////////////////////////////////////////
			////		 Compute Other Views		////
			////////////////////////////////////////////
			var lastFour = null;
			if(cardFocus == -1) {
				if(creditCards.validate(newCardInfo) == null)
					lastFour = newCardInfo.pan.slice(-4);
			} else if(!cards[cardFocus].reqPass || passwords[cardFocus])
				lastFour = cards[cardFocus].lastFour;
			views.push(new PayView(	money.round(totals.total/100),
						money.round(totals.totalDiscount/100),
						money.round(totals.serviceCharge/100),
						tipSlider, tip, 1000000*tip/totals.tippable,
						lastFour != null ? "X-"+lastFour : cardFocus == -1 ?
							text.SELECT_CARD_NUM_REPL :
							text.ENTER_PASSWORD_NUM_REPL,
						emHeight));
			views.push(new CardsView(	cardFocus, cards, passwords,
										newCardInfo, emHeight));
			views.push(new FeedbackView(email, feedback, done));
			views.push(new LoadingView(	loading && loading.message,
										loading && loading.percent));
		} else if(err != null) {
			views = [new ErrorView(err.heading, err.message, err.symbol,
																emHeight)];
		} else {
			views = [new LandscapeView(height)];
		}

		////////////////////////
		////	 Return		////
		////////////////////////
		return {activeView: activeView, views: views, noTrans: noTrans,
				heightClasses: heightClasses, fontSize: fS};
	}
});
