var ServiceChargeView = Fluid.compileView({
	template: templates.pay.serviceCharge,
	/*	@param	serviceCharge The service charge in cents
	 */
	calc: function(serviceCharge) {
		return {serviceCharge: money.toStr(serviceCharge)};
	}
});
