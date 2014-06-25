var ReceiptItemNameView = Fluid.compileView({
	template: templates.receipt.name,
	/*	@param	name The name of the item
	 *	@param	num The numerator of the fraciton for this item.
	 *	@param	denom	The denominator of the fraction for this item.  If
	 *					falsy or equal to the numerator, the fraction is
	 *					assumed to be equal to 1 and omitted from the UI
	 */
	calc: function(name, num, denom) {
		return {name: name, frac: denom && (num!=denom) ?
								new FracView(num,denom) : new EmptyView()};
	}
});
