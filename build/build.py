import os, shutil, errno, re, csv, getopt, sys, subprocess

#############################################################################
# FILE UTILS
#############################################################################

# The functions here serve as an abstraction over the various ways to
# interact with the file system (os, shutil, open).  All paths passed into
# any of these functions should be specified relative to the root directory
# of the project.  The paths should be formated as a unix path

def expandPath(path):
    return os.path.relpath(os.path.join(os.path.dirname(__file__),"..",path))

def ls(path):
    return os.listdir(expandPath(path))

def mkdir(path):
    return os.mkdir(expandPath(path))

def isdir(path):
    return os.path.isdir(expandPath(path))

def cp_r(src, dest):
    src = expandPath(src)
    dest = expandPath(dest)
    if os.path.isdir(src):
        shutil.copytree(src, dest)
    else:
        shutil.copy(src, dest)

def rm_r(path):
    path = expandPath(path)
    if os.path.isdir(path):
        return shutil.rmtree(path)
    else:
        return os.remove(path)

def exists(path):
    return os.path.exists(expandPath(path));

def readfile(path):
    return open(expandPath(path), "r")

def writefile(path):
    return open(expandPath(path), "w")

def appendfile(path):
    return open(expandPath(path), "a")

def sass(src, dest):
    return subprocess.call(["sass", "--update",
                            expandPath(src)+":"+expandPath(dest)])

def compressJS(path):
    path = expandPath(path)
    proc = subprocess.Popen(["uglifyjs", path, "-o", path, "-c", "-m",
                "--define", "DEBUG=false"], stderr=subprocess.PIPE);
    ignoreNext = False;
    for line in proc.stderr:
        if line.strip() == "WARN: Condition always false [null:1,6]":
            ignoreNext = True;
        elif ignoreNext:
            ignoreNext = False;
        else:
            sys.stderr.write(line);
    return proc.returncode;

#############################################################################
# Helper Methods
#############################################################################

javaTmpltDir = "server/Checkout Express/src/templates" # This is ugly. w/e...
tmpltExt = ".tmplt"
tspecExt = ".tspec"
tmpFolder = ".__TEMP_"
jsFolder = "_js"
styleFolder = "_style"
cssFolder = "_css"
rawFolder = "_raw"
templateFolder = "_template"
debugFolder = "_debug"
ignoreFolders = set(["_ignore", "_skip", styleFolder, templateFolder])
mergeFolders = set([jsFolder, cssFolder])
noTemplating = set([rawFolder, "_img"]);
indexHTML = "index.html"
orderFile = "_order"
uSet = type('', (), dict(__contains__ = lambda _,x: True))()

