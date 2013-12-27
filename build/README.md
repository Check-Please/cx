The goals of the builder are as follows:
- Build the `web.xml` file for the server
- Run some basic templating over web files
- Compile `.scss` files into `.css` files
- Compress `js` files, unless the `--debug` flag is specified
- Compile web template files
- Merge `js` and `css` files to reduce the number of requests to the server
- Push files for all websites to the server
- Push files for webapp to any smartphone apps

Building the web.xml file
-----------------------------------------------------------------------------

The file `/server/servlet-list.csv` dictates what is put in the `web.xml`
file. The format for `servlet-list.csv` is as follows:

	sourcefile, url [, servlet]
	sourcefile, url [, servlet]
	...
	sourcefile, url [, servlet]

	Source Package
	sourcefile, url [, servlet]
	sourcefile, url [, servlet]
	...
	sourcefile, url [, servlet]

	Source Package
	sourcefile, url [, servlet]
	sourcefile, url [, servlet]
	...
	sourcefile, url [, servlet]

	.
	.
	.

	Source Package
	sourcefile, url [, servlet]
	sourcefile, url [, servlet]
	...
	sourcefile, url [, servlet]

In order in increase readability, source packages listed servlet-list.csv are
reformatted slightly before being put into the web.xml file.  Specifically,
they undergo the following transfermations (in order):

1.	Anything matching `/\s*-\s+/` is replaced with a `.`
2.	Spaces are replaced with underscores
3.	Uppercase letters are made lowercase
4.	The result is prepended with `servlets.`

Thus, `Parent Folder - Kid` becomes `servlets.parent_folder.kid`

Build-time templating syntax
-----------------------------------------------------------------------------

The macros for build-time templating are defined in the `macros` file in this
folder.  There are two types of macros: constants and functions.

Constants are defined using the syntax:

	{{VAR_NAME}} = value

The constant can then be subsituted into any file using the syntax
`{{VAR_NAME}}`.  So, for instance, if you had the following in your `macros`
folder:

	{{DOMAIN_NAME}} = http://www.chkex.com

And in your code you wrote:

	alert("You are using {{DOMAIN_NAME}}");

The resulting code would be:

	alert("You are using {{DOMAIN_NAME}}");

Funtions are defined similarly.  The following syntax is used:

	{{FUN_NAME}} = (param1, param2, ...) -> replacement

The function is then invoked for build-time substitution using the following
syntax `{{FUN_NAME: param1, param2, ...}}`.  So for instance if you had the
following in the `macros` folder:

	{{ERR}} = (msg) -> alert(msg); throw new Error(msg);

And in your code you wrote:

	{{ERR: "BUG!"}}

The resulting code would be:

	alert("BUG!"); throw new Error("BUG!");


Run time templating
-----------------------------------------------------------------------------

Run time templating is done using our proprietary templating engine.  Our
templating engine has very few features, and compiles to JS and Java.  The
reasoning can be found on the wiki.  But here we decribe the syntax.

