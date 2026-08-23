---
title: 背包级 AI 工作站 — RTX PRO 4000 SFF 极限组装与 M3 Ultra 对决
aliases: [Velka 3 AI 工作站, 世界最小 AI 工作站, RTX PRO 4000 SFF 实测]
tags:
  - local-llm
  - sff-pc
  - hardware
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=H4MHueT2qRY"
  - "https://www.nvidia.com/en-us/products/workstations/professional-desktop-gpus/rtx-pro-4000-sff/"
  - "https://velkase.com/products/velka-3"
author: Alex Ziskind（频道作者）；硬件：NVIDIA / Velkase / MSI / AMD
created: 2026-08-23
updated: 2026-08-23
description: Alex Ziskind 用 RTX PRO 4000 SFF Blackwell + Velka 3（3.99L）组装出可放进背包的 CUDA AI 工作站，实测对比 Mac mini 与 M3 Ultra Mac Studio：prefill 速度翻倍碾压，decode 略输。
level: beginner
stars: 3
---

# 背包级 AI 工作站 — RTX PRO 4000 SFF 极限组装与 M3 Ultra 对决

> Alex Ziskind 与 Micro Center 的 Dan 一起，把一台 24GB VRAM 的 CUDA 工作站塞进 3.99L 的 Velkase Velka 3 机箱——体积接近 Mac Studio、能装进背包。组装过程踩遍 SFF 坑（PCIe riser 兼容性、支架切换、安装顺序），最后用 Qwen3 4B 和 Gemma 4 12B 对比 Mac mini（16GB）与 Mac Studio（M3 Ultra，512GB）。
>
> 适合正在纠结「本地 LLM 选 NVIDIA 小钢炮还是 Apple 统一内存」的人。视频偏组装 vlog，硬核数据有限，本笔记已用官方规格做了事实核查与勘误。

## 目录

