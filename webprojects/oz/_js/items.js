var menu = menu || {};
menu.items = [];
(function(x) {

x[0] = {name: "Kale Dip", price: 80000};
x[1] = {name: "Cauliflower Patties", price: 70000};
x[2] = {name: "Meatball Sliders", price: 80000};
x[3] = {name: "Fries", price: 60000};
x[4] = {name: "Jerk Wings (large)", price: 120000};
x[5] = {name: "Jerk Wings", price: 60000};
x[6] = {name: "Pork Mac & Cheese", price: 80000};
x[7] = {name: "Mozzarella Sticks", price: 80000};
x[8] = {name: "Pulled Pork Sliders", price: 80000};
x[9] = {name: "Bruschetta", price: 60000};
x[10] = {name: "Stuffed Peppers", price: 80000};
x[11] = {name: "Tuna Nachos", price: 100000};
x[12] = {name: "Chicken Dip", price: 80000};
x[13] = {name: "Pepper Hummas", price: 60000};
x[14] = {name: "Guacamole", price: 60000};

x[100] = {name: "Thai Steak Salad", price: 130000};
x[101] = {name: "Grilled Shrimp Salad", price: 120000};
x[102] = {name: "Runs House Salad", price: 100000};
x[103] = {name: "Cobb Salad", price: 100000};
x[104] = {name: "Southwest Salad", price: 130000};
x[105] = {name: "Beet Salad", price: 100000};
x[106] = {name: "Argentinian Steak Salad", price: 130000};
x[107] = {name: "Social Market Salad", price: 70000};

x[200] = {name: "Tomato Soup", price: 40000};
x[201] = {name: "Du Hour Soup", price: 50000};
x[202] = {name: "Cheesy Tomato Soup", price: 50000};

x[300] = {name: "Italian Sandwitch", price: 100000};
x[301] = {name: "Chicken Sausage Sandwitch", price: 100000};
x[302] = {name: "Meatball Sandwitch", price: 100000};
x[303] = {name: "Stuffed Peper Sandwitch", price: 100000};
x[304] = {name: "Chicken Sandwitch", price: 100000};
x[305] = {name: "Cheese Burger", price: 110000};
x[306] = {name: "Turkey Burger", price: 100000};
x[307] = {name: "Veggy Burger", price: 100000};
x[308] = {name: "Steak Sandwitch", price: 120000};
x[309] = {name: "Cuban Sandwitch", price: 100000};
x[310] = {name: "Reuben", price: 100000};
x[310] = {name: "Blackened Mahi Sandwitch", price: 130000};

x[400] = {name: "Pepperoni Pizza", price: 110000};
x[401] = {name: "Pepperoni Pizza", price: 180000};
x[402] = {name: "Supreme Pizza", price: 130000};
x[403] = {name: "Supreme Pizza", price: 200000};
x[404] = {name: "BBQ Chicken Pizza", price: 120000};
x[405] = {name: "BBQ Chicken Pizza", price: 190000};
x[406] = {name: "Chicken Sausage Pizza", price: 120000};
x[407] = {name: "Chicken Sausage Pizza", price: 190000};
x[408] = {name: "Buffalo Chicken Pizza", price: 120000};
x[409] = {name: "Buffalo Chicken Pizza", price: 190000};
x[410] = {name: "Caprese Pizza", price: 110000};
x[411] = {name: "Caprese Pizza", price: 180000};
x[412] = {name: "Margherita Pizza", price: 110000};
x[413] = {name: "Margherita Pizza", price: 180000};
x[414] = {name: "Thai Chicken Pizza", price: 120000};
x[415] = {name: "Thai Chicken Pizza", price: 190000};
x[416] = {name: "Pierogi Pizza", price: 120000};
x[417] = {name: "Pierogi Pizza", price: 190000};
x[418] = {name: "Mushroom Pizza", price: 130000};
x[419] = {name: "Mushroom Pizza", price: 200000};
x[420] = {name: "Eggs & Ham Pizza", price: 120000};
x[421] = {name: "Eggs & Ham Pizza", price: 190000};
x[422] = {name: "Chipotle Sausage Pizza", price: 120000};
x[423] = {name: "Chipotle Sausage Pizza", price: 190000};
x[424] = {name: "Cauliflower Pizza", price: 120000};
x[425] = {name: "Cauliflower Pizza", price: 190000};

x[500] = {name: "Three Cheese Grilled Cheese", price: 80000};
x[501] = {name: "Fried Egg Grilled Cheese", price: 80000};
x[502] = {name: "Cheesy Bacon Grilled Cheese", price: 80000};

x[600] = {name: "Ice Cream Cookie Sandwitch", price: 50000};
x[601] = {name: "Cookies", price: 30000};
x[602] = {name: "Affogato", price: 40000};
x[603] = {name: "Affogato", price: 60000};

x[700] = {name: "Roasted Quinoa", price: 40000};
x[701] = {name: "Fries/Tots", price: 40000};
x[702] = {name: "Parmesan Orzo", price: 30000};
x[703] = {name: "Side Salad", price: 40000};

x[800] = {name: "Pita Pizza", price: 60000};
x[801] = {name: "Chicken Fingers", price: 60000};
x[802] = {name: "Grilled Cheese", price: 60000};

x[900] = {name: "Eggs in Hell", price: 90000};
x[901] = {name: "Steak & Eggs", price: 110000};
x[902] = {name: "Breakfast Pizza", price: 100000};
x[903] = {name: "Social Benny", price: 90000};
x[904] = {name: "Eggle Bagle", price: 90000};
x[905] = {name: "Scramble", price: 80000};
x[906] = {name: "Greek Yogurt & Granola", price: 50000};
x[907] = {name: "Bagel w/ Cream Cheese", price: 30000};

x[1000] = {name: "Sam Adams Boston Lager", price: 45000};
x[1004] = {name: "Sam Adams Boston Lager", price: 45000, discount: 25000};
x[1005] = {name: "PBR", price: 30000};
x[1009] = {name: "PBR", price: 30000, discount: 10000};
x[1010] = {name: "Coors Light", price: 35000};
x[1014] = {name: "Coors Light", price: 35000, discount: 15000};
x[1015] = {name: "Barking Squirrel Lager", price: 60000};
x[1020] = {name: "Miller Light", price: 35000};
x[1024] = {name: "Miller Light", price: 35000, discount: 15000};
x[1025] = {name: "Heineken", price: 40000};
x[1029] = {name: "Heineken", price: 40000, discount: 20000};
x[1030] = {name: "Labatt Blue", price: 40000};
x[1034] = {name: "Labatt Blue", price: 40000, discount: 20000};
x[1035] = {name: "Yuengling Lager", price: 35000};
x[1039] = {name: "Yuengling Lager", price: 35000, discount: 15000};
x[1040] = {name: "Yuengling Light", price: 35000};
x[1044] = {name: "Yuengling Light", price: 35000, discount: 15000};
x[1045] = {name: "Sly Fox Pikeland Pils", price: 40000};
x[1049] = {name: "Sly Fox Pikeland Pils", price: 40000, discount: 20000};
x[1050] = {name: "Anderson Vally el Steinber", price: 80000};
x[1055] = {name: "Bud Light", price: 35000};
x[1059] = {name: "Bud Light", price: 35000, discount: 15000};
x[1060] = {name: "Stella", price: 60000};
x[1065] = {name: "Michelob Ultra", price: 35000};
x[1069] = {name: "Michelob Ultra", price: 35000, discount: 15000};

x[1100] = {name: "Blue Moon", price: 50000};
x[1104] = {name: "Blue Moon", price: 50000, discount: 30000};
x[1105] = {name: "Abita Purple Haze", price: 50000};
x[1109] = {name: "Abita Purple Haze", price: 50000, discount: 30000};
x[1110] = {name: "Goose Island 312", price: 50000};
x[1114] = {name: "Goose Island 312", price: 50000, discount: 30000};

x[1200] = {name: "Finch's Pale Ale", price: 80000};
x[1205] = {name: "Dale's Pale Ale", price: 50000};
x[1209] = {name: "Dale's Pale Ale", price: 50000, discount: 30000};
x[1210] = {name: "Sierra Nevada Pale Ale", price: 50000};
x[1214] = {name: "Sierra Nevada Pale Ale", price: 50000, discount: 30000};
x[1215] = {name: "Cisco Pale Ale", price: 50000};
x[1219] = {name: "Cisco Pale Ale", price: 50000, discount: 30000};
x[1220] = {name: "21st Amendment Bitter American", price: 50000};
x[1224] = {name:"21st Amendment Bitter American",price:50000,discount:30000};

x[1300] = {name: "Finch's Secret Stache", price: 85000};
x[1305] = {name: "Murphy's Irish Stout", price: 60000};

x[1400] = {name: "21st Amendment Back in Black", price: 50000};
x[1404] = {name:"21st Amendment Back in Black", price:50000, discount:30000};
x[1405] = {name: "21st Amendment Brew Free or Die", price: 50000};
x[1409] ={name:"21st Amendment Brew Free or Die",price:50000,discount:30000};
x[1410] = {name: "Finch's Threadless", price: 85000};
x[1415] = {name: "Rivertown Old Wylie IPA", price: 50000};
x[1419] = {name: "Rivertown Old Wylie IPA", price: 50000, discount: 30000};

x[1500] = {name: "Finch's Fascist Pig Ale", price: 85000};
x[1505] = {name: "Old Chub", price: 60000};
x[1510] = {name: "Woodchuck Amber Cider", price: 50000};
x[1514] = {name: "Woodchuck Amber Cider", price: 50000, discount: 30000};
x[1515] = {name: "Magic Hat No. 9", price: 45000};
x[1519] = {name: "Magic Hat No. 9", price: 45000, discount: 25000};
x[1520] = {name: "Finch's Golden Wing", price: 70000};
x[1525] = {name: "Yuengling Black & Tan", price: 35000};
x[1529] = {name: "Yuengling Black & Tan", price: 35000, discount: 15000};
x[1530] = {name: "O'Douls", price: 35000};
x[1534] = {name: "O'Douls", price: 35000, discount: 15000};
x[1535] = {name: "Rivertown Hala Kahiki", price: 50000};
x[1539] = {name: "Rivertown Hala Kahiki", price: 50000, discount: 30000};
x[1540] = {name: "Rivertown Maxwell's Scottish Ale", price: 50000};
x[1544]={name:"Rivertown Maxwell's Scottish Ale",price:50000,discount:30000};
x[1545] = {name: "Anderson Valley Winter Solstice", price: 60000};

x[1600] = {name: "Miller Lite", price: 30000};
x[1604] = {name: "Miller Lite", price: 30000, discount: 10000};
x[1605] = {name: "Bud Light", price: 30000};
x[1609] = {name: "Bud Light", price: 30000, discount: 10000};
x[1610] = {name: "Yuengling", price: 30000};
x[1614] = {name: "Yuengling", price: 30000, discount: 10000};
x[1615] = {name: "Guinnes", price: 55000};
x[1620] = {name: "Pilsner Urquell", price: 55000};

x[1700] = {name: "Sangria", price: 90000};
x[1701] = {name: "Sangria", price: 300000};
x[1702] = {name: "Mojito", price: 90000};
x[1703] = {name: "Mojito", price: 300000};
x[1704] = {name: "Social Punch", price: 90000};
x[1705] = {name: "Social Punch", price: 300000};
x[1706] = {name: "Margarita", price: 90000};
x[1707] = {name: "Margarita", price: 300000};
x[1708] = {name: "Jacked Arnold Palmer", price: 90000};
x[1709] = {name: "Jacked Arnold Palmer", price: 300000};

x[1800] = {name: "Wolfhound", price: 90000};
x[1801] = {name: "The Dude", price: 100000};
x[1802] = {name: "Prosecco Cosmo", price: 100000};
x[1803] = {name: "CBS", price: 100000};
x[1804] = {name: "Bourbon Blast", price: 90000};
x[1805] = {name: "The Alamo", price: 90000};
x[1806] = {name: "Brooklyn Bridge", price: 100000};
x[1807] = {name: "Amaretto Sour", price: 120000};

x[1900] = {name: "Montepulciano d'Abruzzo, Masciarelli", price: 100000};
x[1901] = {name: "Montepulciano d'Abruzzo, Masciarelli", price: 400000};
x[1902] = {name: "Cabernet Sauvignon, Sean Minor", price: 150000};
x[1903] = {name: "Cabernet Sauvignon, Sean Minor", price: 580000};
x[1904] = {name: "Cabernet Sauvignon, Nickname", price: 90000};
x[1905] = {name: "Cabernet Sauvignon, Nickname", price: 360000};
x[1906] = {name: "Bordeaux, Charteau des Leotins", price: 100000};
x[1907] = {name: "Bordeaux, Charteau des Leotins", price: 400000};
x[1908] = {name: "Putto Chianti, Fattoria San Fabiano", price: 120000};
x[1909] = {name: "Putto Chianti, Fattoria San Fabiano", price: 480000};
x[1910] = {name: "Pinot Noir, Montoya", price: 120000};
x[1911] = {name: "Pinot Noir, Montoya", price: 480000};
x[1912] = {name: "Shiraz, Stalking Horse", price: 140000};
x[1913] = {name: "Shiraz, Stalking Horse", price: 560000};
x[1914] = {name: "Garnache, Acentor", price: 110000};
x[1915] = {name: "Garnache, Acentor", price: 440000};
x[1916] = {name: "Beaujolais, Domaine de Foretal", price: 110000};
x[1917] = {name: "Beaujolais, Domaine de Foretal", price: 440000};

x[2000] = {name: "Sauvignon Blanc, Ranga Ranga", price: 110000};
x[2001] = {name: "Sauvignon Blanc, Ranga Ranga", price: 440000};
x[2002] = {name: "Trebbiano d'Abruzzo, Masciarelli", price: 100000};
x[2003] = {name: "Trebbiano d'Abruzzo, Masciarelli", price: 400000};
x[2004] = {name: "Chardonnay, Domaine Martinolles", price: 90000};
x[2005] = {name: "Chardonnay, Domaine Martinolles", price: 360000};
x[2006] = {name: "Pino Gris, A to Z", price: 120000};
x[2007] = {name: "Pino Gris, A to Z", price: 480000};
x[2008] = {name: "Chenin Blanc, Jovly", price: 110000};
x[2009] = {name: "Chenin Blanc, Jovly", price: 440000};
x[2010] = {name: "Sauvignon Blac, Honig", price: 140000};
x[2011] = {name: "Sauvignon Blac, Honig", price: 560000};
x[2012] = {name: "Estate Chardonnay, EOS", price: 120000};
x[2013] = {name: "Estate Chardonnay, EOS", price: 480000};
x[2014] = {name: "Rose, Chateau Jouclary", price: 110000};
x[2015] = {name: "Rose, Chateau Jouclary", price: 440000};

x[2100] = {name: "Brut Cava, Conde de Subirats", price: 110000};
x[2101] = {name: "Brut Cava, Conde de Subirats", price: 440000};

x[2200] = {name: "Rivertown, Grateful White", price: 40000};
x[2205] = {name: "Southern Teir, 2x One", price: 80000};
x[2210] = {name: "East End, Honey Heather", price: 60000};
x[2215] = {name: "East End, Big Hop IPA", price: 60000};
x[2220] = {name: "East End, Cream Ale", price: 60000};
x[2225] = {name: "Fatheads, Head Hunter IPA", price: 60000};
x[2230] = {name: "East End, Monkey Boy", price: 60000};
x[2235] = {name: "Strongbow, Hard Cider", price: 50000};
x[2240] = {name: "Kona, Koko Brown", price: 60000};
x[2245] = {name: "East End, Oatmeal Stout", price: 60000};
x[2250] = {name: "Green Flash, West Coast", price: 70000};

x[3000] = {name: "Coffee", price: 20000};
x[3001] = {name: "Soda", price: 20000};
x[3002] = {name: "Ice Tea", price: 20000};
x[3003] = {name: "Decaf Coffee", price: 20000};

for(var i in x)
	if((typeof x[i] == "object") && (x[i] != null) && (x[i].price != null)) {
		if((i >= 1000) && (i < 3000)) {
			//Tax included for alcohol
			var p = x[i].price;
			var d = x[i].discount;
			x[i].price = money.round(money.round(p/0.0107)/100);
			if(d)
				x[i].discount = x[i].price -
					money.round(money.round((p-d)/0.0107)/100);
		}
		x[i].tax = money.round((x[i].price-(x[i].discount||0))*0.07);
	}

})(menu.items);
