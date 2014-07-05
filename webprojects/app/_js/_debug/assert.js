function assert(condition, msg) {
	if(!condition) {
		msg = "ASSERTION FAILED"+(msg == null ? "" : ": "+msg);
		alert(msg);
		try {
			throw new Error(msg);
		} catch(err) {
			msg += "\n\n" + err.stack;
			console.log(msg);
			if(mvc.inited())
				mvc.err(msg+"\n\n"+err.stack);
			throw err;
		}
	}
}
