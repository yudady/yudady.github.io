---
in: null
title: MiniMax M2.5 本地 AI 模型完整指南
aliases: [MiniMax M2.5 本地 AI 模型完整指南, "M2.5编码模型", "MiniMax M2.5 本地 AI 模型完整指南", "MiniMax M2.5评测", "MiniMax本地部署"]
tags: ["本地部署", "本地模型", "编码", "AI模型", AI, DeepSeek, GLM5, Kimi, MiniMax M2.5, OpenClaw]
source: [https://huggingface.co/MiniMaxAI, https://www.minimax.io, https://www.youtube.com/watch?v=yWXK6zu_kGE]
author: "影片原作者 + AI 补充"
created: 2026-02-22 17:56
updated: 2026-03-07 22:06
description: "MiniMax M2.5 是一款极致性价比的本地 AI 模型，230B 总参数仅 10B 激活，支持 100 TPS 推理速度，SWE-Bench 达到 80.2%，成本仅为 Claude 的 1/10。"
level: intermediate
stars: 5
category: "技术"
summary: "MiniMax M2.5本地AI模型详细指南"

---

# MiniMax M2.5 本地 AI 模型完整指南

## 目录

- [核心规格](#核心规格)
- [基准测试成绩](#基准测试成绩)
- [本地部署指南](#本地部署指南)
- [量化版本选择](#量化版本选择)
- [评测测试结果](#评测测试结果)
- [与其他模型对比](#与其他模型对比)
- [OpenClaw/VS Code 集成](#openclawvs-code-集成)
- [最佳实践](#最佳实践)
- [常见问题](#常见问题)
- [参考资料](#参考资料)

---

## 核心规格

### 模型参数

| 指标 | 数值 | 说明 |
|------|------|------|
| **总参数量** | 229B (2290亿) | MoE 混合专家架构 |
| **激活参数量** | 10B (100亿) | 仅 4.3% 参数被激活 |
| **上下文窗口** | 200K tokens | 超长上下文支持 |
| **推理速度** | 50-100 TPS | Lightning 版达 100 TPS |

### 核心技术

```
┌─────────────────────────────────────────────────────┐
│              MiniMax M2.5 架构                       │
├─────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐                │
│  │ Forge RL    │    │ CISPO 算法   │                │
│  │ Agent 框架  │    │ 训练稳定性   │                │
│  └─────────────┘    └─────────────┘                │
│                                                     │
│  ┌─────────────────────────────────────────────┐   │
│  │  MoE 混合专家架构                             │   │
│  │  229B 总参数 → 10B 激活参数                   │   │
│  └─────────────────────────────────────────────┘   │
│                                                     │
│  ┌─────────────┐    ┌─────────────┐                │
│  │ 过程奖励机制 │    │ 树状合并策略 │                │
│  │ 信用分配    │    │ 40x 训练加速 │                │
│  └─────────────┘    └─────────────┘                │
└─────────────────────────────────────────────────────┘
```

### 价格对比

| 模型 | 输入价格 | 输出价格 | 相对成本 |
|------|---------|---------|----------|
| **MiniMax M2.5** | $0.30/M | $1.20/M | **1x** |
| **M2.5 Lightning** | $0.30/M | $2.40/M | 1.5x |
| Claude Opus 4.6 | $15/M | $75/M | 50x |
| GPT-4 Turbo | $10/M | $30/M | 20x |

---

## 基准测试成绩

### 编程能力

| 评测项目 | MiniMax M2.5 | Claude Opus 4.6 | GLM-5 |
|----------|-------------|-----------------|-------|
| **SWE-Bench Verified** | 80.2% | 80.8% | 77.8% |
| **Multi-SWE-Bench** | **51.3%** 🥇 | 50.3% | - |
| **OpenCode** | **76.1%** | 75.9% | - |
| **Droid** | **79.7%** | 78.9% | - |
| **BFCL Multi-Turn** | **76.8%** | 63.3% | - |

### Agent 能力

| 能力维度 | 得分 |
|----------|------|
| BrowseComp (搜索) | 76.3% |
| RISE (搜索效率) | 节省 20% 搜索轮次 |
| GDPval-MM (办公) | 59.0% |

### 性能提升

```
M2.5 相比 M2.1 的提升：
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
BFCL 工具调用:    34% → 76%  (+124%)
Office 办公:      24.6% → 59% (+140%)
推理速度:                      +37%
Token 效率:                    -5.4%
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 本地部署指南

### 方法一：Ollama（推荐新手）

```bash
# 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 拉取并运行
ollama run minimax-m2.5
```

### 方法二：vLLM（生产环境）

```bash
# 安装 vLLM
pip install vllm==0.4.0

# 启动服务（OpenAI 兼容）
vllm serve MiniMaxAI/MiniMax-M2.5 \
    --quantization awq \
    --tensor-parallel-size 1 \
    --gpu-memory-utilization 0.9 \
    --port 8000
```

### 方法三：HuggingFace 直接下载

```bash
pip install -U huggingface_hub
huggingface-cli download MiniMaxAI/MiniMax-M2.5 \
    --local-dir ./MiniMax-M2.5
```

### 方法四：llama.cpp（CPU/混合推理）

```bash
# 转换为 GGUF 格式
python convert-hf-to-gguf.py ./MiniMax-M2.5 \
    --outfile MiniMax-M2.5-f16.gguf

# 量化
./quantize MiniMax-M2.5-f16.gguf MiniMax-M2.5-Q4_K_M.gguf Q4_K_M

# 运行
./main -m MiniMax-M2.5-Q4_K_M.gguf -p "Your prompt" -n 512
```

---

## 量化版本选择

### 硬件需求对照表

| 量化版本           | 显存需求   | 系统内存   | 精度损失 | 适用场景  |
| -------------- | ------ | ------ | ---- | ----- |
| **INT4 (AWQ)** | 8-12GB | 32GB+  | <2%  | 日常使用  |
| **Q4_K_M**     | ~12GB  | 32GB+  | 较低   | 性价比之选 |
| **Q5_K_M**     | ~16GB  | 64GB+  | 很低   | 追求精度  |
| **Q6_K**       | ~20GB  | 128GB+ | 极低   | 专业用途  |
| **Q8_0 / Q9**  | 24GB+  | 256GB+ | 几乎无  | 极致性能  |

### GPU 推荐配置

| GPU 型号 | 推荐量化 | 预期性能 |
|----------|---------|---------|
| RTX 3060 (16GB) | INT4 | 可运行 |
| RTX 3090/4090 (24GB) | Q6/Q8 | 流畅运行 |
| 2x RTX 3090 | FP16 | 完整精度 |
| Mac Studio (192GB+) | Q6/Q9 | 35-40 TPS |

### 实测数据（视频评测）

| 版本 | 内存占用 | 推理速度 |
|------|---------|---------|
| **Q9** | 239 GB | 33-35 TPS |
| **Q6** | 173 GB | 35-40 TPS |

---

## 评测测试结果

### 3D 地球生成测试

| 模型 | 效果评价 | Token 产出 |
|------|---------|-----------|
| **GLM5** | 华丽，正确纹理 | ~7000 |
| **Kimi K2.5** | 最华丽，有深度贴图 | ~6000 |
| **DeepSeek V3** | 有云层和大气 | ~5000 |
| **MiniMax M2.5 (Q9)** | 华丽，画廊级别 | ~4000 |
| **MiniMax M2.5 (Q6)** | 光照略有问题 | ~2000 |

### 逻辑推理测试

| 测试项 | 禁用思考 | 启用思考 |
|--------|---------|---------|
| 外科医生问题 | ❌ 答错（母亲） | ⚠️ 需自动触发思考 |
| 电车难题（变体） | ⚠️ 含糊 | ⚠️ 无明确答案 |
| 洗车问题 | ❌ 答错（走路） | ✅ 正确（开车） |

### 编码能力测试

| 测试项 | 结果 |
|--------|------|
| Regex 模式匹配 | ✅ 启用思考后通过 |
| 文本宽度调试 | ⚠️ 未识别换行符 |
| Flappy Bird 3D | ⚠️ 忘记放管道间隙 |

### 工具调用测试

| 测试项 | 结果 | 备注 |
|--------|------|------|
| 网页内容获取 | ✅ 成功 | 自定义字符数 |
| 维基百科查询 | ✅ 正确答案 | 有点浪费（读取整页） |
| Apple Notes | ✅ 成功 | - |

---

## 与其他模型对比

### 核心对比表

| 维度 | MiniMax M2.5 | Claude Opus 4.6 | GLM-5 | Kimi K2.5 |
|------|-------------|-----------------|-------|-----------|
| **总参数** | 230B | ~400B+ | 744B | - |
| **激活参数** | 10B | ~400B+ | 40B | ~50B |
| **推理速度** | 100 TPS | ~33 TPS | ~66 TPS | - |
| **SWE-Bench** | 80.2% | 80.8% | 77.8% | - |
| **成本** | **$1.5/M** | $90/M | $4.2/M | $2.3/M |
| **性价比** | 🥇 极高 | 低 | 中 | 中 |

### 优势场景对比

| 场景 | 推荐模型 | 原因 |
|------|---------|------|
| 成本敏感项目 | **MiniMax M2.5** | 成本仅 1/10-1/20 |
| 大规模 Agent | **MiniMax M2.5** | 100 TPS，成本可控 |
| 前端 UI 开发 | **MiniMax M2.5** | 视觉效果最佳 |
| 数学/科学计算 | GLM-5 | 推理能力最强 |
| 多模态任务 | Kimi K2.5 | 原生支持图像视频 |
| 法律/金融高精度 | Claude Opus 4.6 | 综合准确性最高 |
| 复杂系统工程 | Claude Opus 4.6 | 不计成本的最强选择 |

---

## OpenClaw/VS Code 集成

### 配置 OpenClaw 使用 MiniMax

```json
// OpenClaw RAW 配置
{
  "model": {
    "id": "minimax-m2.5",
    "provider": "openai-compatible",
    "api_base": "http://localhost:8000/v1"
  }
}
```

### 启用 Prompt Caching

```json
// settings.json
{
  "prompt_caching": {
    "enabled": true,
    "fixed_date": true  // 缓存有效期一年
  }
}
```

### 配置 VS Code Kilo Code

```bash
# 环境变量配置
export OPENAI_BASE_URL=http://localhost:8000/v1
export OPENAI_API_KEY=sk-fake-key
```

### 注意事项

> ⚠️ **重要**：使用 Infroner 时确保禁用工具调用，避免与 OpenClaw/Kilo Code 的工具调用冲突。

---

## 最佳实践

### 思考模式使用建议

```
┌─────────────────────────────────────────────────────┐
│          思考模式使用策略                            │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ✅ 推荐禁用思考模式，让模型自动触发                 │
│                                                     │
│  原因：                                             │
│  • 禁用时模型会自行判断是否需要思考                 │
│  • 自动触发思考后往往能得出正确答案                 │
│  • 强制启用可能导致错误推理                         │
│                                                     │
│  适用任务：                                         │
│  • 复杂编码问题 → 启用思考                          │
│  • 简单问答 → 禁用思考                              │
│  • 逻辑推理 → 启用思考                              │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### Q6 vs Q9 选择建议

| 情况 | 推荐版本 |
|------|---------|
| 有 256GB+ 内存 | **Q9**（更好质量） |
| 只有 192GB 内存 | **Q6**（能运行） |
| 追求速度 | **Q6** |
| 追求质量 | **Q9** |

### Token 效率优化

- 启用 **Prompt Caching** 避免重复处理
- 使用 **Fixed Date** 延长缓存有效期
- 预估：40000 token ≈ 10 GiB 内存

---

## 常见问题

### Q1: 为什么我的推理速度很慢？

**可能原因**：
1. 量化版本过高（如 Q9）
2. 内存不足导致交换
3. 后台进程占用资源

**解决方案**：使用 Q6 或 INT4 量化

### Q2: 逻辑推理测试失败怎么办？

**解决方案**：
- 禁用思考模式，让模型自动判断
- 或在提示词中明确要求"请仔细思考"

### Q3: Q6 版本光照效果不好？

**原因**：量化导致的艺术细节损失

**解决方案**：
1. 使用 Q9 版本
2. 或在提示词中增加光照描述
3. 多次请求让模型修复

### Q4: 与 OpenClaw 集成时工具调用混乱？

**解决方案**：
- 禁用 Infroner 的工具调用功能
- 只使用 OpenClaw 的工具定义

### Q5: 如何判断应该用 M2.5 还是其他模型？

```
选择决策树：

需要极致准确？ ───── 是 ──→ Claude Opus 4.6
      │
      否
      ↓
预算有限？ ───── 是 ──→ MiniMax M2.5
      │
      否
      ↓
需要多模态？ ───── 是 ──→ Kimi K2.5
      │
      否
      ↓
需要数学推理？ ───── 是 ──→ GLM-5
      │
      否
      ↓
默认选择 ──────────→ MiniMax M2.5
```

---

## 参考资料

### 官方资源

- **官网**: https://www.minimax.io/models/text
- **API 平台**: https://platform.minimaxi.com
- **HuggingFace**: https://huggingface.co/MiniMaxAI/MiniMax-M2.5

### 社区教程

- [不用云服务器！MiniMax M2.5 本地运行攻略](https://m.toutiao.com/a7608921927607960115/)
- [Mac 本地部署 MiniMax M2.5 狂飙 40+ Tokens/s](https://m.bilibili.com/video/BV1qgZ7BXEpX/)

### 视频来源

- 原始评测视频: https://www.youtube.com/watch?v=yWXK6zu_kGE

---

## 总结

### ✅ 优点

- **极致性价比**：成本仅为 Claude 的 1/10-1/20
- **高效推理**：100 TPS 速度
- **编程能力强**：Multi-SWE-Bench 行业第一
- **低显存占用**：10B 激活参数
- **原生 Agent 设计**：Forge RL 框架

### ❌ 缺点

- Token 消耗较高
- 复杂后端逻辑处理能力不足
- 逻辑推理偶有失败
- 非编程领域综合能力有差距
- 量化版本质量差异明显

### 🎯 适用场景

| 推荐使用 | 不推荐使用 |
|----------|-----------|
| 成本敏感项目 | 高精度金融/法律 |
| 大规模 Agent 部署 | 复杂科学研究 |
| 前端 UI 开发 | 需要多模态任务 |
| 快速原型开发 | 极致准确性要求 |

---

*本指南基于 2026-02-22 的信息整理，如有更新请参考官方文档。*

## 相关笔记
- [[004-ai-tools/ollama/Ollama|Ollama]] - 本地 LLM 运行环境
- [[004-ai-tools/ollama/README|Ollama README]] - HuggingFace 配置
- [[004-ai-tools/ollama/Use Ollama with any GGUF Model on Hugging Face Hub|GGUF 模型]]
- [[004-ai-tools/claude/claude|Claude Code]] - AI 编程助手
- [[004-ai-tools/gemini/README|Gemini]] - Google AI
- [[03-knowledge/技术/人工智能/PicoClaw-轻量级AI助手完整指南|PicoClaw]]
- [[03-knowledge/技术/人工智能/AntiGravity-GravityClaw-本地优先AI助手|Antigravity]]
- [[MOC-AI编程工具|AI 编程工具 MOC]]