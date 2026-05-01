---
created: 2026-04-11
tags:
  - 工具/AI-Agent
  - 工具/Claude-Code-Skill
  - 主题/认知科学
  - 主题/提示工程
source: https://github.com/alchaincyf/nuwa-skill
---

# nuwa-skill - Claude Code 认知框架提取工具

> [!info] 基本信息
> - **仓库**: https://github.com/alchaincyf/nuwa-skill
> - **作者**: 花叔 Huashu (@AlchainHust)，独立开发者，代表作小猫补光灯（AppStore 付费榜 Top1）
> - **协议**: MIT
> - **平台**: Claude Code Skill（兼容 skills.sh 标准）
> - **灵感来源**: [colleague-skill](https://github.com/titanwings/colleague-skill)（蒸离职同事成 AI Skill，几天破 5000 星）
> - **多语言**: 中文、英文、日文、韩文、西班牙文

---

## 一句话定位

从公开人物信息中提取认知操作系统（心智模型 + 决策启发式 + 表达 DNA），生成可运行的 Claude Code Skill。不是角色扮演，是认知架构提取。

---

## 核心解决的问题

把名人的「思维方式」系统化地提取出来，让你能用芒格、马斯克、费曼的认知框架来分析自己的问题。超越了简单的名人语录收集，而是提取出一套可推导、可预测的认知操作规则。

---

## 工作原理（四步流程）

### 1. 六路并行采集
6 个 Agent 同时跑，各自采集一个维度的信息：
- 著作
- 播客/访谈
- 社交媒体
- 批评者视角
- 决策记录
- 人生时间线

### 2. 三重验证提炼
一个观点要被收录为心智模型，必须同时满足：
- **跨领域复现**: 在 2+ 个领域出现过（不是随口一说）
- **有预测力**: 能推断对新问题的立场
- **有排他性**: 不是所有聪明人都会这么想

### 3. 构建 Skill
每个 Skill 包含：
- 3-7 个心智模型
- 5-10 条决策启发式
- 表达 DNA（语气、节奏、用词偏好）
- 价值观与反模式
- 诚实边界（明确标注做不到什么）

### 4. 质量验证
- 用 3 个此人公开回答过的问题测试，方向一致才通过
- 再用 1 个他没讨论过的问题测试，应表现适度不确定

---

## 提取的五层模型

| 层次 | 说明 |
| --- | --- |
| **怎么说话** | 表达 DNA — 语气、节奏、用词偏好 |
| **怎么想** | 心智模型、认知框架 |
| **怎么判断** | 决策启发式 |
| **什么不做** | 反模式、价值观底线 |
| **知道局限** | 诚实边界 |

---

## 已蒸馏人物（13+1）

### 人物 Skill

| 人物 | 领域 | 安装命令 |
|------|------|---------|
| Paul Graham | 创业/写作/产品/人生哲学 | `npx skills add alchaincyf/paul-graham-skill` |
| 张一鸣 | 产品/组织/全球化/人才 | `npx skills add alchaincyf/zhang-yiming-skill` |
| Karpathy | AI/工程/教育/开源 | `npx skills add alchaincyf/karpathy-skill` |
| Ilya Sutskever | AI 安全/scaling/研究品味 | `npx skills add alchaincyf/ilya-sutskever-skill` |
| MrBeast | 内容创造/YouTube 方法论 | `npx skills add alchaincyf/mrbeast-skill` |
| 特朗普 | 谈判/权力/传播/行为预判 | `npx skills add alchaincyf/trump-skill` |
| 乔布斯 | 产品/设计/战略 | `npx skills add alchaincyf/steve-jobs-skill` |
| 马斯克 | 工程/成本/第一性原理 | `npx skills add alchaincyf/elon-musk-skill` |
| 芒格 | 投资/多元思维/逆向思考 | `npx skills add alchaincyf/munger-skill` |
| 费曼 | 学习/教学/科学思维 | `npx skills add alchaincyf/feynman-skill` |
| 纳瓦尔 | 财富/杠杆/人生哲学 | `npx skills add alchaincyf/naval-skill` |
| 塔勒布 | 风险/反脆弱/不确定性 | `npx skills add alchaincyf/taleb-skill` |
| 张雪峰 | 教育/职业规划/阶层流动 | `npx skills add alchaincyf/zhangxuefeng-skill` |

### 主题 Skill

| 主题 | 领域 | 安装命令 |
|------|------|---------|
| X 导师 | X/Twitter 运营全栈 | `npx skills add alchaincyf/x-mentor-skill` |

---

## 使用方式

### 安装
```bash
npx skills add alchaincyf/nuwa-skill
```

### 蒸馏新人物
```
> 蒸馏一个保罗·格雷厄姆
> 造一个张小龙的视角 Skill
> 帮我做一个段永平的 Skill
```

### 调用已有 Skill
```
> 用芒格的视角帮我分析这个投资决策
> 费曼会怎么解释量子计算？
> 切换到 Naval，我在纠结三件事
```

---

## 效果示例

**Naval 风格回答**:
```
用户: 我同时想做自媒体、写书、做独立开发，精力完全不够
Naval: 你列了三个欲望。每个欲望都是你跟不快乐签的一份合同。
       不是精力不够，是合同太多。
       问你自己：哪一个做起来你会忘记时间？
       那个就是你的特定知识所在。
       不是三选一。是先一，再一，再一。
       串行复利，不是并行内耗。
```

**马斯克风格回答**:
```
用户: 我们的 SaaS 产品获客成本太高了
马斯克: 先别想怎么降。先算物理极限。
       获客的最小必要动作是什么？用户从知道你到付费，
       信息传递的理论最短路径是什么？
       现在你的实际路径是理论路径的几倍？
       如果超过 3 倍，中间一定有可以砍掉的步骤。
       不是优化漏斗，是质疑漏斗本身该不该存在。
```

---

## 与 Hermes Skills 的兼容性

**不能直接兼容，但适配成本低：**

| 维度 | nuwa-skill 格式 | Hermes 格式 |
|------|----------------|-------------|
| Frontmatter | 简单 metadata | 标准 YAML（name + description，description 用 "Use when..." 触发格式） |
| 目录结构 | SKILL.md + references/ | SKILL.md + references/ + templates/ + scripts/ |
| 内容格式 | Markdown 认知框架 | Markdown 工作流程 |

**适配方案**: 每个 Skill 约需 15-30 分钟（补充 frontmatter + 调整结构），核心内容完全兼容。

---

## 实际价值评估

| 场景 | 价值 | 说明 |
|------|------|------|
| 产品设计决策 | ★★★★★ | 用乔布斯/张一鸣的框架审视产品方向 |
| 写作/汇报 | ★★★★★ | 用 Graham/费曼的表达方式优化沟通 |
| 投资决策 | ★★★★ | 用芒格/塔勒布的多维思维做分析 |
| 团队管理 | ★★★★ | 参考不同领导者的管理心智模型 |
| 自媒体运营 | ★★★★ | MrBeast/X 导师的内容方法论 |

**总体推荐度**: 4/5 — 作为思维工具辅助决策很有价值，但不能替代真正的专业判断。

---

## 仓库结构

```
nuwa-skill/
├── SKILL.md                      # 女娲本体（Meta-Skill）
├── references/
│   ├── extraction-framework.md   # 提炼方法论（核心文档）
│   └── skill-template.md         # 生成 Skill 的模板
├── examples/                     # 13 个完整示例 + 调研数据
│   ├── steve-jobs-perspective/   # 含实战对话记录
│   ├── elon-musk-perspective/
│   ├── naval-perspective/
│   ├── munger-perspective/
│   ├── feynman-perspective/
│   ├── taleb-perspective/
│   ├── zhangxuefeng-perspective/
│   ├── paul-graham-perspective/
│   ├── zhang-yiming-perspective/
│   ├── andrej-karpathy-perspective/
│   ├── ilya-sutskever-perspective/
│   ├── trump-perspective/
│   └── mrbeast-perspective/
└── wechat-qrcode.jpg
```

---

## 安全与隐私

- 仅基于公开信息（著作、演讲、采访、社交媒体）
- 每个 Skill 明确标注「诚实边界」：蒸餾不了直觉、捕捉不了突变、公开表达 ≠ 真实想法
- MIT 协议 — 随便用、随便改、随便造

---

## 关键洞察

1. **不是角色扮演** — 提取的是认知操作系统，不是名人语录库。用框架做推理，不是复读机。
2. **三重验证是亮点** — 跨领域复现 + 预测力 + 排他性，确保提取的是真正的思维模式，不是巧合。
3. **诚实边界设计** — 每个 Skill 都明确标注局限，这在 AI 工具中很少见，增加了可信度。
4. **Meta-Skill 设计** — nuwa 本身是一个「造 Skill 的 Skill」，可以蒸餾任意人物，不只是预置列表。
5. **colleague-skill 的自然延伸** — 从蒸餾同事到蒸餾名人，思路清晰，定位精准。
