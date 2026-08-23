---
title: Llama.cpp + TurboQuant 本地 LLM 部署指南
aliases: [本地 LLM 部署, TurboQuant, llama.cpp 编码 Agent]
tags:
  - ai/local-llm
  - ai/quantization
  - tool/llama-cpp
  - tutorial/setup
  - status/reviewed
source:
  - "https://www.youtube.com/watch?v=5jkAlqbk66A"
  - "https://github.com/nico-martin/gemma4-browser-extension"
author: Samuel Gregory
created: 2026-05-02
updated: 2026-05-02
description: 跳过 LM Studio/Ollama 包装层，直接用 llama.cpp + TurboQuant 分支运行本地 LLM，并连接 Kilo Code 和 OpenClaw 进行编码
level: intermediate
stars: 4
---

# Llama.cpp + TurboQuant 本地 LLM 部署指南

> 教程演示如何绕过 LM Studio 和 Ollama 等包装工具，直接从源码编译带 TurboQuant 支持的 llama.cpp，实现 KV Cache 极致压缩，让有限内存跑更大的模型。最终将本地服务接入 Kilo Code（VS Code）和 OpenClaw 进行 AI 辅助编码。

---

## 目录

1. [为什么不用 LM Studio / Ollama](#为什么不用-lm-studio--ollama)
2. [TurboQuant：KV Cache 极致压缩](#turboquantkv-cache-极致压缩)
3. [模型选择与量化等级](#模型选择与量化等级)
4. [从源码编译 llama.cpp（TurboQuant 分支）](#从源码编译-llamacppturboquant-分支)
5. [运行模型：CLI 与 Server 模式](#运行模型cli-与-server-模式)
6. [接入 Kilo Code（VS Code）](#接入-kilo-codevs-code)
7. [接入 OpenClaw](#接入-openclaw)
8. [VRAM 预算与调优策略](#vram-预算与调优策略)

---

## 为什么不用 LM Studio / Ollama

```
应用层                               推理层
+------------------+               +------------------+
| LM Studio (GUI)  |               |                  |
| Ollama (CLI)     | -- 包装 -->   |   llama.cpp      |
| 其他客户端...     |               |   (实际推理引擎)  |
+------------------+               +------------------+

问题：
  - LM Studio：client timeout 错误常见
  - Ollama：会覆盖 OpenClaw 配置
  - 两者的默认参数不一定最优
  - 额外抽象层增加调试难度
```

**核心理由**：直接使用 llama.cpp 可以精确控制每个参数，包括 KV Cache 量化策略、上下文长度、Flash Attention 等，不受包装器的默认设置限制。

---

## TurboQuant：KV Cache 极致压缩

### 背景

TurboQuant 是 Google Research 2026 年发表（ICLR 2026）的 KV Cache 压缩算法，由开发者 Tom 移植到 llama.cpp 分支中。

### 关键数字

| 指标 | 效果 |
|------|------|
| KV Cache 内存减少 | 6x 以上 |
| 推理加速（H100, 4-bit） | 8x |
| 精度损失 | 接近零（无需微调） |
| 量化目标 | 3-bit KV Cache |

### 工作原理（两阶段）

```
阶段 1: PolarQuant（高质量压缩）
  原始向量 --[随机旋转]--> 几何简化 --[标准量化器]--> 高质量压缩结果
  用大部分 bits 捕获原始向量的主要方向和强度

阶段 2: QJL（消除残余偏差）
  阶段 1 的误差 --[1-bit 符号]--> Johnson-Lindenstrauss 变换 --> 无偏估计
  仅用 1 bit 消除注意力分数的偏差

合并: TurboQuant = PolarQuant + QJL
  结果: 3-bit KV Cache，零精度损失，更快推理
```

### KV Cache 不对称量化（关键配置）

Tom 的测试发现：**K 存储用 Turbo 3（V 存储），V 存储用 8-bit 非对称量化**，可以在不牺牲模型性能的前提下最大化压缩。

```
推荐配置（Tom 分支默认值）：
  -kv-quant turbo3        # V 存储用 Turbo 3 量化
  -kq-quant Q8_0         # K 存储用 8-bit 非对称量化

  不推荐：
  - KV 两边都用 Turbo（对称），会牺牲模型性能
```

---

## 模型选择与量化等级

### GGUF 命名解读

```
Qwen3.6-35B-A3B-Q8_K_L.gguf
  |       |  |  | |  |
  |       |  |  | |  +-- L/XL/M/S: 同 bit 下的尺寸微调
  |       |  |  | +-- K: 量化方法（推荐，现代方法）
  |       |  |  +-- Q8: 8-bit 量化
  |       |  +-- A3B: MoE 模型，35B 总参，3B 活跃参数
  |       +-- 35B: 总参数量
  +-- Qwen3.6: 模型名称
```

### 量化等级选择决策树

```
你的 VRAM 有多大？
  |
  +-- 100GB+ --> FP16（16-bit，无损，完整体验）
  |
  +-- 64-100GB --> Q8_K（8-bit，几乎无损）
  |                  示例: 38GB 模型 + 10GB KV ≈ 48-50GB
  |
  +-- 32-64GB --> Q6_K 或 Q5_K_M（5-6 bit，轻微损失）
  |                 可能需要减少上下文长度
  |
  +-- 16-32GB --> Q4_K_M 或 Q3_K（3-4 bit，明显损失）
  |
  +-- <16GB --> Q2_K 或 Q1（1-2 bit，大损，不推荐）
                 除非只是做简单聊天
```

**核心原则**：在 VRAM 允许的范围内选择最高 bit rate。量化越狠，模型质量下降越明显。

### 推荐来源

| 来源 | 特点 |
|------|------|
| Unsloth（HuggingFace） | 可靠，GGUF 单文件，社区验证 |
| Bartowski | 高质量量化，多版本 |
| 原始模型 | safetensors 多文件，需自行转换 |

---

## 从源码编译 llama.cpp（TurboQuant 分支）

### 前置条件

- macOS: Xcode Command Line Tools（Apple Silicon 用 Metal）
- NVIDIA: CUDA Toolkit
- AMD: ROCm

### 编译步骤

```bash
# 1. 克隆 Tom 的 TurboQuant 分支
cd ~/Sites  # 或你偏好的目录
git clone https://github.com/TheTom/turboquant_plus.git
cd turboquant_plus

# 2. 切换到 TurboQuant 分支
git checkout <turboquant-branch>

# 3. 编译（只需一次）
# Apple Silicon:
./build.sh metal
# NVIDIA:
./build.sh cuda
# AMD:
./build.sh hipblas

# 4. 编译产物在 build/bin/
# 关键文件：
#   llama-cli   - 命令行交互模式
#   llama-server - OpenAI 兼容 API 服务模式
```

### 下载模型

```bash
# 创建模型目录（视频建议放在仓库内的 models/ 文件夹）
mkdir -p models

# 从 HuggingFace 下载 GGUF 文件
# 示例: Qwen 3.6 35B A3B Q8_K
# https://huggingface.co/unsloth/Qwen3.6-35B-A3B-GGUF
# 下载 .gguf 文件到 models/ 目录
```

---

## 运行模型：CLI 与 Server 模式

### CLI 模式（快速测试）

```bash
cd build/bin

./llama-cli \
  -fa \                              # Flash Attention 加速
  -c 262144 \                        # 上下文长度（Qwen 3.6 最大值）
  -kv-quant turbo3 \                 # V 存储 Turbo 3（默认，可省略）
  -kq-quant Q8_0 \                   # K 存储 8-bit 非对称（推荐配置）
  -m ../../models/Qwen3.6-35B-A3B-Q8_K_L.gguf
```

**关键参数说明**：

| 参数 | 作用 | 建议 |
|------|------|------|
| `-fa` | 启用 Flash Attention | 开启，加速推理 |
| `-c` | 上下文窗口大小 | 查模型 card 设置最大值；VRAM 不够则减小 |
| `-kv-quant` | V 存储量化方式 | `turbo3`（默认） |
| `-kq-quant` | K 存储量化方式 | `Q8_0`（非对称，保持模型质量） |
| `-m` | 模型文件路径 | GGUF 文件位置 |
| `-ngl` | GPU 层数（NVIDIA/AMD） | macOS 不需要 |

### Server 模式（接入外部工具）

```bash
# 将 llama-cli 改为 llama-server，其余参数不变
./llama-server \
  -fa \
  -c 262144 \
  -kv-quant turbo3 \
  -kq-quant Q8_0 \
  -m ../../models/Qwen3.6-35B-A3B-Q8_K_L.gguf

# 输出: Main server listening on 127.0.0.1:8080
# 浏览器访问 http://127.0.0.1:8080 有内置聊天 UI
# API 兼容 OpenAI 格式: http://127.0.0.1:8080/v1
```

**获取模型 ID**（供后续配置使用）：

```bash
curl http://127.0.0.1:8080/v1/models
# 返回 JSON，记录 data[0].id 字段
```

---

## 接入 Kilo Code（VS Code）

### 配置步骤

```
1. VS Code -> 安装 Kilo Code 扩展（免费）
2. Settings -> Providers -> Add Custom Provider
3. 填写配置：
   - Provider ID: llama.cpp（自定义）
   - Display Name: Llama.cpp Local
   - Base URL: http://127.0.0.1:8080/v1
4. Kilo Code 自动检测到已加载的模型
5. 底部模型选择器 -> 选择你的模型
```

### 性能预期

| 设备 | Prefill | 生成速度 |
|------|---------|----------|
| M1 Max 64GB | 较慢（老芯片） | ~53 tokens/s |
| M4 MacBook Pro | 快 | 预估 80-100+ tokens/s |
| M4 MacBook Air | 中等 | 预估 60-80 tokens/s |

> 注：Apple Silicon 的统一内存意味着所有应用共享 RAM，运行本地 LLM 时建议关闭其他占内存的应用。

---

## 接入 OpenClaw

### 配置步骤

```
1. 打开 OpenClaw -> Config -> 打开原始配置文件（Raw Config）
2. 在 providers 中添加:
   {
     "llama.cpp": {
       "base_url": "http://127.0.0.1:8080/v1",
       "api_key": "any-string",        // 随意填，llama.cpp 不校验
       "api": "openai-responses"       // llama.cpp 使用 OpenAI 格式
     }
   }

3. 在 models 中添加（通过 curl /v1/models 获取 ID）:
   {
     "id": "<模型ID>",
     "name": "Qwen 3.6 35B",
     "context_length": 262144
   }

4. 设置为默认模型（可选）:
   agents.default_model.primary = "llama.cpp/<模型ID>"
5. 保存配置 -> 刷新 OpenClaw
```

---

## VRAM 预算与调优策略

### 内存占用估算

```
总 VRAM 需求 = 模型大小 + KV Cache 大小 + 运行时开销

示例（Qwen 3.6 35B, Q8_K, TurboQuant）:
  模型:     ~38 GB（Q8_K 量化后）
  KV Cache: ~10 GB（TurboQuant 压缩后，传统需要 ~15 GB）
  运行时:   ~2 GB
  合计:     ~50 GB  <-- 64 GB RAM 可以跑满上下文

对比（无 TurboQuant）:
  模型:     ~38 GB
  KV Cache: ~15 GB
  运行时:   ~2 GB
  合计:     ~55 GB  <-- 64 GB RAM 可能不够跑满上下文
```

### 调优策略

```
如果内存不够：
  1. 降低模型量化等级（Q8 -> Q6 -> Q5）
  2. 减少上下文长度（262144 -> 131072 -> 65536）
  3. TurboQuant 自动压缩 KV Cache（最省心的优化）

如果速度不够：
  1. 用更小的模型（35B -> 27B -> 9B）
  2. 减少上下文长度
  3. 升级硬件（芯片代差影响很大）

最佳实践：
  - 生产编码建议 Mac Mini 做专用推理服务器
  - 主机通过网络连接推理服务器，释放主机内存
  - 简单聊天用 Q4_K_M + 32K 上下文即可
  - 代码生成用 Q8_K + 最大上下文
```

### LM Studio vs Ollama vs llama.cpp 直用

| 维度 | LM Studio | Ollama | llama.cpp 直用 |
|------|-----------|--------|---------------|
| 易用性 | GUI 友好 | CLI 简单 | 需要命令行 |
| 参数控制 | 有限 | 有限 | 完全控制 |
| TurboQuant | 未集成 | 未集成 | Tom 分支支持 |
| MLX 模型 | 支持（长期） | 仅 1 个模型 | 不适用 |
| 超时问题 | 常见 | 偶尔 | 无 |
| 配置冲突 | 可能 | 会覆盖其他工具 | 无（手动管理） |

---

## 参考资料

- [Tom 的 TurboQuant+ 分支](https://github.com/TheTom/turboquant_plus)
- [TurboQuant 论文 / Google Research Blog](https://research.google/blog/turboquant-redefining-ai-efficiency-with-extreme-compression/)
- [Qwen 3.6 GGUF (Unsloth)](https://huggingface.co/unsloth/Qwen3.6-35B-A3B-GGUF)
- [llama.cpp 官方仓库](https://github.com/ggerganov/llama.cpp)
- [视频来源: Samuel Gregory - YouTube](https://www.youtube.com/watch?v=5jkAlqbk66A)

## 相关笔记

- [[Gemma 4 Browser Assistant - 本地浏览器 AI Agent]]
- [[Edge AI]]
- [[本地 LLM 部署]]
