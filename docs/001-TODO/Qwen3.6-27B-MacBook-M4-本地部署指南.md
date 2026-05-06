# Qwen3.6-27B MacBook M4 本地部署指南

> 调研时间：2026-05-06 | 模型发布：2026-04-22 | License: Apache 2.0

---

## 1. 模型概览

### 基本信息

| 项目 | 详情 |
|------|------|
| 模型名 | Qwen3.6-27B |
| 类型 | Dense（27B 全激活，非 MoE） |
| 开发商 | 阿里 Qwen 团队 |
| 参数量 | 27B（270 亿） |
| 上下文长度 | 262,144 tokens（原生），可通过 YaRN 扩展到 1,010,000 |
| 词表大小 | 248,320（padded） |
| 多模态 | 原生支持文本、图片、视频 |
| 架构 | 混合线性注意力（详见下文） |
| HuggingFace | `Qwen/Qwen3.6-27B` |

### 核心亮点

- SWE-bench Verified 77.2%，**超越 Qwen3.5-397B（76.2%）**
- Terminal-Bench 2.0 59.3%（远超 397B 的 52.5%）
- AIME26 94.1%，GPQA Diamond 87.8
- 混合思考模式：同一个 checkpoint 支持思考/非思考切换

### 与同级模型对比

| Benchmark | Qwen3.6-27B | Gemma 4 27B |
|-----------|-------------|-------------|
| SWE-bench Verified | **77.2%** | 43.2% |
| Terminal-Bench | **59.3%** | 31.4% |
| AIME26 | **94.1%** | 52.1% |
| 多模态 | 文本+图片+视频 | 仅文本+图片 |

Qwen3.6-27B 在代码和数学任务上碾压同级对手。

---

## 2. 架构详解（重要：非标准 Transformer）

Qwen3.6-27B 使用**混合注意力架构**，不是传统的 Transformer：

```
64 层 = 16 × (3 × Gated DeltaNet(线性注意力) + 1 × Gated Attention(标准注意力))
```

- **75% 层** 使用 Gated DeltaNet（线性注意力），复杂度 O(n)
- **25% 层** 使用 Gated Attention（标准注意力），复杂度 O(n^2)
- Gated DeltaNet: 48 heads for V, 16 heads for QK, head_dim=128
- Gated Attention: 24 heads for Q, 4 heads for KV, head_dim=256
- Hidden dim: 5,120 | FFN dim: 17,408 | RoPE dim: 64

**对本地部署的影响**：
- 推理速度优于同等参数的标准 Transformer（75% 用线性注意力）
- 需要 llama.cpp 较新版本才支持此架构
- vLLM 需要用 `vllm-metal` 插件（已确认支持 Qwen3.6）

---

## 3. 关闭思考模式（enable_thinking: false）

### 为什么需要关闭

思考模式下模型先输出 `/think ... /end_think` 推理链，再给出答案：
- 优点：复杂推理任务准确度更高
- 缺点：输出 token 数翻倍、速度减半、日常对话浪费算力

### 3.1 llama.cpp 关闭方法

**方法一：命令行参数（推荐）**

```bash
# 两个参数一起用，防止已知 bug
./llama-server \
  -m Qwen3.6-27B-Q4_K_M.gguf \
  --chat-template-kwargs '{"enable_thinking": false}' \
  --reasoning-budget 0
```

- `--chat-template-kwargs` 将 `enable_thinking=false` 注入 Jinja 模板
- `--reasoning-budget 0` 将思考 token 预算设为 0（双重保险）
- **已知 bug**：`llama-cli` 单独用 `enable_thinking` 可能不生效（issue #20182/20196/20409），必须同时加 `--reasoning-budget 0`
- `llama-server` 单独用 `enable_thinking` 即可

**方法二：API 调用时传参**

```python
from openai import OpenAI
client = OpenAI(base_url="http://localhost:8080/v1")

response = client.chat.completions.create(
    model="qwen3.6-27b",
    messages=[{"role": "user", "content": "你好"}],
    extra_body={"chat_template_kwargs": {"enable_thinking": False}}
)
```

