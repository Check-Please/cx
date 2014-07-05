var discountAndSCView = Fluid.compileView({
	template: templates.pay.discountAndSC,
	/*	@param	discount The discount in cents
	 *	@param	serviceCharge The service charge in cents
	 */
	fill: function(discount, serviceCharge) {
		return {discount: discount?money.toStr(-discount).substr(1):"$0.00",
				serviceCharge: money.toStr(serviceCharge)};
	}
});
