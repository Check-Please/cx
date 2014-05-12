window.onload = function() {
	var logo = document.getElementById("logo")
	var body = document.getElementsByTagName("body")[0];
	(window.onscroll = window.ontouchmove = function() {
		var needsScroll = logo.clientHeight < window.scrollY
		var hasScroll = "scroll" == body.getAttribute("class");
		if(!needsScroll && hasScroll)
			body.removeAttribute("class");
		else if(needsScroll && !hasScroll)
			body.setAttribute("class", "scroll");
	})();
	window.onresize = function() {
		logo.setAttribute("style", "font-size: "+body.clientWidth+"px");
		window.onscroll();
	}
};
