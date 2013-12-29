The following commands are supported:

*	`build` - run the buold script
*	`d` - shorthand for "build --debug --local"
*	`help` - get help with this tool
*	`home` - print the path to the root of the project
*	`pwd` - print working directory relative to the project root
*	`untracked` - print untracked files

The actual code of the script is inside the [`cx.py`](cx.py).  The subfolders
of this folder just contain wrappers so that the script can actually be run.
