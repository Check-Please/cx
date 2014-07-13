//Split
Fluid.defineInputType("two-digit", {
	typeAttr: "number",
	validate: /^[0-9]{0,2}$/
});

//Pay
Fluid.defineInputType("price", {
	typeAttr: "number",
	validate: /^[0-9]*(?:\.[0-9]{0,2})?$/,
});

//Cards
Fluid.defineInputType("cc-pan", {
	validate: /^[0-9]{0,19}$/,
	formatChars: / /,
	format: creditCards.format.C($, $, "  ")
});
Fluid.defineInputType("cc-name", {
	validate: function(num) {
		if(num.length > 26)
			return false;
		num = num.toUpperCase();
		for(var i = 0; i < num.length; i++)
			if(num.charCodeAt(i) < 32)
				return false;
			else if(num.charCodeAt(i) > 95)
				return false;
		return true;
	}
});
Fluid.defineInputType("cc-expr-year", {
	typeAttr: "number",
	validate: /^[0-9]{0,4}$/
});
Fluid.defineInputType("cc-cvv", {
	typeAttr: "number",
	validate: /^[0-9]{0,4}$/
});
Fluid.defineInputType("zip", {
	typeAttr: "number",
	validate: /^[0-9]{0,5}$/
});

//Feedback (polyfill)
Fluid.defineInputType("email");
