/*	The MVC for the feedback page
 *
 *	@see mvc.js
 *	@owner sjelin
 */

  ////////////////////
 /////  HEADER  /////
////////////////////

var mvc = mvc || {};

(function () {
	"use strict";

  /////////////////////////////////
 /////  CODE (IN NAMESPACE)  /////
/////////////////////////////////

	var inited = false;
	function buildFeedback()
	{
		if(!inited) {
			inited = true;
			$("#feedback-page .ratings").on("click", "a", function() {
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
				$("#feedback-page .ratings a").addClass("not-picked");
				$this.removeClass("not-picked");
				ajax.send("cx", "rate", {
					mobileKey: mvc.key(),
					clientID: mvc.clientID(),
					rating: rating
				}, $.noop, buildAjaxErrFun("send rating"));
			});
		}
		$("#feedback-page")[(mvc.done()?"add":"remove")+"Class"]("not-done");
	}
	mvc.addDoneListener(buildFeedback);

  ////////////////////
 /////  FOOTER  /////
////////////////////

	mvc.buildFeedback = buildFeedback;
})();
