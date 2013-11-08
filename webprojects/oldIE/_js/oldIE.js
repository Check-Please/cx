var oldIE = true;

if(!Array.toArray)
	Array.toArray = function(x) {
		var a = [];
		a.length = x.length;
		for(var i = 0; i < x.length; i++)
			a[i] = x[i];
		return a;
	}

//See functional.js
if(!Function.prototype.c)
	Function.prototype.c = function() {
		var f = this;
		var a = arguments;
		return function() {
			args = [];
			args.length = a.length+arguments.length;
			for(var i = 0; i < a.length; i++)
				args[i] = a[i];
			for(var i = 0; i < arguments.length; i++)
				args[i+a.length] = arguments[i];
			return f.apply(this, args);
		};
	};
//See functional.js
if(!Function.prototype.C)
	Function.prototype.C = function(a, i) {
		var f = this;
		return function() {
			var args = [];
			if(arguments.length <= i) {
				args.length = i+1;
				for(var j = 0; j < arguments.length; j++)
					args[j] = arguments[j];
				args[i] = a;
			} else {
				var past = 0;
				for(var j = 0; j < arguments.length; j++) {
					if(i == j) {
						args[j] = a;
						past = 1;
					}
					args[j+past] = arguments[j];
				}
			}
			return f.apply(this, args);
		};
	};
