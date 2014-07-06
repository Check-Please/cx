var ReceiptView = Fluid.compileView({
	template: templates.receipt.body,
	/*	@param	items	Information on all the items on the ticket.  Array of
	 *					object.  Each element has the following properties:
	 *						status, name, id, num, denom, price, mods
	 *					See the documentation in items.js for what each of
	 *					these properties should be
	 *	@param	summaries	A map from summary types (subtotal, etc) to the
	 *						total for that type
	 *	@param	emHeight Ten times the height of the window in ems
	 */
	fill: function(items, summaries, emHeight) {
		items.sort(function(a,b){return a.id<b.id ? -1 : a.id>b.id ? 1 : 0});
		var ret = {receipt_height: 0};
		ret.items = {};
		var totals = consts.STATUSES.map(op.id.c(0));
		for(var i = 0; i < items.length; i++) {
			totals[items[i].status] += 1 + items[i].mods.length;
			ret.receipt_height += 1 + items[i].mods.length;
		}
		var positions = {};
		for(var i = 0; i < consts.STATUSES.length; i++) {
			positions[consts.STATUSES[i]] = 0;
			for(var j = 0; j < i; j++)
				positions[consts.STATUSES[i]] += totals[consts.STATUSES[j]];
		}
		for(var i = 0; i < items.length; i++) {
			ret.items[items[i].id] = new ReceiptItem(items[i].status,
				positions[items[i].status], items[i].name, items[i].id,
				items[i].num, items[i].denom, items[i].price, items[i].mods);
			positions[items[i].status] += 1 + items[i].mods.length;
		}
		ret.summaries = [];
		for(var i = 0; i < consts.SUMMARIES.length; i++) {
			var type = consts.SUMMARIES[i];
			if(summaries[type])
				ret.summaries.push(new ReceiptItem(type,ret.summaries.length,
					consts.SUMMARY_NAMES[type], 1, 1, summaries[type], []));
		}
		if(ret.receipt_height + ret.summaries.length + 6.225 < emHeight/10)
			ret.height_class = "tall";
		else
			ret.height_class = "";
		return ret;
	},
	addControls: function($el) {
		$el.find(".confirm").click(function() {
			models.activeView.set(consts.views.PAY);
		});
		$el.find("a.all").click(server.setAllCheck.c(true));
		$el.find("a.none").click(server.setAllCheck.c(false));
	}
});
