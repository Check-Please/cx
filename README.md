Getting Started
===============

The next section ("How to Set Up") tells you how to set up your computer/
directory for doing work.

As for information, you should probably check out the wiki.  These READMEs
are for information you'll want close by while you code.  The wiki is more
designed to bring someone up to speed on how something works in a general
sense in case they've forgotten or are new to the project/team.  It contains
information about the major design decisions and how various pieces of the
software interact.  It also contains non-technical things, like Style Guides.

We also might have a chat room (depending on how far in the future you are).
It will probably be at http://chkex.hipchat.com.  Feel free to ask questions
there.  Helping people is a priority.  If we don't have a chat room, feel
free to get in contact with sjelin directly.  He's friendly.

How to Set Up
=============

1.	Download the following:

	1.	Git
	2.	The lastest JRE and JDK
	3.	Python
	4.	UglifyJS (install node.js, run `npm install uglify-js -g`)
	5.	SASS (install ruby, run `gem install sass`)
	6.	Eclipse (if you need to work on or locally run the server)
	7.	The iPhone SDK (if you are going to work on the iPhone app)
	8.	The Andriod SDK (if you are going to work on the Andriod app)
	9.	Microsoft Visual Studio (if you are going to work on the plugin)

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

License
=======

I mean, the code isn't really valuable without the team.  Honestly this
should probably be a private repo, I'm just cheap.  So you can just assume
that this has as restrictive a license as possible within the confines of
being a public github project.  Also, it probably won't be public for long :(
