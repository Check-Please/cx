var SplitView = Fluid.compileView({
	template: templates.split.body,
	/*	@param	name The name of the item being split
	 *	@param	input The current value inputted into the split view
	 */
	calc: function(name, input) {
		return {name: name,
				input: input,
				confirm_disabled: input.length == 0 ? "disabled" : ""};
	},
	listeners: {"input": models.split.sub("inNumWays")},
	addControls: function($el) {
		$el.find(".confirm").click(function() {
			if(models.split.get().length == 0)
				alert("Enter a number");
			else
				server.doSplit();
		});
		$el.find(".cancel").click(function() {models.split.set()});
	}
});