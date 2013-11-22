/*	The view for the split page
 *
 *	@owner sjelin
 */

mvc.views = mvc.views || {};

(function () {
	"use strict";

	var items = null;//A map from item ids to the information about the items
	var elems = null;//A map from item ids to the $elements for those items

	/* Send a message to the server about split stuff
	 */
	function send(cmd, params, f1, f2, f3) {
		params = params || {};
		params.tableKey = mvc.key();
		params.connectionID = mvc.connectionID();
		ajax.send("cx/split", cmd, params, f1, f2, f3);
	};

	var $view = null;
	mvc.views.split = {
		build: function($trgt) {
			if(!$view) {
				$view = $(templates.split("<REPLACE_ME />"));
				var $tmp = $view.find("REPLACE_ME");

				//Add click event to toggle selection
				$tmp.parent().on("click", "a", function() {
					var id = $(this).attr("itemID");
					var selection = mvc.selection();
					send((selection[id] = !selection[id]) ? 'add' : 'remove',
															{itemID : id});
					mvc.selection.notify();
				});

				//Actually build the thing
				elems = {tmp: $tmp};
				buildItems();
				$trgt.append($view);
			} else
				$view.show();

			//Start split
			if(mvc.split() == null) {
				mvc.split({});
				send('start');
			}
		},
		unbuild: function() {$view.hide()},
		nextView: function() {
			if(isValid())
				return mvc.views.receipt;
			else if(mvc.selection()==null||$.isEmptyObject(mvc.selection()))
				alert("Tap on the items you which to pay for");
			else
				alert(	"Before you can proceed, everyone must select the "+
						"items they are paying for.  If some of your "+
						"friends are unable to use the Checkout Express "+
						"app or website, you will need to pay in the "+
						"traditional way through the waitstaff");
		},
		valid: isValid
	}

	/*	Build/replace the DOM element corresponding to a particular item id
	 *
	 *	@pre items[id] != null
	 */
	function updateItem(id)
	{
		var info = items[id];
		var on = mvc.selection() != null && !!mvc.selection()[id];
		var others = mvc.split() == null ? 0 : (mvc.processedSplit()[id]||0);
		var mods = info.mods.map(function(mod) {
			return templates.itemMod(mod.name,
				mod.price ?  money.toStr(mod.price) : "");
		}).join("");
		var $item = $(templates.splitItem(id, on, others, info.name, 
					money.toStr(info.price), mods));
		if(elems[id] != null)
			elems[id].replaceWith($item);
		elems[id] = $item;
	}

	/*	Populate the DOM with the items to be split
	 *
	 *	@pre	elems is not empty and the elements in it are the ones to be
	 *			replaced
	 */
	function buildItems()
	{
		var mvcItems = mvc.processedItems();
		if(mvcItems.length == 0)
			return;//The order has been paid (hopefully)

		var $trgt = null;
		for(var id in elems)
			if($trgt == null)
				$trgt = elems[id];
			else
				elems[id].remove();
		elems = {};

		var ids = [];
		items = {};
		for(var i = 0; i < mvcItems.length; i++) {
			var id = mvcItems[i].id;
			items[id] = mvcItems[i];
			updateItem(id);
			ids.push(id);
		}
		$trgt.replaceWith(ids.map(function(id){return elems[id].get(0);}));
		updateConfirm();
	}

	/*	Updates all the items and the confirm button
	 */
	function update()
	{
		for(var id in items)
			updateItem(id);
		updateConfirm();
	}

	/*	Determines if the split is valid
	 */
	function isValid()
	{
		if(items == null)
			return mvc.processedSplit() == null;
		var sel = mvc.selection() || {};
		var sp = mvc.processedSplit() || {};
		for(var id in items)
			if(!sel[id] && !sp[id])
				return false;
		return true;
	}

	/*	Updates (disables or enables) the confirm button
	 */
	function updateConfirm()
	{
		$view.find(".confirm")[(isValid() ? "remove" : "add") + "Class"](
																"disabled");
	}

	mvc.items.listen(buildItems);
	mvc.split.listen(update);
	mvc.selection.listen(update);
})();
