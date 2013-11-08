
			  /////////////////////////////////////////////
			 /////////////////  INDEXOF  /////////////////
			/////////////////////////////////////////////

//From MDN
if (!Array.prototype.indexOf) {
  Array.prototype.indexOf = function (searchElement /*, fromIndex */ ) {
    'use strict';
    if (this == null) {
      throw new TypeError();
    }
    var n, k, t = Object(this),
        len = t.length >>> 0;

    if (len === 0) {
      return -1;
    }
    n = 0;
    if (arguments.length > 1) {
      n = Number(arguments[1]);
      if (n != n) { // shortcut for verifying if it's NaN
        n = 0;
      } else if (n != 0 && n != Infinity && n != -Infinity) {
        n = (n > 0 || -1) * Math.floor(Math.abs(n));
      }
    }
    if (n >= len) {
      return -1;
    }
    for (k = n >= 0 ? n : Math.max(len - Math.abs(n), 0); k < len; k++) {
      if (k in t && t[k] === searchElement) {
        return k;
      }
    }
    return -1;
  };
}

			  /////////////////////////////////////////////
			 /////////////////  FOREACH  /////////////////
			/////////////////////////////////////////////

// Production steps of ECMA-262, Edition 5, 15.4.4.18
// Reference: http://es5.github.com/#x15.4.4.18
if ( !Array.prototype.forEach ) {

  Array.prototype.forEach = function( callback, thisArg ) {

    var T, k;

    if ( this == null ) {
      throw new TypeError( " this is null or not defined" );
    }

    // 1. Let O be the result of calling ToObject passing the |this| value as the argument.
    var O = Object(this);

    // 2. Let lenValue be the result of calling the Get internal method of O with the argument "length".
    // 3. Let len be ToUint32(lenValue).
    var len = O.length >>> 0; // Hack to convert O.length to a UInt32

    // 4. If IsCallable(callback) is false, throw a TypeError exception.
    // See: http://es5.github.com/#x9.11
    if ( {}.toString.call(callback) != "[object Function]" ) {
      throw new TypeError( callback + " is not a function" );
    }

    // 5. If thisArg was supplied, let T be thisArg; else let T be undefined.
    if ( thisArg ) {
      T = thisArg;
    }

    // 6. Let k be 0
    k = 0;

    // 7. Repeat, while k < len
    while( k < len ) {

      var kValue;

      // a. Let Pk be ToString(k).
      //   This is implicit for LHS operands of the in operator
      // b. Let kPresent be the result of calling the HasProperty internal method of O with argument Pk.
      //   This step can be combined with c
      // c. If kPresent is true, then
      if ( k in O ) {

        // i. Let kValue be the result of calling the Get internal method of O with argument Pk.
        kValue = O[ k ];

        // ii. Call the Call internal method of callback with T as the this value and
        // argument list containing kValue, k, and O.
        callback.call( T, kValue, k, O );
      }
      // d. Increase k by 1.
      k++;
    }
    // 8. return undefined
  };
}

			  /////////////////////////////////////////////
			 ///////////////////  MAP  ///////////////////
			/////////////////////////////////////////////

// Production steps of ECMA-262, Edition 5, 15.4.4.19
// Reference: http://es5.github.com/#x15.4.4.19
if (!Array.prototype.map) {
  Array.prototype.map = function(callback, thisArg) {

    var T, A, k;

    if (this == null) {
      throw new TypeError(" this is null or not defined");
    }

    // 1. Let O be the result of calling ToObject passing the |this| value as the argument.
    var O = Object(this);

    // 2. Let lenValue be the result of calling the Get internal method of O with the argument "length".
    // 3. Let len be ToUint32(lenValue).
    var len = O.length >>> 0;

    // 4. If IsCallable(callback) is false, throw a TypeError exception.
    // See: http://es5.github.com/#x9.11
    if ({}.toString.call(callback) != "[object Function]") {
      throw new TypeError(callback + " is not a function");
    }

    // 5. If thisArg was supplied, let T be thisArg; else let T be undefined.
    if (thisArg) {
      T = thisArg;
    }

    // 6. Let A be a new array created as if by the expression new Array(len) where Array is
    // the standard built-in constructor with that name and len is the value of len.
    A = new Array(len);

    // 7. Let k be 0
    k = 0;

    // 8. Repeat, while k < len
    while(k < len) {

      var kValue, mappedValue;

      // a. Let Pk be ToString(k).
      //   This is implicit for LHS operands of the in operator
      // b. Let kPresent be the result of calling the HasProperty internal method of O with argument Pk.
      //   This step can be combined with c
      // c. If kPresent is true, then
      if (k in O) {

        // i. Let kValue be the result of calling the Get internal method of O with argument Pk.
        kValue = O[ k ];

        // ii. Let mappedValue be the result of calling the Call internal method of callback
        // with T as the this value and argument list containing kValue, k, and O.
        mappedValue = callback.call(T, kValue, k, O);

        // iii. Call the DefineOwnProperty internal method of A with arguments
        // Pk, Property Descriptor {Value: mappedValue, Writable: true, Enumerable: true, Configurable: true},
        // and false.

        // In browsers that support Object.defineProperty, use the following:
        // Object.defineProperty(A, Pk, { value: mappedValue, writable: true, enumerable: true, configurable: true });

        // For best browser support, use the following:
        A[ k ] = mappedValue;
      }
      // d. Increase k by 1.
      k++;
    }

    // 9. return A
    return A;
  };      
}

			  /////////////////////////////////////////////
			 //////////////////  REDUCE  /////////////////
			/////////////////////////////////////////////