""" Makes the web.xml file for the server

    @param src The CSV file which describes the servlet mapping
    @param dest The web.xml file to overwrite (inside the WEB-INF folder)
"""
def makeWebXML(src, dest):
    print "Building web.xml..."
    infil = readfile(src);
    outfl = writefile(dest);

    outfl.write("<?xml version=\"1.0\" encoding=\"UTF-8\" "+
                "standalone=\"no\"?><web-app "+
                "xmlns=\"http://java.sun.com/xml/ns/javaee\" "+
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
                "version=\"2.5\" "+
                "xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee"+
                "                "+
                "http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\">\n" +
                "\n" +
                "\t<!-- Custom MIME types -->\n" +
                "\t<mime-mapping>\n" +
                "\t\t<extension>woff</extension>\n" +
                "\t\t<mime-type>font/opentype</mime-type>\n" +
                "\t</mime-mapping>\n" +
                "\n" +
                "\t<!-- Google Endpoint API Stuff -->\n" +
                "\t<servlet>\n" +
                "\t\t<servlet-name>SystemServiceServlet</servlet-name>\n" +
                "\t\t<servlet-class>" +
                    "com.google.api.server.spi.SystemServiceServlet" +
                "</servlet-class>\n" +
                "\t\t<init-param>\n" +
                "\t\t\t<param-name>services</param-name>\n" +
                "\t\t\t<param-value/>\n" +
                "\t\t</init-param>\n" +
                "\t</servlet>\n" +
                "\t<servlet-mapping>\n" +
                "\t\t<servlet-name>SystemServiceServlet</servlet-name>\n" +
                "\t\t<url-pattern>/_ah/spi/*</url-pattern>\n" +
                "\t</servlet-mapping>\n" +
                "\n" +
                "\t<!-- - - - - - - - - - - - - - - - - - -->\n" +
                "\t<!--        Servlets (mostly)          -->\n" +
                "\t<!-- - - - - - - - - - - - - - - - - - -->\n\n");

    prefix = "servlets"
    usedServlets = set([]);
    for line in infil:
        if "," in line:
            elems = line.split(",")
            assert(len(elems) > 1);
            fname = elems[0]
            if fname.startswith("."):
                fname = prefix + fname
            url = elems[1].strip().rstrip("/");
            isJSP = "JSP" in fname.upper();
            tagName = "jsp-file" if isJSP else "servlet-class";
            servlet = None;
            if len(elems) < 3:
                if isJSP:
                    servlet = re.sub(r'^.*\/(.*)\.jsp$', r'\1Servlet', fname)
                else:
                    servlet = re.sub(r'^.*\.(.*)$', r'\1', fname);
                servlet = servlet[0].lower() + servlet[1:];
                if servlet in usedServlets:
                    i = 0;
                    while (servlet+"_"+str(i)) in usedServlets:
                        i += 1;
                    servlet += "_"+str(i);
                usedServlets.add(servlet);
            else:
                servlet = elems[2].strip();
            outfl.write("\t<servlet>\n" +
                        "\t\t<servlet-name>"+servlet+"</servlet-name>\n" +
                        "\t\t<"+tagName+">"+fname+"</"+tagName+">\n" +
                        "\t</servlet>\n" +
                        "\t<servlet-mapping>\n" +
                        "\t\t<servlet-name>"+servlet+"</servlet-name>\n" +
                        "\t\t<url-pattern>"+url+"</url-pattern>\n" +
                        "\t</servlet-mapping>\n" +
                        "\t<servlet-mapping>\n" +
                        "\t\t<servlet-name>"+servlet+"</servlet-name>\n" +
                        "\t\t<url-pattern>"+url+"/</url-pattern>\n" +
                        "\t</servlet-mapping>\n");
        else:
            line = line.strip()
            if line.startswith("WELCOME:"):
                line = line[8:].strip();
                outfl.write("\t<!-- Default page to serve -->\n" +
                            "\t<welcome-file-list>\n" +
                            "\t\t<welcome-file>"+line+"</welcome-file>\n" +
                            "\t</welcome-file-list>\n" +
                            "\n")
            elif len(line) > 0:
                outfl.write("\n\t<!-- "+line+" -->\n");
                prefix = "servlets."+ re.sub(r' ', r'_',
                        re.sub(r'\s*-\s+', r'.', line.lower()))
    outfl.write("</web-app>")
    
    outfl.close();
    infil.close();

""" Gets the map for build-time templating

    @param path The CSV file defining the substitutions
    @return A 2-tuple of lists of strings, first keys then values
"""
def loadBuildTemplate(path):
    infil = readfile(path);
    keys = [];
    vals = [];
    for row in csv.reader(infil):
        keys.append(row[0]);
        vals.append(row[1]);
    infil.close();
    return keys, vals;

""" Runs build-time templating on a file

    Also replaces any "\r\n" with "\n".  Those "\r"'s were wreaking havok on
    everything outside the "_raw" folder (because of the mixed formatting),
    and this is as good a time as any to remove them

    @param path The file to run templating over
    @param buildTemplate A 2-tuple string lists, first keys then values
"""
def runBuildTemplate(path, buildTemplate):
    keys, vals = buildTemplate;
    infil = readfile(path);
    content = infil.read();
    infil.close();
    content = content.replace("\r\n", "\n");
    # The following loop takes time proportional to the number of keys.  This
    # could be made more efficent by searching for r'{{.*?}}' and seeing if
    # any key was matched
    for i in xrange(0, len(keys)):
        content = content.replace("{{"+keys[i]+"}}", vals[i])
    outfil = writefile(path);
    outfil.write(content);
    outfil.close();

