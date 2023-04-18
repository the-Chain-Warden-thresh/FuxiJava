import json
import sys
import os

def parse(file_location, analyse_token):
    with open(file_location, "r", encoding="utf8") as f:
        res = json.load(f)
        buggy_dependency_list = res["buggy_dependency_list"]
        for buggy_dependency in buggy_dependency_list:
            api_to_cve_dict = {}
            api_to_cve_dict["org.apache.logging.log4j.Logger.info(String,Object,Object,Object)"]="CVE-2021-44832"
            vulnerable_api_call_to_cve_map = buggy_dependency["vulnerable_api_call_to_cve_map"]
            for vulnerable_api_call_to_cve_element in vulnerable_api_call_to_cve_map:
                cve_to_buggy_method_list = vulnerable_api_call_to_cve_element["cve_to_buggy_method"]
                for cve_to_buggy_method in cve_to_buggy_method_list:
                    buggy_method = cve_to_buggy_method["buggy_method"]
                    if buggy_method not in api_to_cve_dict.keys():
                        api_to_cve_dict[buggy_method] = set()
                    cve_list = cve_to_buggy_method["cve_list"]
                    for cve in cve_list:
                        api_to_cve_dict[buggy_method].add("CVE-"+cve)
            vulnerable_method_call_chain_map = buggy_dependency["vulnerable_method_call_chain_map"]
            vulnerable_method_call_chain_list = vulnerable_method_call_chain_map["vulnerable_method_call_chain"]
#             print(api_to_cve_dict)
            for vulnerable_method_call_chain in vulnerable_method_call_chain_list:
                call_chain = vulnerable_method_call_chain["call_chain"]
                if len(call_chain)==0:
                    continue
                new_call_chain = parse_call_chain(call_chain, api_to_cve_dict)
                vulnerable_method_call_chain["call_chain"] = new_call_chain
    temp_list = file_location.split("/")
    temp_list[-1] = "1" + temp_list[-1]
    new_file_location = "/".join(temp_list)
    with open(new_file_location, "w", encoding="utf8") as f:
        res["analyseToken"] = analyse_token
        f.write(json.dumps(res))
    return new_file_location


def parse_call_chain(call_chain, api_to_cve_dict):
    ans = {}
    root_name = call_chain[0][0]
    ans["label"] = root_name
    ans["children"] = []
    for path in call_chain:
        cur = ans["children"]
        for i in range(len(path)):
            if i != 0:
                flag = False
                for child in cur:
                    if path[i] == child["label"]:
                        cur = child["children"]
                        flag = True
                        break
                if not flag:
                    temp = {}
                    temp["label"] = path[i]
                    temp["children"] = []
                    cur.append(temp)
                    cur = temp["children"]
                    if i == len(path) - 1 and path[i] in api_to_cve_dict.keys():
                        temp["label"] += "("
                        temp["label"] += ",".join(api_to_cve_dict[path[i]])
                        temp["label"] += ")"
    return ans


if __name__ == '__main__':
    file_location = sys.argv[1]
    analyse_token = sys.argv[2]
#     for file in os.listdir("test"):
#         print(file)
#         parse("test/"+file,1)
    new_file_location = parse(file_location, analyse_token)
    print(new_file_location)
