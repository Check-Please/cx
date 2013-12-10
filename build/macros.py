import re, os
import bash

""" Parses a macro
    @param  body The body content of a macro

    @return If the macro was a constant, then: <the_constant>, None
            Otherwise: None, <the_function_pattern>
"""
def parseMacro(body):
    if re.match(r'\s*\((?:\s*\w+\s*,)*\s*\w+\s*\)\s*->', body) == None:
        return body.strip(), None
    else:
        args, ret = body.split("->", 1);
        args = args.strip();
        args = args[1:len(args)-1];
        args = args.split(",");
        ret = ret.strip();
        for i in xrange(0, len(args)):
            arg = args[i].strip();
            ret = re.sub(re.compile("\\b"+arg+"\\b"), "\\\\"+str(i+1), ret);
        return None, ret;

""" Loads from macros from a file

    @param  path The file to load macros from
    @return A 2-tuple of dictionaries where the first is of constants and the
            second is of functions
"""
def load(path):
    consts = {};
    funs = {};
    name = None;
    body = None;
    for line in bash.readfile(path):
        if re.match(r'\s*{{\s*\w+\s*}}\s*=', line) == None:
            if body != None:
                body += line;
        else:
            if name != None:
                const, fun = parseMacro(body)
                if const != None:
                    consts[name] = const;
                if fun != None:
                    funs[name] = fun;
            name, body = line.split("=", 1);
            name = name.strip();
            name = name[2:len(name)-2].strip();
    if name != None:
        const, fun = parseMacro(body)
        if const != None:
            consts[name] = const;
        if fun != None:
            funs[name] = fun;
    return consts, funs;

""" Runs some macros over some files.  The macros are described in README.md

    @param path The path of the files to run macros on
    @param consts A dictionary from constant names to values
    @param funs A dictionary from function names to regex
"""
def run(path, consts, funs):
    if bash.isdir(path):
        for fil in ls(path):
            doMacros(path+"/"+fil, consts, funs);
    else:
        content = runOnFile(path, consts, funs);
        outfil = bash.writefile(path);
        outfil.write(content);
        outfil.close();

""" Runs some macros over a file and returns the contents

    @param  path The path of the files to run macros on
    @param  consts A dictionary from constant names to values
    @param  funs A dictionary from function names to regex
    @return The macro'd contents
"""
def runOnFile(path, consts, funs):
    infil = bash.readfile(path);
    content = "";
    failedIf = False;
    consts['FILE_NAME'] = os.path.basename(path);
    lNum = 1;
    for line in infil:
        l = line.strip();
        if l == "END_IF":
            failedIf = False;
        elif re.match(r'^IF(?:_NOT)?_[A-Z]+$', l) != None:
            neg = re.match(r'^IF_NOT_', l) != None;
            var = re.sub(r'IF(?:_NOT)_', "", l);
            val =  var in consts and consts[var] == "true";
            failedIf = val == neg;
        elif not failedIf:
            content += runOnText(line, consts, funs, lNum); 
        lNum += 1;
    infil.close();

    return runOnText(content, consts, funs);

""" Runs some macros over a some string and returns the modified version

    @param  text The text to work on
    @param  path The path of the files to run macros on
    @param  consts A dictionary from constant names to values
    @param  lNum The line number of the text.
"""
def runOnText(text, consts, funs, lNum=None):
    lNum = -1 if lNum == None else lNum;
    doAnotherPass = True;
    while doAnotherPass:
        doAnotherPass = False;
        i = 0;
        # The following allows me to assign i as part of a condition
        while [i for i in [text.find("{{", i)] if (i != -1)]:
            j = i+1;
            while [j for j in [text.find("}}", j+1)] if (j != -1)]:
                oc = False;
                cc = False;
                nOpen = 0;
                for k in xrange(i, j+2):
                    c = text[k];
                    if c == '{':
                        if oc:
                            nOpen += 1;
                            oc = False;
                        else:
                            oc = True;
                    elif c == '}':
                        if cc:
                            nOpen -= 1;
                            cc = False;
                        else:
                            cc = True;
                    else:
                        oc = False;
                        cc = False;
                if nOpen == 0:
                    break;
            if j != -1:
                cmd = text[i+2:j].strip();
                replaceText = None;
                if cmd in consts:
                    replaceText = consts[cmd];
                elif [k for k in [cmd.find(":")] if (k != -1)]:
                    fun = cmd[:k].strip();
                    if fun == "ESCAPE":
                        replaceText = cmd[k+1:].strip().replace("\\", "\\\\"
                              ).replace("\"", "\\\"").replace("\'", "\\\'");
                    elif fun in funs:
                        args = [];
                        arg = [];
                        dq = False;# Double Quote
                        sq = False;# Single Quote
                        lc = False;# Line comment
                        bc = False;# Block comment
                        op = 0;# Open parenthesss
                        ob = 0;# Open bracket
                        for l in xrange(k+1,len(cmd)):
                            c = cmd[l];
                            if lc:
                                if c == '\n':
                                    lc = False;
                            elif bc:
                                if c == '/' and cmd[l-1] == '*':
                                    bc = False;
                            elif c == '/' and l+1<len(cmd) and cmd[l+1]=='/':
                                lc = True;
                            elif c == '/' and l+1<len(cmd) and cmd[l+1]=='*':
                                bc = True;
                            else:
                                if dq:
                                    if c == "\"" and cmd[l-1] == "\\":
                                        dq = False;
                                elif sq:
                                    if c == "\'" and cmd[l-1] == "\\":
                                        sq = False;
                                elif c == '\"':
                                    dq = True;
                                elif c == '\'':
                                    sq = True;
                                elif c == '(':
                                    op += 1;
                                elif c == ')':
                                    op -= 1;
                                elif c == '[':
                                    ob += 1;
                                elif c == ']':
                                    ob -= 1;
                                if c!=',' or dq or sq or op!=0 or ob!=0:
                                    arg.append(c);
                                else:
                                    args.append(''.join(arg).strip());
                                    arg = [];
                        args.append(''.join(arg).strip());
                        srcRegex = "";
                        for a in args:
                            srcRegex += "(.{"+str(len(a))+"})";
                        replaceText = re.sub(re.compile(srcRegex), funs[fun],
                                            ''.join(args));
                if replaceText != None:
                    text = text[:i]+replaceText+text[j+2:];
                    doAnotherPass = True;
                else:
                    i += 1;
            else:
                i = len(text);
    return re.sub(r'{{\s*LINE_NUM\s*}}', str(lNum), text);
