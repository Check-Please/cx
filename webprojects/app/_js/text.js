text = {
	NO_GEOLOCATION_ERR:
		"No geolocation available.  Please upgrade your browser",
	WAITING_FOR_GEOLOCATION_MSG:
		"Still waiting for user decision",
	NO_TABLE_INFO_HDG:
		"No Table Info",
	NO_TABLE_INFO_MSG:
		"Couldn't find information about where you're sitting",
	INVALID_TABLE_HDG:
		"Invalid Table",
	INVALID_TABLE_MSG:
		"Couldn't find information about where you're sitting",
	EMPTY_TABLE_HDG:
		"Empty",
	EMPTY_TABLE_MSG:
		"There are no unpaid items at this table",
	UPDATE_REQ_HDG:
		"Update",
	UPDATE_REQ_MSG:
		"The app needs to be updated",
	APP_DISABLED_HDG:
		"Disabled",
	APP_DISABLED_MSG:
		"The app is currently disabled",
	"500_ERROR_HDG":
		"Server Error",
	"500_ERROR_MSG":
		"Something went wrong with on server",
	BLUETOOTH_DISABLED_HDG:
		"Bluetooth Disabled",
	BLUETOOTH_DISABLED_MSG:
		"Turn on bluetooth to use this app",
	BLUETOOTH_ERROR_HDG:
		"Bluetooth Error",
	BLUETOOTH_ERROR_MSG:
		"Make sure bluetooth is enabled and working",
	NO_LOCATION_DATA_HDG:
		"Can't find you",
	NO_LOCATION_DATA_MSG:
		"Can't get data on where you're sitting",
	LOADING_ORDER_ERROR_HDG:
		"Couldn't load",
	LOADING_ORDER_ERROR_MSG:
		"Couldn't get your order from the server",
	GETTING_ORDER_LOAD_MSG:
		"Getting Order",
	ENTER_TIP_ALERT:
		"Enter a tip",
	SELECT_CARD_ALERT:
		"Select a credit card",
	ENTER_CARD_PASS_ALERT:
		"Enter card password",
	STARTING_PAYMENT_LOAD_MSG:
		"Sending payment information",
	NO_PADDWORD_REQ_ALERT:
		"The card you picked doesn't need a password",
	PASSWORD_REQ_ALERT:
		"The card you selected requires a password",
	INCORRECT_PASSWORD_ALERT:
		"The password you entered is incorrect",
	LOST_ENCRYPT_KEY_ALERT:
		"Please re-enter credit card information",
	NO_CIPHERTEXT_ALERT:
		"Saved credit card information partially missing.  Please use a " +
		"different card or re-enter information.",
	CIPHERTEXT_CORRUPED_ALERT:
		"Saved credit card information corrupted.  Please use a different " +
		"card or re-enter information",
	PROCESSING_PAY_LOAD_MSG:
		"Processing Payment",
	CANNOT_PAY_HDG:
		"Couldn't Pay",
	PAY_ERROR_HDG:
		"Payment Error",
	SOCKET_CLEAR_HDG:
		"Closed",
	SOCKET_CLEAR_MSG:
		"The server just asked this app to close.  Please reload.",
	SOCKET_UNKNOWN_CMD_HDG:
		"Unknown command",
	SOCKET_INACTIVE_HDG:
		"Inactive",
	SOCKET_INACTIVE_MSG:
		"Please reload",
	SOCKET_ERROR_HDG:
		"Unknown Error",
	SOCKET_ERROR_MSG:
		"Please reload",
	ASSERT_FAIL_HDG:
		"Assertion Failed",
	SELECT_CARD_NUM_REPL:
		"(Select Card)",
	ENTER_PASSWORD_NUM_REPL:
		"(Enter Password)",
	PICK_CARD_ALERT:
		"Please select a card",
	ENTER_CARD_NUM_ALERT:
		"Please enter card password",
	PASSWORD_QCHECK_FAIL:
		"Incorrect password",
	INVALID_EMAIL_ALERT:
		"Please enter a valid email address",
	LOADING_DONE:
		"Done!",
	TIP_ON_ZERO:
		"Thank you!",
	GIANT_TIP:
		"Thank You!",
	SPLIT_EMPTY_ALERT:
		"Enter a number",
	SPLIT_ZERO_ALERT:
		"Cannot split an item zero ways",
	getError: function(name, symbol) {
		return {	heading: text[name+"_HDG"], symbol: symbol,
					message: text[name+"_MSG"]};
	}
}
