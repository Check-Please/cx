In this README we explain the MVC setup used here.  We use the MVC structure
very losely.

First off, all the models are defined in models.js.  They all have the same
interface, and are really nothing more than a way to store global state and
send notifications to listeners when a value changes.  Read models.js for a
list of models and how to access them.

The views are all defined in the views/ folders.  The views also serve as
controlers to some extent.  The line is simply not clearly drawn between the
two.  More information on the views can be found in views/README.txt

The nav.js folder defines the navigation between the views (as well as some
other details).  At no point does the nav.js file change any of the models,
though it does read from them and listen to them.  Nor does the nav.js file
export any symbols.  All code relating to navigation is totally handled
within the file

The socket.js file handles communications from the server.  Generally all it
does is update the appropriate models.  The only symbols exported are
"socket.open()" and "socket.close()"

The loading.js file displays a loading overlay with a message.  This file
exports only "loading.init()" which initializes the module.  All other
interaction is handled by mutating the "loadMsg" model.

All other files (i.e. device.js, js files imported from other projects) are
simply libraries.
