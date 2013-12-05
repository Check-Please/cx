In this README we explain the setup used here.  In particular, we focus on
how the files interact with the MVC.  We use the MVC structure very losely.

First off, all the models are defined in `models.js`.  They all have the same
interface, and are really nothing more than a way to store global state and
send notifications to listeners when a value changes.  Read `models.js` for a
list of models and how to access them.

The views are all defined in the `views/` folders.  The views also serve as
controlers to some extent.  The line is simply not clearly drawn between the
two.  More information on the views can be found in `views/README.md`

The `nav.js` file hadles all the navigation.  It listens to various models
but rarely changes them.  It also sets up the current view and calls the
views member funtions in order to handle the navigation described in
`views/README.md`.  Any additional navigation should be handled by setting
the hashcode in the URL.  For compatibility with browsers which do not
support `window.onhashchange`, this should be done by setting the `href`
attribute of an `a` tag.  The only symbol exported is `nav.init`.

The `init.js` file loads some information from the server, initializes the
MVC, and initializes the socket module.  No symbols are exported.

The `resize.js` file handles responsiveness, including what is described
in `views/README.md`.  No symbols are exported.

The `socket.js` file handles communications from the server.  Generally all
it does is update the appropriate models.  The only symbols exported are
`socket.init` and `socket.close`

The `loading.js` file displays a loading overlay with a message.  This file
exports only `loading.init` which initializes the module.  All other
interaction is handled by mutating the `loadMsg` model.

All other files (i.e. `device.js`, js files imported from other projects) are
simply libraries.
