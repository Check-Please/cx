var DiscountView = Fluid.compileView({
	template: templates.pay.discount,
	/*	@param	discount The discount in cents
	 */
	calc: function(discount) {
		return {discount: discount?money.toStr(-discount).substr(1):"$0.00"};
	}
});
