var LandscapeView = Fluid.compileView({
	template: templates.landscape.body,
	fill: function(height) {return {window_height: height};},
	addControls: function($el) {
		$el.find("a").click(function() {models.allowLandscape(true);});
	}
});
