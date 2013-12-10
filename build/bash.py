import re, os, sys, shutil, subprocess

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

def compressJS(path, native):
    path = expandPath(path)
    proc = subprocess.Popen(["uglifyjs", path, "-o", path, "-c", "-m"
                ]+(["--screw-ie8"] if native else []),
            stderr=subprocess.PIPE);

    ignoreNext = False;
    for line in proc.stderr:
        if re.match(r'WARN: Condition always (?:true|false) \[',
                line.strip()) != None or re.match(
                r'WARN: Dropping side-effect-free statement \[',
                line.strip()) != None:
            ignoreNext = True;
        elif ignoreNext:
            ignoreNext = False;
        else:
            sys.stderr.write(line);
    return proc.returncode;


