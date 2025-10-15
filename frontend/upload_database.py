import pandas as pd
import pymysql

# === 配置区 ===
EXCEL_PATH = r"./python-program.xlsx"  # ← Excel 文件路径
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

# === 读取 Excel ===
df = pd.read_excel(EXCEL_PATH)
print(f"📘 成功读取 {len(df)} 行数据")

# === 获取或创建知识点 ===
def get_or_create_knowledge(name: str) -> int:
    """根据知识点名称获取ID，不存在则创建"""
    if not name:
        return None
    cursor.execute("SELECT id FROM knowledge_python WHERE name=%s", (name,))
    row = cursor.fetchone()
    if row:
        return row[0]
    cursor.execute(
        "INSERT INTO knowledge_python (parent_id, name, knowledge, num_q) VALUES (1, %s, %s, 0)",
        (name, name)
    )
    conn.commit()
    return cursor.lastrowid


# === 插入主表和关联表 ===
success_count = 0

for i, row in df.iterrows():
    try:
        question_text = str(row.get("problem_text", "")).strip()
        skill_names = str(row.get("skill_name", "")).strip()
        choice_a = str(row.get("choiceA", "")).strip() or None
        choice_b = str(row.get("choiceB", "")).strip() or None
        choice_c = str(row.get("choiceC", "")).strip() or None
        choice_d = str(row.get("choiceD", "")).strip() or None
        correct_answer = str(row.get("answer", "")).strip()
        difficulty = int(row.get("level", 1)) if not pd.isna(row.get("level")) else 1

        # 自动判断题型（无选项 → 代码题）
        q_type = 0 if not choice_a else 1

        # === 插入 exercise_python ===
        cursor.execute("""
            INSERT INTO exercise_python
            (question_text, correct_answer, difficulty, choice_a, choice_b, choice_c, choice_d, type, chapter)
            VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)
        """, (question_text, correct_answer, difficulty,
              choice_a, choice_b, choice_c, choice_d, 0, "Python知识点"))
        conn.commit()
        exercise_id = cursor.lastrowid

        # === 处理知识点多对多关系 ===
        if skill_names:
            skills = [s.strip() for s in skill_names.split(",") if s.strip()]
            for skill in skills:
                knowledge_id = get_or_create_knowledge(skill)
                if knowledge_id:
                    cursor.execute("""
                        INSERT INTO exercise_knowledge_python (exercise_id, knowledge_id)
                        VALUES (%s, %s)
                    """, (exercise_id, knowledge_id))
            conn.commit()

        success_count += 1
        print(f"✅ 第 {i+1} 条插入成功")

    except Exception as e:
        print(f"❌ 第 {i+1} 条出错: {e}")
        conn.rollback()

# === 更新知识点题目数量 ===
print("🔄 正在更新知识点题目数量 ...")
cursor.execute("""
UPDATE knowledge k
SET num_q = (
    SELECT COUNT(*) FROM exercise_knowledge_python ek WHERE ek.knowledge_id = k.id
)
""")
conn.commit()

print(f"🎉 全部完成！共成功插入 {success_count}/{len(df)} 条题目。")

cursor.close()
conn.close()
