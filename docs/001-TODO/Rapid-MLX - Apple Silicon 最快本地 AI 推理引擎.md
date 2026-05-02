---
title: Rapid-MLX - Apple Silicon 最快本地 AI 推理引擎
aliases: [RapidMLX, rapid-mlx]
tags:
  - llm
  - apple-silicon
  - mlx
  - local-inference
  - status/active
  - type/doc
source:
  - "https://github.com/raullenchai/Rapid-MLX"
author: raullenchai
created: 2026-05-02
updated: 2026-05-02
description: 基于 MLX 框架的 Apple Silicon 本地 LLM 推理引擎，比 Ollama 快 2-4x，原生 OpenAI API 兼容，支持 17 种 tool call parser
level: intermediate
stars: 4
---

# Rapid-MLX - Apple Silicon 最快本地 AI 推理引擎

> [!info] 基本信息
> - **仓库**: https://github.com/raullenchai/Rapid-MLX
> - **Stars**: 快速增长中（热门项目）
> - **语言**: Python
> - **协议**: Apache 2.0
> - **平台**: Apple Silicon (M1-M5)，Python 3.10+
> - **作者**: raullenchai

## 一句话定位

基于 Apple MLX 框架构建的本地 LLM 推理服务器，主打 **速度碾压 Ollama**、**原生 tool calling 支持**、**OpenAI API 即插即用**。

## 核心解决的问题

1. **推理速度** -- 通过 MLX Metal 原生计算 + DeltaNet state snapshot + KV cache trimming，在 Apple Silicon 上比 Ollama 快 2-4x
2. **Tool Calling 兼容性** -- 17 种 parser 格式自动检测，量化模型 tool call 退化时自动恢复为结构化格式
3. **多轮对话延迟** -- cached TTFT 低至 0.08s（Kimi-Linear-48B），通过 prompt cache 和 DeltaNet state snapshot 实现
4. **生态兼容** -- OpenAI API 兼容，直接对接 Cursor、Claude Code、Hermes Agent、Aider 等

## 主要功能/特性

### 推理性能
- **KV Prompt Cache** -- transformer 模型通用，trim KV cache 跳过重复 prefill
- **DeltaNet State Snapshots** -- Qwen3.5 系列专属，Gated DeltaNet（75% RNN）+ attention（25% KV）混合架构，RNN state 恢复仅需 ~0.1ms
- **TurboQuant V-cache** -- V cache 旋转 + Lloyd-Max 压缩，dense 模型省 86% 显存
- **Tool Logits Bias** -- jump-forward decoding，加速 tool call token 生成

### Tool Calling
- 17 种 parser：hermes, minimax, qwen3\_coder\_xml, deepseek\_v31, glm47, kimi, llama 等
- **自动恢复机制** -- 量化模型输出退化时，检测并转换回结构化格式
- MHI（Model-Harness Index）评测体系：Qwopus 27B + Hermes 达 92 分

### Reasoning 分离
- thinking/reasoning 输出到独立的 `reasoning_content` 字段
- 支持 Qwen3、DeepSeek-R1、MiniMax、GPT-OSS

### Cloud Routing
- 大 context 请求自动路由到云端 LLM
- `--cloud-model openai/gpt-5 --cloud-threshold 20000`

### 多模态
- Vision（`pip install 'rapid-mlx[vision]'`）
- Audio STT/TTS（`pip install 'rapid-mlx[audio]'`）
- Embeddings

## 使用方式

```
# 安装（三选一）
brew install raullenchai/rapid-mlx/rapid-mlx    # Homebrew 推荐
pip install rapid-mlx                            # Python 3.10+

# 启动服务
rapid-mlx serve qwen3.5-9b --port 8000

# 对接 Hermes Agent（~/.hermes/config.yaml）
model:
  provider: "custom"
  default: "default"
  base_url: "http://localhost:8000/v1"
  context_length: 32768
```

## 选型参考（按 Mac 内存）

```
16 GB  → Qwen3.5-4B   (2.4 GB, 160 tok/s)
24 GB  → Qwen3.5-9B   (5.1 GB, 108 tok/s)
32 GB  → Nemotron-Nano 30B (18 GB, 141 tok/s, 100% tool calling)
64 GB  → Qwen3.5-35B  (37 GB, 83 tok/s)
96 GB  → Qwen3.5-122B (65 GB, 57 tok/s)
128 GB → DeepSeek V4 Flash 158B-A13B (91 GB, 56 tok/s, 1M context)
```

## 技术栈

- **MLX** -- Apple 的统一内存 ML 框架，原生 Metal GPU 计算
- **Python** -- FastAPI server
- **DeltaNet** -- Gated DeltaNet 混合 RNN+Attention（Qwen3.5 系列架构）
- **Speculative Decoding** -- 路线图中的 EAGLE-3（3-6.5x decode）、ReDrafter

## 性能对比（Mac Studio M3 Ultra 256GB）

```
Phi-4 Mini 14B:     180 tok/s (Ollama 56 tok/s, 3.2x)
Qwen3.5-9B:        108 tok/s (Ollama 41 tok/s, 2.6x)
Qwen3.5-35B-A3B:    83 tok/s (oMLX 75 tok/s, 1.1x)
Qwen3-Coder 80B:    74 tok/s (mlx-lm 69 tok/s, 1.1x)
Qwen3.5-122B:       44 tok/s (mlx-lm 43 tok/s, ~1.0x)
```

TTFT（cached）:
```
Kimi-Linear-48B:    0.08s
Llama 3.2 3B:       0.10s
Qwen3.5-35B (snap): 0.19s (cold 0.49s, 2.6x)
```

## 仓库结构

```
vllm_mlx/
  server.py           # App factory + model loading + CLI (1047 行)
  config/             # ServerConfig singleton
  service/
    postprocessor.py  # Streaming pipeline (100% test coverage)
  routes/             # /v1/chat/completions, /v1/messages (Anthropic API)
  engine/             # BatchedEngine (continuous batching)
  reasoning/          # 7 种 reasoning parser
  tool_parsers/       # 20+ tool call parser
  agents/             # 11 个 agent profile (YAML)
  runtime/            # Model registry, cache persistence
  doctor/             # 自诊断工具 (rapid-mlx doctor)
tests/                # 2000+ pytest 单元测试
scripts/              # stress/soak test, benchmark
```

## Roadmap

```
Standard Speculative Decode  → 1.5-2.3x decode (Not started)
EAGLE-3                     → 3-6.5x decode   (Not started)
ReDrafter (Apple RNN draft) → 1.4-1.5x decode  (Not started)
```

## 安全与隐私

- 完全本地运行，无云端依赖（cloud routing 可选）
- 支持 `--api-key` 和 `--rate-limit` 访问控制
- Apache 2.0 开源协议

---

## 参考资料
- [GitHub 仓库](https://github.com/raullenchai/Rapid-MLX)
- [Cline Discussion: 2-4x faster local LLM backend](https://github.com/cline/cline/discussions/9940)
- [YouTube: 2-3x Faster Local LLMs on Mac](https://www.youtube.com/watch?v=475i_AsrUBI)
- [Apple MLX + M5 GPU Research](https://machinelearning.apple.com/research/exploring-llms-mlx-m5)

## 相关笔记
