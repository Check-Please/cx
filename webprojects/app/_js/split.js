/*	The MVC for the split page
 *
 *	@see mvc.js
 *	@owner sjelin
 */

  ////////////////////
 /////  HEADER  /////
////////////////////

(function () {
	"use strict";

  /////////////////////////////////
 /////  CODE (IN NAMESPACE)  /////
/////////////////////////////////

	var items = null;
	var elems = null;

	function send(cmd, params, f1, f2, f3) {
		params = params || {};
		params.mobileKey = mvc.key();
		params.clientID = mvc.clientID();
		ajax.send("cx/split", cmd, params, f1, f2, f3);
	};

	function toggleSelection()
	{
		var id = $(this).attr("itemID");
		var selection = mvc.selection();
		send((selection[id]=!selection[id]) ? 'add' : 'remove', {itemID:id});
		mvc.notifySelection();
	}

	var inited = false;
	function buildSplit()
	{
		if(!inited) {
			inited = true;
			$("#split-page .items").on("click", "a", toggleSelection);
			buildItems();
		}
		if(mvc.split() == null) {
			mvc.split({});
			send('start');
		}
	}

	function updateItem(id)
	{
		var info = items[id];
		var on = mvc.selection() != null && !!mvc.selection()[id];
		var others = mvc.split() == null ? 0 : (mvc.processedSplit()[id]||0);
		var mods = info.mods.map(function(mod) {
			return template.cxItemMod(mod.name,
				mod.price ?  money.toStr(mod.price) : "");
		}).join("");
		var $item = $(template.cxSplitItem(id, on, others, info.name, 
					money.toStr(info.price), mods));
		if(elems[id] != null)
			elems[id].replaceWith($item);
		elems[id] = $item;
	}

	function buildItems()
	{
		var mvcItems = mvc.processedItems();
		if(mvcItems.length == 0)
			return;//The order has been paid (hopefully)

		var $trgt = null;
		if(elems == null)
			$trgt = $("#split-page .REPLACE-ME");
		else for(var id in elems)
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

	function update()
	{
		for(var id in items)
			updateItem(id);
		updateConfirm();
	}

	function validSplit()
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

	function updateConfirm()
	{
		$("#split-page .confirm")[(validSplit()?"remove":"add")+"Class"](
				"disabled");
	}

	mvc.addItemsListener(buildItems);
	mvc.addSplitListener(update);
	mvc.addSelectionListener(update);

  ////////////////////
 /////  FOOTER  /////
////////////////////

	mvc.buildSplit = buildSplit;
	mvc.validSplit = validSplit;
})();
