import os, shutil, errno, re, csv, getopt, sys, subprocess, urllib2
import bash, macros

javaProjDir = "server/Check Please"; # This is ugly. w/e...
javaTmpltDir = javaProjDir + "/src/templates"; # This is ugly. w/e...
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
webPlat = "web";
localServer = "http://localhost:8888";
webServer = "https://www.getthecheck.com";


""" Makes the web.xml file for the server

    @param src The CSV file which describes the servlet mapping
    @param dest The web.xml file to overwrite (inside the WEB-INF folder)
"""
def makeWebXML(src, dest):
    print "Building web.xml..."
    infil = bash.readfile(src);
    outfl = bash.writefile(dest);

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

""" Makes sense of the raw template files

    @param src Path of the template file pair (file extention not included)
    @return A 3-tuple containing the param types, param names, and content to
            return (in that order).  Param types default to None
"""
def compileTemplateInner(src):
        print "Compiling template \""+src+"\"..."
        assert(bash.exists(src+tmpltExt))
        assert(bash.exists(src+tspecExt))
        sfil = bash.readfile(src+tspecExt);
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
        tfil = bash.readfile(src+tmpltExt);
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
    if not bash.exists(packagePath):
        bash.mkdir(packagePath);
    for pack in package:
        packagePath += "/"+pack;
        packageName += "."+pack;
        if not bash.exists(packagePath):
            bash.mkdir(packagePath);

    outfil = bash.writefile(packagePath + "/" + className + ".java");
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
    @param mVars A dictionary from macro constant names to values
    @param mFuns A dictionary from macro function names to regex
    @param package  The package for the templates, formatted as a list of
                    strings.  The package "templates" is implied
"""
def compileServerOnlyTemplates(src, mVars, mFuns, package=[]):
    if not bash.exists(src):
        bash.mkdir(src);
    tmpPath = src+"/"+tmpFolder;
    if not bash.exists(tmpPath):
        bash.mkdir(tmpPath);
    for fil in bash.ls(src):
        path = src+"/"+fil;
        if bash.isdir(path) and fil != tmpFolder:
            compileServerOnlyTemplates(path, mVars, mFuns, package+[fil]);
        elif fil.endswith(tmpltExt):
            fil = fil[:len(fil)-len(tmpltExt)];
            assert(bash.exists(src+"/"+fil+".tspec"));
            tmpTmplt = tmpPath+"/"+fil+".tmplt"; 
            tmpTspec = tmpPath+"/"+fil+".tspec"; 
            bash.cp_r(src+"/"+fil+".tmplt", tmpTmplt);
            bash.cp_r(src+"/"+fil+".tspec", tmpTspec);
            macros.run(tmpTmplt, mVars, mFuns);
            macros.run(tmpTspec, mVars, mFuns);
            compileTemplateToJava(tmpPath+"/"+fil, package);
    bash.rm_r(tmpPath);

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
    outfil = bash.writefile(dest);
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
    if not bash.exists(jsPath):
        bash.mkdir(jsPath);
    for fil in bash.ls(path):
        if bash.isdir(path+"/"+fil):
            compileWebTemplates(path+"/"+fil, parentsOfT, parentsInT+[fil]);
        elif fil.endswith(tmpltExt):
            fil = fil[:len(fil)-len(tmpltExt)];
            compileTemplateToJava(path+"/"+fil,["web"]+parentsOfT+parentsInT)
            compileTemplateToJS(path+"/"+fil, jsPath + "/templates." +
                ".".join(parentsInT) + ("." if len(parentsInT) > 0 else "") +
                fil + ".js", parentsInT);

""" Compiles the contents of the folder

    In more detail, the following is done:
        renaming any index.html files and placing them thier "_raw" folder
        sass -> css compilation
        template compilation

    @param path The path of the folder to run preprocessing over
    @param parents  A list of parent folders (including the current folder),
                    leading back to the root of the compilation
