var DEBUG = true;

function assert(condition, msg) {
	if(!condition) {
		msg = "ASSERTION FAILED"+(msg == null ? "" : ": "+msg);
		alert(msg);
		var err = new Error(msg);
		if(mvc.inited())
			mvc.err(msg+"\n\n"+err.stack);
		console.log(msg+"\n\n"+err.stack);
		throw err;
	}
}
