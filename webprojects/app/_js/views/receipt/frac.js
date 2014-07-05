var FracView = Fluid.compileView({
	template: templates.receipt.frac,
	/*	@param	num The numerator of the fraction
	 *	@param	denom The denominator of the fraction
	 */
	fill: function(num, denom) {
		return {num: num, denom: denom};
	}
});