""" Makes sense of the raw template files

    @param src Path of the template file pair (file extention not included)
    @return A 3-tuple containing the param types, param names, and content to
            return (in that order).  Param types default to None
"""
def compileTemplateInner(src):
        print "Compiling template \""+src+"\"..."
        assert(exists(src+tmpltExt))
        assert(exists(src+tspecExt))
        sfil = readfile(src+tspecExt);
        params = [];
        types = [];
        for line in sfil:
            line.strip();
            if line.startswith("@param"):
                tkns = line[6:].split(None, 2);
                #Yeah yeah, the following has a bit of code duplication. Deal
                if len(tkns) == 1:
                    params.append(tkns[0]);
                    types.append(None);
                elif len(tkns) > 1:
                    if tkns[0].startswith("{") and tkns[0].endswith("}"):
                        params.append(tkns[1]);
                        types.append(tkns[0][1:len(tkns[0])-1]);
                    elif tkns[1].startswith("{") and tkns[1].endswith("}"):
                        params.append(tkns[0]);
                        types.append(tkns[1][1:len(tkns[1])-1]);
                    else:
                        params.append(tkns[0]);
                        types.append(None);
        sfil.close();

        content = "";
        tfil = readfile(src+tmpltExt);
        tmplt = tfil.read().rstrip();
        tfil.close();
        tokens = re.split(r'(?:\{\{|\}\})', tmplt);
        for i in xrange(0, len(tokens)):
            token = tokens[i];
            if i % 2 == 0:
                content +=  "\""+token.replace( "\\", "\\\\"
                                    ).replace(  "\t", "\\t"
                                    ).replace(  "\r", ""
                                    ).replace(  "\f", "\\f"
                                    ).replace(  "\'", "\\\'"
                                    ).replace(  "\"", "\\\""
                                    ).replace(  "\n", "\"+\n\t\t\t\"")+"\"";
            else:
                token = token.strip();
                char = token[len(token)-1];
                if (char != "?" and char != ":" and
                                    token.count("(") == token.count(")")):
                    token = "("+token+")";
                if char != "?" and char != ":" and char != "(":
                    token = token+"+";
                if '\n' in token:
                    token="\n\t\t\t\t"+re.sub(r'\s+',' ',token)+"\n\t\t\t";
                char = token[0];
                if char != "?" and char != ":" and char != ")":
                    token = "+"+token;
                content += token;
        content = re.sub(r'([^\\])""\s*\+', r'\1', content);
        return types, params, content;


""" Makes a java file out of a template file pair

    @param src Path of the template file pair (file extention not included)
    @param package  The package for the template, formatted as a list of
                    strings.  The package "templates" is implied
"""
def compileTemplateToJava(src, package):
    types, params, content = compileTemplateInner(src);
    className = src[src.rfind("/")+1:];
    className = className[0].upper() + className[1:];
    packagePath = javaTmpltDir;
    packageName = "templates";
    if not exists(packagePath):
        mkdir(packagePath);
    for pack in package:
        packagePath += "/"+pack;
        packageName += "."+pack;
        if not exists(packagePath):
            mkdir(packagePath);

    outfil = writefile(packagePath + "/" + className + ".java");
    outfil.write(   "package "+packageName+";\n\n"+
                    "public class "+className+" {\n"+
                    "\tpublic static String run(");
    for i in xrange(0, len(types)):
        if i > 0:
            outfil.write(", ");
        outfil.write(("Object" if types[i] == None else types[i]) + " " +
                    params[i]);
    outfil.write(") {\n\t\treturn\t"+content+";\n\t}\n}");
    outfil.close();
    

""" Compiles the templates which are only used server side

    @param src The folder containing the templates
    @param buildTemplate A 2-tuple string lists, first keys then values
    @param package  The package for the templates, formatted as a list of
                    strings.  The package "templates" is implied
"""
def compileServerOnlyTemplates(src, buildTemplate, package=[]):
    tmpPath = src+"/"+tmpFolder;
    if not exists(tmpPath):
        mkdir(tmpPath);
    for fil in ls(src):
        path = src+"/"+fil;
        if isdir(path) and fil != tmpFolder:
            compileServerOnlyTemplates(path, buildTemplate, package+[fil]);
        elif fil.endswith(tmpltExt):
            fil = fil[:len(fil)-len(tmpltExt)];
            assert(exists(src+"/"+fil+".tspec"));
            tmpTmplt = tmpPath+"/"+fil+".tmplt"; 
            tmpTspec = tmpPath+"/"+fil+".tspec"; 
            cp_r(src+"/"+fil+".tmplt", tmpTmplt);
            cp_r(src+"/"+fil+".tspec", tmpTspec);
            runBuildTemplate(tmpTmplt, buildTemplate);
            runBuildTemplate(tmpTspec, buildTemplate);
            compileTemplateToJava(tmpPath+"/"+fil, package);
    rm_r(tmpPath);

