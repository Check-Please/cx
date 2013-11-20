window.DEBUG = true;

function assert(condition, msg) {
	msg = "ASSERTION FAILED"+(msg == null ? "" : ": "+msg);
	alert(msg);
	var err = new Error(msg);
	mvc.err(msg+"\n\n"+err.stack);
	throw err;
}
