var template = template || {};

template.cxErr = function(msg) {
	return	"<div id=\"err-page\">\r\n"+
			"\t<h1>Sorry!</h1>\r\n"+
			"\t<p>Something went wrong.  We\'re getting the following error message:</p>\r\n"+
			"\t<p class=\"error\">"+(msg)+"</p>\r\n"+
			"\t<p>Please pay your bill through the waitstaff</p>\r\n"+
			"</div>";
};