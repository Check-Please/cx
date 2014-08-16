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

window.onscroll = window.ontouchmove = window.onresize = function() {
	$("#menu").toggleClass("scroll", window.scrollY > 0);
};

