!{{NATIVE}} && (function() {
	//From http://remysharp.com/downloads/font.js
	var fontDetector = (function () {
		var test_string = 'mmmmmmmmmwwwwwww';
		var test_font = '"Comic Sans MS"';
		var notInstalledWidth = 0;
		var testbed = null;
		var guid = 0;
		
		return {
			// must be called when the dom is ready
			setup : function () {
				if ($('#fontInstalledTest').length) return;

				$('head').append('<' + 'style> #fontInstalledTest, #fontTestBed { position: absolute; left: -9999px; top: 0; visibility: hidden; } #fontInstalledTest { font-size: 50px!important; font-family: ' + test_font + ';}</' + 'style>');
				
				
				$('body').append('<div id="fontTestBed"></div>').append('<span id="fontInstalledTest" class="fonttest">' + test_string + '</span>');
				testbed = $('#fontTestBed');
				notInstalledWidth = $('#fontInstalledTest').width();
			},
			
			isInstalled : function(font) {
				guid++;
			
				var style = '<' + 'style id="fonttestStyle"> #fonttest' + guid + ' { font-size: 50px!important; font-family: ' + font + ', ' + test_font + '; } <' + '/style>';
				
				$('head').find('#fonttestStyle').remove().end().append(style);
				testbed.empty().append('<span id="fonttest' + guid + '" class="fonttest">' + test_string + '</span>');
							
				return (testbed.find('span').width() != notInstalledWidth);
			}
		};
	})();

	function loadSourceSansPro() {
		window.WebFontConfig = { google: { families: [
			'Source+Sans+Pro:200,300,400,700,300italic:latin' ] } };
		var wf = document.createElement('script');
		wf.src = ('https:' == document.location.protocol ? 'https' : 'http') +
			'://ajax.googleapis.com/ajax/libs/webfont/1/webfont.js';
		wf.type = 'text/javascript';
		wf.async = 'true';
		var s = document.getElementsByTagName('script')[0];
		s.parentNode.insertBefore(wf, s);
	}

	//All the helveticas!
	var helveticas = [	"Helvetica Neue", "HelveticaNeue", "Helvetica",
						"Helvetica Neue LT", "HelveticaNeueLT",
						"Helvetica Neue Pro", "HelveticaNeuePro",
						"Helvetica Neue LT Pro", "HelveticaNeueLTPro",
						"HelveticaNeueLTPro-Roman"];

	$(document).ready(function() {
		fontDetector.setup();
		for(var i = 0; i < helveticas.length; i++)
			if(fontDetector.isInstalled(helveticas[i]))
				return;
		loadSourceSansPro();
	});
})();
