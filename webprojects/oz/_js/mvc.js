/**	This is an MVC modeled after the MVC used in the main checkout express
 *	app.  Please read js/mvc.js in order to understand this MVC.  We use
 *	the following variables here:
 *		items - The items on the tables.  Map from table keys to arrays of
 *				objects.  Objects follow format in TicketItem.java
 *		split -	The way the items are split between the payers.  Map from
 *				table keys to maps from clientIDs to arrays of item ids.
 *		
 */
