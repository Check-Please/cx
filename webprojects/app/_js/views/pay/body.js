var PayView = Fluid.compileView({
	template: templates.pay.body,
	/*	@param	price The amount due in cents
	 *	@param	discount The discount in cents
	 *	@param	sc The service charge in cents
	 *	@param	slider See documentation for models.tipSlider
	 *	@param	tip	See documentation for models.tip
	 *	@param	tipPrct The percent the tip is of the total bill
	 *	@param	cardMsg	A message about the state of the selected card.
	 *					One of the following:
	 *						-	The string "X-____" where "____" is the last
	 *							four digits of the card
	 *						-	A message about needing a password
	 *						-	A message about needing to select a card
	 */
	calc: function(price, discount, sc, slider, tip, tipPrct, cardMsg) {
		return {
			price: money.toStr(price),
			notes:	!discount && !sc	? new EmptyView()			:
					!discount			? new ServiceChargeView(sc)	:
					!sc					? new DiscountView(discount):
									new DiscountAndSCView(discount, sc),
			slider_focus:	slider!=consts.tipSlider.CUSTOM_TIP	? "focus":"",
			small_focus:	slider==consts.tipSlider.SMALL_TIP	? "focus":"",
			med_focus:		slider==consts.tipSlider.MED_TIP	? "focus":"",
			large_focus:	slider==consts.tipSlider.LARGE_TIP	? "focus":"",
			other_focus:	slider==consts.tipSlider.CUSTOM_TIP	? "focus":"",
			tip: money.toStr(tip, ""),
			tipPrct: tipPrct,
			tipPrctHide: slider == null ? "" : "display: none",
			cardMsg: cardMsg,
			confirm_disable: cardFour && (tip!=undefined) ? "" : "disabled"};
	},
	listeners: {".tip .value input": models.tip},
	addControls: function($el) {
		$el.find("> .back").click(function() {
			models.activeView.set(consts.views.RECEIPT);
		});
		$el.find(".tip .slider .small").click(function() {
			models.tipSlider.set(consts.tipSlider.SMALL_TIP);
		});
		$el.find(".tip .slider .med").click(function() {
			models.tipSlider.set(consts.tipSlider.MED_TIP);
		});
		$el.find(".tip .slider .large").click(function() {
			models.tipSlider.set(consts.tipSlider.LARGE_TIP);
		});
		$el.find(".tip .slider .other").click(function() {
			models.tipSlider.set(consts.tipSlider.CUSTOM_TIP);
		});
		$el.find(".tip .value .back").click(function() {
			models.tipSlider.set(consts.tipSlider.MED_TIP);
		});

		$el.find(".confirm").click(server.pay);
	}
});
