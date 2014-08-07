var DiscountAndSCView = Fluid.compileView({
	template: templates.pay.discountAndSC,
	/*	@param	discount The discount in cents
	 *	@param	serviceCharge The service charge in cents
	 */
	fill: function(discount, serviceCharge) {
		return {discount: money.toStr(-(discount || 0)).substr(1),
				serviceCharge: money.toStr(serviceCharge || 0)};
	}
});