//I assume I got this from MDN
if (!Array.prototype.reduce) {
  Array.prototype.reduce = function reduce(accumulator){
    if (this===null || this===undefined) throw new TypeError("Object is null or undefined");
    var i = 0, l = this.length >> 0, curr;

    if(typeof accumulator !== "function") // ES5 : "If IsCallable(callbackfn) is false, throw a TypeError exception."
      throw new TypeError("First argument is not callable");

    if(arguments.length < 2) {
      if (l === 0) throw new TypeError("Array length is 0 and no second argument");
      curr = this[0];
      i = 1; // start accumulating at the second element
    }
    else
      curr = arguments[1];

    while (i < l) {
      if(i in this) curr = accumulator.call(undefined, curr, this[i], i, this);
      ++i;
    }

    return curr;
  };
}

			  /////////////////////////////////////////////
			 //////////////////  FILTER  /////////////////
			/////////////////////////////////////////////

//I assume I got this from MDN
if (!Array.prototype.filter)
{
  Array.prototype.filter = function(fun /*, thisp */)
  {
    "use strict";

    if (this == null)
      throw new TypeError();

    var t = Object(this);
    var len = t.length >>> 0;
    if (typeof fun != "function")
      throw new TypeError();

    var res = [];
    var thisp = arguments[1];
    for (var i = 0; i < len; i++)
    {
      if (i in t)
      {
        var val = t[i]; // in case fun mutates this
        if (fun.call(thisp, val, i, t))
          res.push(val);
      }
    }

    return res;
  };
}

			  /////////////////////////////////////////////
			 /////////////////   isArray  ////////////////
			/////////////////////////////////////////////

//I think I found this online somewhere....
if(!Array.isArray)
	Array.isArray = function(array) {
		return Object.prototype.toString.call(array) === "[object Array]";
	};

			  /////////////////////////////////////////////
			 ///////////////////  BIND  //////////////////
			/////////////////////////////////////////////

//Credit to MDN
if (!Function.prototype.bind) {
  Function.prototype.bind = function (oThis) {
    if (typeof this !== "function") {
      // closest thing possible to the ECMAScript 5 internal IsCallable function
      throw new TypeError("Function.prototype.bind - what is trying to be bound is not callable");
    }

    var aArgs = Array.prototype.slice.call(arguments, 1), 
        fToBind = this, 
        fNOP = function () {},
        fBound = function () {
          return fToBind.apply(this instanceof fNOP
                                 ? this
                                 : oThis,
                               aArgs.concat(Array.prototype.slice.call(arguments, 1)));
        };

    fNOP.prototype = this.prototype;
    fBound.prototype = new fNOP();

    return fBound;
  };
}

			  /////////////////////////////////////////////
			 ///////////////  String.trim  ///////////////
			/////////////////////////////////////////////

if (!String.prototype.trim) {
  String.prototype.trim = function () {
    return this.replace(/^\s+|\s+$/g, '');
  };
}

			  /////////////////////////////////////////////
			 //////////  Object.getPrototypeOf  //////////
			/////////////////////////////////////////////

