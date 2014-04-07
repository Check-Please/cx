var menu = menu || {};
menu.mods = [];
(function(x) {

menu.mods[0] = {name: "Extra Dressing", price: 5000};
menu.mods[1] = {name: "Extra Market Topping", price: 5000};
menu.mods[2] = {name: "Cheese", price: 10000}
menu.mods[3] = {name: "Nuts", price: 10000};
menu.mods[4] = {name: "Ahi Tuna", price: 80000};
menu.mods[5] = {name: "Anchovy", price: 20000};
menu.mods[6] = {name: "Bacon", price: 10000};
menu.mods[7] = {name: "Fried Chicken", price: 40000};
menu.mods[8] = {name: "Blackened Mahi", price: 70000};
menu.mods[9] = {name: "Grilled Chicken", price: 40000};
menu.mods[10] = {name: "Grilled Shrimp", price: 60000};
menu.mods[11] = {name: "Steak", price: 70000};
menu.mods[12] = {name: "Artichokes", price: 10000};
menu.mods[13] = {name: "Asparagus", price: 10000};
menu.mods[14] = {name: "Avocado", price: 20000};
menu.mods[15] = {name: "Beets", price: 20000};
menu.mods[16] = {name: "Deviled Egg", price: 10000};
menu.mods[17] = {name: "Fingerlings", price: 20000};
menu.mods[18] = {name: "Fries", price: 10000};
menu.mods[19] = {name: "Portobella", price: 20000};
menu.mods[20] = {name: "Roasted Red Peppers", price: 10000};
menu.mods[21] = {name: "Roasted Romatoes", price: 20000};
menu.mods[100] = {name: "Parm Orzo", price: 10000};
menu.mods[101] = {name: "Cheesy Tomato", price: 10000};

for(var i in x)
	if((typeof x[i] == "object") && (x[i] != null) && (x[i].price != null))
		x[i].tax = money.round((x[i].price-(x[i].discount||0))*0.07);

})(menu.mods);
