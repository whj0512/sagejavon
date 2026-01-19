import csv
import argparse
from typing import Iterable, Dict, List, Optional

from sqlalchemy import create_engine, MetaData, Table, Column
from sqlalchemy import BigInteger, String
from sqlalchemy.exc import SQLAlchemyError


def iter_rows(csv_path: str, encoding: str = "utf-8-sig") -> Iterable[Dict[str, str]]:
    """
    从 CSV 读取两列：明码、暗码。
    - 如果有表头，支持表头名：明码/暗码 或 mingma/anma
    - 如果没有表头，则默认第一列=明码，第二列=暗码
    """
    with open(csv_path, "r", encoding=encoding, newline="") as f:
        reader = csv.reader(f)
        first = next(reader, None)
        if first is None:
            return

        def norm(s: str) -> str:
            return (s or "").strip().lower()

        # 判断是否是表头
        first_norm = [norm(x) for x in first]
        header_map = {}
        if any(x in ("明码", "mingma", "plain", "code_plain") for x in first_norm) and any(
                x in ("暗码", "anma", "cipher", "code_cipher") for x in first_norm
        ):
            # 表头模式：定位两列索引
            idx_m = None
            idx_a = None
            for i, name in enumerate(first_norm):
                if name in ("明码", "mingma", "plain", "code_plain"):
                    idx_m = i
                if name in ("暗码", "anma", "cipher", "code_cipher"):
                    idx_a = i
            if idx_m is None or idx_a is None:
                raise ValueError("检测到表头但无法定位明码/暗码列，请检查 CSV 表头。")

            for row in reader:
                if not row or len(row) <= max(idx_m, idx_a):
                    continue
                mingma = (row[idx_m] or "").strip()
                anma = (row[idx_a] or "").strip()
                if mingma == "" and anma == "":
                    continue
                yield {"mingma": mingma, "anma": anma}
        else:
            # 无表头模式：first 就是第一行数据
            if len(first) >= 2:
                mingma = (first[0] or "").strip()
                anma = (first[1] or "").strip()
                if mingma != "" or anma != "":
                    yield {"mingma": mingma, "anma": anma}

            for row in reader:
                if not row or len(row) < 2:
                    continue
                mingma = (row[0] or "").strip()
                anma = (row[1] or "").strip()
                if mingma == "" and anma == "":
                    continue
                yield {"mingma": mingma, "anma": anma}


def chunked(it: Iterable[Dict[str, str]], size: int) -> Iterable[List[Dict[str, str]]]:
    batch = []
    for x in it:
        batch.append(x)
        if len(batch) >= size:
            yield batch
            batch = []
    if batch:
        yield batch


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--db", required=True, help="SQLAlchemy DB URL, e.g. sqlite:///test.db")
    ap.add_argument("--csv", required=True, help="CSV path, e.g. 1.csv")
    ap.add_argument("--table", default="code_map", help="table name")
    ap.add_argument("--batch", type=int, default=2000, help="batch size")
    ap.add_argument("--encoding", default="utf-8-sig", help="csv encoding (default utf-8-sig)")
    args = ap.parse_args()

    engine = create_engine(args.db, future=True)
    md = MetaData()

    # id 自增：SQLAlchemy 会按不同数据库生成对应自增/identity
    t = Table(
        args.table,
        md,
        Column("id", BigInteger, primary_key=True, autoincrement=True),
        Column("mingma", String(255), nullable=False),
        Column("anma", String(255), nullable=False),
    )

    try:
        md.create_all(engine)

        total = 0
        with engine.begin() as conn:
            for batch in chunked(iter_rows(args.csv, encoding=args.encoding), args.batch):
                conn.execute(t.insert(), batch)  # executemany
                total += len(batch)

        print(f"✅ 导入完成：{total} 行 -> 表 {args.table}（id 自动生成）")

    except (SQLAlchemyError, ValueError) as e:
        print(f"❌ 导入失败：{e}")
        raise


if __name__ == "__main__":
    main()