//Credit to John Resig, http://ejohn.org/blog/objectgetprototypeof/
if(typeof Object.getPrototypeOf !== "function") {
	if (typeof "test".__proto__ === "object") {
		Object.getPrototypeOf = function(object){
			return object.__proto__;
		};
	} else {
		Object.getPrototypeOf = function(object){
			// May break if the constructor has been tampered with
			return object.constructor.prototype;
		};
	}
}
			  /////////////////////////////////////////////
			 ///////////////   Object.keys  //////////////
			/////////////////////////////////////////////

//Credit to MDN
if(!Object.keys) {
	Object.keys = (function () {
		var hasOwnProperty = Object.prototype.hasOwnProperty,
			hasDontEnumBug = !({toString: null}).propertyIsEnumerable('toString'),
			dontEnums = [
				'toString',
				'toLocaleString',
				'valueOf',
				'hasOwnProperty',
				'isPrototypeOf',
				'propertyIsEnumerable',
				'constructor'],
			dontEnumsLength = dontEnums.length;

		return function (obj) {
			if (typeof obj !== 'object' && typeof obj !== 'function' || obj === null) throw new TypeError('Object.keys called on non-object');

			var result = [];
			for (var prop in obj) {
				if (hasOwnProperty.call(obj, prop)) result.push(prop);
			}

			if (hasDontEnumBug) {
				for (var i=0; i < dontEnumsLength; i++) {
					if (hasOwnProperty.call(obj, dontEnums[i])) result.push(dontEnums[i]);
				}
			}
			return result;
		}
	})()
};
/*
    json2.js
    2011-10-19

    Public Domain.

    NO WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.

    See http://www.JSON.org/js.html


    This code should be minified before deployment.
    See http://javascript.crockford.com/jsmin.html

    USE YOUR OWN COPY. IT IS EXTREMELY UNWISE TO LOAD CODE FROM SERVERS YOU DO
    NOT CONTROL.


    This file creates a global JSON object containing two methods: stringify
    and parse.

        JSON.stringify(value, replacer, space)
            value       any JavaScript value, usually an object or array.

            replacer    an optional parameter that determines how object
                        values are stringified for objects. It can be a
                        function or an array of strings.

            space       an optional parameter that specifies the indentation
                        of nested structures. If it is omitted, the text will
                        be packed without extra whitespace. If it is a number,
                        it will specify the number of spaces to indent at each
                        level. If it is a string (such as '\t' or '&nbsp;'),
                        it contains the characters used to indent at each level.

            This method produces a JSON text from a JavaScript value.

            When an object value is found, if the object contains a toJSON
            method, its toJSON method will be called and the result will be
            stringified. A toJSON method does not serialize: it returns the
            value represented by the name/value pair that should be serialized,
            or undefined if nothing should be serialized. The toJSON method
            will be passed the key associated with the value, and this will be
            bound to the value

            For example, this would serialize Dates as ISO strings.

                Date.prototype.toJSON = function (key) {
                    function f(n) {
                        // Format integers to have at least two digits.
                        return n < 10 ? '0' + n : n;
                    }

                    return this.getUTCFullYear()   + '-' +
                         f(this.getUTCMonth() + 1) + '-' +
                         f(this.getUTCDate())      + 'T' +
                         f(this.getUTCHours())     + ':' +
                         f(this.getUTCMinutes())   + ':' +
                         f(this.getUTCSeconds())   + 'Z';
                };

            You can provide an optional replacer method. It will be passed the
            key and value of each member, with this bound to the containing
            object. The value that is returned from your method will be
            serialized. If your method returns undefined, then the member will
            be excluded from the serialization.

            If the replacer parameter is an array of strings, then it will be
            used to select the members to be serialized. It filters the results
            such that only members with keys listed in the replacer array are
            stringified.

            Values that do not have JSON representations, such as undefined or
            functions, will not be serialized. Such values in objects will be
            dropped; in arrays they will be replaced with null. You can use
            a replacer function to replace those with JSON values.
            JSON.stringify(undefined) returns undefined.

            The optional space parameter produces a stringification of the
            value that is filled with line breaks and indentation to make it
            easier to read.

            If the space parameter is a non-empty string, then that string will
            be used for indentation. If the space parameter is a number, then
            the indentation will be that many spaces.

            Example:

            text = JSON.stringify(['e', {pluribus: 'unum'}]);
            // text is '["e",{"pluribus":"unum"}]'


            text = JSON.stringify(['e', {pluribus: 'unum'}], null, '\t');
            // text is '[\n\t"e",\n\t{\n\t\t"pluribus": "unum"\n\t}\n]'

            text = JSON.stringify([new Date()], function (key, value) {
                return this[key] instanceof Date ?
                    'Date(' + this[key] + ')' : value;
            });
            // text is '["Date(---current time---)"]'


        JSON.parse(text, reviver)
            This method parses a JSON text to produce an object or array.
            It can throw a SyntaxError exception.

            The optional reviver parameter is a function that can filter and
            transform the results. It receives each of the keys and values,
            and its return value is used instead of the original value.
            If it returns what it received, then the structure is not modified.
            If it returns undefined then the member is deleted.

            Example:

            // Parse the text. Values that look like ISO date strings will
            // be converted to Date objects.

            myData = JSON.parse(text, function (key, value) {
                var a;
                if (typeof value === 'string') {
                    a =
/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)Z$/.exec(value);
                    if (a) {
                        return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3], +a[4],
                            +a[5], +a[6]));
                    }
                }
                return value;
            });

            myData = JSON.parse('["Date(09/09/2001)"]', function (key, value) {
                var d;
                if (typeof value === 'string' &&
                        value.slice(0, 5) === 'Date(' &&
                        value.slice(-1) === ')') {
                    d = new Date(value.slice(5, -1));
                    if (d) {
                        return d;
                    }
                }
                return value;
            });


    This is a reference implementation. You are free to copy, modify, or
    redistribute.
*/

