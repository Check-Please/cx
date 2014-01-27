/*	The job of this module is to handle initialization
 *
 *	@see README.md
 *
 *	@owner sjelin
 */

(function () {
	"use strict";

	window.onload = function() {
		if(!{{NATIVE}}) {
			if((window.location.hostname != "localhost") &&
						(window.location.protocol != "https:"))
				window.location.href = "https:" +
					window.location.href.substring(location.protocol.length);
			if(navigator.userAgent.indexOf("iPhone") != -1)
				$("body").addClass("platform-iOS");
		} else
			$("body").addClass("platform-{{PLATFORM}}");
		setTimeout(window.onresize, 0);
		if("{{PLATFORM}}" == "iOS")
			device.iOSTitleBar("Loading...");
		inParallel([device.getTableInfo,device.getPos], function(tInfo,pos) {
			if(tInfo[1] == 0) {
				if({{NATIVE}}) {
					mvc.init({err: "noKey"});
					loading.init();
					mvc.err.notify();
				} else
					location = "http://" + location.host + "/website.html";
				return;
			}
			tInfo = tInfo[0] || "";
			device.ajax.send("cx", "init", {
				isNative: {{NATIVE}},
				tableInfo: tInfo,
				clientID: device.getClientID(),
				platform: device.getPlatform(),
				lat: pos[0],
				"long": pos[1],
				accuracy: pos[2],
				versionNum: {{VERSION_NUM}}
			}, function(data) {
				data = JSON.parse(data);
				if(data.errCode != null) {
					mvc.init({err:	data.errCode == 0 ? "noKey" :
									data.errCode == 1 ? "invalidKey" :
									data.errCode == 2 ? "empty" :
									data.errCode == 5 ? "reqUpdate" :
														"500"});
					loading.init();
					mvc.err.notify();
					return;
				}
				if(data.deleteCCs)
					device.deleteCards();
				var style = JSON.parse(data.restrStyle || "{}");
				mvc.init({
					key: data.tKey,
					restrName: data.restrName,
					restrAddress: data.restrAddress,
					receiptImg: style.receiptImg,
					connectionID: data.connectionID,
					items: data.items,
					split: data.split,
					selection: {}
				});
				socket.init(data.channelToken);
				$("title").text("Pay your ticket at "+data.restrName);
				if(style.sheet != null) {
					var $css = $("<link>");
					$css.attr("type", "text/css");
					$css.attr("rel", "Stylesheet");
					$css.attr("media", "all");
					$css.attr("href", "{{SERVER}}/cx/customStyle.css?" +
																style.sheet);
					$("head").append($css);
				}
				if(style.headerImg == null) {
					var $name = $("<span>");
					$name.text(data.restrName);
					$("#header").append($name);
				} else {
					var $img = $("<img>");
					$img.attr("src", "{{SERVER}}/cx/headerImg.png?" +
															style.headerImg);
					$("#header").append($img);
				}
				loading.init();
				window.onhashchange();
				mvc.views.error.allowReload();
			}, function() {
				mvc.init({err: "Couldn't connect to the server"});
				loading.init();
				mvc.err.notify();
			});
		});
		if(device.getDebugID() != null) {
			var $script = $("<script>");
			$script.attr("src",
					"http://jsconsole.com/remote.js?"+device.getDebugID());
			$("head").append($script);
		}
		nav.init();
	};

})();
