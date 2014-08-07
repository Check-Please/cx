var LoadingView = Fluid.compileView({
	template: templates.loading.body,
	/*	@param	name The name of the item being split
	 *	@param	input The current value inputted into the split view
	 */
	fill: function(message, percent) {
		if(message == null)
			return {};
		else
			return {message: message || text.LOADING_DONE,
					percent: percent == null ? "" : percent,
					hide_percent: percent == null ? "hide" : ""};
	}
});
