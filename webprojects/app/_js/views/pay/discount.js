var DiscountView = Fluid.compileView({
	template: templates.pay.discount,
	/*	@param	discount The discount in cents
	 */
	fill: function(discount) {
		return {discount: money.toStr(-(discount || 0)).substr(1)};
	}
});