""" Comples a template into a JS file

    @param src Path of the template file pair (file extention not included)
    @param dest The place to put the resulting 
    @param package  The package for the templates, formatted as a list of
                    strings.  The package "templates" is implied
"""
def compileTemplateToJS(src, dest, package=[]):
    types, params, content = compileTemplateInner(src);
    funName = src[src.rfind("/")+1:];
    funName = funName[0].lower() + funName[1:];
    outfil = writefile(dest);
    outfil.write("var templates = templates || {};\n")
    packageName = "templates";
    for pack in package:
        packageName += "."+pack;
        outfil.write(packageName + " = " + packageName + " || {};\n");
    outfil.write(   packageName+"."+funName+" = function(" +
                    ", ".join(params) + ") {\n" +
                    "\treturn\t"+content+";\n};")
    outfil.close();

""" Compiles templates into both JS and Java

    @param path The folder containing the templates to compile
    @param parentsOfT   Folders which are parents of the "_templates" folder,
                        leading back to the root of the compilation
    @param parentsInT   A list of parent folders (including the current
                        folder), leading back to the "_templates" folder
"""
def compileWebTemplates(path, parentsOfT, parentsInT=[]):
    jsPath = path + "/.."*len(parentsInT) + "/../" + jsFolder;
    if not exists(jsPath):
        mkdir(jsPath);
    for fil in ls(path):
        if isdir(path+"/"+fil):
            compileWebTemplates(path+"/"+fil, parentsOfT, parentsInT+[fil]);
        elif fil.endswith(tmpltExt):
            fil = fil[:len(fil)-len(tmpltExt)];
            compileTemplateToJava(path+"/"+fil,["web"]+parentsOfT+parentsInT)
            compileTemplateToJS(path+"/"+fil, jsPath + "/templates." +
                ".".join(parentsInT) + ("." if len(parentsInT) > 0 else "") +
                fil + ".js", parentsInT);

""" Compiles the contents of the folder

    In more detail, the following is done:
        build-time templating
        renaming any index.html files and placing them thier "_raw" folder
        sass -> css compilation
        template compilation

    @param path The path of the folder to run preprocessing over
    @param buildTemplate A 2-tuple of lists of strings, first keys then values
    @param parents  A list of parent folders (including the current folder),
                    leading back to the root of the compilation
"""
def compileFolder(path, buildTemplate, parents=[]):
    for fil in ls(path):
        fpath = path+"/"+fil;
        if isdir(fpath):
            if not fil in noTemplating:
                compileFolder(fpath, buildTemplate, parents+[fil]);
        else:
            runBuildTemplate(fpath, buildTemplate);
    if isdir(path+"/"+styleFolder):
        sass(path+"/"+styleFolder, path+"/"+cssFolder);
    if isdir(path+"/"+templateFolder):
        compileWebTemplates(path+"/"+templateFolder, parents);

""" Deletes the contents of a folder

    @param path The folder to delete the contents of
    @param protectedList    A CSV file describing what not to delete.  If
                            None, everything is deleted
"""
def clearFolder(path, protectedList=None):
    protected = set(['.gitignore']);
    if protectedList != None:
        infil = readfile(protectedList);
        for row in csv.reader(infil):
            protected |= set(row);
        infil.close()
    for fil in ls(path):
        if not fil in protected:
            rm_r(path+"/"+fil);

