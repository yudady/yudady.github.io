---
title: Caveman Compression - LLM 语义压缩方法
tags:
  - ai/llm
  - 工具/prompt-optimization
  - 工具/token-优化
source:
  - "https://github.com/wilpel/caveman-compression"
author: William Peltomäki
created: 2026-04-18 09:34
updated: 2026-04-18 09:34
description: |
  去除 LLM 上下文中的可预测语法，保留不可预测的事实内容，实现无损语义压缩以节省 token。
level: beginner
stars: 3
---

# Caveman Compression - LLM 上下文语义压缩

> [!info] 基本信息
> | 项目 | 详情 |
> |------|------|
> | **仓库** | https://github.com/wilpel/caveman-compression |
> | **Stars / Forks** | 731 / 44 |
> | **语言** | Python 3.8+ |
> | **协议** | MIT |
> | **作者** | William Peltomäki (@wilpel) |
> | **提交** | 36 commits, 最后更新 2025-12-04 |
> | **Issues / PRs** | 4 open / 2 open |

---

## 一句话定位

去除文本中 LLM 能可靠重建的语法（冠词、连词、被动语态），保留承载意义的实体和事实，实现 15%-58% 的 token 压缩，且语义无损。

---

## 核心解决的问题

LLM 上下文窗口有限，system prompt、RAG 检索结果、agent 推理链都会大量消耗 token。Caveman Compression 的关键洞察是：**LLM 擅长填补语言空隙，可以可靠预测语法和结构**。因此只需保留"不可预测"的内容（数字、名称、术语、约束），让 LLM 在推理时自动补全语法。

---

## 压缩原理

### 移除的内容（可预测）

| 类别 | 示例 |
|------|------|
| 语法词 | "a", "the", "is", "are" |
| 连接词 | "therefore", "however", "because" |
| 被动语态 | "is calculated by" |
| 填充词 | "very", "quite", "essentially" |

### 保留的内容（不可预测）

| 类别 | 示例 |
|------|------|
| 事实数据 | 数字、名称、日期 |
| 技术术语 | "O(log n)", "binary search" |
| 约束条件 | "medium-large", "frequently accessed" |
| 具体信息 | "Stockholm", "99.9% uptime" |

### 压缩示例

```
原始: "In order to optimize the database query performance, 
       we should consider implementing an index on the 
       frequently accessed columns..." (70 tokens)

压缩: "Need fast queries. Check which columns used most. 
       Add index to those columns..." (50 tokens)

压缩率: 29%
```

---

## 三种压缩方法

| 维度 | LLM-based | MLM-based | NLP-based |
|------|-----------|-----------|-----------|
| **压缩率** | 40-58% | 20-30% | 15-30% |
| **成本** | 需要 OpenAI API key | 免费 | 免费 |
| **质量** | 最佳，上下文感知 | 优秀，基于可预测性 | 良好，基于规则 |
| **速度** | ~2s/请求 | ~1-5s/文档（本地模型） | <100ms |
| **多语言** | 英文为主 | 英文 | 15+ 语言（中/日/俄等） |
| **离线** | 否 | 是（~500MB 模型） | 是 |
| **脚本** | `caveman_compress.py` | `caveman_compress_mlm.py` | `caveman_compress_nlp.py` |

### LLM-based

依赖 OpenAI API，压缩率最高，适合对 token 成本敏感的场景。

```bash
pip install -r requirements.txt
# 配置 .env 中的 OPENAI_API_KEY

python caveman_compress.py compress "Your verbose text here"
python caveman_compress.py compress -f input.txt -o output.txt
python caveman_compress.py decompress "Caveman text here"
```

### MLM-based

使用 RoBERTa 掩码语言模型，按 token 可预测性移除（top-k 最可预测的 token），免费离线。

```bash
pip install -r requirements-mlm.txt
python -m spacy download en_core_web_sm

python caveman_compress_mlm.py compress "Your verbose text here"
python caveman_compress_mlm.py compress -f input.txt -k 30  # 调整压缩级别
```

### NLP-based

基于 spaCy 规则，速度最快，支持最多语言，完全离线。

```bash
pip install -r requirements-nlp.txt
python -m spacy download en_core_web_sm

python caveman_compress_nlp.py compress "Your verbose text here"
python caveman_compress_nlp.py compress -f input.txt -l es  # 指定语言
```

---

## 核心压缩规则

1. **去连接词** -- 移除 therefore, however, because, in order to
2. **每句 2-5 词** -- 一个原子思想一句话
3. **用动词** -- do, make, fix, check 而非 facilitate, optimize
4. **具体化** -- "test five, test six" 而非 "test values 5-6"
5. **主动语态** -- "calculate value" 而非 "value is calculated"
6. **保留有意义的信息** -- 数字、大小、名称、约束

---

## 基准测试结果

| 测试场景 | 原始 token | 压缩后 token | 压缩率 |
|----------|-----------|-------------|--------|
| System Prompt | 171 | 72 | **58%** |
| API 文档 | 137 | 79 | **42%** |
| 简历 | 201 | 156 | **22%** |
| **平均** | **170** | **102** | **40%** |

事实保留测试：13/13 事实 100% 保留，验证语义无损。

---

## 适用场景

### 适合

- LLM reasoning/thinking blocks（推理链压缩）
- Token 受限的上下文
- 内部文档
- 分步指令
- RAG 知识库（压缩后存入向量数据库）
- Agent 内部推理（chain-of-thought 节省 50% token）

### 不适合

- 面向用户的内容
- 营销文案
- 法律文档
- 情感沟通

---

## 仓库结构

```
caveman-compression/
├── benchmark/              # 基准测试（事实保留验证）
├── examples/               # 压缩前后对比示例
├── images/                 # 文档配图
├── prompts/                # LLM 压缩/解压 system prompt
│   ├── compression.txt
│   └── decompression.txt
├── caveman_compress.py         # LLM-based 压缩
├── caveman_compress_nlp.py     # NLP-based 压缩
├── caveman_compress_mlm.py     # MLM-based 压缩
├── SPEC.md                     # 完整压缩规范
├── requirements.txt
├── requirements-nlp.txt
├── requirements-mlm.txt
└── .env.example
```

---

## 与类似方案的关系

灵感来自 TOON（Token Optimization）和 token-optimization 运动。属于 prompt 压缩/优化的一个分支，核心差异在于：

| 方案 | 策略 |
|------|------|
| LLMLingua | 基于 perplexity 的 prompt 压缩 |
| TOON | token 优化指令 |
| **Caveman Compression** | 语义层去语法，保留事实，人可读 |

Caveman 压缩后的文本人也能读懂（类似"穴居人语言"），不是单纯的 token 级别删减。

---

## 参考资料

- [GitHub 仓库](https://github.com/wilpel/caveman-compression)
- [SPEC.md - 完整压缩规范](https://github.com/wilpel/caveman-compression/blob/main/SPEC.md)

## 相关笔记

- [[Prompt Engineering]]
- [[Token 优化]]
- [[RAG 系统]]
