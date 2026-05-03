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


只需一個指令，即可在您的 Mac 上運行 Qwen3.6：

pip install -U rapid-mlx
rapid-mlx serve qwen3.6-27b    # dense 27B, 14.9GB, 36.5 tok/s
rapid-mlx serve qwen3.6-35b    # MoE 35B-A3B, 19GB, 92 tok/s


```
curl http://localhost:8000/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{"model":"default","messages":[{"role":"user","content":"Say hello"}]}'
```

### New Aliases

| 別名                 | Model                              | RAM     | 速度         |
| ------------------ | ---------------------------------- | ------- | ---------- |
| `qwen3.6-27b`      | mlx-community/Qwen3.6-27B-4bit     | 14.9 GB | 36.5 tok/s |
| `qwen3.6-27b-8bit` | unsloth/Qwen3.6-27B-MLX-8bit       | 32.3 GB | 18.9 tok/s |
| `qwen3.6-35b-6bit` | mlx-community/Qwen3.6-35B-A3B-6bit | 約 28 GB | ~72 tok/s  |

### Highlights

- Qwen3.6-35B：比 Qwen3.5-35B 快 12%（每秒 92 個詞元，而 Qwen3.5-35B 為每秒 82 個詞元）
- Qwen3.6-27B：密集型混合模型（64 層，DeltaNet + 注意力機制），擁有 262K 的上下文處理能力，適用於視覺任務。
- 自動偵測的解析器： `qwen3_coder_xml` — 就是 `rapid-mlx serve qwen3.6-27b` ，解析器已自動配置完成
- 編碼：在測試套件中的完成度為 100%
- 壓力測試：8/8 準格


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

### oMLX vs Rapid-MLX 功能说明与比较

两者都是 Apple Silicon (M1-M4) 上的本地 LLM 推理服务器，基于 Apple MLX 框架，提供 OpenAI 兼容 API。但定位和架构差异很大。

oMLX (jundot/omlx)

定位: 全功能本地 LLM 管理平台，强调多模型管理和持久化缓存。

核心卖点:
- macOS 原生菜单栏 App (PyObjC，非 Electron)，从菜单栏启停/监控服务
- 双层 KV Cache (Hot RAM + Cold SSD): 热缓存满时自动卸载到 SSD (safetensors 格式)，下次匹配 prefix 直接恢复，重启后仍可用
- 多模型同时服务: LLM / VLM / Embedding / Reranker 同一个端口，LRU 淘汰、手动加载卸载、模型 Pin、per-model TTL
- Web Admin Dashboard: 模型管理、聊天、benchmark、HuggingFace 下载器、集成配置 (OpenClaw/OpenCode/Codex)
- Process Memory Enforcer: 总内存上限控制 (默认 RAM - 8GB)，防 OOM
- Claude Code 优化: context scaling + SSE keep-alive
- 支持 OpenAI + Anthropic API 兼容
- MCP 支持

模型类型: LLM, VLM, OCR, Embedding, Reranker

安装: Homebrew / DMG / pip



Rapid-MLX (raullenchai/Rapid-MLX)

定位: 极致性能推理引擎，强调速度和 tool calling 可靠性。

核心卖点:
- Raw 速度: 号称比 Ollama 快 1.3x~4.2x，cached TTFT 最低 0.08s
- 17 种 tool call parser: 覆盖 Qwen/GLM/DeepSeek/Llama/Gemma/Mistral/Phi/MiniMax/Kimi/GPT-OSS 等，带自动修复 (量化模型输出损坏的 tool call 自动转 回结构化)
- Prompt Cache: 标准 KV trim + DeltaNet state snapshot (Qwen3.5 混合 RNN 架构也能缓存)
- Reasoning 分离: chain-of-thought 输出单独 reasoning_content 字段，与 content 流式分离
- Cloud Routing: 本地慢时自动转发到云端 LLM (GPT-5, Claude 等)
- Tool logits bias: jump-forward decoding 加速 tool call
- Audio: STT/TTS via mlx-audio
- TurboQuant V-cache: V cache 压缩，dense 模型省 86%
- MHI (Model-Harness Index): 标准化评估模型+agent 组合的兼容性

单模型服务 (CLI rapid-mlx serve <model>)

安装: Homebrew / pip / curl 一键脚本



核心对比

| 维度 | oMLX | Rapid-MLX |
|------|------|-----------|
| 核心理念 | 多模型管理平台 | 极致单模型性能 |
| 多模型并发 | 多模型同时加载，LRU/TTL 管理 | 单模型 (一个 serve 命令) |
| KV Cache | RAM + SSD 双层持久化 | RAM prefix cache + DeltaNet snapshot |
| SSD Cache | 有 (safetensors, 重启持久) | 无 |
| Tool Calling | 依赖 mlx-lm 内置 parser (约 9 种格式) | 17 种 parser + 自动修复 + logits bias |
| Tool Call 恢复 | 无 | 自动将文本格式转回结构化 |
| Cloud Routing | 无 | 有 (本地慢 → 转云端) |
| Audio (STT/TTS) | 无 | 有 |
| Vision | 有 | 有 |
| Embedding / Reranker | 有 | 有 (embedding) |
| Admin UI | 完整 Dashboard (监控/聊天/下载/集成) | 无 (纯 CLI) |
| macOS 菜单栏 App | 有 (原生 PyObjC) | 无 |
| Benchmark 工具 | 内置 (admin panel 一键) | 内置 (CLI + 详细对比数据) |
| Eval 套件 | 内置 (MMLU/MATH 等) | MHI + 多维度 eval |
| API 兼容 | OpenAI + Anthropic | OpenAI + Anthropic |
| MCP 支持 | 有 | 有 |
| 安装体验 | DMG/ brew / pip | brew / pip / curl |
| 开发者 | junkim.dot@gmail.com (韩国) | raullenchai |
| 起源 | 基于 vllm-mlx v0.1.0 深度演进 | 独立项目 |



选型建议

选 oMLX 的场景:
- 需要同时跑多个模型 (LLM + embedding + reranker + VLM)
- 经常切换模型、不想重复加载
- 想要 GUI 管理 (Dashboard + 菜单栏)
- 重启后希望 KV cache 仍然可用 (SSD 持久化)
- 内存有限，需要自动淘汰和内存上限控制

选 Rapid-MLX 的场景:
- 追求极致推理速度
- 大量使用 tool calling 的 agent 场景 (Claude Code / Cursor)
- 用的是 Qwen3.5 等 DeltaNet 混合架构，需要 prompt cache
- 需要 reasoning 输出分离
- 想要本地/云端混合路由
- 需要 STT/TTS

两者可以共存 -- oMLX 跑 8000 端口管理日常多模型，Rapid-MLX 按需起特定端口跑高性能推理。