/**	Runs some asynchronous functions in sequence.
 *	
 *	@param	funs {Function[]} The functions to be executed.  Each function
 *			must take a callback function as it's first parameter and a
 *			failure function as it's second.  Each function should call its
 *			callback function exactly once.  If a function does not call its
 *			callback function then the squence of calls ends, and the final
 *			final callback function is never called.  If a function calls its
 *			callback function multiple times, then all future function will
 *			be called multiple times.
 *	@param	callback {Function} A callback final function, to be called upon
 *			the success of all the functions passed into this function
 *	@param	failFun {Function} The failure function.  Called if any of the
 *			functions fails.  Gets passed the index of the function which
 *			failed.
 */
function inSequence(funs, callback, failFun)
{
	failFun = failFun || $.noop;
	function myARun(i) {
		if(i == funs.length)
			callback();
		else
			funs[i](myARun.c(i+1), failFun.c(i));
	}
	myARun(0);
}
/**	Runs some asynchronous functions in parallel.
 *	
 *	@param	funs {Function[]} The functions to be executed.  Each function
 *			must take a callback function as it's first parameter and a
 *			failure function as it's second.  Each function should call its
 *			callback function exactly once.  If a function does not call its
 *			callback function then the squence of calls ends, and the final
 *			final callback function is never called.  If a function calls its
 *			callback function multiple times, then all future function will
 *			be called multiple times.
 *	@param	callback {Function} A callback final function, to be called upon
 *			the success of all the functions passed into this function
 *	@param	failFun {Function} The failure function.  Called if any of the
 *			functions fails.  Gets passed the index of the function which
 *			failed.
 */
function inParallel(funs, callback, failFun)
{
	if(funs.length == 0) {
		callback([]);
	} else {
		failFun = failFun || $.noop;
		var rets = new Array(funs.length);
		var finished = Array.tabulate(funs.length, op.id.c(false));
		function success(i) {
			rets[i] = Array.prototype.slice.call(arguments, 1);
			finished[i] = true;
			if(finished.reduce(op.and))
				callback.apply(this, rets);
		}
		for(var i = 0; i < funs.length; i++)
			funs[i](success.c(i), failFun.c(i));
	}
}
