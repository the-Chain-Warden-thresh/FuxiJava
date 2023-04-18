import sys
import os

if __name__ == '__main__':
    dir = sys.argv[1]
    token = sys.argv[2]
    split_list = dir.split("/")
    os.makedirs(dir)
    username = split_list[-3]
    repo_name = split_list[-2]
    # https://github.com/jxfzzzt/testVul.git
    os.chdir("/".join(split_list[:-2]))
    os.system("rm -rf " + repo_name)
    os.system("git clone --depth 1 --recurse-submodules https://"+username+":"+token+"@hub.yzuu.cf/" + username + "/" + repo_name + ".git")
    os.chdir(dir)
    os.system("mvn clean install skip test skip javadoc")
