Table of contents:

*	[Getting Started](#getting-started)
*	[How to Set Up](#how-to-set-up)  (i.e. installation guide)
*	[Layout of Project](#layout-of-project) (i.e. what folder contains what)
*	[License](#license)

Getting Started
===============

The [next section](how-to-set-up) tells you how to set up your computer/
directory for doing work.  If you are new and need some guidance, check out
the [Getting Started](../../wiki/Getting-Started) page on the wiki.

How to Set Up
=============

1.	Download the following:

	1.	Git
	2.	The lastest JRE and JDK
	3.	Python
	4.	node.js
	5.	UglifyJS (`sudo npm install uglify-js -g`)
	6.	jQuery's node package (`npm install jquery`)
	7.	jsdom node package (`npm install jsdom`)
	8.	Mocha (`sudo npm install mocha -g`)
	9.	SASS (install ruby, run `gem install sass`)
	10.	Eclipse (if you need to work on or locally run the server)
	11.	The iPhone SDK (if you are going to work on the iPhone app)
	12.	The Andriod SDK (if you are going to work on the Andriod app)
	13.	Microsoft Visual Studio (if you are going to work on the plugin)

3.	Clone this project to your machine

2.	Run build.py

3.	In order to get eclipse up and running, do the following:

	1.	Open eclipse, and make its workspace `./server`
	2.	Make sure eclipse is using the latest JDK for your JRE
			(Preferences > Java > Installed JREs)
	3.	Install the Google App Engine Plugin
			(https://developers.google.com/appengine/docs/java/tools/eclipse)
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
	With bash, just add the folder at `./scripts/bash` to your `$PATH`. Other
	shells are not yet supported, but you can be a hero and add them
	yourself!  The actual work is done in python, you'll just need to write
	a wrapper file

Layout of Project
=================

You'll find the following files/subfolders in this folder:

*	`LICENSE` - The license file
*	`README.md` - This readme.
*	`build` - The folder containing the build code
*	`iOS` - The folder containing the iOS projects
*	`server` - The folder containing server code
*	`webprojects` - The folder containing web code
*	`scripts` - The folder containing internal scripts
*	`specs` - The folder containing some specs 
*	`misc` - The folder containing other stuff

License
=======

Copyright (C) 2013 Martin Jelin.  See the LICENSE file for license rights and
limitations. 
