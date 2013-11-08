/*	The MVC for the login page
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


	var $loginCb = null;
	var $registerCb = null;
	var complete;

	function validate()
	{
		complete = ($("#login-page .info .email input").val().length != 0) &&
			($("#login-page .info .password input").val().length != 0) &&
			(!$registerCb[0].checked ||
				($("#login-page .info .confirm-password input"
					).val().length != 0));
		$("#login-page .confirm")[(complete ? "remove" : "add")
					+ "Class"]("disabled");
	}

	function completeLogin()
	{
		return complete;
	}

	function refresh()
	{
		var register = $registerCb[0].checked;
		$("#login-page .info").addClass(register ? "register" : "login");
		$("#login-page .info").removeClass(register ? "login":"register");
		$("#login-page .confirm").text(register ? "Register" : "Login");
		window.onresize();
		validate();
	}

	var oldKeyDown = null;
	function buildLogin()
	{
		function setCbs(login) {
			$loginCb[0].checked = login;
			$registerCb[0].checked = !login;
			refresh();
		}
		if($loginCb == null) {
			$loginCb = $("#login-page .lor .login input");
			$registerCb = $("#login-page .lor .register input");
			$("#login-page .lor .login").click(setCbs.c(true));
			$("#login-page .lor .register").click(setCbs.c(false));
			$("#login-page .info input").keydown(validate);
			$("#login-page .info input").keyup(validate);
			$("#login-page .info input").keypress(validate);
			$("#login-page .agreements > a").click(function() {
				oldKeyDown = window.onkeydown;
				window.onkeydown = function(x) {
					if(x.keyCode == 13)
						$("#login-page .agreements .agree:visible").click();
				}
				$("#login-page .agreements .popup." + 
					$(this).attr("class")).addClass("display");
			});
			$("#login-page .agreements .agree").click(function() {
				window.onkeydown = oldKeyDown;
				oldKeyDown = null;
				$(this).parent().parent().removeClass("display");
			});
			$("#login-page .agreements .disagree").click(function() {
				window.onkeydown = oldKeyDown;
				oldKeyDown = null;
				mvc.err("You must agree to both the Terms of Use and the " +
						"Privacy Policy in order to use this app.");
			});
		}
		mvc.unbuild = function() {
			if(oldKeyDown != null)
				window.onkeydown = oldKeyDown;
		}
		refresh();
	}

	function login(callback)
	{
		var $btn = $("#login-page .confirm");
		if($btn.hasClass("loading"))
			return;
		var email = $("#login-page .info .email input").val();
		var password = $("#login-page .info .password input").val();
		function send(cmd) {
			$btn.addClass("loading");
			ajax.send('user', cmd, {
				username: email,
				password: password
			},  function(resp) {
				$btn.removeClass("loading");
				mvc.username(email);
				mvc.cards(JSON.parse(resp));
				callback();
			}, buildAjaxErrFun(cmd));
		}
		if(!isEmail(email))
			alert("The email address you entered is invalid");
		else if($registerCb[0].checked) {
			if(password.length < 6)
				alert("Password is too short");
			else if(/^[0-9]*$/.test(password) && password.length < 30)
				alert("Password is not strong enough.  Either make it very long or add symbols/letters");
			else if(/^[a-zA-Z]*$/.test(password) && password.length < 20)
				alert("Password is not strong enough.  Either make it very long or add symbols/numbers");
			else if(/^[a-zA-Z0-9]*$/.test(password) && password.length<16)
				alert("Password is not strong enough.  Either make it longer or add symbols");
			else if(password != 
					$("#login-page .info .confirm-password input").val())
				alert("Passwords do not match");
			else
				send('register');
		} else
			send('login');
	}

  ////////////////////
 /////  FOOTER  /////
////////////////////

	mvc.buildLogin = buildLogin;
	mvc.completeLogin = completeLogin;
	mvc.login = login;
})();
