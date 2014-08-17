///////////////////////////////
///  EMAIL LIST POPUP CODE  ///
///////////////////////////////

//Model code
var state = {
	popup: false,
	submit: false
}
var oldState = {};

//View Code
function draw() {
	var resetPopup = false;

	if(state.popup && !oldState.popup) {
		$('input[name="email"]').focus();
		if(state.submit) {
			state.submit = false;
			$('input[type="email"]').val("");
			$('input[type="text"]').val("");
			resetPopup = true;
		}
	}

	$("#sign-up").toggleClass("popup", state.popup);
	$("#sign-up").toggleClass("submit", state.submit);
	$("#sign-up").toggleClass("reset-popup", resetPopup);

	$.extend(oldState, state);
}

//Controller code
window.onload = function() {
	//The following is write-only code ok? :D
	var $email = $('input[type="email"]');
	$("#menu a").click(function() {draw(state.popup = true);});
	$("#sign-up").click(function() {draw(state.popup = false);});
	$("#sign-up .wrapper").click(function(ev) {ev.stopPropagation();});
	$('#sign-up input[type="button"]').click(function() {
		var email = $email.val();
		var zip = $('input[type="text"]').val();
		if(!email || !($email[0].validity || {
				valid: email.match(/\w+@\w+\.\w+/) != null}).valid)
			return alert("Please enter a valid email address");
		if(!zip)
			return alert("Please enter a zip code");
		if(zip.length != 5)
			return alert("Please enter a give a five digit zip code");
		$.ajax("splash/new_email", {data: {email: email, zip: zip},
									type: "POST"});
		draw(state.submit = true);
	});

	window.onresize();
	draw();
}
window.onkeypress = function(ev) {
	if(state.popup)
		switch(ev.keyCode) {
			case 13: //Enter
				if(!state.submit) {
					$('#sign-up input[type="button"]').click();
					break;
				}
			case 27: //Escape
				$("#sign-up").click();
		}
}

///////////////////////
///  MENU BAR CODE  ///
///////////////////////

window.onscroll = window.ontouchmove = function() {
	$("#menu").toggleClass("scroll", window.scrollY > 0);
};


/////////////////
///  RESPOND  ///
/////////////////

var CSS_VARS = {
	marginTotal: 40,
	headerFS: 24,
	headerMinW: 500,
	splashFS: 1,
	splashLineHeight: 70,
	splashNumLines: 4,
	splashMinW: 450
}

var $win = $(window);
function fixFS($elem, minW, baseFS, unit) {
	if($win.width() < minW)
		$elem.css("font-size",	($win.width() - CSS_VARS.marginTotal) /
								(minW - CSS_VARS.marginTotal)*baseFS + unit);
	else
		$elem.css("font-size", "");
}

var splashShortenWLowerB = 0;
var splashSplitW = null;
window.onresize = function() {
	//Menu
	fixFS($("#menu"), CSS_VARS.headerMinW, CSS_VARS.headerFS, "px");

	//Splash
	var shorten = $win.width() <= splashShortenWLowerB;
	var $p = $("#splash p");
	$p.height("");
	$p.width("");
	$p.css("font-size", "");
	$("#splash").removeClass("split");
	$p.toggleClass("shorten", shorten);
	if(!shorten && ($p[0].scrollHeight > CSS_VARS.splashLineHeight *
									(0.2+CSS_VARS.splashNumLines))) {
		shorten = true;
		$p.addClass("shorten");
		splashShortenWLowerB = $win.width();
	}
	var split = false;
	if(shorten) {
		if(splashSplitW == null) {
			var wNeeded = $p[0].scrollWidth - $p.width();
			if(wNeeded > 0) {
				splashSplitW = $win.width() + wNeeded;
				$("#splash").addClass("split");
			}
		}
		split = splashSplitW && ($win.width() <= splashSplitW);
	}
	$("#splash").toggleClass("split", !!split);
	if(split)
		$p.height("auto");
	else {
		$p.height($p.find(".wrapper").height());
		$p.width($p.find(".wrapper").width());
	}
	fixFS($p, CSS_VARS.splashMinW, CSS_VARS.splashFS, "em");
	

	//How
	//TODO

	//Other events
	window.onscroll();
}
