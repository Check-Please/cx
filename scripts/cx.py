import sys, os
from subprocess import call

if len(sys.argv) < 2 or len(sys.argv) == 2 and sys.argv[1] == "help":
    print "usage: cx <command> [<args>]"
    print ""
    print "The most commonly used commands are:"
    print "\tbuild - run the buold script"
    print "\tdb - shorthand for \"build --debug --local\""
    print "\thelp - get help with this tool" 
    print "\thome - print the path to the root of the project"
    print "\tpwd - print working directory relative to the project root"
    print "\tuntracked - print untracked files"
else:
    home = os.path.realpath(os.path.join(os.path.dirname(__file__),".."))

    if sys.argv[1] == "build":
        call(["python", os.path.join(home,"build","build.py")]+sys.argv[2:])
    elif sys.argv[1] == "db":
        call(["python", os.path.join(home,"build","build.py"), "-d", "-l"]
                +sys.argv[2:])
    elif sys.argv[1] == "help":
        if sys.argv[2] == "build":
            print "Run the build script.  Pass \"-d\" or \"--debug\" flag to"
            print "run in debug mode (no compression, files in \"debug\""
            print "subfolder included).  Pass \"-l\" or \"--local\" flag to"
            print "run using localhost as the server for native apps."
        elif sys.argv[2] == "help":
            print "Run without extra arguments to get the usage and a list "
            print "of command.  Run with an extra argument to get details "
            print "on that command"
        elif sys.argv[2] == "home":
            print "Print the path of the root of the project"
            print ""
            print "A common command is \"cd `cx home`\" or \"cd $(cx home)\""
        else:
            print "No extra information on command \""+sys.argv[2]+"\""
    elif sys.argv[1] == "home":
        print home
    elif sys.argv[1] == "pwd":
        pwd = os.path.realpath(os.getcwd())
        if len(pwd) < len(home):
            sys.strerr.write("Not currently in the project");
        if len(pwd) == len(home):
            print "/"
        else:
            print pwd[len(home):]
    elif sys.argv[1] == "untracked":
        call(["git", "ls-files", "--other", "--exclude-standard"]);
    else:
        sys.stderr.write("Unknown command \""+sys.argv[1]+"\"\n");
