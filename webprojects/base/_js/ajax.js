function ajax(method, url, rawData, callback, failFunc, boringUpdate)
{
	"use strict";

	//Format data
	function addArray(array, name, target)
	{
		for(var i = 0; i < array.length; i++) {
			var val = array[i];
			if(Array.isArray(val))
				addArray(val, name+"_"+i, target);
			else if(val != null)
				target[name+"_"+i] = val;
		}
		target[name+"_"] = array.length;
	}

	var data = undefined;
	if(rawData != null) {
		data = {};
		for(var key in rawData) {
			var val = rawData[key];
			if(val != null) {
				if(Array.isArray(val))
					addArray(val, key, data);
				else
					data[key] = val;
			}
		}
	}

	//Actually make request
	var xmlhttp = window.XMLHttpRequest ? new XMLHttpRequest() :
						new ActiveXObject("Microsoft.XMLHTTP");
	if((callback!=null) || (failFunc!=null) || (boringUpdate!=null)) {
		//There is an odd bug in ajax (for only chrome?) where the ready
		//states are not always 1,2,3,4.  Instead, they are AT LEAST 1,2,3,4
		//on the 1st, 2nd, 3rd, 4th call, respectively.  I suspect this is
		//some race condition.  Normally 4 only shows up on the forth call
		//though (why? I don't know). However, when using breakpoints (in
		//only chrome?), this is not the case, and I've gotten 4 to show up
		//up to three times.  This is a problem, because it means the
		//callback function gets called multiple times.  Even though break
		//points shouldn't exist in production, it makes debugging annoying,
		//and since the bug is not understood some sort of solution needs to
		//be in place.  In this case, what we're doing is having an extra
		//variable keep track of if we've seen state 4 yet, and sending extra
		//state 4s onto the boring update function.
		var stateFourSeen = false;
		xmlhttp.onreadystatechange = function ()
		{
			if((xmlhttp.readyState == 4) && !stateFourSeen) {
				stateFourSeen = true;
				if(xmlhttp.status == 200) {
					if(callback != null)
						callback(xmlhttp.responseText, xmlhttp);
				} else if(failFunc != null) {
					var msg = xmlhttp.statusText;
					if(msg.substr(0,3) == "404")
						msg = msg.substr(3);
					msg = msg.trim();
					failFunc(xmlhttp.status, xmlhttp.statusText,
							xmlhttp.responseText, xmlhttp);
				}
			} else if(boringUpdate != null) {
				boringUpdate(xmlhttp.readyState, xmlhttp);
			}
		}
	}
	method = method.toUpperCase();
	if((method == "GET") && (data != null)) {
		url += "?"+$.param(data);
		data = null;
	}
	xmlhttp.open(method, url, callback != null);
	if(data != null) {
		data = data instanceof Object ? $.param(data) : data;
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");
	}
	xmlhttp.send(data);
	return xmlhttp;
}
["get", "post", "put", "delete"].forEach(function(method) {
	ajax[method] = function() {
		var args = [method.toUpperCase()];
		args.push.apply(args, arguments);
		this.apply(window, args);
	};
});

for(var i = 0; i < 2; i++)
	ajax[i == 0 ? "receive" : "send"] = function(module, cmd) {
		module = module || "";
		if(module.endsWith("/"))
			module = module.slice(0, module.length-1);
		if(module.length > 0)
			arguments[1] = module + (cmd[0] == "/" ? "" : "/") + cmd;
		arguments[0] = i == 0 ? "GET" : "POST";
		this.apply(window, Array.toArray(arguments));
	}

function buildAjaxErrFun(cmd, dontStopLoading)
{
	return function(code, _, msg) {
		if(!dontStopLoading)
			$("a.loading").removeClass("loading");
		alert("Could not "+cmd+".  Reason:\n"+(code==404?"":code+" ")+msg);
	};
}
