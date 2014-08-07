function assert(condition, msg) {
	if(!condition) {
		msg = "ASSERTION FAILED"+(msg == null ? "" : ": "+msg);
		alert(msg);
		try {
			throw new Error(msg);
		} catch(err) {
			msg += "\n\n" + err.stack;
			console.log(msg);
			models.error({	heading: text.ASSERT_FAIL_HDG,
							symbol: String.fromCharCode(215),
							message: msg+"\n\n"+err.stack});
			throw err;
		}
	}
}