""" Merges some files and transfers them to their final destination

    Also compresses JS files unless --debug

    @param src The folder with the files to merge and transfer
    @param dest The root of the folder which 
    @param plat The platform which the resulting files will run on
    @param debug Whether or not the --debug flag was specified
    @param parents  A list of parent folders, leading back to the root of the
                    transfer
    @param merge    Whether or not the files in the folder should be merged
                    into a single file
"""
def transferLeaf(src, dest, plat, debug, parents, merge):
    if merge:
        assert(len(parents) > 0);
    isDir = isdir(src)
    ext = src[src.rfind("/_")+2:] if isDir else src[src.rfind(".")+1:];
    outPath = dest+("/"+ext if isDir else "");
    for parent in parents:
        if not exists(outPath):
            mkdir(outPath);
        outPath += "/" + parent;
    if not merge and not exists(outPath):
        mkdir(outPath);

    if isDir:
        # Get list of files to transfer 
        baseFolders = ["",  "/_"+plat] + (["/"+debugFolder,
                            "/_"+plat+"/"+debugFolder] if debug else []);

        # Actually transfer
        if merge:
            ofpath = outPath+"."+ext;
            outfil = writefile(ofpath);
            i = 0;
            while i < len(baseFolders):
                baseFolder = src+baseFolders[i];
                if exists(baseFolder):
                    fils = ls(baseFolder);
                    oFil = baseFolder+"/"+orderFile;
                    if exists(oFil):
                        oFilReader = readfile(oFil);
                        o = oFilReader.read().strip().split();
                        oFilReader.close();
                        fils = o+list(set(fils).difference(o));
                    for fil in fils:
                        fname = baseFolder+"/"+fil;
                        if isdir(fname):
                            if fil[0] != '_':
                                baseFolders.insert(i+1, fname[len(src):]);
                        elif fil.endswith(ext):
                            infil = readfile(fname);
                            outfil.write(infil.read()+"\n");
                            infil.close()
                i += 1;
            outfil.close();
            if not debug and ext == "js":
                print "\tCompressing \""+ofpath+"\"..."
                compressJS(ofpath);
        else:
            for baseFolder in baseFolders:
                baseFolder = src+baseFolder;
                if exists(baseFolder):
                    for fil in ls(baseFolder):
                        fname = baseFolder+"/"+fil;
                        if fil[0] != '_' or not isdir(fname):
                            cp_r(fname, outPath+"/"+fil);
    else:
        cp_r(src, outPath + ("."+ext if merge else ""));

""" Takes the compiled files and places them in their final directory

    Also compresses JS files unless --debug

    @param src The folder to transfer to the final directory
    @param dest The root of the folder to transfer to the final directory
    @param plat The platform which the resulting files will run on
    @param debug Whether or not the --debug flag was specified
    @param foldersToFollow  A set-like object which specifies if a folder
                            should be recursed upon
    @param parents  A list of parent folders, leading back to the root of the
                    transfer
"""
def transferFiles(src, dest, plat, debug, foldersToFollow, parents=[]):
    if len(parents) == 0:
        print "Transfering files for "+plat+"..."
    for fil in ls(src):
        fpath = src + "/" + fil;
        if isdir(fpath):
            if fil[0] == "_":
                if fil == rawFolder:
                    dpath = dest;
                    for parent in parents:
                        dpath += "/"+parent;
                        if not exists(dpath):
                            mkdir(dpath);
                    for f in ls(fpath):
                        cp_r(fpath+"/"+f, dpath+"/"+f);
                elif not fil in ignoreFolders:
                    transferLeaf(fpath, dest, plat, debug, parents,
                                                        fil in mergeFolders);
            elif fil in foldersToFollow:
                transferFiles(fpath, dest, plat, debug, uSet, parents+[fil])
    htmlPath = src+"/"+indexHTML;
    if exists(htmlPath):
        transferLeaf(htmlPath, dest, plat, debug, parents, True)

#############################################################################
# Executed liness
#############################################################################

debug = len((lambda x: x[0])(getopt.getopt(sys.argv[1:], "", ["debug"]))) > 0
projectsForApp = set([]);
projectsForAppFil = readfile("build/app-web-projects.csv");
for row in csv.reader(projectsForAppFil):
    projectsForApp |= set(row);
projectsForAppFil.close();

makeWebXML("server/servlet-list.csv",
        "server/Checkout Express/war/WEB-INF/web.xml");

buildTemplate = loadBuildTemplate("build/build-vars.csv");
if exists(javaTmpltDir):
    rm_r(javaTmpltDir);
compileServerOnlyTemplates("server/templates", buildTemplate);

platforms = ["web", "iOS"];
platformPaths = ["server/Checkout Express/war", "iOS/Checkout Express/www"];

if exists(tmpFolder):
    rm_r(tmpFolder);
cp_r("webprojects", tmpFolder);
compileFolder(tmpFolder, buildTemplate);
for i in xrange(0, len(platforms)):
    if exists(platformPaths[i]):
        clearFolder(platformPaths[i],
            "server/protected_war.csv" if i == 0 else None)
    else:
        mkdir(platformPaths[i])
    transferFiles(tmpFolder, platformPaths[i], platforms[i], debug,
                    uSet if i == 0 else projectsForApp);

rm_r(tmpFolder);
