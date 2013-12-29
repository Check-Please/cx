/**	Fixes first argument(s), as though the function were "curried", hence "c"
 *
 *	E.g.
 *		f.c(x)(y) = f(x,y)
 *		f.c(x,y)(z) = f(x,y,z)
 *		f.c(x)(y,z) = f(x,y,z)
 *
 *	Very similar to underscore.js's "_.partial", but does not allow you to
 *	use "_" as a placeholder.
 *
 *	The "this" object which the origonal function is bound to may not
 *	transfer onto the curried function the way you'd expect.  For instance,
 *	the following:
 *		obj.f.c(0)(1)
 *	is equivilant to:
 *		window.f = obj.f; f.c(0)(1); delete f;
 *	This is because the function returned by the curry opperation is not a
 *	member of any object, but is rather a free floating value.  One could
 *	solve this problem in any of the following ways:
 *		(1) Use ".bind" instead
 *		(2) Use ".call" or ".apply" when invoking the curried function
 *		(3) Set the curried function to be a member of the desired object
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

/**	More general curry
 *
 *	Allows you to use "$" as a placeholder, in very much the same way that
 *	underscore.js allows you to use "_"
 *	E.g.
 *		f.C(1,$,3)(2) = f(1,2,3)
 *
 *	If some of the placeholders are never filled in, "undefined" is used.
 *	E.g.
 *		f.C(1,$,3)() = f(1, undefined, 3);
 *	This can have some odd consequences.
 *	E.g.
 *		f.C($,$,3).C(1)(2) = f(1,2,3)
 *		f.C(1).C($,$,3)(2) = f(1, 2, undefined, 3);
 */
if(!Function.prototype.C)
	Function.prototype.C = function() {
		var f = this;
		var a = Array.prototype.slice.call(arguments, 0);
		var p = [];
		var i = -1;
		while((i = a.indexOf($, i+1)) != -1)
			p.push(i);
		return function() {
			for(var i = 0; i < p.length; i++)
				a[p[i]] = arguments[i];
			return f.apply(this, 
				a.concat(Array.prototype.slice.call(arguments, p.length)));
		};
	};

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
 *
 *	Name "o" is a reference to the math symbol
 */
if(!Function.prototype.o)
	Function.prototype.o = function(g) {
		var f = this;
		return function() {return f.call(this, g.apply(this, arguments));};
	};

/**	Composition, but for functions returning arrays/taking multiple arguments
 *
 *	For instance, suppose that f & g are functions, and g() returns [g1, g2].
 *	Then f.O(g)() = f(g1, g2)
 */
if(!Function.prototype.O)
	Function.prototype.O = function(g) {
		var f = this;
		return function() {return f.apply(this, g.apply(this, arguments));};
	};

/**	Basic opperators
 */
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

/**	Functional opperators
 */
["c", "C", "u", "U", "o", "O", "sum", "min", "max"].forEach(function(x){
	op[x] = op.custom(x);
});

/**	Sums up the values in an array
 *
 *	E.g. [1,2,3].sum() == 6
 */
if(!Array.prototype.sum)
	Array.prototype.sum = Array.prototype.reduce.c(op.p, 0);

/**	Finds the minimum value in an array
 *
 *	E.g. [1,2,3].min() == 1
 */
if(!Array.prototype.min)
	Array.prototype.min = function() {
		return Math.min.apply(Math, this);
	};

/**	Finds the maximum value in an array
 *
 *	E.g. [1,2,3].max() == 3
 */
if(!Array.prototype.max)
	Array.prototype.max = function() {
		return Math.max.apply(Math, this);
	};

/**	Runs a function on each object in an array
 *
 *	E.g. ["Hello", "World"].each(function(x) {console.log(x);})
 */
if(!Array.prototype.each)
	Array.prototype.each = Array.prototype.forEach;

/**	Creates an array of a given length with values determined by a function
 *
 *	The tabulate function from SML, except that if no function is passed it
 *	assumes the indentity function
 *
 *	@see http://www.standardml.org/Basis/list.html#SIG:LIST.tabulate:VAL
 *
 *	@param	n The length of the array to make
 *	@param	f The function which determines the values of the array.  Always
 *			passed the index of the element to make a value for.  Defaults
 *			to the identity function
 *	@return	The generated array
 */
if(!Array.tabulate)
	Array.tabulate = function(n, f) {
		var a = new Array(n);
		f = f || op.id;
		for(var i = 0; i < n; i++)
			a[i] = f(i);
		return a;
	};