- [[#一、專案目標與硬體選型]]
- [[#二、SFF 組裝過程與技術痛點]]
- [[#三、AI 推論效能實測]]
- [[#四、CUDA GPU vs Apple 統一記憶體]]
- [[#五、事實核查與數據勘誤]]
- [[#六、行動建議]]
- [[#參考資料]]

---

## 一、專案目標與硬體選型

### 核心概念

- **SFF（Small Form Factor，小体积装机）**：在数升容积内塞进全功能台式机，核心矛盾是散热、供电与扩展性的三方取舍。
- 项目目标：体积逼近 Mac Studio、能放进背包随身的 **CUDA AI 工作站**——不是媒体主机，是能跑本地 LLM 的标准 x86 机器。

### 完整硬件清单

| 组件 | 型号 | 关键规格 | 选型理由 |
|------|------|----------|----------|
| GPU | **NVIDIA RTX PRO 4000 SFF Blackwell** | 24GB GDDR7 ECC、70W、PCIe 5.0 x8、167×69×40mm、432 GB/s（官方） | 插槽供电免外接电源线；24GB 是能塞进超小机箱的最大显存 |
| 主板 | MSI B850 ITX（AM5） | $289（vs ASUS $359） | ITX 尺寸 + 省预算留给内存 |
| CPU | AMD Ryzen 5 9600X | 6 核，能效导向 | 避免狭小空间过热降频；AI 推理用不满多核 |
| 散热 | Thermalright 超薄下压式 | 需 ≤37mm（Velka 3 限高） | Noctua 同性能但性价比低（Dan 的观点） |
| 电源 | FSP Flex Guru 650W | **Flex ATX** 规格 | Velka 3 只兼容 Flex ATX，不兼容 SFX |
| 内存 | Crucial 6400 CL32 32GB | $369.99（64GB 要 $879） | 模型经系统 RAM 加载进 VRAM，32GB≈24GB VRAM+系统开销 |
| 机箱风扇 | Noctua 120mm + Kingwin 80mm | 80mm 仅 $6 | Velka 3 双风扇排风 |
| 机箱 | **Velkase Velka 3** | 3.99L（内部 3.81L）、37mm 散热限高、GPU 限长约 178mm | 市面上最接近 Mac Studio 体积的 ITX 机箱 |

### 体积对比（ASCII）

```
 容积示意（升）
 ML09 (Silverstone)      Velka 3                Mac Studio
 ┌──────────────┐        ┌────────┐            ┌────────┐
 │              │        │ 3.99 L │            │ ~3.7 L │
 │   ~13 L      │        └────────┘            └────────┘
 │  (13×8×4″)   │          ↑ 侧面尺寸接近 Mac Studio，
 │              │            但高度约为其 2 倍
 └──────────────┘
 初始计划用 ML09 → 发现太大 → 换 Velka 3（Dan 事先不知情）
```

### 选型最佳实践

- ✅ GPU 先行：SFF 装机由 GPU 尺寸/供电决定一切，先锁显卡再选机箱
- ✅ CPU 选能效不选核心数：AI 推理瓶颈在 GPU，多余发热只会连累整机
- ✅ 系统内存 ≈ VRAM + 系统开销即可（32GB 配 24GB 卡）
- ❌ 别为用不上的多核买单（视频里的 9900X 被换成了 9600X）
- ❌ 64GB 内存 +879−369=$509 的差价，不如留着升级别的

---

## 二、SFF 組裝過程與技術痛點

### 安装顺序与回退（ASCII 流程图）

```
装 Flex ATX 电源 → 装 ITX 主板 → 装显卡（全高支架）
        │
        ├─ ✗ 显卡高度不够，碰电源
        │     ← 拆电源腾高度
        │     ← 换低剖面支架（low-profile bracket）
        │
        ├─ 首次点亮 ✗ → 发现开机跳线插到了 LED 针脚
        │
        ├─ 首次点亮 ✓（"It's alive"）
        │
        └─ 一周后：PCIe 4.0 riser 无法工作（不点亮）
              → 换 PCIe 5.0 riser（更长，重新走线）
              → 稳定运行 ✓

  口诀（视频原话）：N steps back, three steps forward
  组装全程反复拆装 4-5 轮，公差以毫米计
```

### 三大技术痛点

| 痛点 | 现象 | 解决方案 |
|------|------|----------|
| **PCIe riser 兼容性** | PCIe 4.0 转接线在 B850 + RTX PRO 4000 SFF 上无法点亮 | 必须换 **PCIe 5.0 riser**（更贵更长，走线更难） |
| **显卡支架冲突** | 标准支架高度顶到电源 | 切换 low-profile bracket，卡略微「悬空」固定 |
| **机箱不含 riser** | Velka 3 不附带转接线（作者原话喊话厂商："Velka, put the riser cable in the box, please"） | 自购；开源箱（open-box）$60 捡漏含 riser 的型号 |

### 组装检查清单

- ✅ 确认 riser 代际 ≥ 主板/显卡所需的 PCIe 版本（新卡 + 新主板建议直接 PCIe 5.0）
- ✅ Flex ATX 电源尺寸 ≠ SFX，买前对孔位
- ✅ CPU 散热器限高 37mm（Velka 3），下压式为主
- ✅ 前面板跳线插「PWR BTN」不是「PWR LED」（视频真实翻车点）
- ❌ 不要指望机箱附说明书齐全——Velka 3 和 FSP 电源都没配 manual

---

## 三、AI 推論效能實測

### 两阶段推论（背景知识）

```
一次 LLM 推论 = Prefill + Decode

Prefill（提示词处理）           Decode（逐 token 生成）
┌─────────────────────┐        ┌─────────────────────┐
│ 吃满并行算力         │        │ 吃内存带宽            │
│ CUDA / Tensor Core  │        │ 每 token 都要读一遍   │
│ 的主场               │        │ 整个模型权重          │
└─────────────────────┘        └─────────────────────┘
  → NVIDIA 强项                   → 高带宽统一内存强项
```

### 实测数据汇总

| 测试 | 设备 | 指标 | 结果 |
|------|------|------|------|
| Qwen3 4B | **SFF 工作站** vs Mac mini（16GB） | 生成速度 | **103 tok/s** vs ~30 tok/s（3.4 倍） |
| Gemma 4 12B | vs Mac Studio M3 Ultra（512GB） | **Decode** | 39.6 tok/s vs **45.4 tok/s**（M3U 胜 ~15%） |
| Gemma 4 12B | vs Mac Studio M3 Ultra | **Prefill** | **1505 tok/s** vs 650 tok/s（**2.3 倍**，NVIDIA 胜） |

### 数据解读

- 对 Mac mini 的胜利没有参考意义——16GB 统一内存跑 4B 模型已接近上限，属于「能跑」而非「能打」。
- 真正的对决是 vs M3 Ultra：**decode 差距只有 ~15%**（45.4 vs 39.6），但 **prefill 差了 2.3 倍**。
- 实际体感：长上下文输入（RAG、Agent 多轮、代码库塞 prompt）时，prefill 速度决定「等待第一字出现的时间」，NVIDIA 优势会持续放大；纯聊天短 prompt 则两者体感接近。

### 硬件层面对照

```
                 RTX PRO 4000 SFF          M3 Ultra (Mac Studio)
 显存/内存       24GB GDDR7 (独占 VRAM)     最高 512GB 统一内存
 带宽            432 GB/s (官方)             819 GB/s
                  └ 视频口述 672 GB/s，见勘误
 理论模型上限    ~24GB 权重（12B FP16 /     ~512GB（可跑 400B+ 级）
                  70B 级需量化+offload）
 生态            CUDA / TensorRT / 全栈      MLX / Metal，开箱即用
```

---

## 四、CUDA GPU vs Apple 統一記憶體

### 架构对比

| 维度 | NVIDIA SFF（CUDA + 独立 VRAM） | Apple Silicon（统一内存 Unified Memory） |
|------|-------------------------------|------------------------------------------|
| Prefill（prompt 处理） | **强**（1505 tok/s，大规模并行算力） | 弱（650 tok/s） |
| Decode（生成） | 略弱（39.6 tok/s，受 432GB/s 带宽限制） | **强**（45.4 tok/s，819GB/s 带宽） |
| 可载模型规模 | 受 VRAM 硬限（24GB），offload 掉速严重 | 统一内存最大 512GB，可跑超大模型 |
| 软件生态 | CUDA 全家桶：微调、TensorRT、vLLM、Triton | MLX 生态成长中，开箱即用零配置 |
| 散热/噪音 | 满载热集中，机箱烫手（视频原话 "pretty toasty"，装包前要等几分钟） | 温控噪音优秀 |
| 可升级性 | GPU/CPU/RAM/SSD 全可换 | 几乎不可升级（内存焊死） |
| 组装门槛 | 高（riser、公差、走线层层坑） | 零（买来即用） |

### 选型决策树

```
本地 LLM 硬件怎么选？
│
├─ 工作流 = 长上下文 / Agent 高频调用 / 需要 CUDA 生态
│   （微调、TensorRT、vLLM、自定义 kernel）
│     └→ x86 + NVIDIA（prefill 优势 + 生态）★本视频方案
│
├─ 需要本地跑 70B+ 甚至 400B 级模型、要安静低温
│     └→ Mac Studio M3 Ultra（512GB 统一内存 + 819GB/s）
│
├─ 预算敏感 / 不在乎体积
│     └→ 全尺寸 RTX PRO 4000（145W，672GB/s，更快更便宜方案的讨论见相关笔记）
│
└─ 要「插电即用」的桌面小超算
      └→ NVIDIA DGX Spark / AMD Ryzen AI Max+ 395 "Halo"（128GB）
         （视频里两次顺路探访，作为未来对比对象）
```

### 最佳实践

- ✅ 先想清楚自己的负载类型：prompt 长、调用频 → NVIDIA；模型大、要安静 → Apple
- ✅ Agent/RAG 场景重点看 prefill（大量上下文重复处理）
- ❌ 不要只看 decode 的 tok/s 数字选硬件——那是带宽游戏，不是体验全貌
- ❌ 16GB Mac mini 跑本地 LLM 只能算入门体验，别当生产力主力

---

## 五、事實核查與數據勘誤

视频口述与字幕（YouTube 自动生成）存在多处数字粘连/口误，以下为对照官方规格的核查结果：

| 项目 | 视频/字幕说法 | 核查结果 | 判定 |
|------|--------------|----------|------|
| GPU 型号 | "RTX 4000 SFF"（部分摘要误写为 Ada 架构） | **RTX PRO 4000 SFF Blackwell**（2025 发布，描述 hashtag 也是 #rtxpro4000） | 更正：Blackwell 非 Ada |
| 显存带宽 | 口述 "672 GB/s" | 官方 SFF 版为 **432 GB/s**；672 GB/s 是**全尺寸** RTX PRO 4000 Blackwell（145W）的数字 | 口误/字幕误差，注意引用 |
| 测试模型一 | 字幕 "Qwen 3 4B"（摘要误作 "Qwen 34B"） | **Qwen3 4B**——Mac mini 仅 16GB 内存，4B 才合理 | 更正摘要 |
| 测试模型二 | 字幕 "Gemma 4 412 billion"（数字粘连） | **Gemma 4 12B**（Google 2026-06 发布，11.95B 参数、256K 上下文） | 更正：不存在 412B 版本 |
| M3 Ultra 规格 | 819 GB/s 带宽、最高 512GB 内存 | 与 Apple 官方一致 | ✓ |
| SFF 功耗 | 70W 插槽供电 | 与 NVIDIA 官方一致 | ✓ |
| 机箱容积 | "Velca 3" | 正确拼写 **Velkase Velka 3**，3.99L（内部 3.81L） | 拼写勘误 |
| GPU 长度 | — | 卡长 167mm < Velka 3 限长 ~178mm，双槽厚度兼容 | ✓ 可装 |

**方法论提醒**：YouTube 字幕的数字粘连（"4B"+"12B"→"412 billion"）和近音词是高发错误源；引用任何 benchmark 数字前，先用厂商官方 spec 页面交叉验证。

---

## 六、行動建議

1. **按工作负载选型**：Prompt 重度处理、Agent 调用、微调、CUDA 专用库 → 自组 SFF NVIDIA；本地跑 70B+ 大模型、追求低温静音 → Mac Studio 仍是首选。
2. **SFF 组装三项预检**：PCIe riser 代际（建议直接 5.0）、散热器限高（37mm 级）、电源规格（Flex ATX 非 SFX），并预留毫米级公差——视频里同一步骤返工 4 次以上。
3. **预算分配参考**：GPU > 内存 > 机箱电源 > CPU。CPU 能效优先（9600X 级别足够），省下的钱投给显存和带宽。
4. **扩展路径**：24GB VRAM 跑 12B FP16 或量化后的 30B 级模型；更大模型需要多卡（参考 Reddit r/LocalLLaMA 上双 RTX PRO 4000 跑 GPT-OSS-120B / Qwen3-Next-80B 的方案）或转向统一内存平台。

---

## 參考資料

- [Building the World's Smallest AI Workstation — Alex Ziskind（YouTube）](https://www.youtube.com/watch?v=H4MHueT2qRY)
- [NVIDIA RTX PRO 4000 Blackwell SFF 官方页面](https://www.nvidia.com/en-us/products/workstations/professional-desktop-gpus/rtx-pro-4000-sff/)
- [Velkase Velka 3 官方规格](https://velkase.com/products/velka-3)
- [TechPowerUp — RTX PRO 4000 Blackwell SFF 规格](https://www.techpowerup.com/gpu-specs/rtx-pro-4000-blackwell-sff.c4329)
- [Introducing Gemma 4 12B — Google Blog](https://blog.google/innovation-and-ai/technology/developers-tools/introducing-gemma-4-12b/)
- [RTX Pro 4000 vs 5000 Local AI Benchmarks（含 672GB/s 全尺寸数据）](https://computingforgeeks.com/rtx-pro-4000-vs-rtx-pro-5000-blackwell-local-ai-benchmarks/)

## 相關筆記

- [[本地AI硬件搭建 - 个人主权之路]]（DGX Spark vs Mac Studio vs RTX 5090 平台对比）
- [[Ornith-1.5 35B MoE Mac 本地实测]]（同 M3 Ultra 512GB 平台的 MLX 实测）
- [[DeepSeek-V4-Flash-0731-本地部署全解析]]（Mac Studio 512GB 部署大模型）
- [[LM Studio Bionic — 本地開源模型的 AI 代理工作站]]（Gemma 4 26B on Mac）

---

*文档生成时间：2026-08-23，基于 Alex Ziskind 2026-07-04 视频实测 + 官方规格交叉验证*
