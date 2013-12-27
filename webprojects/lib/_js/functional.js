/**	Fixes first argument(s), as though the function were "curried", hence "c"
 *
 *	E.g.
 *		f.c(x)(y) = f(x,y)
 *		f.c(x,y)(z) = f(x,y,z)
 *		f.c(x)(y,z) = f(x,y,z)
 *
 *	The "this" object which the origonal function is bound to may not transfer
 *	onto the curried function.  For instance, the following:
 *		obj.f.c(0)(1)
 *	is equivilant to:
 *		window.f = obj.f; f.c(0)(1); delete f;
 *	This is because the function returned by the curry opperation is not a
 *	member of any object, but is rather a free floating value.  One could
 *	solve this problem by using ".bind", which asks for an explicit "this"
 *	object, or by doing one of the following two options:
 *		obj.f.c(0).call(obj, 1);
 *		obj.f_ = obj.f.c(0); obj.f_(); delete obj.f_;
 */
if(!Function.prototype.c) {
	Function.prototype.c = function() {
		var f = this;
		var a = Array.prototype.slice.call(arguments, 0);
		return function() { return f.apply(this,
					a.concat(Array.prototype.slice.call(arguments, 0)));
		};
	};
	eval.c = function(arg) {
		if((arg.indexOf(";") == -1) && (arg.indexOf("{") == -1))
			return Function("return "+arg);
		else
			return function() {return eval(arg);};
	};
}

/**	Fixes last argument(s), as though they were "curried".  It's not like
 *	normal currying though, so we use a k for "kurry"
 *
 *	E.g.
 *		f.k(y)(x) = f(x,y)
 *		f.k(y,z)(x) = f(x,y,z)
 *		f.k(z)(x,y) = f(x,y,z)
 */
if(!Function.prototype.k)
	Function.prototype.k = function() {
		var f = this;
		var a = Array.prototype.slice.call(arguments, 0);
		return function() { return f.apply(this,
					Array.prototype.slice.call(arguments, 0).concat(a));
		};
	};

/**	Fixes argument at a particular index
 *	E.g.
 *		f.C(x,0)(y) = f(x,y)
 *		f.C(y,1)(x) = f(x,y)
 *		f.C(y,1)(x,z) = f(x,y,z)
 *		f.C(z,2)(x,y) = f(x,y,z)
 *		f.C(y,-1)(x,z) = f(x,y,z)
 *		f.C(x,-2)(y,z) = f(x,y,z)
 *		f.C(y,1)() = f(undefined, y)
 *		f.C(y,z,1)(x) = f(x,y,z)
 *
 *	Note that since -0 == 0, this function cannot be used to append to the end
 *	of the passed arguments.  That's what ".k" is for.
 *
 *	Also, be aware that there are some weird implications of padding the passed
 *	arguments with "undefined."  For instance:
 *		f.C(y,1).C(x,0)() = f(x,y)
 *		f.C(x,0).C(y,1)() = f(x, undefined, y)
 *	This is not a bug.  It's just weird.  Think about it.
 */
if(!Function.prototype.C)
	Function.prototype.C = function() {
		var f = this;
		var i = arguments[arguments.length-1];
		var a = Array.prototype.slice.call(arguments, 0, -1);
		return function() {
			var args = Array.prototype.slice.call(arguments, 0);
			if(i < 0)
				i = Math.max(0, args.length+i);
			if(args.length <= i) {
				args.length = i;
				return f.apply(this, args.concat(a));
			}
			return f.apply(this, args.splice.bind(args,i,0).apply(args,a));
		};
	};

/**	Similar to .C but limits the arugments which can be passed later
 *
 *	E.g.
 *		f.K(x,y,0)(z,t) = f(x,y)
 *		f.K(x,y,1)(z,t) = f(z,x,y)
 *		f.K(x,y,-1)(z,t) = f(x,y,z)
 *
 *	As a special case, if the last parameter passes is not an integer, or if
 *	no parameters are passed, then it is as though 0 were appended to the end
 *	of the parameter list.  For instance:
 *		f.K("a", "b")("c") = f("a", "b");
 *		f.K()("a","b") = f();
 *	Be careful with this special case though.  If you do something like this:
 *		f.K(x,y)(z)
 *	and y is an integer, you may not get the behavior you were looking for
 */
if(!Function.prototype.K)
	Function.prototype.K = function() {
		var f = this;
		var a;
		var i = arguments.length ? arguments[arguments.length-1] : null;
		if(i != (i | 0)) {
			a = Array.prototype.slice.call(arguments, 0);
			i = 0;
		} else
			a = Array.prototype.slice.call(arguments, 0, -1);
		return i == 0 ? function() {return f.apply(this, a);} : function() {
			args = Array.prototype.slice.call(arguments, 0, Math.abs(i));
			return f.apply(this, i < 0 ? a.concat(args) : args.concat(a));
		};
	}

