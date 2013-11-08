var template = template || {};

template.cxFacts = function(name, code) {
	return	"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\r\n"+
			"\t\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n"+
			"\r\n"+
			"<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n"+
			"\t<head>\r\n"+
			"\t\t<title>Checkout Express Fact Sheet</title>\r\n"+
			"\t\t<link type=\"text/css\" rel=\"Stylesheet\" href=\"merged/facts.css\" media=\"all\" />\r\n"+
			"\t</head>\r\n"+
			"\t<body class=\"just-one\">\r\n"+
			"\t\t<h1>Checkout Express Fact Sheet "+(name==null ? "":" for "+name)+"</h1>\r\n"+
			"\t\t<p>(please excuse any typos, this was prepared in a hurry)</p>\r\n"+
			"\t\t<table>\r\n"+
			"\t\t\t<tbody>\r\n"+
			"\t\t\t\t<tr>\r\n"+
			"\t\t\t\t\t<td>What</td>\r\n"+
			"\t\t\t\t\t<td>\r\n"+
			"\t\t\t\t\t\tCheckout Express is an website/app which allows your customers to bring up their bill on their smartphone and pay for it without having to get anything from the waitstaff.  I am looking to test this app out at a restaurant to determine if this is something customers would appreciate.\r\n"+
			"\t\t\t\t\t</td>\r\n"+
			"\t\t\t\t</tr>\r\n"+
			"\t\t\t\t<tr>\r\n"+
			"\t\t\t\t\t<td>Why</td>\r\n"+
			"\t\t\t\t\t<td>\r\n"+
			"\t\t\t\t\t\tThe normal checkout process is slow, taking a couple minutes and requiring someone from the waitstaff to make several trips back and forth to the POS.  Letting customers pay for themselves will reduce the load on the waitstaff, save customers time, and help you turn tables faster.\r\n"+
			"\t\t\t\t\t</td>\r\n"+
			"\t\t\t\t</tr>\r\n"+
			"\t\t\t\t<tr>\r\n"+
			"\t\t\t\t\t<td>How</td>\r\n"+
			"\t\t\t\t\t<td>\r\n"+
			"\t\t\t\t\t\tThe way the app works is that each table at your restaurant has a small card with a QR code and URL placed on it (example: <a href=\"https://www.chkex.com/card?"+(code)+"\">http://chkex.com/card?"+(code)+"</a>).  When the customer is done eating, they scan the QR code or enter the URL into their smartphone, which then brings them to a website (example: <a href=\"https://www.chkex.com?"+(code)+"\">http://chkex.com?"+(code)+"</a>, username: \"example@example.com\", password: \"abcd1234_\"). The website automatically looks up what the customer has ordered and lets the customer enter in their credit card information so they can pay.  The credit card information is forwarded along to your POS, which then processes it exactly as though a waiter or waitress had inputted the information into one of the terminals.  Once the credit card has been charged, the ticket is automatically closed on your POS.\r\n"+
			"\t\t\t\t\t</td>\r\n"+
			"\t\t\t\t</tr>\r\n"+
			"\t\t\t\t<tr>\r\n"+
			"\t\t\t\t\t<td>Plugin</td>\r\n"+
			"\t\t\t\t\t<td>\r\n"+
			"\t\t\t\t\t\tThe website communicates with your POS via a small plugin which I would have to install onto your machine.  The plugin is made by a company called \"SubtleData\".  A small factsheet about the plugin can be found at <a href=\"http://www.chkex.com/plugin.pdf\">http://chkex.com/plugin.pdf</a>.  The important facts are that the POS plugin is totally secure and installing it on your machines does not violate any PCI security standards.\r\n"+
			"\t\t\t\t\t</td>\r\n"+
			"\t\t\t\t</tr>\r\n"+
			"\t\t\t\t<tr>\r\n"+
			"\t\t\t\t\t<td>Ease</td>\r\n"+
			"\t\t\t\t\t<td>\r\n"+
			"\t\t\t\t\t\tThis app in no way prevents the customer from getting the bill from the waitstaff and paying in the usual way.  The app is merely an alternative, one which has the potential to save everyone time.  What\'s more, the service is totally automatic, requiring no special training for your servers.  If you want to disable the app temporary, it\'s as easy as removing the cards from the tables.  Without the cards, customers have no way to access the website and the app is effectively disabled.  I will handle all the setup myself.  When the testing is over, I will uninstall the plugin and throw out all the cards.  In essence, the whole process requires little no no effort or risk on your part.\r\n"+
			"\t\t\t\t\t</td>\r\n"+
			"\t\t\t\t</tr>\r\n"+
			"\t\t\t\t<tr>\r\n"+
			"\t\t\t\t\t<td>Setup</td>\r\n"+
			"\t\t\t\t\t<td>\r\n"+
			"\t\t\t\t\t\tThe setup can be done rather quickly.  I can install the plugin in a matter of seconds.  I\'ll need to talk to someone from the waitstaff for a minute or two to ask questions about how you guys use your POS (questions like \"How many tables are there?\"), but beyond that I shouldn\'t have to bother anyone.  The rest of the setup I can do remotely.  The day after I install the plugin I should be able to come in with all the cards and card stands ready and everything working.  At that point I\'ll buy a beer or something and pay for it with the app just to make sure there are no bugs.  \r\n"+
			"\t\t\t\t\t</td>\r\n"+
			"\t\t\t\t</tr>\r\n"+
			"\t\t\t\t<tr>\r\n"+
			"\t\t\t\t\t<td>Timeframe</td>\r\n"+
			"\t\t\t\t\t<td>\r\n"+
			"\t\t\t\t\t\tUnfortunately, SubtleData is going out of business, and is going to begin shutting down its service at the start of next month.  I can build an alternative to SublteData, but for that I need investors.  In order to get investors, I need to show that this is a product that there\'s demand for.  In order to do that, I need to actually have the app running at a restaurant and see if this is really something customers like.  With that in mind, I\'d like to get the thing set up at "+(name == null ? "your restaurant" : name)+" as soon as possible.\r\n"+
			"\t\t\t\t\t</td>\r\n"+
			"\t\t\t\t</tr>\r\n"+
			"\t\t\t\t<tr>\r\n"+
			"\t\t\t\t\t<td>Contact</td>\r\n"+
			"\t\t\t\t\t<td>\r\n"+
			"\t\t\t\t\t\tI\'d love to meet with you guys and hammer out any questions or details which you\'re interested in.  My scheduled is very flexable and my number is 310-880-4668.  I wake up early and stay up late, so call any time.\r\n"+
			"\t\t\t\t\t</td>\r\n"+
			"\t\t\t\t</tr>\r\n"+
			"\t\t\t</tbody>\r\n"+
			"\t\t</table>\r\n"+
			"\t</body>\r\n"+
			"</html>";
};