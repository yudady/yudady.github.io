---
title: Ollama MLX 后端 - Mac 本地模型速度翻倍指南
tags:
  - apple-silicon
  - ollama
  - mlx
  - nvfp4
  - llm-inference
  - status/active
  - type/video-notes
source:
  - "https://www.youtube.com/watch?v=HDlMRaJq8FE"
author: CloudYeti
created: 2026-05-09
updated: 2026-05-09
description: Ollama 0.19 新增 MLX 后端支持，正确拉取模型变体即可在 Apple Silicon 上获得约 2x 速度提升
level: beginner
stars: 3
---

# Ollama MLX 后端 - Mac 本地模型速度翻倍指南

> Ollama 0.19 在 Apple Silicon 上引入 MLX 后端，但不会自动启用。需要拉取带特定 tag 的模型变体才能获得速度提升，实测同模型同机器 decode 速度从 ~36 tok/s 提升到 ~78 tok/s。

## 背景：Ollama 的双引擎架构

```
之前:  Ollama ──→ llama.cpp (跨平台，通用)
现在:  Ollama ──┬─→ llama.cpp (默认路径)
                 └─→ MLX (Apple Silicon 专用，更快)
                         │
                    由模型 tag 决定走哪条路
```

- **llama.cpp** — 久经验证的跨平台推理引擎，Linux/Windows/Mac 通用
- **MLX** — Apple 专为 Apple Silicon 设计的 ML 框架，深度优化统一内存架构

---

## 如何启用 MLX 路径

**前置条件：**
- Ollama >= 0.19
- Apple Silicon Mac（M1/M2/M3/M4/M5）
- 32GB+ 统一内存（官方建议）

**MLX 路径的 tag 标识：**

| 关键词 | 格式说明 | 示例 |
|--------|---------|------|
| `nvfp4` | NVIDIA 4-bit 浮点量化 | `qwen3.5:35b-a3b-coding-nvfp4` |
| `mxfp8` | MLX 8-bit 浮点 | `qwen3.5:mxfp8` |
| `mlx-bf16` | MLX BFloat16 | `qwen3.5:mlx-bf16` |

> 不含以上关键词的 tag → 走 llama.cpp 路径

**操作步骤：**

```bash
# 1. 更新 Ollama
ollama --version  # 确认 >= 0.19

# 2. 拉取 MLX 变体（以 Qwen 3.5 为例）
ollama pull qwen3.5:35b-a3b-coding-nvfp4

# 3. 运行（Ollama 自动识别 tag 选择 MLX 引擎）
ollama run qwen3.5:35b-a3b-coding-nvfp4

# 4. 查看 decode 速率
ollama run qwen3.5:35b-a3b-coding-nvfp4 --verbose
# 底部会显示 eval rate: ~78 tokens/sec
```

---

## 如何找到 MLX 变体

**问题：** Ollama 模型搜索页（ollama.com/search）暂未索引 MLX 变体，直接搜会得到社区上传的其他版本。

**解决方案（按优先级）：**

```
1. 官方博客（最权威）
   https://ollama.com/blog/mlx
   列出所有已支持 MLX 的模型

2. Web 搜索
   搜索 "ollama mlx qwen3.5" 或 "ollama mlx qwen3.6"
   能快速找到对应 tag

3. GitHub Issue / Reddit
   社区讨论常有完整 tag 列表
```

**目前支持 MLX 的模型：**
- Qwen 3.5 系列（首发）
- Qwen 3.6 系列
- 更多模型持续添加中

---

## 实测性能对比

**测试环境：** MacBook Pro M3, 128GB RAM, Qwen 3.5 35B A3B

```
MLX 路径 (nvfp4):     ~78 tokens/sec  ████████████████████
llama.cpp 路径 (默认): ~36 tokens/sec  ████████████
                                      ────────────────────
                                      ~2.2x 速度差距
```

**Ollama 官方基准（M5 Pro/Max，更新数据）：**

| 指标 | llama.cpp (Q4_K_M) | MLX (NVFP4) | MLX (int4) |
|------|---------------------|-------------|------------|
| Prefill (tok/s) | 基线 | 显著提升 | **1851** |
| Decode (tok/s) | 基线 | ~78 (M3) | **134** |

> M5 系列芯片额外利用 GPU Neural Accelerator，TTFT 和 decode 进一步加速。

---

## NVFP4 量化格式

NVFP4 是 NVIDIA 的 4-bit 浮点量化格式，Ollama 首次在 MLX 后端中支持：

```
NVFP4 优势:
├── 更低内存带宽 → 更快推理
├── 更小存储体积
├── 与云端推理（生产环境）结果一致 → 本地/云端 parity
└── 支持 NVIDIA model optimizer 优化的模型

限制:
├── 仅 macOS 版 Ollama 支持（Linux/Windows 不支持）
├── 当前仅有少量模型提供 NVFP4 变体
└── 非 MLX 后端无法使用
```

---

## 缓存改进（Ollama 0.19）

MLX 后端还带来了缓存机制的升级，对 Agent 编码场景尤其重要：

```
改进前:                       改进后:
┌──────────────────┐         ┌──────────────────┐
│ 每次对话独立缓存  │         │ 跨对话复用缓存    │
│ 内存浪费大        │         │ 共享 system prompt │
│ 分支时缓存失效    │         │ 智能检查点保存    │
└──────────────────┘         │ 共享前缀更长存活  │
                             └──────────────────┘
```

- **跨对话缓存复用** — 共享 system prompt（如 Claude Code）不重复计算
- **智能检查点** — 在 prompt 关键位置保存缓存快照，减少重复处理
- **更智能的驱逐** — 共享前缀在分支切换时存活更久

---

## 快速决策：该用哪个？

```
你在 Mac 上用 Ollama?
├── 模型有 MLX 变体? ──是──→ 用 MLX tag (nvfp4/mxfp8)
│                         速度 ~2x，缓存更好
│
└── 没有 MLX 变体? ──────→ 用默认 tag (llama.cpp)
                          稳定可靠，模型覆盖广

注意:
├── 追求编码/Agent 场景 → MLX 变体（缓存改进大）
└── 追求模型多样性 → 默认路径（更多模型可选）
```

---

## 参考资料

- [Ollama MLX 官方博客](https://ollama.com/blog/mlx) — MLX 支持公告，2026-03-30
- [Ollama 0.19 MLX 后端深度分析](https://gingter.org/2026/04/23/ollama-goes-mlx/) — Gingter, 性能与工程细节
- [Ollama MLX Reddit 讨论](https://www.reddit.com/r/ollama/comments/1s8wk20/ollama_added_support_to_mlx/) — 社区实测数据
- [视频配套 GitHub](https://github.com/ravsau/ai-tutorials/tree/main/ollama-mlx-speed) — 基准测试脚本
- [NVFP4 Ollama Issue](https://github.com/ollama/ollama/issues/15172) — NVFP4 支持讨论

## 相关笔记

- [[oMLX vs Ollama - SSD KV缓存与Apple Silicon推理对比]]
