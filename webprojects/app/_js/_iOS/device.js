(function() {

	device.getTableInfo = function(c) {
		c("IKA");
/*		iOS.call("getTableInfo", [], c, function(err) {
			c(null, err.code, err.message);
		});
*/	};
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
})();