**方法三：自定义 Jinja 模板（硬编码关闭）**

```bash
./llama-cli -m model.gguf -cnv --jinja --chat-template-file custom.jinja
```

custom.jinja 顶部加：
```
{%- set enable_thinking = false %}
```

### 3.2 vLLM 关闭方法

**方法一：服务端全局关闭（推荐）**

```bash
vllm serve Qwen/Qwen3.6-27B \
  --reasoning-parser qwen3 \
  --default-chat-template-kwargs '{"enable_thinking": false}'
```

**方法二：每次请求单独控制**

```bash
curl http://localhost:8000/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "Qwen/Qwen3.6-27B",
    "messages": [{"role": "user", "content": "你好"}],
    "chat_template_kwargs": {"enable_thinking": false}
  }'
```

**方法三：自定义 chat template 文件**

```bash
vllm serve Qwen/Qwen3.6-27B --chat-template ./qwen3_nonthinking.jinja
```

### 注意事项

- Qwen3.6 不支持 prompt 里的 `/no_think` 标记（Qwen3 才支持），切换只能通过 API 参数
- 关闭思考后推理能力会下降（有用户报告 STEM 测试从 22/24 降到 12/24）
- 复杂任务建议保持思考开启，日常对话/简单问答关闭

---

## 4. llama.cpp 部署（MacBook M4）

### 4.1 安装 llama.cpp

```bash
# 推荐 Homebrew
brew install llama.cpp

# 或从源码编译（获取最新功能）
git clone https://github.com/ggerganov/llama.cpp.git
cd llama.cpp
cmake -B build -DGGML_METAL=ON -DGGML_ACCELERATE=ON
cmake --build build --config Release -j$(sysctl -n hw.ncpu)
```

### 4.2 下载 GGUF 模型

**推荐源（imatrix 校准量化）**：
- Unsloth: `unsloth/Qwen3.6-27B-GGUF`（HuggingFace，最推荐）
- Bartowski: `bartowski/Qwen3.6-27B-GGUF`
- LM Studio 内置: `qwen/qwen3.6-27b`

```bash
# 下载 Q4_K_M（最佳平衡点）
huggingface-cli download unsloth/Qwen3.6-27B-GGUF \
  Qwen3.6-27B-Q4_K_M.gguf \
  --local-dir ./models
```

### 4.3 GGUF 量化选择

| 量化级别 | 文件大小 | 质量 | 24GB Mac | 32GB Mac | 64GB+ Mac |
|---------|---------|------|----------|----------|-----------|
| IQ3_XXS | 12.4 GB | 可用 | 勉强 | OK | OK |
| Q3_K_M | 14.4 GB | 一般 | 勉强 | OK | OK |
| **Q4_K_M** | **17.5 GB** | **很好** | **OK** | **OK** | **OK** |
| Q5_K_M | 20.5 GB | 接近无损 | 紧张 | OK | OK |
| Q6_K | 23.2 GB | 近乎无损 | 放不下 | 紧张 | OK |
| Q8_0 | 28.7 GB | 极高 | 放不下 | 放不下 | OK |
| BF16 | 53.8 GB | 完美 | 放不下 | 放不下 | 仅 128GB |

**推荐**：32GB Mac 用 Q5_K_M，24GB Mac 用 Q4_K_M

**避坑**：IQ4_XS 在 Apple Metal 上有已知性能退化（issue #21655），速度只有 Q4_K_M 的 1/3，暂不推荐。

### 4.4 运行命令

**非思考模式（日常对话）**：

```bash
./llama-server \
  -m ./models/Qwen3.6-27B-Q4_K_M.gguf \
  -c 16384 \
  -t 10 \
  -ngl 99 \
  -b 2048 \
  --chat-template-kwargs '{"enable_thinking": false}' \
  --reasoning-budget 0 \
  --temp 0.6 \
  --top-p 0.8 \
  --top-k 20 \
  --repeat-penalty 1.1 \
  --cache-type-k bf16 \
  --cache-type-v bf16 \
  --port 8080
```

**思考模式（复杂推理）**：

