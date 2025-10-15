import pandas as pd
from collections import Counter

# === 配置 ===
EXCEL_PATH = r"./python-program.xlsx"  # ← 改为你的文件路径
OUTPUT_PATH = r"./python-knowledges-program.csv"

# === 读取 Excel ===
df = pd.read_excel(EXCEL_PATH)
print(f"📘 成功读取 {len(df)} 行题目")

# === 提取所有知识点 ===
all_skills = []

for skill_str in df["skill_name"].dropna():
    skills = [s.strip() for s in str(skill_str).split(",") if s.strip()]
    all_skills.extend(skills)

# === 统计频次 ===
counter = Counter(all_skills)
unique_skills = sorted(counter.items(), key=lambda x: -x[1])

# === 输出结果 ===
df_out = pd.DataFrame(unique_skills, columns=["knowledge_name", "count"])
df_out.to_csv(OUTPUT_PATH, index=False, encoding="utf-8-sig")

print(f"🎯 共提取 {len(unique_skills)} 个独立知识点，已保存到：{OUTPUT_PATH}")
print(df_out.head(20))
