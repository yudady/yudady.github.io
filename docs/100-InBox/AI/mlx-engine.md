---
id: mlx-engine
created_date: 19/04/2026
source: https://github.com/lmstudio-ai/mlx-engine
tags:
  - ai/llm
  - inference
  - apple-mlx
  - lmstudio
---

# mlx-engine — LM Studio 的 Apple MLX LLM 推理引擎

## 概述

MLX Engine 是 LM Studio 开发的高性能 LLM 推理引擎，专为 **Apple Silicon** (M系列芯片) 硬件设计，基于 Apple MLX 框架构建。提供统一的 Python API 加载和运行纯文本及视觉-语言模型，支持推测解码、跨提示词缓存、KV 缓存量化等高级优化。

**依赖核心库：**
- [mlx-lm](https://github.com/ml-explore/mlx-lm) — Apple MLX 推理引擎 (MIT)
- [Outlines](https://github.com/dottxt-ai/outlines) — 结构化输出 (Apache 2.0)
- [mlx-vlm](https://github.com/Blaizzy/mlx-vlm) — 视觉模型推理 (MIT)

**系统要求：** macOS 14.0+ (Sonoma)，Python 3.11

## 架构概览

```
mlx-engine/
├── mlx_engine/
│   ├── __init__.py              # 公共 API: load_model, create_generator, tokenize
│   ├── generate.py              # 核心生成流水线 (stream_generate 封装)
│   ├── cache_wrapper.py         # KV 缓存管理 + 跨提示词缓存
│   ├── model_kit/
│   │   ├── model_kit.py         # 文本模型 + 有专用 vision add-on 的视觉模型
│   │   └── vision_add_ons/      # 特定模型的视觉处理插件
│   │       ├── gemma3.py
│   │       ├── gemma3n.py
│   │       ├── pixtral.py
│   │       ├── mistral3.py
│   │       └── lfm2_vl.py
│   ├── vision_model_kit/
│   │   └── vision_model_kit.py  # 通用视觉模型包装 (mlx-vlm)
│   ├── processors/              # 生成处理器 (重复惩罚、停止字符串)
│   └── utils/                   # 工具集
│       ├── speculative_decoding.py    # 推测解码
│       ├── kv_cache_quantization.py   # KV 缓存量化
│       ├── prompt_processing.py       # 提示词处理
│       └── outlines_transformer_tokenizer.py  # Outlines 结构化输出
```

## 双路径架构

MLX Engine 根据模型类型选择两条初始化路径：

### ModelKit（文本 + 有专用 add-on 的视觉模型）
- 通过 `config.json` 中的 `model_type` 判断
- 支持 KV 缓存量化 (`kv_bits`, `kv_group_size`)
- 支持跨提示词缓存 (cross-prompt cache)
- 支持推测解码 (speculative decoding)

### VisionModelKit（通用视觉模型）
- 当 `model_type` 不在 `VISION_ADD_ON_MAP` 中时使用
- 封装 `mlx-vlm` 模型，兼容性更广但功能受限：
  - ❌ 不支持 KV 缓存量化
  - ❌ 不支持跨提示词缓存
  - ❌ 不支持推测解码

### Vision Add-on 映射表

| model_type | Vision Add-on |
|------------|---------------|
| gemma3 | Gemma3VisionAddOn |
| gemma3n | Gemma3nVisionAddOn |
| lfm2-vl | LFM2VisionAddOn |
| mistral3 | Mistral3VisionAddOn |
| pixtral | PixtralVisionAddOn |

> ⚠️ Qwen VL 系列 (qwen2_vl, qwen2_5_vl, qwen3_vl) 的 port 有 bug，暂被注释掉 (issue #237)。

## 核心 API

### 加载模型
```python
from mlx_engine import load_model, create_generator, tokenize

model_kit = load_model(
    "mlx-community/Meta-Llama-3.1-8B-Instruct-4bit",
    max_kv_size=4096,        # 最大 KV cache 大小
    kv_bits=None,            # KV 缓存量化位数 (None=不量化)
    kv_group_size=64,        # KV 缓存量化组大小
    quantized_kv_start=None, # 开始量化的步数
)
```

### 生成文本 (流式)
```python
prompt_tokens = tokenize(model_kit, "你好，世界")

for result in create_generator(
    model_kit, prompt_tokens,
    stop_strings=["结束"],
    temp=0.7, top_p=0.9,
    json_schema='{"type":"object",...}',  # 结构化输出
):
    print(result.text, end="", flush=True)
```

### 推测解码 (Speculative Decoding)
```python
from mlx_engine import load_draft_model, is_draft_model_compatible

if is_draft_model_compatible(model_kit, "lmstudio-community/Qwen2.5-0.5B-Instruct-MLX-8bit"):
    load_draft_model(model_kit, "lmstudio-community/Qwen2.5-0.5B-Instruct-MLX-8bit")
    # 小模型做草稿生成，大模型做验证，加速推理
```

### 视觉模型
```python
model_kit = load_model("mlx-community/pixtral-12b-4bit")

for result in create_generator(
    model_kit, prompt_tokens,
    images_b64=["base64_encoded_image..."],
):
    print(result.text)
```

## 关键组件详解

### CacheWrapper — 跨提示词缓存
- 维护 KV cache 的增量更新
- `_find_common_prefix()`: 计算当前 prompt 与上次缓存的公共前缀长度
- `_get_unprocessed_tokens()`: 返回需要重新处理的 token（超出公共前缀的部分）
- `_prefill()`: 分块处理 prompt (chunk_size=512)，支持进度回调和用户取消
- 用户取消时保留已处理的缓存状态，可从中断处继续

### 推测解码 (Speculative Decoding)
- 加载一个小模型作为 draft model
- Draft model 快速生成多个 token，主模型并行验证
- `set_draft_model()`: 合并 draft model cache 到主模型 cache 末尾
- Draft model 不支持视觉模型

### KV 缓存量化
- 可配置 `kv_bits` (如 4bit, 8bit) 减少内存占用
- 量化后 `max_kv_size` 被忽略（非旋转 KV cache 限制）
- 通过 `maybe_quantize_kv_cache()` 在 prefill 阶段应用

### 生成流水线
```
用户 prompt → tokenize() → process_prompt() (含跨提示词缓存命中)
  → stream_generate() [mlx-lm] → yield GenerationResult (text, tokens, top_logprobs)
  → StopStringProcessor 检查停止条件
  → EOS token 检测
```

## 支持的视觉模型

| 模型 | 量化级别 | lms 命令 |
|------|----------|----------|
| Llama-3.2-Vision (11B) | 4bit | `lms get mlx-community/Llama-3.2-11B-Vision-Instruct-4bit` |
| Pixtral (12B) | 4bit | `lms get mlx-community/pixtral-12b-4bit` |
| Qwen2-VL (7B) | 4bit | `lms get mlx-community/Qwen2-VL-7B-Instruct-4bit` |
| Llava-v1.6 (7B) | 4bit | `lms get mlx-community/llava-v1.6-mistral-7b-4bit` |

## 限制与已知问题

1. **Qwen VL 系列** — vision add-on 有 bug，暂不支持 (issue #237)
2. **视觉模型** — 不支持 KV 缓存量化、跨提示词缓存、推测解码
3. **Python** — requirements.txt 针对 Python 3.11 编译，其他版本可能不兼容
4. **VisionModelKit 重置** — 每次新预测需重新加载模型，无增量重置机制
5. **进度回调** — 已修复 issue #226 (ratchet 机制确保单调递增)

## 参考链接

- GitHub: https://github.com/lmstudio-ai/mlx-engine
- LM Studio 文档: https://lmstudio.ai/docs/cli
- Discord: https://discord.gg/aPQfnNkG
