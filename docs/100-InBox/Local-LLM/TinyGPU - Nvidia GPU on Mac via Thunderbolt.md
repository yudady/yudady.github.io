---
title: TinyGPU - 在 Mac 上通过 Thunderbolt 运行 Nvidia GPU
aliases: [TinyGPU, tiny-grad-gpu, Nvidia on Mac]
tags:
  - egpu
  - nvidia
  - apple-silicon
  - thunderbolt
  - llm
  - status/early
  - type/doc
source:
  - "https://www.youtube.com/watch?v=C4KWsmezXm4"
author: Alex Ziskind (Tiny Corp)
created: 2026-05-02
updated: 2026-05-02
description: Tiny Corp 发布开源 Mac OS Nvidia GPU 驱动，首次自 2019 年来在 Mac 上原生运行 Nvidia GPU，通过 Thunderbolt 连接 eGPU 进行 LLM 推理
level: intermediate
stars: 3
---

# TinyGPU - 在 Mac 上通过 Thunderbolt 运行 Nvidia GPU

> [!info] 基本信息
> - **视频**: [Nvidia GPU on Mac](https://www.youtube.com/watch?v=C4KWsmezXm4)
> - **频道**: Alex Ziskind
> - **时长**: 13:31
> - **发布**: 2026-04-12
> - **关键项目**: [tinygrad.org](https://tinygrad.org) (Tiny Corp)

## 一句话定位

Tiny Corp 写了一个开源 Mac OS kernel extension，让 Nvidia/AMD GPU 通过 Thunderbolt eGPU 在 Mac 上原生运行计算任务（包括 LLM 推理）。

## 背景故事

- **2018 年**: Apple 和 Nvidia 闹翻，macOS Mojave 砍掉 Nvidia 支持，CUDA 在 Mac 上死亡
- **2019-2026 年**: 7 年沙漠期，Mac 上无法使用 Nvidia GPU 计算
- **2026 年 4 月**: Tiny Corp 发布 **TinyGPU** 开源驱动，Apple 正式批准，支持 Blackwell 架构（RTX 50 系列）

## 工作原理

```
Mac Mini (M4 Pro) --Thunderbolt/USB4--> eGPU Enclosure --> Nvidia GPU (RTX 5060 Ti/5070 Ti/5090)
                                    |
                              TinyGPU Driver
                         (macOS Kernel Extension)
                                    |
                              Tinygrad Compiler
                              (Docker + NVCC/HIP)
```

**关键点**: 不是虚拟机、不是 hack，是真正的开源内核扩展直接与 GPU 通信。

## 安装步骤

1. 准备 Thunderbolt/USB4 eGPU enclosure + Nvidia GPU
2. 运行 curl 命令安装 TinyGPU 驱动（详见 tinygrad.org）
3. 系统设置 → 通用 → 扩展 → 启用 TinyGPU
4. 安装 Docker Desktop（自动处理 Nvidia 编译器）
5. 指定 `--device nv` 运行推理

**总安装时间**: 5 分钟以内

## 硬件配置测试

### 测试平台

- Mac Mini M4 Pro, 64GB RAM
- eGPU: USB4 dock / Razer Thunderbolt 5 enclosure（自备电源）

### GPU 对比

```
+---------------+--------+--------+-------+
| GPU           | VRAM   | FP32   | 功耗  |
+---------------+--------+--------+-------+
| RTX 5060 Ti   | 16 GB  | 22.7 T | 150W  |
| RTX 5070 Ti   | 16 GB  | 342 T  | 300W  |
| RTX 5090      | 32 GB  | ~342 T | 575W  |
| M4 Pro Metal  | 共享   | 33 T   | 内置  |
+---------------+--------+--------+-------+
```

**注意**: 5060 Ti 的小型 USB4 dock 供电不足以驱动 5070 Ti，需 Razer Thunderbolt 5 enclosure + 独立电源。12V 供电线需转接头。

## 性能基准测试

### 矩阵乘法（FP32）

```
RTX 5060 Ti:  22.7 TFlops
RTX 5070 Ti:  342 TFlops (64% boost from 5060 Ti, 8Kx8K matrix)
RTX 5090:     ~342 TFlops (与 5070 Ti 接近)
M4 Pro Metal: 33 TFlops (矩阵乘法甚至赢了 5060 Ti)
```

### LLM 推理速度（Tinygrad 内置 benchmark）

```
+------------------+----------+--------+--------+--------+
| 模型             | RTX 5090 | 5070Ti | 5060Ti | M4 Pro |
+------------------+----------+--------+--------+--------+
| Qwen3 8B (Q4)    | ~6 tok/s | 5.5    | 4.6    | 3.66   |
| Qwen3 30B MoE    | 6.5      | -      | -      | -      |
| Llama 3.1 8B Q8  | 7.48     | -      | -      | -      |
| Qwen 2.5 14B Q4  | 3.75     | -      | -      | -      |
+------------------+----------+--------+--------+--------+
```

**5090 实测显存带宽**: 28.8 GB/s（理论 1.8 TB/s，利用率仅 1.6%）

### 端到端 API Benchmark（LlamaBench，Qwen3 4B）

```
+------------------+----------+--------+
| 指标             | RTX 5090 | Metal  |
+------------------+----------+--------+
| Token 生成速度   | 7.39/s   | 4.29/s |
| TTFT             | ~1.3s    | ~5s    |
| 生成速度提升     | 72% 快   | 基准   |
+------------------+----------+--------+
```

### 残酷对比：Tiny vs Llama.cpp

```
+------------------+----------+----------------+
| 指标             | Tiny+5090| Llama.cpp+Metal|
+------------------+----------+----------------+
| Token 生成速度   | 7.39/s   | 70+ tok/s      |
| TTFT             | ~5s      | 651ms          |
| 倍数差距         | 基准     | 10x-18x 快     |
+------------------+----------+----------------+
```

## 瓶颈分析

### Thunderbolt 是瓶颈吗？

**不是。** LLM 推理的数据流：

```
启动时: 模型权重 --> Thunderbolt --> GPU VRAM (一次性传输)
推理时: VRAM --> GPU 计算 --> VRAM (GPU 内部完成)
                                  |
              每个 token 仅几字节通过 Thunderbolt
```

**真正的瓶颈**: Tinygrad 的 kernel 效率。5090 显存带宽 1.8 TB/s，实际只用了 33 GB/s（1.6%）。这是软件优化问题，不是线缆问题。

### 为什么比 Llama.cpp 慢这么多？

```
+---------------------------+------------------------------------+
| Llama.cpp                 | Tinygrad                           |
+---------------------------+------------------------------------+
| 数年手写 Metal kernel      | 通用编译器自动生成 kernel           |
| 融合量化感知矩阵乘法       | 尚未针对推理做专门优化             |
| 优化的 KV cache 管理       | 通用方案                           |
| 数千贡献者持续压榨性能     | Tiny Corp 小团队，非主打推理速度   |
+---------------------------+------------------------------------+
```

## 现状评估

### 优点

- 首次实现 Nvidia GPU 在 Mac 上原生计算（非 VM）
- 安装极简，5 分钟完成
- 热插拔支持，自动识别 GPU 变更
- 开源驱动，kernel extension 已被 Apple 批准
- 5090 的 32GB VRAM 可跑更大模型（~14B 舒适区）
- TTFT 比 Metal 快 3-4x（聊天体验更流畅）

### 不足

- 推理速度仅为 Llama.cpp (Metal) 的 1/10
- 显存带宽利用率极低（1.6%）
- 不支持并发请求
- 供电问题：小型 dock 无法驱动高端 GPU
- eGPU enclosure + 电源成本高

## 为什么这件事仍然重要

> "Two trillion-dollar companies (Apple and Nvidia) refused to make it happen. And a community project just did it."

**硬骨头已经啃完了**:
- 驱动 ✅
- 编译器管线 ✅
- 内存管理器 ✅

**可以优化的部分**: kernel 效率（纯软件优化，随时间会改善）

## 实际建议

| 场景 | 推荐方案 |
|------|----------|
| Mac 上跑 LLM 追求速度 | Llama.cpp / Ollama / Rapid-MLX（Metal） |
| Mac 上需要 Nvidia CUDA 生态 | TinyGPU（目前唯一选择） |
| 想尝试/支持开源项目 | 可以玩，但别指望生产性能 |
| 需要 32GB+ VRAM 跑大模型 | 5090 eGPU + TinyGPU（有意义） |

---

## 参考资料
- [TinyGPU 官网](https://tinygrad.org)
- [视频: Nvidia GPU on Mac](https://www.youtube.com/watch?v=C4KWsmezXm4)
- [Alex Ziskind 频道](https://www.youtube.com/@AZisk)

## 相关笔记
- [[Rapid-MLX - Apple Silicon 最快本地 AI 推理引擎]]
