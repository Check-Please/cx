/*	The job of this module is to handle responsiveness.
 *
 *	@see README.md
 *
 *	@owner sjelin
 */

(function () {
	"use strict";

	window.onresize = function()
	{
		var $win = $(window);
		var fSz = $win.width()*0.0354;
		var $body = $("body");
		if($body.size() == 0)
			return;
		$("html").css("font-size", $win.width()+"px");
		$(".popup-bg").css("font-size",
					Math.min($win.width(), $win.height())+"px");

		{{JS_SCOPE: 1+       1}};

		if("{{PLATFORM}}" != "iOS") {{JS_SCOPE:
			//Use different logo images for each small size
			var $logo = $("#footer img");
			var logoH = fSz * (window.devicePixelRatio || 1) * 2.5;
			var verySmall = logoH < 19;
			if(logoH < 21.5)
				logoH = 20;
			else if(logoH < 60) {
				var x = logoH % 5;
				logoH = Math.round(logoH - x + (x < 1.5 ? 0 : x < 4 ? 3:5));
			}
			$logo.attr("src", "img/app/cx" + (logoH>60?"":"_"+logoH)+".png");
			if(verySmall || logoH > 60)
				$logo.removeAttr("style");
			else
				$logo.css("height",
						Math.round(logoH/(window.devicePixelRatio||1))+"px");
		}}

		//The following is inefficient but works 100% of the time
		//All the min and max stuff is to deal with browser inconsistency
		$body.removeClass("tall");
		if(Math.max($body.get(0).scrollHeight,
					$("html").get(0).scrollHeight) <=
				Math.min($body.get(0).clientHeight,
					$("html").get(0).clientHeight))
			$body.addClass("tall");

		for(var v in mvc.views)
			if($body.hasClass(v) && mvc.views[v].onResize)
				mvc.views[v].onResize(Math.floor(fSz));
	};
})();
