var BodyView = Fluid.compileView({
	template: templates.body,
	/*	See models.js for a description of what the parameters mean
	 */
	calc: function(	activeView, width, height, items, split, tipSlider, tip,
					cards, passwords, newCardInfo, cardFocus, email,
					feedback, err){
		////////////////////////////////////////////////
		////		Compute Basic Style Info		////
		////////////////////////////////////////////////
		if((activeView == consts.views.RECEIPT) && (models.split() != null))
			activeView = consts.views.SPLIT;
		var heightClasses = "";
		var fS = Math.round(width/8);
		var i;//get rid of warnings from uglifyjs
		if({{DEBUG}}) {
			for(i = 0; i <= Math.min(height/fS, 16); i += 0.1)
				heightClasses += " h_" + i + "em";
		} else {
			var heights = [8, 12, 14.2];
			for(i = 0; i <= heights.length; i++)
				if(heights[i] < height/fS)
					heightClasses += " h_"+heights[i]+"em";
		}
		heightClasses = heightClasses.trim();

		var views;
		if(err == null) {
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
			for(var i in items) {
				var item = items[i];
				var frac = item.denom ? (item.num || 0) / item.denom : 1;
				var mods = [];
				for(var j = 0; j < item.mods.length; j++) {
					var mod = item.mods[j];
					if(item.type == consts.statuses.CHECKED)
						updateTotals(mod, frac);
					mods.push({
						name: mod.name,
						price: money.round(frac*(mod.price-mod.discount)/100)
					});
				}
				if(item.type == consts.statuses.CHECKED)
					updateTotals(item, frac);
				elems.push({
					name: item.name,
					id: item.itemID,
					status: item.status,
					num: item.num,
					denom: item.denom,
					price: money.round(frac*(item.price-item.discount)/100),
					mods: mods
				});
			}

			totals.total =	totals.subtotal + totals.discount + totals.tax +
							totals.serviceCharge;
			views = [new ReceiptView(elems, {
				subtotal: money.round(totals.subtotal/100),
				discount: money.round(totals.discount/100),
				serviceCharge: money.round(totals.serviceCharge/100),
				tax: money.round(totals.tax/100),
				total: money.round(totals.total/100)
			}), new SplitView(split.name, split.inNumWays)];

			////////////////////////////////////////////
			////		 Compute Other Views		////
			////////////////////////////////////////////
			views.push(new PayView(	money.round(totals.total/100),
									money.round(totals.totalDiscount/100),
									money.round(totals.serviceCharge/100),
									tipSlider, tip,
									10000*tip/totals.tippable,
									cards[cardFocus] ? "(Select Card)":
										(!cards[cardFocus].reqPass || 
											passwords[cardFocus]) ?
												"X-"+cards[cardFocus
																].lastFour :
												"(Enter Password)"));
			views.push(new CardsView(cardFocus,cards,passwords,newCardInfo));
			views.push(new FeedbackView(email, feedback));
		} else {
			views = [new ErrorView(err.heading, err.message, err.symbol)];
		}

		////////////////////////
		////	 Return		////
		////////////////////////
		return {activeView: activeView, views: views,
				heightClasses: heightClasses, fontSize: fS};
	},
	noMemoize: true
});
