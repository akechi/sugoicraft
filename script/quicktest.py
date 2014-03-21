#!/usr/bin/python
"""Setup Sugoicraft at local."""

TESTDIR = './test_server'
IVY2_CACHE = '~/.ivy2/cache'

import os
import glob
import shutil
import subprocess

def get_git_root():
    """Returns git root directory for Sugoicraft repository."""
    pwd = os.getcwd()
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    try:
        out = subprocess.check_output(['git', 'rev-parse', '--show-toplevel']).split('\n')[0]
        os.chdir(pwd)
        return out
    except Exception, err:
        os.chdir(pwd)
        raise e
    

def run(test_dir, ivy2_cache):
    """Copies files to test directory and run application."""
    # cd git root
    os.chdir(get_git_root())
    # expand ~/
    test_dir = os.path.expanduser(test_dir)
    ivy2_cache = os.path.expanduser(ivy2_cache)
    # build jar file path
    app_jar = glob.glob('./target/scala-*/sugoicraft*')[-1]

    # init testdir
    if not os.path.exists(test_dir + '/plugins'):
        os.makedirs(test_dir + '/plugins')
    shutil.copyfile(app_jar, test_dir + '/plugins/' + os.path.basename(app_jar))

    # find bukkit and scala, then run server
    if not os.path.exists(test_dir + '/craftbukkit.jar'):
        bukkit_jar_path = glob.glob(ivy2_cache + '/org.bukkit/bukkit/jars/bukkit*')[0]
        shutil.copyfile(bukkit_jar_path, test_dir + '/craftbukkit.jar')
    if not os.path.exists(test_dir + '/scala-library.jar'):
        scala_jar_path = glob.glob(ivy2_cache + '/org.scala-lang/scala-library/jars/scala-library*')[0]
        shutil.copyfile(scala_jar_path, test_dir + '/scala-library.jar')
    cmd = 'java -Xmx3072M -cp "%s:%s" org.bukkit.craftbukkit.Main -o true' % ('craftbukkit.jar', 'scala-library.jar')
    print cmd
    os.chdir(test_dir)
    subprocess.call(cmd, shell=True)

if __name__ == '__main__':
    import sys
    if len(sys.argv) == 2:
        run(TESTDIR, sys.argv[1])
    else:
        run(TESTDIR, IVY2_CACHE)
