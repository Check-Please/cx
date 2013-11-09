import sys, os
from subprocess import call

if len(sys.argv) < 2 or len(sys.argv) == 2 and sys.argv[1] == "help":
    print "usage: cx <command> [<args>]"
    print ""
    print "The most commonly used commands are:"
    print "\tbuild - run the buold script"
    print "\thelp - get help with this tool" 
    print "\thome - go to the root of the project"
    print "\tpwd - print working directory relative to the project root"
else:
    home = os.path.relpath(os.path.join(os.path.dirname(__file__),".."))

    if sys.argv[1] == "build":
        call(["python", os.path.join(home,"build","build.py")]+sys.argv[2:])
    elif sys.argv[1] == "help":
        if sys.argv[2] == "build":
            print "Run the build script.  Pass \"--debug\" flag to avoid js "
            print "compression"
        elif sys.argv[2] == "help":
            print "Run without extra arguments to get the usage and a list "
            print "of command.  Run with an extra argument to get details "
            print "on that command"
        elif sys.argv[2] == "home":
            print "Go to the root of the project"
        elif sys.argv[2] == "pwd":
            print "Print working directory relative to the project root"
        else:
            print "No information on command \""+argv[2]+"\""
    elif sys.argv[1] == "home":
        os.chdir(home);
    elif sys.argv[1] == "pwd":
        pwd = os.path.realpath(os.getcwd())[len(os.path.realpath(home)):]
        if len(pwd) == 0:
            pwd = "/"
        print pwd
    else:
        sys.stderr.write("Unknown command \""+sys.argv[1]+"\"\n");