/*jslint evil: true, regexp: true */

/*members "", "\b", "\t", "\n", "\f", "\r", "\"", JSON, "\\", apply,
    call, charCodeAt, getUTCDate, getUTCFullYear, getUTCHours,
    getUTCMinutes, getUTCMonth, getUTCSeconds, hasOwnProperty, join,
    lastIndex, length, parse, prototype, push, replace, slice, stringify,
    test, toJSON, toString, valueOf
*/


// Create a JSON object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

var JSON;
if (!JSON) {
    JSON = {};
}

(function () {
    'use strict';

    function f(n) {
        // Format integers to have at least two digits.
        return n < 10 ? '0' + n : n;
    }

    if (typeof Date.prototype.toJSON !== 'function') {

        Date.prototype.toJSON = function (key) {

            return isFinite(this.valueOf())
                ? this.getUTCFullYear()     + '-' +
                    f(this.getUTCMonth() + 1) + '-' +
                    f(this.getUTCDate())      + 'T' +
                    f(this.getUTCHours())     + ':' +
                    f(this.getUTCMinutes())   + ':' +
                    f(this.getUTCSeconds())   + 'Z'
                : null;
        };

        String.prototype.toJSON      =
            Number.prototype.toJSON  =
            Boolean.prototype.toJSON = function (key) {
                return this.valueOf();
            };
    }

    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        gap,
        indent,
        meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        },
        rep;


    function quote(string) {

// If the string contains no control characters, no quote characters, and no
// backslash characters, then we can safely slap some quotes around it.
// Otherwise we must also replace the offending characters with safe escape
// sequences.

        escapable.lastIndex = 0;
        return escapable.test(string) ? '"' + string.replace(escapable, function (a) {
            var c = meta[a];
            return typeof c === 'string'
                ? c
                : '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
        }) + '"' : '"' + string + '"';
    }


    function str(key, holder) {

// Produce a string from holder[key].

        var i,          // The loop counter.
            k,          // The member key.
            v,          // The member value.
            length,
            mind = gap,
            partial,
            value = holder[key];

// If the value has a toJSON method, call it to obtain a replacement value.

        if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
            value = value.toJSON(key);
        }

// If we were called with a replacer function, then call the replacer to
// obtain a replacement value.

        if (typeof rep === 'function') {
            value = rep.call(holder, key, value);
        }

// What happens next depends on the value's type.

        switch (typeof value) {
        case 'string':
            return quote(value);

        case 'number':

// JSON numbers must be finite. Encode non-finite numbers as null.

            return isFinite(value) ? String(value) : 'null';

        case 'boolean':
        case 'null':

// If the value is a boolean or null, convert it to a string. Note:
// typeof null does not produce 'null'. The case is included here in
// the remote chance that this gets fixed someday.

            return String(value);

// If the type is 'object', we might be dealing with an object or an array or
// null.

        case 'object':

// Due to a specification blunder in ECMAScript, typeof null is 'object',
// so watch out for that case.

            if (!value) {
                return 'null';
            }

// Make an array to hold the partial results of stringifying this object value.

            gap += indent;
            partial = [];

// Is the value an array?

            if (Object.prototype.toString.apply(value) === '[object Array]') {

// The value is an array. Stringify every element. Use null as a placeholder
// for non-JSON values.

                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || 'null';
                }

// Join all of the elements together, separated with commas, and wrap them in
// brackets.

                v = partial.length === 0
                    ? '[]'
                    : gap
                    ? '[\n' + gap + partial.join(',\n' + gap) + '\n' + mind + ']'
                    : '[' + partial.join(',') + ']';
                gap = mind;
                return v;
            }

// If the replacer is an array, use it to select the members to be stringified.

            if (rep && typeof rep === 'object') {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    if (typeof rep[i] === 'string') {
                        k = rep[i];
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            } else {

// Otherwise, iterate through all of the keys in the object.

                for (k in value) {
                    if (Object.prototype.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            }

// Join all of the member texts together, separated with commas,
// and wrap them in braces.

            v = partial.length === 0
                ? '{}'
                : gap
                ? '{\n' + gap + partial.join(',\n' + gap) + '\n' + mind + '}'
                : '{' + partial.join(',') + '}';
            gap = mind;
            return v;
        }
    }

// If the JSON object does not yet have a stringify method, give it one.

    if (typeof JSON.stringify !== 'function') {
        JSON.stringify = function (value, replacer, space) {

// The stringify method takes a value and an optional replacer, and an optional
// space parameter, and returns a JSON text. The replacer can be a function
// that can replace values, or an array of strings that will select the keys.
// A default replacer method can be provided. Use of the space parameter can
// produce text that is more easily readable.

            var i;
            gap = '';
            indent = '';

// If the space parameter is a number, make an indent string containing that
// many spaces.

            if (typeof space === 'number') {
                for (i = 0; i < space; i += 1) {
                    indent += ' ';
                }

// If the space parameter is a string, it will be used as the indent string.

            } else if (typeof space === 'string') {
                indent = space;
            }

// If there is a replacer, it must be a function or an array.
// Otherwise, throw an error.

            rep = replacer;
            if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                    typeof replacer.length !== 'number')) {
                throw new Error('JSON.stringify');
            }

// Make a fake root object containing our value under the key of ''.
// Return the result of stringifying the value.

            return str('', {'': value});
        };
    }


