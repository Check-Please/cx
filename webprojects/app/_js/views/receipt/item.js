var ReceiptItemView = Fluid.compileView({
	template: templates.receipt.item,
	/*	@param	type The type of the item (as defined by models.js)
	 *	@param	pos The position of the item on the receipt
	 *	@param	name The name of the item
	 *	@param	id The id for the item
	 *	@param	num The numerator of the fraciton for this item.
	 *	@param	denom	The denominator of the fraction for this item.  If
	 *					falsy or equal to the numerator, the fraction is
	 *					assumed to be equal to 1 and omitted from the UI
	 *	@param	price The price of the item in cents
	 *	@param	mods	The modifiers for the item.  Array of object.  Each
	 *					element of the array has the following properties:
	 *						name: The name of the modifier
	 *						price: The price of the modifier in cents
	 */
	fill: function(type, pos, name, id, num, denom, price, mods) {
		var modViews = [];
		for(var i = 0; i < mods.length; i++)
			modViews.push(new ReceiptModView(mods[i].name,mods[i].price,i));
		return {type: consts.ITEM_CLASSES[type],
				position: pos,
				height: 1+consts.MOD_HEIGHT*mods.length,
				name: new ReceiptItemNameView(name, num, denom),
				price: money.toStr(price),
				mods: modViews,
				first: pos == 0 ? "" : "not-first"};
	},
	addControls: function($el, type, __, ___, id) {
		if(consts.SUMMARY_NAMES[type] == undefined) {
			$el.find(".checkbox").click(server.toggleItemCheck.c(id));
			$el.find(".split").click(function() {
				models.split({trgt: id.split(":")[0]});
			});
		}
	}
});