```bash
./llama-server \
  -m ./models/Qwen3.6-27B-Q5_K_M.gguf \
  -c 32768 \
  -t 10 \
  -ngl 99 \
  -b 2048 \
  --temp 0.6 \
  --top-p 0.95 \
  --top-k 40 \
  --repeat-penalty 1.05 \
  --cache-type-k bf16 \
  --cache-type-v bf16 \
  --port 8080
```

**参数说明**：

| 参数 | 说明 | M4 推荐值 |
|------|------|-----------|
| `-c` | 上下文长度 | 16384（日常）/ 32768（推理） |
| `-t` | CPU 线程数 | 10（M4 Pro）/ 16（M4 Max） |
| `-ngl 99` | GPU offload 层数 | 99（全部 offload 到 Metal） |
| `-b` | batch size | 2048（Metal 最优） |
| `--cache-type-k/v bf16` | KV cache 类型 | bf16（大上下文防乱码） |

### 4.5 推理速度实测

| 硬件 | 量化 | 生成速度 | prompt 处理 | 内存占用 |
|------|------|---------|-------------|---------|
| M4 Max 128GB | Q4_K_M | 16.56 t/s | 114.5 t/s | 29 GB |
| M4 Max 128GB | Q6_K | 13.34 t/s | 112.5 t/s | ~30 GB |
| M4 Max 128GB | Q3_K_M | 15.30 t/s | 111.7 t/s | 26 GB |
| M4 Pro 64GB | 4-bit (MLX) | 14.9 t/s | 109.3 t/s | 15.8 GB |

**并发性能**（M4 Pro 64GB, 4-bit）：
- 1 请求: 14.9 t/s
- 2 并发: 27.4 t/s（1.84x 加速）
- 4 并发: 24.0 t/s（1.61x 加速）

---

## 5. vLLM 部署（MacBook M4）

### 5.1 vllm-metal（官方插件，推荐）

**安装**：

```bash
# 一键安装脚本（安装到 ~/.venv-vllm-metal）
curl -fsSL https://raw.githubusercontent.com/vllm-project/vllm-metal/main/install.sh | bash
source ~/.venv-vllm-metal/bin/activate
```

安装内容：vllm 0.13.0 + Metal 插件（pinned 版本）

**启动服务**：

```bash
vllm serve Qwen/Qwen3.6-27B \
  --host 127.0.0.1 \
  --port 8000 \
  --reasoning-parser qwen3 \
  --default-chat-template-kwargs '{"enable_thinking": false}' \
  --enable-prefix-caching \
  --max-model-len 32768
```

**优点**：
- 官方维护，Qwen3.6 已确认支持
- Hybrid SDPA + GDN 线性注意力内核
- OpenAI 兼容 API
- 支持 continuous batching

**限制**：
- 不支持多模态（纯文本）
- vllm 版本 pinned 在 0.13.0（上游已到 0.15.x）
- Prefix caching 对混合注意力模型需手动 `--enable-prefix-caching`

### 5.2 vllm-mlx（社区方案）

```bash
pip install vllm-mlx
# 或
uv tool install vllm-mlx
```

**启动**：

```bash
vllm-mlx serve mlx-community/Qwen3.6-27B-4bit \
  --port 8000 \
  --continuous-batching
```

**适用场景**：
- 内存有限的 Mac（更好的量化支持）
- 需要运行 4-bit 量化模型

### 5.3 非思考模式采样参数

| 参数 | 通用任务 | 推理任务 |
|------|---------|---------|
| temperature | 0.6 | 0.4 |
| top_p | 0.8 | 0.95 |
| top_k | 20 | 40 |
| presence_penalty | 1.5 | 1.0 |
| max_tokens | 8192 | 16384 |
| repeat_penalty | 1.1 | 1.05 |

---

## 6. 其他部署方案

### 6.1 Ollama（最简单）

```bash
# 安装
brew install ollama

# 运行
ollama run batiai/qwen3.6-27b

# 关闭思考模式
ollama run batiai/qwen3.6-27b "[INST] 你好 [/INST]"
```

