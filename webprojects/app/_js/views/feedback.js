/*	The view for the feedback page
 *
 *	@owner sjelin
 */

mvc.views = mvc.views || {};
(function () {
	"use strict";

	var $view;
	function markDone()
	{
		$view[(mvc.done() ? "add" : "remove") + "Class"]("not-done");
	}

	mvc.views.feedback = {
		build: function() {
			if(!$view) {
				$view = $(templates.feedback());
				$view(".ratings").on("click", "a", function() {
					var $this = $(this);
					var rating;
					if($this.hasClass("bad"))
						rating = -1;
					else if($this.hasClass("ok"))
						rating = 0;
					else if($this.hasClass("just"))
						rating = 1;
					else
						rating = 2;
					$view.find(".ratings a").addClass("not-picked");
					$this.removeClass("not-picked");
					ajax.send("cx", "rate", {
						tableKey: mvc.key(),
						connectionID: mvc.connectionID(),
						rating: rating
					}, $.noop, buildAjaxErrFun("send rating"));
				});
			} else
				$view.show();
			markDone();
		},
		unbuild: function() {$view.hide();}
	}
	mvc.done.listen(markDone);
})();
