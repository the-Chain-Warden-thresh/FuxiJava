import sqlite3
import json
import sys
import datetime


def parse(file_location, db_location):
    conn = sqlite3.connect(db_location)
    cursor = conn.cursor()
    cache_dict = {}
    root = {}
    root["id"] = 1
    id_cnt = 2
    root["children"] = []
    root["direct_vulns"] = []
    with open(file_location, "r", encoding="utf8") as f:
        raw_dict = json.load(f)
        for module in raw_dict.keys():
            chain_list = raw_dict[module]
            for chain in chain_list:
                cur = root["children"]
                for token in chain:
                    gav_info = token.split("__fdse__")
                    group_id = gav_info[0]
                    artifact_id = gav_info[1]
                    version = gav_info[3]
                    gav = group_id + "/" + artifact_id + "/" + version + ".jar"
                    if token not in cache_dict.keys():
                        cache_dict[token] = []
                        sqlstr = "Select * from group_artifact_version_cve where group_id = '" + group_id.__str__() + "' and artifact_id = '" + artifact_id.__str__() + "' and version = '" + version.__str__() + "'"
                        res = list(cursor.execute(sqlstr))
                        for line in res:
                            cache_dict[token].append("CVE-" + line[4])
                    flag = False
                    for child in cur:
                        if child["label"] == gav:
                            flag = True
                            cur = child["children"]
                            break
                    if not flag:
                        add_gav = {}
                        add_gav["label"] = gav
                        add_gav["id"] = id_cnt.__str__()
                        id_cnt += 1
                        add_gav["direct_vulns"] = cache_dict[token]
                        add_gav["children"] = []
                        cur.append(add_gav)
                        cur = add_gav["children"]
    generate_res(root)
    return root


def parse_complete(file_location, res):
    root = {}
    root["children"] = []
    root["id"] = 1
    id_cnt = 2
    with open(file_location, "r", encoding="utf8") as f:
        raw_dict = json.load(f)
        for module in raw_dict.keys():
            chain_list = raw_dict[module]
            for chain in chain_list:
                cur = root["children"]
                for token in chain:
                    gav_info = token.split("__fdse__")
                    group_id = gav_info[0]
                    artifact_id = gav_info[1]
                    version = gav_info[3]
                    gav = group_id + "/" + artifact_id + "/" + version + ".jar"
                    flag = False
                    for child in cur:
                        if child["label"] == gav:
                            flag = True
                            cur = child["children"]
                    if not flag:
                        add_gav = {}
                        add_gav["label"] = gav
                        add_gav["id"] = id_cnt
                        id_cnt += 1
                        add_gav["children"] = []
                        cur.append(add_gav)
                        cur = add_gav["children"]
    root["label"] = res["label"]
    root["direct_vulns_num"] = res["direct_vulns_num"]
    res_dict = {}
    gen_dict(res, res_dict)
    gen_res_complete(root, res_dict)
    return root


def gen_res_complete(element, res_dict):
    if element["label"] in res_dict.keys():
        element["direct_vulns_num"] = res_dict[element["label"]]
    else:
        element["direct_vulns_num"] = 0
    for child in element["children"]:
        gen_res_complete(child, res_dict)


def gen_dict(res, res_dict):
    if res["label"] not in res_dict.keys():
        res_dict[res["label"]] = res["direct_vulns_num"]
    for child in res["children"]:
        gen_dict(child, res_dict)


def generate_res(element):
    temp_list = []
    element["children_num"] = len(element["children"])
    element["direct_vulns_num"] = len(element["direct_vulns"])
    if len(element["children"]) == 0:
        element["transtive_vulns"] = []
        element["transtive_vulns_num"] = len(element["transtive_vulns"])
        element["all_vulns"] = element["direct_vulns"]
        return
    for child in element["children"]:
        if "transtive_vulns" not in child.keys():
            generate_res(child)
        temp_list.extend(child["transtive_vulns"])
        temp_list.extend(child["direct_vulns"])
    element["transtive_vulns"] = list(set(temp_list))
    element["transtive_vulns_num"] = len(element["transtive_vulns"])
    element["all_vulns"] = list()
    for ele in element["direct_vulns"]:
        element["all_vulns"].append(ele)
    for ele in element["transtive_vulns"]:
        element["all_vulns"].append(ele)
    element["all_vulns"] = list(set(element["all_vulns"]))


if __name__ == '__main__':
    # /home/hadoop/dfs/data/Workspace/Luchenhao/output/data/dependency/parse2jar_filter/testVul_2023-04-10_t.json
    file_filter_location = sys.argv[1]
    # /home/hadoop/dfs/data/Workspace/Luchenhao/LibDetectRec/VulDB/DB/VULNERABILITY_DB.db
    db_location = sys.argv[2]
    # FUXI-bb2196ae182a4ec28140b70d295eaa3a
    analyse_token = sys.argv[3]
    # testVul
    root_label = sys.argv[4]
    file_location = file_filter_location.replace("parse2jar_filter", "parse2jar")
    res_minimal = parse(file_filter_location, db_location)
    res_minimal["label"] = root_label
    res_complete = parse_complete(file_location, res_minimal)
    output = {}
    output["code"] = 200
    output["data_minimal"] = res_minimal
    output["data_complete"] = res_complete
    output["message"] = "OK"
    output["analyseToken"] = analyse_token
    write_json_location = "./component/" + root_label + "-" + datetime.datetime.now().strftime(
        "%Y-%m-%d") + "-" + analyse_token + ".json"
    with open(write_json_location,
              "w", encoding="utf8") as f:
        json.dump(output, f)
    print(write_json_location)
