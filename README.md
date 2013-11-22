How to set up
===============================

1.	First, download the following:

	1.	The lastest JRE and JDK
	2.	Python
	3.	UglifyJS (install node.js, run `npm install uglify-js -g`)
	4.	SASS (install ruby, run `gem install sass`)
	5.	Eclipse (if you need to work on or locally run the server)
	6.	The iPhone SDK (if you are going to work on the iPhone app)
	7.	The Andriod SDK (if you are going to work on the Andriod app)
	8.	Microsoft Visual Studio (if you are going to work on the plugin)

2.	Run build.py

3.	In order to get eclipse up and running, do the following:

	1.	Open eclipse, and make its workspace `./server`
	2.	Make sure eclipse is using the latest JDK for your JRE
			(Preferences > Java > Installed JREs)
	3.	Install the Google App Engine Plugin
			https://developers.google.com/appengine/docs/java/tools/eclipse
	4.	Import the Checkout Express project from `./server`

	5.	You will be missing some JARs (you'll probably hear the SDK is
		missing).  To fix this, do the following: 

		1.	Make a new temporary App Engine project
				(G > New Web Application Project)
		2.	Copy the `.jar`'s inside the new project's `war/WEB-INF/lib` into
				the corresponding folder in checkout express's project.
		3.	Delete the temporary project

4.	Optionally, you can install our commandline tool `cx`.  This makes
	various common tasks slighly easier (e.g. `cx build`).  You can run
	`cx help` (after installing the script) for details.
	With bash, just add the folder at `./scripts/bash` to your `$PATH`.  Other
	shells are not yet supported, but you can be a hero and add them
	yourself!  The actual work is done in python, you'll just need to write
	a wrapper file

Markdown Style Guide
====================

*	We use GFM (https://help.github.com/articles/github-flavored-markdown)
*	Please use `` ` `` aggressively
*	Please write some sort of spec before writing code
	(http://tom.preston-werner.com/2010/08/23/readme-driven-development.html)
*	Please have fun :D