Ollama 默认启用非思考模式（对小模型），27B 需确认配置。

### 6.2 LM Studio

- 内置模型库直接搜索 `qwen3.6-27b`
- GUI 配置关闭思考模式
- 底层也是 llama.cpp

---

## 7. 硬件需求总结

| Mac 型号 | 统一内存 | 推荐量化 | 可行性 |
|----------|---------|---------|--------|
| Mac mini M4 16GB | 16 GB | 无 | 不可行（swap thrash 0.02 t/s） |
| MacBook Pro M4 24GB | 24 GB | Q4_K_M | 勉强可用 |
| MacBook Pro M4 32GB | 32 GB | Q4_K_M/Q5_K_M | 推荐 |
| MacBook Pro M4 Max 48GB | 48 GB | Q5_K_M/Q6_K | 舒适 |
| MacBook Pro M4 Max 64GB | 64 GB | Q6_K/Q8_0 | 最佳 |
| MacBook Pro M4 Max 128GB | 128 GB | Q8_0/BF16 | 任意量化 |

**结论**：32GB 统一内存是 Qwen3.6-27B 的实用门槛，64GB 以上体验最佳。

---

## 8. Qwen3.6-27B vs Qwen3.6-35B-A3B（MoE）

| 维度 | 27B Dense | 35B-A3B MoE |
|------|-----------|-------------|
| 每token激活参数 | 27B（全部） | 3B（仅 1/12） |
| 内存占用 (Q4) | ~17 GB | ~18 GB |
| 推理速度 | ~15 t/s | ~50-80 t/s |
| SWE-bench | 77.2% | ~72% |
| 适用场景 | 质量/准确度优先 | 速度/吞吐优先 |

两者内存占用几乎一样，如果日常对话为主、速度优先，35B-A3B 可能更合适。

---

## 9. 快速开始（推荐路径）

### 方案 A：llama.cpp + GGUF（最灵活）

```bash
# 1. 安装
brew install llama.cpp

# 2. 下载模型（Q4_K_M）
huggingface-cli download unsloth/Qwen3.6-27B-GGUF \
  Qwen3.6-27B-Q4_K_M.gguf --local-dir ./models

# 3. 启动（非思考模式）
llama-server \
  -m ./models/Qwen3.6-27B-Q4_K_M.gguf \
  -c 16384 -t 10 -ngl 99 -b 2048 \
  --chat-template-kwargs '{"enable_thinking": false}' \
  --reasoning-budget 0 \
  --cache-type-k bf16 --cache-type-v bf16 \
  --port 8080

# 4. 测试
curl http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{"model":"qwen","messages":[{"role":"user","content":"你好"}]}'
```

### 方案 B：vllm-metal（OpenAI API 兼容）

```bash
# 1. 安装
curl -fsSL https://raw.githubusercontent.com/vllm-project/vllm-metal/main/install.sh | bash
source ~/.venv-vllm-metal/bin/activate

# 2. 启动
vllm serve Qwen/Qwen3.6-27B \
  --port 8000 \
  --reasoning-parser qwen3 \
  --default-chat-template-kwargs '{"enable_thinking": false}' \
  --enable-prefix-caching \
  --max-model-len 32768
```

---

## 10. 参考链接

- 官方博客: https://qwen.ai/blog?id=qwen3.6-27b
- HuggingFace 模型卡: https://huggingface.co/Qwen/Qwen3.6-27B
- GGUF (Unsloth): https://huggingface.co/unsloth/Qwen3.6-27B-GGUF
- GGUF (Bartowski): https://huggingface.co/bartowski/Qwen3.6-27B-GGUF
- llama.cpp: https://github.com/ggerganov/llama.cpp
- vllm-metal: https://github.com/vllm-project/vllm-metal
- vllm-mlx: https://github.com/waybarrios/vllm-mlx
- Qwen vLLM 部署文档: https://qwen.readthedocs.io/en/latest/deployment/vllm.html
- vLLM Qwen3.5/3.6 Recipe: https://docs.vllm.ai/projects/recipes/en/latest/Qwen/Qwen3.5.html
