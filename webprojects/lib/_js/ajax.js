/** A function for sending ajax requests to the server.
 *
 *	@param	method The method for the request (e.g. "GET")
 *	@param	url The URL to send a request to
 *	@param	rawData	An object containing the parameters to send the request
 *					with.   Supports both primitive data and arrays.
 *	@param	callback	The function to be called when the request is
 *						completed successfully.  Takes the text returned from
 *						the server as its first parameter, and the request
 *						object as its second.
 *						If null, the request is run synchronously
 *	@param	failFunc	The function to be called if the request fails.  It
 *						takes the following parameters, in order:
 *							- The status code
 *							- The status text
 *							- The responce text
 *							- The requst object
 *						If null, nothing is called should the request fail
 *	@param	boringUpdate	The function to be called when the request is
 *							still in process, but the ready state has
 *							changed.  Rarely useful.  Takes the new ready
 *							state as its first parameter, and the request
 *							object as its second.
 *							If null, nothing happens on these read state
 *							changes.
 *	@return	The request object
 */
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
	var xmlhttp = {{MODERN}} || window.XMLHttpRequest ?
						new window.XMLHttpRequest() :
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
		//variable keep track of if we've seen state 4 yet, and ignoring
		//extra state 4s
		var stateFourSeen = false;
IF_DEBUG
		var stack;
		try {
			throw new Error();
		} catch(e) {
			stack = e.stack;
		}
END_IF
		xmlhttp.onreadystatechange = function ()
		{
IF_DEBUG
			try {
END_IF
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
				} else if((boringUpdate!=null) && (xmlhttp.readyState<4)) {
					boringUpdate(xmlhttp.readyState, xmlhttp);
				}
IF_DEBUG
			} catch(e) {
				console.log("Stack for ajax call:\n\n"+stack);
				throw e;
			}
END_IF
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

/*	The following defines ajex.get, ajex.post, ajax.put, and ajex.delete
 *
 *	These functions are all just like the normal ajax function, but with the
 *	request method fixed.
 */
["get", "post", "put", "delete"].forEach(function(method) {
	ajax[method] = function() {
		var args = [method.toUpperCase()];
		args.push.apply(args, arguments);
		this.apply(window, args);
	};
});

/*	The following defines ajax.receive and ajax.send.
 *
 *	These are just like ajax.get and ajax.post, except that instead of one
 *	URL parameter there are two.  This is useful if many of the URLs you
 *	access follow the form "module/command"
 */
for(var i = 0; i < 2; i++)
	ajax[i == 0 ? "receive" : "send"] = function(module, cmd) {
		module = module || "";
		if(module.endsWith("/"))
			module = module.slice(0, module.length-1);
		if(module.length > 0)
			arguments[1] = module + (cmd[0] == "/" ? "" : "/") + cmd;
		arguments[0] = i == 0 ? "GET" : "POST";
		this.apply(window, $.makeArray(arguments));
	}

/** Creates a function to be called if an ajax request failed.
 *
 *	@param	cmd The command which the request failed at doing
 *	@param	dontStopLoading	Normally, any <a> tag with the "loading" class
 *							has that class removed from it.  If this
 *							parameter is true, that is not done
 *	@return	The function to call if the request fails
 */
function buildAjaxErrFun(cmd, dontStopLoading)
{
	return function(code, _, msg) {
		if(!dontStopLoading)
			$("a.loading").removeClass("loading");
		alert("Could not "+cmd+".  Reason:\n"+(code==404?"":code+" ")+msg);
	};
}
