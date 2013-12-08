(function() {

	device.getTableInfo = function(c) {
		c("IKA");
/*		iOS.call("getTableInfo", [], c, function(err) {
			c(null, err.code, err.message);
		});
*/	};
	var kc;
	iOS.call("keychainLoad", [], function(keychain) { kc = keychain; });
	device.accData = function(k, v) {
		if(arguments.length == 1)
			return kc[k] && JSON.parse(kc[k]);
		else {
			if(v === undefined) {
				delete kc[k];
				iOS.call("keychainDelete", [k]);
			} else {
				v = JSON.stringify(v);
				kc[k] = v;
				iOS.call("keychainSet", [k, v]);
			}
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