"""
def compileFolder(path, parents=[]):
    for fil in bash.ls(path):
        fpath = path+"/"+fil;
        if bash.isdir(fpath):
            if not fil in noTemplating:
                compileFolder(fpath, parents+[fil]);
    if bash.isdir(path+"/"+styleFolder):
        bash.cp_r(path+"/"+styleFolder, path+"/"+cssFolder);
        bash.sass(path+"/"+cssFolder, path+"/"+cssFolder);
    if bash.isdir(path+"/"+templateFolder):
        compileWebTemplates(path+"/"+templateFolder, parents);

""" Deletes the contents of a folder

    @param path The folder to delete the contents of
    @param protectedList    A CSV file describing what not to delete.  If
                            None, everything is deleted
"""
def clearFolder(path, protectedList=None):
    protected = set(['.gitignore']);
    if protectedList != None:
        infil = bash.readfile(protectedList);
        for row in csv.reader(infil):
            protected |= set(row);
        infil.close()
    for fil in bash.ls(path):
        if not fil in protected:
            bash.rm_r(path+"/"+fil);

""" Merges some files and transfers them to their final destination

    Also compresses JS files unless --debug

    @param src The folder with the files to merge and transfer
    @param dest The root of the folder which 
    @param plat The platform which the resulting files will run on
    @param debug Whether or not the --debug flag was specified
    @param mVars A dictionary from macro constant names to values
    @param mFuns A dictionary from macro function names to regex
    @param parents  A list of parent folders, leading back to the root of the
                    transfer
    @param merge    Whether or not the files in the folder should be merged
                    into a single file
"""
def transferLeaf(src, dest, plat, debug, mVars, mFuns, parents, merge):
    if merge:
        assert(len(parents) > 0);
    isDir = bash.isdir(src)
    wantsMacros = not (isDir and (os.path.basename(src) in noTemplating));
    ext = src[src.rfind("/_")+2:] if isDir else src[src.rfind(".")+1:];
    outPath = dest+("/"+ext if isDir else "");
    for parent in parents:
        if not bash.exists(outPath):
            bash.mkdir(outPath);
        outPath += "/" + parent;
    if not merge and not bash.exists(outPath):
        bash.mkdir(outPath);

    if isDir:
        # Get list of files to transfer 
        baseFolders = ["",  "/_"+plat] + (["/"+debugFolder,
                            "/_"+plat+"/"+debugFolder] if debug else []);

        # Actually transfer
        if merge:
            ofpath = outPath+"."+ext;
            outfil = bash.writefile(ofpath);
            i = 0;
            while i < len(baseFolders):
                baseFolder = src+baseFolders[i];
                if bash.exists(baseFolder):
                    fils = bash.ls(baseFolder);
                    oFil = baseFolder+"/"+orderFile;
                    if bash.exists(oFil):
                        oFilReader = bash.readfile(oFil);
                        o = oFilReader.read().strip().split();
                        oFilReader.close();
                        fils = o+list(set(fils).difference(o));
                    for fil in fils:
                        fname = baseFolder+"/"+fil;
                        if bash.isdir(fname):
                            if fil[0] != '_':
                                baseFolders.insert(i+1, fname[len(src):]);
                        elif fil.endswith("."+ext):
                            infil = bash.readfile(fname);
                            outfil.write(infil.read()+"\n");
                            infil.close()
                i += 1;
            outfil.close();
            if wantsMacros:
                macros.run(ofpath, mVars, mFuns);
            if ext == "js" and not debug:
                print "\tCompressing \""+ofpath+"\"..."
                bash.compressJS(ofpath, (plat != webPlat));
        else:
            for baseFolder in baseFolders:
                baseFolder = src+baseFolder;
                if bash.exists(baseFolder):
                    for fil in bash.ls(baseFolder):
                        fname = baseFolder+"/"+fil;
                        if fil[0] != '_' or not bash.isdir(fname):
                            oPath = outPath+"/"+fil;
                            bash.cp_r(fname, oPath);
                            if wantsMacros:
                                macros.run(oPath, mVars, mFuns);
    else:
        oPath = outPath + ("."+ext if merge else "");
        bash.cp_r(src, oPath);
        if wantsMacros:
            macros.run(oPath, mVars, mFuns);

""" Takes the compiled files and places them in their final directory

    Also compresses JS files unless --debug

    @param src The folder to transfer to the final directory
    @param dest The root of the folder to transfer to the final directory
    @param plat The platform which the resulting files will run on
    @param debug Whether or not the --debug flag was specified
    @param mVars A dictionary from macro constant names to values
    @param mFuns A dictionary from macro function names to regex
    @param foldersToFollow  A set-like object which specifies if a folder
                            should be recursed upon
    @param parents  A list of parent folders, leading back to the root of the
                    transfer
