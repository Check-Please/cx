var FeedbackView = Fluid.compileView({
	template: templates.feedback.body,
	/*	@param	email See documentation for models.email
	 *	@param	feedback See documentation for models.feedback	
	 */
	fill: function(email, feedback, done) {
		var ret = {	sent: email ? "sent" : "", email: email || "",
					finished: done ? "finished" : "unfinished"};
		for(var rating in consts.feedback) {
			var val = consts.feedback[rating];
			ret[rating.toLowerCase()+"_rating"] = val;
			ret[rating.toLowerCase()+"_focus"] =
				feedback == null ? "" : feedback == val ? "focus" : "blur";
		}
		return ret;
	},
	addControls: function($el) {
		$el.find(".email a").click(function() {
			var email = $el.find(".email input").val();
			if(isEmail(email))
				server.sendEmail(email);
			else
				alert(text.INVALID_EMAIL_ALERT);
		});
		//We don't use event delegation because the UI does weird stuff
		//on iOS with event delegation & it's not super valuable here
		$el.find(".feedback a").click(function() {
			server.sendRating($(this).attr("rating"));
		});
		$el.find(".confirm").click(function() {window.location.reload();});
	}
});
