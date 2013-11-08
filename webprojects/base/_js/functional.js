if(!window.eval_c)
	window.eval_c = function(arg) {
		if((arg.indexOf(";") == -1) && (arg.indexOf("{") == -1))
			return Function("return "+arg);
		else
			return function() {return eval(arg);};
	};

//Fixes first argument(s), as though the function were "curried"
//
//Be careful about the "this" object.  Use bind sometimes.
//
//E.g.
//	f.c(x)(y) = f(x,y)
//	f.c(x,y)(z) = f(x,y,z)
//	f.c(x)(y,z) = f(x,y,z)
//Named after currying
if(!Function.prototype.c)
	Function.prototype.c = function() {
		"use strict";
		var f = this;
		var a = Array.prototype.slice.call(arguments, 0);
		return f == eval ? eval_c(a[0]) : function() {
			return f.apply(this,
					a.concat(Array.prototype.slice.call(arguments, 0)));
		};
	};

//Fixes argument at a particular index
//e.g. f.C(x,1)(y) = f(y,x)
//Edge cases like f.C(x,3)(y) will be be resolved be filling in the missing
//arguments with undefined, e.g. f.C(x,3)(y) = f(y, undefined, undefined, x)
if(!Function.prototype.C)
	Function.prototype.C = function(a, i) {
		"use strict";
		var f = this;
		return function() {
			var args = Array.prototype.slice.call(arguments, 0);
			if(args.length <= i) {
				args.length = i+1;
				args[i] = a;
			} else
				args.splice(i, 0, a);
			return f.apply(this, args);
		};
	};


//Comosition, f.o(g)(x) = f(g(x))
//Name "o" is a reference to the math symbol
if(!Function.prototype.o)
	Function.prototype.o = function(g) {
		"use strict";
		var f = this;
		return function() { return f(g.apply(this, arguments)); };
	};

//Basic opperators
//The *_c versions of the functions are designed for currying. *_c <==> *.c
//The _C versions curry the second parameter, instead of the first
var op = {
	p: function(x,y) {return x+y;},
	m: function(x,y) {return x-y;},
	t: function(x,y) {return x*y;},
	d: function(x,y) {return x/y;},
	mod: function(x,y) {return x%y;},
	o: function(f, g) {return function() {return f(g(this, arguments));};},

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

	p_c: function(x) {return function(y) {return x+y;};},
	m_c: function(x) {return function(y) {return x-y;};},
	t_c: function(x) {return function(y) {return x*y;};},
	d_c: function(x) {return function(y) {return x/y;};},
	mod_c: function(x) {return function(y) {return x%y;};},
	o_c: function(f) {return function (g) {return function()
		{return f(g(this, arguments));};};},

	eq_c: function(x) {return function(y) {return x==y;};},
	neq_c: function(x) {return function(y) {return x!=y;};},
	eeq_c: function(x) {return function(y) {return x===y;};},
	neeq_c: function(x) {return function(y) {return x!==y;};},
	gt_c: function(x) {return function(y) {return x>y;};},
	lt_c: function(x) {return function(y) {return x<y;};},
	ge_c: function(x) {return function(y) {return x>=y;};},
	le_c: function(x) {return function(y) {return x<=y;};},

	or_c: function(x) {return function(y) {return x||y;};},
	and_c: function(x) {return function(y) {return x&&y;};},

	id_c: function(x) {return function() {return x;};},

	p_C: function(y) {return function(x) {return x+y;};},
	m_C: function(y) {return function(x) {return x-y;};},
	t_C: function(y) {return function(x) {return x*y;};},
	d_C: function(y) {return function(x) {return x/y;};},
	mod_C: function(y) {return function(x) {return x%y;};},
	o_C: function(g) {return function (f) {return function()
		{return f(g(this, arguments));};};},

	eq_C: function(y) {return function(x) {return x==y;};},
	neq_C: function(y) {return function(x) {return x!=y;};},
	eeq_C: function(y) {return function(x) {return x===y;};},
	neeq_C: function(y) {return function(x) {return x!==y;};},
	gt_C: function(y) {return function(x) {return x>y;};},
	lt_C: function(y) {return function(x) {return x<y;};},
	ge_C: function(y) {return function(x) {return x>=y;};},
	le_C: function(y) {return function(x) {return x<=y;};},

	or_C: function(y) {return function(x) {return x||y;};},
	and_C: function(y) {return function(x) {return x&&y;};}
}

function callAll(funs)
{
	var args = Array.prototype.slice.call(arguments, 1);
	for(var i = 0; i < funs.length; i++)
		funs[i].apply(this, args);
}


if(!Array.prototype.sum)
	Array.prototype.sum = Array.prototype.reduce.c(op.p, 0);

if(!Array.prototype.min)
	Array.prototype.min = function() {
		"use strict";
		return Math.min.apply(Math, this);
	};

if(!Array.prototype.max)
	Array.prototype.max = function() {
		"use strict";
		return Math.max.apply(Math, this);
	};

//The tabulate function from SML, except that if no function is passed it
//assumes the indentity function
if(!Array.tabulate)
	Array.tabulate = function(n, f) {
		"use strict";
		var a = new Array(n);
		if(f == null) 
			for(var i = 0; i < n; i++)
				a[i] = i;
		else
			for(var i = 0; i < n; i++)
				a[i] = f(i);
		return a;
	};

//"sfx" stands for suffix.  Please just read this one - it's weird.
//It's useful, for example, if you have a 2D array and you want the lengths
//of each sub-array, you could do a.map(sfx("length"))
//If you are just trying to get a property, use sfx.prop.  If you are just
//trying to call a sunfuction, use sfx.fun.  If you need to call a function
//and give it arguments, then use sfx.fun_args
if(!window.sfx) {
	window.sfx = function(s) {
		"use strict";
		return function(x) { return eval("x."+s); };
	};
	window.sfx.prop = function(p) {
		"use strict";
		return function(x) { return x[p]; }
	};
	window.sfx.fun = function(f) {
		"use strict";
		return function(x) { return x[f](); }
	};
	window.sfx.fun_args = function(f) {
		"use strict";
		var a = Array.prototype.slice.call(arguments, 1);
		return function(x) { return x[f].apply(x, a); }
	};
}
