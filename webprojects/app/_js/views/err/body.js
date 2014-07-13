var ErrorView = Fluid.compileView({
	template: templates.err.body,
	/*	@param	heading A one or two word decription of what is wrong
	 *	@param	message The error message
	 *	@param	symbol A character that evokes the feeling of what's wrong
	 *	@param	emHeight Ten times the height in em
	 */
	fill: function(heading, message, symbol, emHeight) {
		return {heading: heading, message: message, symbol: symbol,
				font_ratio: Math.min(1, emHeight/60)};
	},
	addControls: function($el) {
		$el.find(".confirm").click(location.reload.bind(location));
	}
});
