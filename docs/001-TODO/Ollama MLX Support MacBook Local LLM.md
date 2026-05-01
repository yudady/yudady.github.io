# Ollama MLX 支持 — MacBook 本地运行大语言模型

> [!info] 来源
> YouTube: [Ollama MLX Support MacBook](https://www.youtube.com/watch?v=EwcHUo3IrTo)
> created: 2026-04-11
> tags: [LLM, Ollama, MLX, Apple-Silicon, 本地推理]

## 核心内容

Ollama 发布了对 Apple Silicon 专用推理引擎 **MLX** 的新支持，大幅提升 MacBook 本地运行 LLM 的性能。

### 性能提升

| 指标 | MLX 支持 (v0.9) | 上一版本 |
|------|-----------------|---------|
| 综合推理性能 | 1810 | 1154 |
| 文本生成速度 | 112 tok/s | 58 tok/s |

接近翻倍的性能提升。

### 演示模型

- **Qwen 3.5 35B** — 量化为 NVFP4 格式（NVIDIA 4-bit 量化）
- 模型大小：21 GB（压缩后 18.66 GB）
- 推荐最低 32 GB 统一内存

### 实测环境与结果

- **设备**: Apple M3 Max, 36 GB 统一 RAM
- **内存占用**: 约 19 GB（80% RAM 使用率）
- **GPU 利用率**: 接近 100%（三核全开）
- **生成速度**: 约 65-66 tokens/秒
- **Prompt 处理**: 5.3 tokens/秒

### 安装方式

1. 下载 Ollama macOS 版本（v0.9+）
2. 拖拽到 Applications 文件夹
3. 终端运行  即可

### 关键要点

- MLX 是 Apple Silicon 专用推理引擎，能高效利用 GPU 和统一内存
- RAM 不足时可借助 swap 补充（会有性能损失）
- Ollama 现提供 ChatGPT 风格的 Web UI，支持参数调节和模型切换
- GPU 利用率 100% 说明 MLX 被高效调用