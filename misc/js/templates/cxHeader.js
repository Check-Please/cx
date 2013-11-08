var template = template || {};

template.cxHeader = function(name, logo) {
	return	"<div id=\"header\" "+(logo != null ? "class='has-logo'" : "")+">\r\n"+
			"\t"+(logo != null ? "<img src='cx_custom/"+logo+"_header.png' />" : "")+"\r\n"+
			"\t<span>"+(name)+"</span>\r\n"+
			"</div>";
};