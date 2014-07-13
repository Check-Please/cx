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
	 *	@param	emH Ten times the height of the view in em
	 */
	fill: function(price, discount, sc, slider, tip, tipPrct, cardMsg, emH) {
		function focusClass(hasFocus) { return hasFocus ? "focus" : "blur"; }

		//Scale down image from 12em to 10.4em
		var scaleIndex = Math.max(Math.min((emH - 100)/16, 1), 0); 
		function scaleStyle(prop, full, min) {
			if(scaleIndex == 1)
				return "";
			return prop.split(",").map(function(p) {
				return p + ": " + ((full-min)*scaleIndex+min) + "em";
			}).join("; ");
		};

		var tipMsg = !parseFloat(tip) ? "0%" : isNaN(tipPrct) ? "Thank you!":
				tipPrct < 100 ? (Math.floor(tipPrct * 10) / 10)+"%" :
				tipPrct < 1000 ? Math.floor(tipPrct) +"%" :
				tipPrct < 10000 ? (Math.floor(tipPrct / 10) / 10)+"x" :
				tipPrct < 100000 ? Math.floor(tipPrct)+"x" : "Thank You!";
		var ret = {
			price: money.toStr(price),
			notes:	!discount && !sc	? new TaxTipNoteView()		:
					!discount			? new ServiceChargeView(sc)	:
					!sc					? new DiscountView(discount):
									new DiscountAndSCView(discount, sc),
			slider_focus:	focusClass(slider!=consts.tipSlider.CUSTOM_TIP),
			small_focus:	focusClass(slider==consts.tipSlider.SMALL_TIP),
			med_focus:		focusClass(slider==consts.tipSlider.MED_TIP),
			large_focus:	focusClass(slider==consts.tipSlider.LARGE_TIP),
			other_focus:	focusClass(slider==consts.tipSlider.CUSTOM_TIP),
			tip_msg: tipMsg,
			tip_thank: isNaN(parseInt(tipMsg)) ? "thank" : "",
			card_msg: cardMsg,
			confirm_disable: cardMsg.match(/^X-[0-9]{4}$/) &&
									(tip != undefined) ? "" : "disabled",
			less_than_ten: price < 1000 ? "less-than-ten" : "",
			back_bottom_padding: scaleStyle("padding-bottom", 1, 0.75),
			amount_bottom_margin: scaleStyle("margin-bottom", 0.4, 0.3),
			tip_bottom_margin: scaleStyle("margin-bottom", 0.8, 0.4),
			tip_cap_bottom_margin: scaleStyle("margin-bottom", 1.4, 1.1),
			tip_inputs_font_size: scaleStyle("font-size", 1, 0.8),
			confirm_margins:scaleStyle("margin-bottom,margin-top",
																1.25, 0.625),
			overflow: scaleIndex ? "hidden" : "scroll" };
		if(slider != consts.tipSlider.CUSTOM_TIP)
			ret.tip = money.toStr(tip, "");
		return ret;
	},
	listeners: {".tip .value input": models.tip},
	addControls: function($el) {
		$el.find("> .back").click(models.activeView.c(consts.views.RECEIPT));
		$el.find(".card").click(models.activeView.c(consts.views.CARDS));

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
			models.tipSlider(consts.tipSlider.CUSTOM_TIP);
			models.tip(undefined);
			$el.find(".tip .value input").val("");

			//Set the focus to the input element, but only after the
			//transition is complete.  I know this is bad code design, I'm
			//sorry :(
			function setFocus() { $el.find(".tip .value input").focus(); }
			if($el.parent().hasClass("h130"))
				setFocus();
			else
				setTimeout(setFocus, 600);
		});
		$el.find(".tip .value .back").click(function() {
			models.tipSlider.set(consts.tipSlider.MED_TIP);
		});

		var tipBoxVal;
		var oldSlider;
		$el.find(".tip .value input").focus(function() {
			tipBoxVal = $(this).val() || undefined;
			oldSlider = models.tipSlider();
			models.tipSlider(consts.tipSlider.CUSTOM_TIP);
		});
		$el.find(".tip .value input").blur(function() {
			if(tipBoxVal == $(this).val())
				models.tipSlider(oldSlider);
		});

		$el.find(".confirm").click(server.pay);
	}
});
