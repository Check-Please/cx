window.onload = function() {
	var nav = document.getElementById("nav");
	var body = document.getElementsByTagName("body")[0];
	(window.onscroll = window.onresize = function() {
		body.setAttribute("class", "");
		if(nav.offsetTop < window.scrollY)
			body.setAttribute("class", "scroll");
	})();
};
