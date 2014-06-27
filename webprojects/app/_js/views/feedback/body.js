var FeedbackView = Fluid.compileView({
	template: templates.feedback.body,
	/*	@param	email See documentation for models.email
	 *	@param	feedback See documentation for models.feedback	
	 */
	calc: function(email, feedback) {
		var ret = {sent: email ? "sent" : "", email: email || ""};
		for(var rating in consts.feedback) {
			ret[rating.toLowerCase()+"_rating"] = consts.feedback[rating];
			ret[rating.toLowerCase()+"_focus"] =
										feedback == rating ? " focus" : "";
		}
		return ret;
	},
	addControls: function($el) {
		$el.find(".email a").click(function() {
			var email = $el.find(".email input").val();
			if(isEmail(email))
				server.sendEmail(email);
			else
				alert("Please enter a valid email address");
		});
		//We don't use event delegation because the UI does weird stuff
		//on iOS with event delegation & it's not super valuable here
		$el.find(".feedback a").click(function() {
			server.sendRating($(this).attr("rating"));
		});
	}
});
