/**	Various constants 	
 *
 *	@owner	sjelin	
 */

var consts = {
	MOD_HEIGHT: 0.5,
	FOCUS_DELAY: 501
};

consts.statuses = {
	CHECKED: 0,
	UNCHECKED: 1,
	TAKEN: 2,
	PAID: 3
};

consts.summaries = {
	SUBTOTAL: 4,
	DISCOUNT: 5,
	S_C: 6,
	TAX: 7,
	TOTAL: 8,
};

consts.SUMMARY_NAMES = {};
consts.SUMMARY_NAMES[consts.summaries.SUBTOTAL] = "Subtotal";
consts.SUMMARY_NAMES[consts.summaries.DISCOUNT] = "Discount";
consts.SUMMARY_NAMES[consts.summaries.S_C] = "Service Charge";
consts.SUMMARY_NAMES[consts.summaries.TAX] = "Tax";
consts.SUMMARY_NAMES[consts.summaries.TOTAL] = "Total";

consts.ITEM_CLASSES = {};
consts.ITEM_CLASSES[consts.statuses.CHECKED] = "checked";
consts.ITEM_CLASSES[consts.statuses.UNCHECKED] = "unchecked";
consts.ITEM_CLASSES[consts.statuses.TAKEN] = "taken";
consts.ITEM_CLASSES[consts.statuses.PAID] = "paid";
consts.ITEM_CLASSES[consts.summaries.SUBTOTAL] = "subtotals";
consts.ITEM_CLASSES[consts.summaries.DISCOUNT] = "discount";
consts.ITEM_CLASSES[consts.summaries.S_C] = "serviceCharge";
consts.ITEM_CLASSES[consts.summaries.TAX] = "tax";
consts.ITEM_CLASSES[consts.summaries.TOTAL] = "total";

consts.STATUSES	=Object.keys(consts.statuses).map(
					op.get.c(consts.statuses)).sort();
consts.SUMMARIES=Object.keys(consts.summaries).map(
					op.get.c(consts.summaries)).sort();


consts.tipSlider = {
	SMALL_TIP: -1,
	MED_TIP: 0,
	LARGE_TIP: 1,
	CUSTOM_TIP: null
}


consts.views = {
	RECEIPT: "receipt",
	SPLIT: "split",
	PAY: "pay",
	CARDS: "cards",
	FEEDBACK: "feedback",
	LOADING: "loading",
	ERROR: "error"
}

consts.INPUT_VIEWS = [	consts.views.RECEIPT, consts.views.PAY,
						consts.views.CARDS];


consts.feedback = {
	BAD: -1,
	OK: 0,
	GOOD: 1,
	VERY_GOOD: 2
}
