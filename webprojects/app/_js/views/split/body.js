var SplitView = Fluid.compileView({
	template: templates.split.body,
	/*	@param	name The name of the item being split
	 *	@param	input The current value inputted into the split view
	 */
	fill: function(name, input) {
		var ret = {	input: input,
					confirm_disabled: (input || "0") == 0 ? "disabled" : ""};
		if(name !== undefined)
			ret.name = name;
		return ret;
	},
	listeners: {"input": models.split.sub("inNumWays")},
	addControls: function($el) {
		$el.find(".confirm").click(function() {
			if(models.split().inNumWays.length == 0)
				alert(text.SPLIT_EMPTY_ALERT);
			else if(models.split().inNumWays == 0)
				alert(text.SPLIT_ZERO_ALERT);
			else
				server.doSplit();
		});
		$el.find(".cancel").click(function() {models.split({})});
	}
});
