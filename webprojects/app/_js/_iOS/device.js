(function() {
	device.getTableInfo = function(c) {
		if(location.hash == "#demo") {
			location.hash = "";
			c("IKA");
			return;
		}
		function attempt(nAttempts) {
			iOS.call("getTableInfo", [], function(tInfo) {
				if($.isEmptyObject(tInfo) && nAttempts < 20)
					setTimeout(attempt, 100, nAttempts+1); 
				else {
					if($.isEmptyObject(tInfo))
						return c(null, 0);
					c(JSON.stringify(tInfo));
				}
			}, function(err) {
				c(null, err.code, err.message);
			});
		}
		attempt(0);
	};
	var kc;
	iOS.call("getKeychain", [], function(kcStr){
		try {
			kc = JSON.parse(kcStr);
		} catch(e) {
			kc = {};
		}
	});
	device.accData = function(k, v) {
		{{ASSERT: k.match(/^[_0-9a-z]*$/i)}};
		if(arguments.length == 1)
			return kc[k];
		else {
			if(v === undefined) {
				if(kc.hasOwnProperty(k))
					delete kc[k];
			} else
				kc[k] = v;
			iOS.call("setKeychain", [JSON.stringify(kc)]);
			return v;
		}
	};
	device.ccPassReq = op.id.c(false);
	device.ccPassAllowed = op.id.c(false);
	device.getPosInner = function(callback) {
		iOS.call("getPos", [], function(pos) {
			callback(pos);
		}, function(err) {
			callback(err);
		});
	};
	device.iOSTitleBar = function(title, back)
	{
		iOS.call("setTitleBar", [title, back == null ? "" : back]);
	}
})();