"""
def transferFls(src,dest,plat,debug,mVars,mFuns,foldersToFollow,parents=[]):
    if len(parents) == 0:
        print "Transfering files for "+plat+"..."
    for fil in bash.ls(src):
        fpath = src + "/" + fil;
        if bash.isdir(fpath):
            if fil[0] == "_":
                if fil == rawFolder:
                    dpath = dest;
                    for parent in parents:
                        dpath += "/"+parent;
                        if not bash.exists(dpath):
                            bash.mkdir(dpath);
                    for f in bash.ls(fpath):
                        bash.cp_r(fpath+"/"+f, dpath+"/"+f);
                elif not fil in ignoreFolders:
                    transferLeaf(fpath, dest, plat, debug, mVars, mFuns,
                                            parents, fil in mergeFolders);
            elif fil in foldersToFollow:
                transferFls(fpath, dest, plat, debug, mVars, mFuns, uSet,
                                                            parents+[fil])
    htmlPath = src+"/"+indexHTML;
    if bash.exists(htmlPath):
        transferLeaf(htmlPath,dest,plat,debug,mVars,mFuns,parents,True);

""" Downloads some resource from the server so they can be used by the native
    app.

    @param  server The server to download from
    @param  folder The folder to add the files to
    @param  path The path to the resource
    @param  preprocess  A function which is run over the contents of the
                        file.  If None/not specified, no preprocessing occurs
"""
def downloadFile(server, folder, path, preprocess=None):
    if preprocess == None:
        preprocess = lambda x: x;

#############################################################################
# Executed liness
#############################################################################

debug = False;
local = False;
server = None;
for (op, val) in getopt.getopt(sys.argv[1:], "lds", ["debug", "local",
                                                            "server"])[0]:
    debug = debug or op == "-d" or op == "--debug";
    local = local or op == "-l" or op == "--local";
    if op == "-s" or op == "--server":
        server = val;
if server == None:
    server = localServer if local else webServer;

projectsForApp = set([]);
projectsForAppFil = bash.readfile("build/app-web-projects.csv");
for row in csv.reader(projectsForAppFil):
    projectsForApp |= set(row);
projectsForAppFil.close();

makeWebXML("server/servlet-list.csv",
        javaProjDir+"/war/WEB-INF/web.xml");

mVars, mFuns = macros.load("build/macros");
mVars['DEBUG'] = "true" if debug else "false";
mVars['LOCAL'] = "true" if local else "false";
if bash.exists(javaTmpltDir):
    bash.rm_r(javaTmpltDir);
compileServerOnlyTemplates("server/templates", mVars, mFuns);

platforms = ["web", "iOS"];
platformPaths = [javaProjDir+"/war", "iOS/Checkout Express/www"];

if bash.exists(tmpFolder):
    bash.rm_r(tmpFolder);
bash.cp_r("webprojects", tmpFolder);
compileFolder(tmpFolder);
for i in xrange(0, len(platforms)):
    if bash.exists(platformPaths[i]):
        clearFolder(platformPaths[i],
            "server/protected_war.csv" if i == 0 else None)
    else:
        bash.mkdir(platformPaths[i])
    plat = platforms[i];
    native = plat != webPlat;
    path = platformPaths[i];
    mVars['PLATFORM'] = plat;
    mVars['NATIVE'] = "true" if native else "false";
    mVars['SERVER'] = server if native else "";
    transferFls(tmpFolder, path, plat, debug, mVars, mFuns,
                                        uSet if i == 0 else projectsForApp);
    # Download channel API
    if native:
        apiPath = "_ah/channel/jsapi";
        bash.mkdir(os.path.join(path, "_ah"));
        bash.mkdir(os.path.join(path, "_ah/channel"));
        content =   urllib2.urlopen(
                        os.path.join(server, apiPath) if local else
                        "https://talkgadget.google.com/talkgadget/channel.js"
                    ).read();
        apiFile = bash.writefile(os.path.join(path, apiPath));
        apiFile.write(re.sub(r'/_ah', os.path.join(server, "_ah"), content));
        apiFile.close();

bash.rm_r(tmpFolder);