Each template has two files associated to it: a `.tmplt` (pronounced
"template") file and a `.tspec` (pronounced "t-spec", or "template
specification") file.  The `.tspec` file defines the variables used in the
template.  The `.tmplt` file defines the actual output of the file.

`.tspec` files consist of a series of declarations.  Declarations can be
formatted in any of the following ways:

	@param varName
	@param {type} varName
	@param varName {type}
	@param varName comment
	@param {type} varName comment
	@param varName {type} comment

The `type` is only used for the java output.  If no type is specified, the
type `Object` is used.  The comments can be as many lines as desired.  Any
line which does not start with `@param` will be assumed to be part of a
comment.

The `.tmplt` contains the contents of what the output from the template
should look like.  However, expressions can be evaluated in the `.tmplt` file
by using the syntax `{{expr}}`.  So, for instance, if your `.tspec` file was:

	@param title The title for a button

And your `.tmplt` file was:

	<button>{{title}}</button>

Then runing the template with the parameter `Click Me` would result in:

	<button>Click Me</button>

However, expressions can be more complicated.  For instance, if you had a
`.tspec` file:

	@param {int} x
	@param {int} y

And a `.tmplt` file:

	<p>Did you know that {{x}} + {{y}} = {{x+y}}?</p>

Then if you used the parameters `1` and `2` you would get:

	<p>Did you know that 1 + 2 = 3?</p>

Not that it was important to specify the types for `x` and `y`, or else the
java output would have thrown an error (since you can't add variables of type
`Object`).  You can also use ternary statements.  For instance, if you had a
`.tspec` file:

	@param {boolean} loggedIn

And a `.tmplt` file:

	<a>
		{{ (loggedIn ? }}
			Sign In
		{{ : }}
			Sign Out
		{{ ) }}
	</a>

Note the use of parentheses.  If you didn't use parentheses, the templating
engine would have tried to evaluate `loggedIn ?` or `:` on their own without
the larger context that they are part of one, continuous, larger expression.
This would have thrown an error.

Example of what happens to the files /webprojects/
-----------------------------------------------------------------------------

Suppose the contents were as follows:

	app/
	|
	|--	index.html
	|
	|--	_js/
	|------	mvc.js
	|------	sockets.js
	|
	|--	_style/
	|------	layout.scss
	|------	shapes.scss
	|------	_web/
	|---------	colors.scss
	|------	_iOS/
	|----------	colors.scss
	|
	|-- _img/
	|------	logo.png
	|------	pic.png
	|
	|--	_ignore/
	|------	more_mixins.scss
	|
	|--	_templates/
	|------	t.tmplt
	|------	t.tspec
	

	website/
	|
	|--	index.html
	|
	|--	_js/
	|------	popup.js
	|
	|--	_style/
	|------	layout.scss
	|
	|-- _img/
	|------	logo.png
	|
	|--	faq/
	|------	index.html
	|------	_style/
	|----------	list.scss

	shared/
	|
	|--	_js/
	|------	json2.js
	|------	util.js
	|------	functional.js

	_ignore/
	|
	|--	mixins.scss

	_raw/
	|
	|--	js/
	|------	jquery-1.10.2.min.js
	|------	jquery-1.10.2.min.map
	|------	jquery-2.0.3.min.js
	|------	jquery-2.0.3.min.map
	|------	helvetica.js
	|
	|--	fonts/
	|------	helvetica.woff

And lets suppose the contents of `build-vars.csv` is as follows:

	_JQUERY_MODERN_VERSION__, 2.0.3
	_JQUERY_OLD_IE_VERSION__, 1.10.2

And the contents of `app-web-projects.csv` is as follows:

	app

This would compile to the following within the server's `war/` folder:

	app.html

	website.html
	website/
	|--	faq.html

	js/
	|--	app.js
	|--	website.js
	|-- shared.js
	|--	jquery-1.10.2.min.js
	|--	jquery-1.10.2.min.map
	|--	jquery-2.0.3.min.js
	|--	jquery-2.0.3.min.map
	|--	helvetica.js

	css/
	|--	app.css
	|--	website.css
	|--	website/
	|------	faq.css

	img/
	|-- app/
	|------	logo.png
	|------	pic.png
	|-- website/
	|------logo.png

	fonts/
	|--	helvetica.js

The following steps would be done to create these files:

1.	All the files in the `war/` directory not listed in
	`/server/protected_war.csv`
2.	The children of any `_raw` folder would be copied directly over
3.	The remaining files would app be copied to a temporary directory to avoid
	altering the origonals
4.	In the temporary files, the builder would search for the strings
	`{{_JQUERY_MODERN_VERSION__}}` and `{{_JQUERY_OLD_IE_VERSION__}}` and
	replace them with `2.0.3` and `1.10.2` (respectively).
5.	SASS would be run over the files in the `_style` folders and the results
	will be placed into corresponding `_css` folders.
6.	The contents of `app/_templates` would be compiled and the results placed
	into `app/_js`
7.	The final files would be created by merging files according to the
	following rules:

		app/index.html					=>	app.html
		website/faq/index.html			=>	website/faq.html
		app/_js/*						=>	js/app.js
		app/_css/* | app/_css/_web/*	=>	css/app.css
		website/_js/*					=>	js/website.js
		website/_css/*					=>	css/website.css
		website/faq/_css/*				=>	css/website/faq.css

	Note that the `*` refers only to direct children.
8.	The temporary directory would be deleted.

In the `www/` of the smartphone apps, you'd find the following:

	app.html

	js/
	|--	app.js
	|-- shared.js
	|--	jquery-1.10.2.min.js
	|--	jquery-1.10.2.min.map
	|--	jquery-2.0.3.min.js
	|--	jquery-2.0.3.min.map
	|--	helvetica.js

	css/
	|--	app.css

	img/
	|-- app/
	|------	logo.png
	|------	pic.png

	fonts/
	|--	helvetica.js

Essentially the same process would be used to create the files here as would
have been used to create those in the war/ folder, but in step (1) all files
would be deleted and in step (7) instead of the files `app/_css/_web/*` being
merged into `css/app.css`, the iPhone's version of the file would have files
`app/_css/_iOS/*` merged into it, and other smartphone apps would have
nothing merged into them.

Compiling run-time templates for the server
-----------------------------------------------------------------------------

Templates described in `server/templates/` are placed in the `templates`
package.  Templates which are in sub folders are put into corresponding
subpackages.

Templates described in `webprojects/` are placed in the `templates.web`
package.  Subpackages are used for templates of specific website.

So, if `server/templates/` had the content:

	a.tmplt
	a.tspec
	f/
		b.tmplt
		b.tspec

And `webprojects/` had:

	_templates/
		c.tmplt
		c.spec
	app/
		_templates/
			d.tmplt
			d.tspec
	website/
		_templates/
			e.tmplt
			e.tspec
		faq/
			_templates/
				f.tmplt
				f.tspec

The result would be the following java files:

	templates.a
	templates.f.b
	templates.web.c
	templates.web.app.d
	templates.web.website.e
	templates.web.websute.faq.f