/**	Basic uncurry
 *
 *	The "this" object will basically always be "window" when the
 *	function is called unless it has been fixed by bind()
 *
 *	E.g.
 *		f.u()(x,y) = f(x)(y)
 */
if(!Function.prototype.u)
	Function.prototype.u = function() {
		var f = this;
		return function() {
			for(var i = 0; i < arguments.length; i++)
				f = f(arguments[i]);
			return f;
		};
	};

/**	More general uncurry
 *
 *	Same rules for the "this" object as ".c"
 *
 *	E.g.
 *		f.U()(x,y,z) = f(x)(y)(z)
 *		f.U(0)(x,y,z) = f()(x)(y)(z)
 *		f.U(1)(x,y,z) = f(x)(y)(z)
 *		f.U(2)(x,y,z) = f(x,y)(z)
 *		f.U(3)(x,y,z) = f(x,y,z)
 *		f.U(4)(x,y,z) = f(x,y,z)
 *		f.U(1,2)(x,y,z) = f(x)(y,z)
 *		f.U(2,1)(x,y,z) = f(x,y)(z)
 *		f.U(2,2)(x,y,z) = f(x,y)(z)
 *		f.U(-1)(x,y,z) = f(z)(x)(y)
 *		f.U(-2)(x,y,z) = f(y,z)(x)
 *		f.U(-1,2)(x,y,z) = f(z)(x,y)
 */
if(!Function.prototype.U)
	Function.prototype.U = function() {
		var f = this;
		var lens = arguments;
		return function() {
			var i = 0;
			var l = 0;
			var h = arguments.length;
			while(l < h) {
				var len = (i < lens.length) ? lens[i++] : 1;
				var a;
				if(len >= 0) {
					a = Array.prototype.slice.call(arguments, l,
							Math.min(h, l+len));
					l += len;
				} else {
					a = Array.prototype.slice.call(arguments, 
							Math.max(l, h+len), h);
					h += len;
				}
				f = f.apply(this, a);
			}
			return f;
		};
	};


/**	Comosition, f.o(g)(x) = f(g(x))
 *	Name "o" is a reference to the math symbol
 */
if(!Function.prototype.o)
	Function.prototype.o = function(g) {
		var f = this;
		return function() { return f(g.apply(this, arguments)); };
	};

//Basic opperators
op = {
	p: function(x,y) {return x+y;},
	m: function(x,y) {return x-y;},
	t: function(x,y) {return x*y;},
	d: function(x,y) {return x/y;},
	mod: function(x,y) {return x%y;},

	eq: function(x,y) {return x==y;},
	neq: function(x,y) {return x!=y;},
	eeq: function(x,y) {return x===y;},
	neeq: function(x,y) {return x!==y;},
	gt: function(x,y) {return x>y;},
	lt: function(x,y) {return x<y;},
	ge: function(x,y) {return x>=y;},
	le: function(x,y) {return x<=y;},

	or: function(x,y) {return x||y;},
	and: function(x,y) {return x&&y;},
	not: function(x) {return !x;},

	id: function(x) {return x;},

	call: function(f) {return f();},
	get: function(o, k) {return o[k];},
	set: function(o, k, v) {return (o[k] = v);},

	custom: function(op) {return function(obj) {
		return obj[op].apply(obj, Array.prototype.slice.call(arguments, 1));
	};}
};

["c", "C", "k", "K", "u", "U", "o", "sum", "min", "max"].forEach(function(x){
	op[x] = op.custom(x);
});

if(!Array.prototype.sum)
	Array.prototype.sum = Array.prototype.reduce.c(op.p, 0);

if(!Array.prototype.min)
	Array.prototype.min = function() {
		return Math.min.apply(Math, this);
	};

if(!Array.prototype.max)
	Array.prototype.max = function() {
		return Math.max.apply(Math, this);
	};

if(!Array.prototype.each)
	Array.prototype.each = Array.prototype.forEach;

/**	The tabulate function from SML, except that if no function is passed it
 *	assumes the indentity function
 *
 *	@see http://www.standardml.org/Basis/list.html#SIG:LIST.tabulate:VAL
 */
if(!Array.tabulate)
	Array.tabulate = function(n, f) {
		var a = new Array(n);
		f = f || op.id;
		for(var i = 0; i < n; i++)
			a[i] = f(i);
		return a;
	};
