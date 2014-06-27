var ErrorView = Fluid.compileView({
	template: templates.err.body,
	/*	@param	heading A one or two word decription of what is wrong
	 *	@param	message The error message
	 *	@param	symbol A character that evokes the feeling of what's wrong
	 */
	calc: function(heading, message, symbol) {
		return {heading: heading, message: message, symbol: symbol};
	},
	addControls: function($el) {
		$el.find(".confirm").click(location.reload.bind(location));
	}
});
