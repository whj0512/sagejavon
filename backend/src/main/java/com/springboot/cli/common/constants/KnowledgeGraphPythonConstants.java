package com.springboot.cli.common.constants;

import com.springboot.cli.model.DO.KnowledgeEdgesDO;
import com.springboot.cli.model.DO.KnowledgeNodesDO;

import java.util.Arrays;
import java.util.List;

public class KnowledgeGraphPythonConstants {

    public static final List<KnowledgeNodesDO> DEFAULT_PY_NODES = Arrays.asList(
            // 根
            new KnowledgeNodesDO("1",  "基础知识", 140, 120),
            new KnowledgeNodesDO("26", "算法与数据结构", 140, 120),

            // 基础知识 层级
            new KnowledgeNodesDO("2",  "基础概念", 140, 120),
            new KnowledgeNodesDO("3",  "概念", 140, 120),
            new KnowledgeNodesDO("4",  "进制", 140, 120),
            new KnowledgeNodesDO("5",  "运算符", 140, 120),
            new KnowledgeNodesDO("6",  "数值类型", 140, 120),
            new KnowledgeNodesDO("7",  "数值类型及运算", 140, 120),
            new KnowledgeNodesDO("8",  "复数", 140, 120),
            new KnowledgeNodesDO("9",  "数值运算", 140, 120),

            new KnowledgeNodesDO("10", "语法", 140, 120),
            new KnowledgeNodesDO("11", "分支结构", 140, 120),
            new KnowledgeNodesDO("12", "循环结构", 140, 120),
            new KnowledgeNodesDO("13", "函数", 140, 120),

            new KnowledgeNodesDO("14", "文件和数据格式化", 140, 120),
            new KnowledgeNodesDO("15", "文件操作", 140, 120),

            new KnowledgeNodesDO("16", "组合数据类型", 140, 120),
            new KnowledgeNodesDO("17", "字符串", 140, 120),
            new KnowledgeNodesDO("18", "字符串的索引与切片", 140, 120),
            new KnowledgeNodesDO("19", "字符串的比较规则", 140, 120),
            new KnowledgeNodesDO("20", "字符串格式化", 140, 120),
            new KnowledgeNodesDO("21", "列表", 140, 120),
            new KnowledgeNodesDO("22", "字典", 140, 120),
            new KnowledgeNodesDO("23", "数据类型转换", 140, 120),
            new KnowledgeNodesDO("24", "基础数据类型", 140, 120),

            // 算法与数据结构 层级
            new KnowledgeNodesDO("27", "数据结构", 140, 120),
            new KnowledgeNodesDO("28", "数组", 140, 120),
            new KnowledgeNodesDO("29", "二维数组", 140, 120),
            // 复用“字符串”“列表”“字典”
            new KnowledgeNodesDO("31", "链表", 140, 120),
            new KnowledgeNodesDO("32", "栈", 140, 120),
            new KnowledgeNodesDO("33", "队列", 140, 120),
            new KnowledgeNodesDO("34", "树", 140, 120),
            new KnowledgeNodesDO("35", "二叉树", 140, 120),
            new KnowledgeNodesDO("36", "图", 140, 120),
            new KnowledgeNodesDO("37", "欧拉回路", 140, 120),
            new KnowledgeNodesDO("38", "哈希表", 140, 120),
            new KnowledgeNodesDO("39", "哈希", 140, 120),
            new KnowledgeNodesDO("40", "哈希函数", 140, 120),
            new KnowledgeNodesDO("41", "堆", 140, 120),
            new KnowledgeNodesDO("42", "排序", 140, 120),
            new KnowledgeNodesDO("43", "桶排序", 140, 120),
            new KnowledgeNodesDO("44", "计数", 140, 120),

            new KnowledgeNodesDO("45", "算法思想", 140, 120),
            new KnowledgeNodesDO("46", "分治", 140, 120),
            new KnowledgeNodesDO("47", "动态规划", 140, 120),
            new KnowledgeNodesDO("48", "回溯", 140, 120),
            new KnowledgeNodesDO("49", "贪心", 140, 120),
            new KnowledgeNodesDO("50", "递归", 140, 120),
            new KnowledgeNodesDO("51", "迭代", 140, 120),

            new KnowledgeNodesDO("52", "算法技巧", 140, 120),
            new KnowledgeNodesDO("53", "双指针", 140, 120),
            new KnowledgeNodesDO("54", "快慢指针", 140, 120),
            new KnowledgeNodesDO("55", "边界处理", 140, 120),
            new KnowledgeNodesDO("56", "二分", 140, 120),
            new KnowledgeNodesDO("57", "位运算", 140, 120),
            new KnowledgeNodesDO("58", "数字操作", 140, 120),
            new KnowledgeNodesDO("59", "数学", 140, 120),
            new KnowledgeNodesDO("60", "二分查找", 140, 120),
            new KnowledgeNodesDO("61", "字符串匹配", 140, 120),
            new KnowledgeNodesDO("62", "指针", 140, 120),
            new KnowledgeNodesDO("67", "深度优先搜索", 140, 120),
            new KnowledgeNodesDO("68", "广度优先搜索", 140, 120),
            new KnowledgeNodesDO("69", "回文子串", 140, 120)
    );