// If the JSON object does not yet have a parse method, give it one.

    if (typeof JSON.parse !== 'function') {
        JSON.parse = function (text, reviver) {

// The parse method takes a text and an optional reviver function, and returns
// a JavaScript value if the text is a valid JSON text.

            var j;

            function walk(holder, key) {

// The walk method is used to recursively walk the resulting structure so
// that modifications can be made.

                var k, v, value = holder[key];
                if (value && typeof value === 'object') {
                    for (k in value) {
                        if (Object.prototype.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }


// Parsing happens in four stages. In the first stage, we replace certain
// Unicode characters with escape sequences. JavaScript handles many characters
// incorrectly, either silently deleting them, or treating them as line endings.

            text = String(text);
            cx.lastIndex = 0;
            if (cx.test(text)) {
                text = text.replace(cx, function (a) {
                    return '\\u' +
                        ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }

// In the second stage, we run the text against regular expressions that look
// for non-JSON patterns. We are especially concerned with '()' and 'new'
// because they can cause invocation, and '=' because it can cause mutation.
// But just to be safe, we want to reject all unexpected forms.

// We split the second stage into 4 regexp operations in order to work around
// crippling inefficiencies in IE's and Safari's regexp engines. First we
// replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
// replace all simple value tokens with ']' characters. Third, we delete all
// open brackets that follow a colon or comma or that begin the text. Finally,
// we look to see that the remaining characters are only whitespace or ']' or
// ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.

            if (/^[\],:{}\s]*$/
                    .test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@')
                        .replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']')
                        .replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

// In the third stage we use the eval function to compile the text into a
// JavaScript structure. The '{' operator is subject to a syntactic ambiguity
// in JavaScript: it can begin a block or an object literal. We wrap the text
// in parens to eliminate the ambiguity.

                j = eval('(' + text + ')');

// In the optional fourth stage, we recursively walk the new structure, passing
// each name/value pair to a reviver function for possible transformation.

                return typeof reviver === 'function'
                    ? walk({'': j}, '')
                    : j;
            }

// If the text is not JSON parseable, then a SyntaxError is thrown.

            throw new SyntaxError('JSON.parse');
        };
    }
}());
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
var oldIE = oldIE || false;

function serializeXML(xml)
{
	"use strict";
	if (window.XMLSerializer)
		return (new XMLSerializer()).serializeToString(xml);
	else
		return xml.xml;
}

jQuery.fn.innerXML = function()
{
	"use strict";
	var sXML = serializeXML($(this).get(0));
	var startI = sXML.indexOf(">")+1;
	var endI = sXML.lastIndexOf("<");
	return sXML.substr(startI, endI-startI);
}

var parseXML = $.parseXML

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
	var xmlhttp = window.XMLHttpRequest ? new XMLHttpRequest() :
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
		//variable keep track of if we've seen state 4 yet, and sending extra
		//state 4s onto the boring update function.
		var stateFourSeen = false;
		xmlhttp.onreadystatechange = function ()
		{
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
			} else if(boringUpdate != null) {
				boringUpdate(xmlhttp.readyState, xmlhttp);
			}
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
ajax.get = ajax.c("GET");
ajax.post = ajax.c("POST");
ajax.receive = function(base, cmd, data, callback, failFun, boringUpdate)
{
	return ajax.get("/"+base+"/"+cmd,data,callback,failFun,boringUpdate);
}
ajax.send = function(base, cmd, data, callback, failFun, boringUpdate)
{
	return ajax.post("/"+base+"/"+cmd,data,callback,failFun,boringUpdate);
}

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
 *	@param	failfun {Function} The failure function.  Called if any of the
 *			functions fails.  Gets passed the index of the function which
 *			failed.
 */
function aRun(funs, callback, failfun)
{
	function myARun(i) {
		if(i == funs.length)
			callback();
		else
			funs[i](myARun.c(i+1), failfun.c(i));
	}
	myARun(0);
}

function randStr()
{
	return Math.random().toString(36).substr(2); 
}

function ordinalNumStr(i)
{
	switch(Math.abs(i)%100) {
		case 11:
		case 12:
		case 13: return i+"th";
		default: switch(Math.abs(i)%10) {
			case 1: return i+"st";
			case 2: return i+"nd";
			case 3: return i+"rd";
			default: return i+"th";
		}
	}
}

var money = {
	round: function(price)
	{
		"use strict";
		return Math.ceil(price-0.00001);
	},

	toStr: function(price, currency, noRound)
	{
		"use strict";
		if(!noRound)
			price = money.round(price);
		var absCents = Math.abs(price);
		return	(price < 0 ? "-" : "") +
				(currency != null ? currency : "$") +
				absCents/100.0 +
				(absCents % 100 == 0 ? ".00" :
					(absCents % 10 == 0 ? "0" : ""));
	}
};

function buildAjaxErrFun(cmd, dontStopLoading)
{
	return function(code, _, msg) {
		if(!dontStopLoading)
			$(".loading").removeClass("loading");
		alert("Could not "+cmd+".  Reason:\n"+(code==404?"":code+" ")+msg);
	};
}

if(!String.prototype.endsWith)
	String.prototype.endsWith = function(suffix) {
		return this.indexOf(suffix, this.length-suffix.length) !== -1;
	};

if(!Array.toArray)
	Array.toArray = function(x) {return Array.prototype.slice.call(x, 0);};

if(!Math.gcd)
	Math.gcd = function(a,b)
	{
		if(a < 0) {a = -a;};
		if(b < 0) {b = -b;};
		if(b > a)
			return Math.gcd(b, a);
		while (true) {
			if(a == 0)
				return b;
			b %= a;
			if(b == 0)
				return a;
			a %= b;
		}
		return b;
	}

if(!window.parseBool)
	window.parseBool = function(s) {
		s = s.toUpperCase().charAt(0);
		return	((s == "T") || (s == "1")) ? true :
				((s == "F") || (s == "0")) ? false :
				null;
	}

if(!window.isEmail)
	window.isEmail = function(email) {
		//RFC822 spec,
		//Source: http://maximeparmentier.com/2012/04/09/javascript-rfc2822-email-validation/
		return /^([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x22([^\x0d\x22\x5c\x80-\xff]|\x5c[\x00-\x7f])*\x22)(\x2e([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x22([^\x0d\x22\x5c\x80-\xff]|\x5c[\x00-\x7f])*\x22))*\x40([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x5b([^\x0d\x5b-\x5d\x80-\xff]|\x5c[\x00-\x7f])*\x5d)(\x2e([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x5b([^\x0d\x5b-\x5d\x80-\xff]|\x5c[\x00-\x7f])*\x5d))*$/.test(email);
	}
