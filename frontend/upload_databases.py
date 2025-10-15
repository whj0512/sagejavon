import pandas as pd
import pymysql
import uuid

DB_CONFIG = {
    "host": "127.0.0.1",
    "port": 3311,              # SSH 隧道的本地端口
    "user": "SageJavon",
    "password": "",
    "database": "SageJavon",  # ← 改成你的数据库名
    "charset": "utf8mb4"
}


# === 建立连接 ===
conn = pymysql.connect(**DB_CONFIG)
cursor = conn.cursor()
print("✅ 已连接到数据库")

# === 定义层级结构 ===
graph_structure = {
    "基础知识": {
        "基础概念": ["概念", "进制", "运算符", "数值类型", "数值类型及运算", "复数", "数值运算"],
        "语法": {
            "分支结构": [],
            "循环结构": [],
            "函数": [],
            "文件和数据格式化": ["文件操作"],
            "组合数据类型": [
                "字符串的索引与切片", "字符串的比较规则", "字符串格式化", "列表", "字典", "数据类型转换"
            ],
        },
    },
    "算法与数据结构": {
        "数据结构": {
            "数组": ["二维数组", "字符串匹配"],
            "字符串": [],
            "链表": [],
            "栈": [],
            "队列": [],
            "树": ["二叉树", "二分查找", "深度优先搜索", "广度优先搜索"],
            "图": ["欧拉回路", "哈希函数"],
            "哈希表": ["哈希", "哈希函数"],
            "堆": [],
            "字典": []
        },
        "算法思想": ["分治", "动态规划", "回溯", "贪心", "递归", "迭代"],
        "算法技巧": {
            "双指针": ["快慢指针", "边界处理"],
            "二分": [],
            "排序": ["桶排序", "计数"],
            "位运算": [],
            "数字操作": [],
            "数学": []
        }
    }
}

# === 递归插入函数 ===
node_map = {}  # text -> node_id

def insert_node(name):
    if name in node_map:
        return node_map[name]
    node_id = str(uuid.uuid4())[:8]
    cursor.execute("""
        INSERT INTO knowledge_nodes_python (student_id, node_id, text, width, height)
        VALUES (%s, %s, %s, %s, %s)
    """, (STUDENT_ID, node_id, name, 140, 120))
    node_map[name] = node_id
    return node_id


def insert_structure(structure, parent=None):
    for key, value in structure.items():
        parent_id = insert_node(key)
        if isinstance(value, dict):
            for subkey, subvalue in value.items():
                child_id = insert_node(subkey)
                # 建立父子关系
                cursor.execute("""
                    INSERT INTO knowledge_edges_python (student_id, from_node_id, to_node_id, label)
                    VALUES (%s, %s, %s, %s)
                """, (STUDENT_ID, parent_id, child_id, "包含"))
                if isinstance(subvalue, list):
                    for leaf in subvalue:
                        leaf_id = insert_node(leaf)
                        cursor.execute("""
                            INSERT INTO knowledge_edges_python (student_id, from_node_id, to_node_id, label)
                            VALUES (%s, %s, %s, %s)
                        """, (STUDENT_ID, child_id, leaf_id, "包含"))
                else:
                    insert_structure(subvalue, subkey)
        elif isinstance(value, list):
            for item in value:
                child_id = insert_node(item)
                cursor.execute("""
                    INSERT INTO knowledge_edges_python (student_id, from_node_id, to_node_id, label)
                    VALUES (%s, %s, %s, %s)
                """, (STUDENT_ID, parent_id, child_id, "包含"))

# === 插入所有节点与边 ===
for root_name, children in graph_structure.items():
    root_id = insert_node(root_name)
    insert_structure(children, root_name)

conn.commit()
print(f"🎉 成功插入 {len(node_map)} 个知识点节点与层级关系！")
cursor.close()
conn.close()