    public static final List<KnowledgeEdgesDO> DEFAULT_PY_EDGES = Arrays.asList(
            // 根到一层
            new KnowledgeEdgesDO("1",  "2",  "includes"),
            new KnowledgeEdgesDO("1",  "10", "includes"),
            new KnowledgeEdgesDO("1",  "16", "includes"),
            new KnowledgeEdgesDO("1",  "24", "includes"),
            new KnowledgeEdgesDO("1",  "14", "includes"),
            new KnowledgeEdgesDO("1",  "13", "includes"),

            // 基础概念分支
            new KnowledgeEdgesDO("2", "3", "includes"),
            new KnowledgeEdgesDO("2", "4", "includes"),
            new KnowledgeEdgesDO("2", "5", "includes"),
            new KnowledgeEdgesDO("2", "6", "includes"),
            new KnowledgeEdgesDO("2", "7", "includes"),
            new KnowledgeEdgesDO("2", "8", "includes"),
            new KnowledgeEdgesDO("2", "9", "includes"),

            // 语法分支
            new KnowledgeEdgesDO("10", "11", "includes"),
            new KnowledgeEdgesDO("10", "12", "includes"),
            new KnowledgeEdgesDO("10", "13", "includes"),
            new KnowledgeEdgesDO("10", "14", "includes"),
            new KnowledgeEdgesDO("14", "15", "includes"),

            // 组合数据类型分支
            new KnowledgeEdgesDO("16", "17", "includes"),
            new KnowledgeEdgesDO("16", "21", "includes"),
            new KnowledgeEdgesDO("16", "22", "includes"),
            new KnowledgeEdgesDO("16", "23", "includes"),
            new KnowledgeEdgesDO("17", "18", "includes"),
            new KnowledgeEdgesDO("17", "19", "includes"),
            new KnowledgeEdgesDO("17", "20", "includes"),

            // 算法与数据结构：根到一级
            new KnowledgeEdgesDO("26", "27", "includes"),
            new KnowledgeEdgesDO("26", "45", "includes"),
            new KnowledgeEdgesDO("26", "52", "includes"),

            // 数据结构分支
            new KnowledgeEdgesDO("27", "28", "includes"),
            new KnowledgeEdgesDO("28", "29", "includes"),
            new KnowledgeEdgesDO("27", "17", "includes"), // 字符串
            new KnowledgeEdgesDO("27", "31", "includes"),
            new KnowledgeEdgesDO("27", "32", "includes"),
            new KnowledgeEdgesDO("27", "33", "includes"),
            new KnowledgeEdgesDO("27", "34", "includes"),
            new KnowledgeEdgesDO("34", "35", "includes"),
            new KnowledgeEdgesDO("27", "36", "includes"),
            new KnowledgeEdgesDO("36", "37", "includes"),
            new KnowledgeEdgesDO("27", "38", "includes"),
            new KnowledgeEdgesDO("38", "39", "includes"),
            new KnowledgeEdgesDO("38", "40", "includes"),
            new KnowledgeEdgesDO("27", "41", "includes"),
            new KnowledgeEdgesDO("27", "21", "includes"), // 列表（复用）
            new KnowledgeEdgesDO("27", "22", "includes"), // 字典（复用）

            // 算法思想
            new KnowledgeEdgesDO("45", "46", "includes"),
            new KnowledgeEdgesDO("45", "47", "includes"),
            new KnowledgeEdgesDO("45", "48", "includes"),
            new KnowledgeEdgesDO("45", "49", "includes"),
            new KnowledgeEdgesDO("45", "50", "includes"),
            new KnowledgeEdgesDO("45", "51", "includes"),

            // 算法技巧
            new KnowledgeEdgesDO("52", "53", "includes"),
            new KnowledgeEdgesDO("53", "54", "includes"),
            new KnowledgeEdgesDO("53", "55", "uses"),
            new KnowledgeEdgesDO("52", "56", "includes"),
            new KnowledgeEdgesDO("56", "60", "uses"),
            new KnowledgeEdgesDO("52", "42", "includes"),
            new KnowledgeEdgesDO("42", "43", "is a"),
            new KnowledgeEdgesDO("42", "44", "is a"),
            new KnowledgeEdgesDO("52", "57", "includes"),
            new KnowledgeEdgesDO("52", "58", "includes"),
            new KnowledgeEdgesDO("52", "59", "includes"),
            new KnowledgeEdgesDO("52", "61", "includes"),
            new KnowledgeEdgesDO("52", "62", "includes"),

            // 搜索策略（作为树/图的子能力）
            new KnowledgeEdgesDO("34", "67", "uses"),
            new KnowledgeEdgesDO("34", "68", "uses"),
            new KnowledgeEdgesDO("36", "67", "uses"),
            new KnowledgeEdgesDO("36", "68", "uses"),

            // 其他专题
            new KnowledgeEdgesDO("17", "69", "includes") // 字符串 → 回文子串
    );


}

