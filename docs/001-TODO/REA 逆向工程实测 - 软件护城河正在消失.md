---
title: REA 逆向工程实测 - AI 拆解软件与护城河正在消失
aliases: ["Reverse Engineer Anything", "AI 开始逆向一切", "REA 实测"]
tags:
  - ai-agent
  - reverse-engineering
  - mcp
  - codex
  - hermes
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=iDBDZoEBofQ"
  - "https://github.com/morluto/rea"
  - "https://rea.tools/showcase/dx-ball/"
  - "https://github.com/N0zoM1z0/dx-ball"
  - "https://github.com/storytold/photocraft"
  - "https://wow-site-steel.vercel.app/"
author: WOW Insight（YouTube 频道 wow）
created: 2026-10-15
updated: 2026-10-15
description: WOW Insight 实测 REA（Reverse Engineer Anything）逆向 Apple 官网动效并交付上线：调查式逆向方法论、DX-Ball 官方案例真实数字、双 Agent 流水线分工，以及软件护城河从「实现」转向「定义与审美」的省思——全部关键声称经 GitHub API 与官方案例页逐条交叉验证
level: intermediate
stars: 4
---

# REA 逆向工程实测 - AI 拆解软件与护城河正在消失

> WOW Insight（wow 频道）2026-10-14 发布的实测视频：用 GitHub 爆火的 REA（Reverse Engineer Anything，morluto/rea）逆向 Apple 官网动效，把学到的设计机制应用到自己的 WOW Insight 网站，再交给 Hermes 部署上线。视频从 PhotoCraft、游戏混搭 mod、DX-Ball 二进制还原三个现场切入，最后落到一个判断：当 AI 拉平「实现」的门槛，软件护城河正在从技术实现转向产品定义与审美。
>
> 本笔记以视频字幕为一手素材，所有关键数字与产品归属用 GitHub API、REA 官方案例页、GitHub 仓库逐条核验（见文末[验证与勘误记录](#验证与勘误记录)）。

> [!info] 视频基本信息
> - **频道**：WOW Insight（wow）｜ **发布**：2026-10-14 ｜ **时长**：12:41
> - **播放/点赞**：6,660 / 114（发布次日快照）
> - **描述中的流水线**：REA → Codex → Gemini 4 → Hermes 升级网站
> - **产物**：https://wow-site-steel.vercel.app/ （已上线，数据快照 2026/10/14，378 期内容）
> - **相关仓库**：REA https://github.com/morluto/rea ｜ PhotoCraft https://github.com/storytold/photocraft

## 目录

- [一、三个现场：AI 重构正在发生](#一、三个现场：ai-重构正在发生)
- [二、REA 是什么：Agent 的调查装备](#二、rea-是什么：agent-的调查装备)
- [三、实战：逆向 Apple 官网动效](#三、实战：逆向-apple-官网动效)
- [四、双 Agent 接力流水线](#四、双-agent-接力流水线)
- [五、护城河省思：实现贬值，定义升值](#五、护城河省思：实现贬值，定义升值)
- [六、行动建议](#六、行动建议)
- [验证与勘误记录](#验证与勘误记录)
- [参考资料](#参考资料)
- [相关笔记](#相关笔记)

---

## 一、三个现场：AI 重构正在发生

视频开场连放三个案例，从「重写应用」到「混搭世界」再到「还原二进制」，难度递进：

| 案例 | 内容 | 手段 | 核实结果 |
|------|------|------|----------|
| Adobe 替代潮 | 7 款 Adobe 软件的开源替代，主打 PhotoCraft（Photoshop 挑战者） | AI 辅助开发，纯 Rust 重写，代码全公开 | ✅ PhotoCraft（storytold/photocraft）确为「clean-room reimplementation of Photoshop in pure Rust」，61K stars；但套件实际更大（见下） |
| 游戏世界混搭 | 马里奥进《艾尔登法环》三段跳打 Boss；蜘蛛侠在《阿卡姆骑士》的哥谭荡蛛丝；Minecraft 与其他游戏互联 | AI 辅助 mod：拉取一方游戏的碰撞数据/摆荡点，喂给另一方的引擎 | ✅ 2026-10 媒体有报道（pcmrace 2026-10-02）；更早的 Mario mod 是蘑菇皮肤 cosmetic，2026 新一波做到了引擎级融合 |
| DX-Ball 二进制还原 | AI 追踪 1996 年 Windows 游戏 DX-Ball 的机器指令，还原砖块音效计算 | REA 原生二进制分析（Ghidra 类反编译 + 行为验证闭环） | ⚠️ 游戏与 63 字节重编译匹配属实；但函数用途口播有误（是声像计算，不是挡板移动，见勘误表 #7） |

**PhotoCraft 背后的 ArtCraft 套件**（storytold org，62 个公开仓库）远不止 7 款——明确对标 Adobe 的至少 5 款，另覆盖 Word/Excel/PPT/Pro Tools/AutoCAD/PDF/视频剪辑：

| Craft 应用 | 对标产品 | Stars（2026-10-15） |
|------------|----------|--------------------|
| PhotoCraft | Adobe Photoshop | 61.1K |
| PdfCraft | PDF 阅读编辑 | 13.7K |
| FilmCraft | 非线性视频剪辑 | 13.3K |
| LightCraft | Lightroom 式修图 | 12.6K |
| WordCraft | Microsoft Word | 12.4K |
| VectorCraft | Illustrator 式矢量 | 8.9K |
| EffectCraft | After Effects 动效合成 | 6.5K |
| GridCraft | Excel 表格 | 4.9K |
| CADCraft | AutoCAD | 4.8K |
| DesignCraft | InDesign 式排版 | 3.9K |

PhotoCraft 的两个细节值得注意：wgpu GPU 合成器（Metal/Vulkan/DX12/WebGPU）、无 Electron 无 WebView；以及**Agent-ready 设计**——每个操作都是命令，同一引擎可从 UI、CLI、JSON 控制通道或 MCP server 驱动。它自己就是「为 AI 协作而建」的软件。

**DX-Ball 的官方真实数字**（rea.tools/showcase/dx-ball/，checkpoint 2026-10-07）：

```
项目目标：把 DX-Ball 1.07（1996）重建为可维护的 C 版本
├─ 55 个已维护 C 函数（PROGRESS.md: source-present 55）
├─ 45,380 次与原版代码的直接用例对比
├─ 33 个函数达到重编译字节级完全匹配
├─ 声像函数（0x406400）：63 字节与原版完全一致 ← 视频引用的就是这个
└─ 挡板/球的物理（ball and paddle physics）：官方明说仍在进行中
```

声像函数还原出的 C 代码只有一行核心逻辑（砖块屏幕坐标 → 左右声道声像值）：

```c
DxBallInt dxball_screen_pan(DxBallInt x)
{
    double pan;
    pan = (double)x;
    pan = pan * 1.5625;      // 常量 1.5625 从可执行文件 0x420068 读出
    pan = pan - 500.0;       // 常量 500.0 从 0x4210a0 读出
    pan = pan * dxball_pan_scale;
    return (DxBallInt)pan;   // 左边缘 -500，中心 0，右边缘 +500
}
```

---

## 二、REA 是什么：Agent 的调查装备

### 定位：不是新反编译器，是 MCP 编排层

视频把 REA 的本质讲得很准（与官方 README 一致）：REA **没有发明任何新的反编译技术**，它把既有的逆向分析能力（Hopper、Ghidra、IDA、JS/Electron 静态分析、网站调试）组织成 AI Agent 可以调用的**调查工具集**——一个 MCP server。Agent 从「瞎猜」变成「基于实测数据迭代」。

| 分析对象 | 依赖工具 | 说明 |
|----------|----------|------|
| 原生二进制（native binaries） | Hopper / Ghidra / IDA（用已装好的，setup 可代装 Hopper） | 反编译、函数分析、字节读取 |
| JavaScript / Electron 应用 | 内置静态分析（ASAR/JS 模块） | 不需要原生分析引擎 |
| .NET 程序集 | 内置 | IL 层面调查 |
| 网站（如 Apple 官网） | 网页调试类工具 | 结构、CSS、JS、滚动行为，产出可溯源调查报告 |
| 运行时行为 | 运行时验证 | 行为对比测试 |

```
REA 工作闭环（视频 5:08，官方方法论一致）

  观察行为 → 提出实现猜想 → 写出版本 → 与真实行为对比
     ▲                                       │
     └───────── 不一致：继续调查、修正 ←────────┘

  AI 的优势：不厌其烦地跑这个循环
  边界：验证通过 ≠ 所有大系统都能轻易重建
       （海量相互依赖、性能问题、运行环境、人的判断仍在）
```

### 安装与使用（官方 README 的真实命令）

```bash
# 一条命令接入 agent（支持 Claude Code、Codex、Cursor、Gemini CLI、Grok Build 等）
npx rea-agents setup

# 终端直接分析 Electron/JS 应用目录或 ASAR
npx -y rea-agents@latest analyze-javascript-application /absolute/path/to/app --json

# 全局安装
npm install --global rea-agents
rea --help

# 原生分析示例（DX-Ball 官方案例）
scripts/rea function original/DXBALL.EXE 0x00406400 \
  --snapshot .analysis/rea/dxball.snapshot.json --json
```

给 Agent 的典型 prompt（官方案例原文）：`Use REA to find how DX-Ball calculates sound panning. Explain the calculation and show the code.`

> [!warning] 假仓库警告（官方 README 原文提醒）
> 只从 **morluto/rea**、**rea.tools**、npm 包 **rea-agents** 获取 REA。相似仓库名或抄来的 README 不代表官方——实测确有仿冒仓库存在（同名 fork 类仓库 433 stars）。安装/下载链接务必走官方三源。

### 什么时候该用 REA：判断决策树

```
想复刻/理解某产品的功能或交互
├─ 目标有公开源码？
│    └─ 是 → 直接读源码，别逆向
├─ 只想要「那种感觉」？
│    ├─ 裸 prompt「做个 Apple 风格」→ 黑底大字+弹入动画，形似神不似
│    └─ 更好 → REA 调查真实实现，汲取节奏与结构思想
├─ 需要行为级还原（无源码二进制）？
│    └─ REA 原生分析路径（Ghidra/Hopper/IDA + 行为验证闭环）
└─ 调查完怎么落地？
     ├─ 需要完整机制 → 按原架构实现（如完整 video scrub 管线）
     └─ 只需视觉节奏 → 原生 CSS/JS + IntersectionObserver 轻量实现
```

---

## 三、实战：逆向 Apple 官网动效

### 问题定义

作者的网站托管几百期视频，功能齐全（浏览/搜索/播放列表）但「只是一排卡片」——一张图一个标题接下一张。而 Apple 的产品页：一台 Mac、一个功能都有专属舞台，滚动到哪、什么文字停留多久，全是设计。直接对 AI 说「做一个 Apple 风格的网站」，只会得到黑底、大字、几个弹入动画——**看起来像，但 AI 并不知道 Apple 是怎么做的**，剩下的活还是得人来干，或者烧几天 token 摸索。

所以策略反过来：**先让 Agent 调查真实的 Apple 网站，再回来 redesign 自己的站**。Codex 在 Mac Studio 上接 REA，观察页面结构、CSS、JavaScript 和真实滚动行为，留下一份可溯源的调查报告。

### 两个核心发现

**发现 1：时序靠 Group 机制组织，不是散装 delay（8:00）**

滚动入场时标题先出、描述稍后跟进、整组内容像排练过一样入场。标准做法是滚动检测 + 给文本加延迟；但 REA 调查发现 Apple 页面有自己的 **group 机制**统一组织 scroll trigger 和动画序列。

| | 想当然的实现 | Apple 的真实机制（REA 调查结论） |
|---|---|---|
| 入场时序 | 每个元素独立检测滚动 + 各自 delay | Group 机制统一编排触发与序列 |
| 效果 | 形似，节奏散 | 排练感：整组内容按设计节奏入场 |

**发现 2：Video Scrub——你以为的 CSS 动画是视频（8:44）**

页面里那些「跟着手指走」的 3D 旋转展示：滚动前进它前进，停下它也停。调查结论：用的是**预制作视频 + video scrub 控制**——页面把滚动进度映射到视频时间轴，而不是标准播放。这解释了一个反直觉事实：**看到漂亮动画 ≠ 它是 CSS 实时渲染的**。

| | CSS 实时渲染 | Video Scrub（Apple 的做法） |
|---|---|---|
| 产物 | 代码驱动的变换 | 预渲染视频 + 进度映射 |
| 视觉上限 | 受浏览器渲染能力约束 | 可用离线渲染的重质感 3D |
| 交互感 | 代码控制 | 滚动进度 ↔ 视频时间轴线性对应 |
| 成本 | 轻 | 重（需要整套视频生产管线） |

### 工程取舍：汲取逻辑，不照搬系统 ✅/❌

✅ 澄清对手机理后再决定自己要哪部分——「作品集站不需要背负整套视频系统」
✅ 用原生 CSS + 少量 JS + IntersectionObserver 实现自己的滚动入场与节奏转场
✅ 明确这不是克隆 Apple 代码——Apple 用自有私有时序机制，自己用更简单的等价实现
✅ 几小时内建完 demo、录视频分享，控制投入产出比
❌ 把 Apple 的全部机制搬进自己的站
❌ 跳过调查直接让 AI 凭印象模仿（黑底大字警告）
❌ 认为「屏幕上的动画都是 CSS 渲染的」

### 合规边界（作者主动声明，7:39）

只研究 Apple **公开网页加载到浏览器的内容**，不碰服务器、不破解软件。这条边界让整个行为停留在「学习公开可见的实现」范畴。

---

## 四、双 Agent 接力流水线

这是视频最有复用价值的部分：**研发 Agent 与运维 Agent 物理隔离，靠结构化交接文档接力**。

```
[Mac Studio 研发位] Codex + REA
   ├─ 逆向 apple.com（仅公开网页内容）
   ├─ 产出：调查报告 + 新版站点（独立实验版，不覆盖生产）
   └─ 写交接文档（调查结论/代码改动/数据规则/构建命令/注意事项）→ 停
                    │
                    ▼  交接文档 + 实验版代码
[Mac mini M4 生产位] Hermes（常驻，7x24）
   ├─ 检查真实生产环境
   ├─ 保留既有数据与人工修正
   ├─ 在独立副本中完成整合
   └─ 自动化更新管线：拉数据 → 算指标 → 查高清封面 → 生成页面 → 验证版本 → 准备发布
                    │
                    ▼
        Vercel 上线（wow-site-steel.vercel.app）
        日常：每晚 23:00 自动拉数据 → commit GitHub → 部署
```

| | Codex（Mac Studio） | Hermes（Mac mini M4） |
|---|---|---|
| 角色 | 研发实证：调查 + 创建 | 生产运维：接管 + 发布 |
| 关键动作 | REA 逆向 Apple、写新站、写交接文档 | 检查环境、独立副本整合、跑自动化管线 |
| 红线 | 不覆盖生产项目 | 保留数据与手工编译，先看环境再动手 |
| 交接物 | 调查报告 + 代码改动 + 数据规则 + 构建命令 + 注意事项 | 人工验收（作者亲自打开页面确认效果） |

**交接文档是整条流水线的枢纽**。没有它，运维 Agent 只能拿到一坨代码盲改生产；有了它，Hermes 才能知道哪些数据规则要保留、构建命令是什么、哪些地方有坑。这与视频的另一句话互为表里：「两个 Agent 接力完成了整个项目，但真正重要的是人自己的选择与判断」——分工、边界、验收标准都是人定的。

---

## 五、护城河省思：实现贬值，定义升值

视频的收束论点（11:30 起）：当 AI 能把马里奥塞进艾尔登法环、把 DX-Ball 的函数还原到字节级一致，「功能实现」和「动效实现」不再构成壁垒。

| | 旧时代 | AI 逆向普及后 |
|---|---|---|
| 复刻一个交互 | 需要资深工程师人肉调试、函数调用追踪 | Agent + REA 自动调查、验证、重建 |
| 护城河来源 | 技术实现本身（我会做你不会） | 产品定义、审美取舍、整体体验 |
| 关键问题 | 怎么做出来 | **谁能创造出下一个让人着迷的超级马里奥** |
| 人的位置 | 写代码的人 | 界定业务边界、做架构取舍、定义产品的人 |

Apple 的魅力从来不只是那几段动画——就像好游戏绝不是「把熟脸角色塞进另一个世界」。视频的原话：真正值得期待的，是有人用这些能力去创造**从未见过的作品**。

```
资产可复制性判断（从视频论点推导）

一项资产是否还是护城河？
├─ 纯功能实现 / 动效实现 → 可被 Agent 逆向复刻 → 贬值
├─ 底层机制知识 → 可被 REA 调查还原 → 贬值（但学习价值仍在）
├─ 数据资产（如 378 期内容的积累）→ 随时间复利 → 升值
├─ 品牌心智 / 审美判断 → 无法从二进制里读出 → 升值
└─ 产品定义能力（决定做什么、不做什么）→ 升值
```

---

## 六、行动建议

1. **给 Agent 建立调查工具链**：在工作流中引入 REA / Ghidra / 浏览器调试协议这类调查工具，让 Agent 从「瞎猜」变成「基于实测数据迭代」。一条 `npx rea-agents setup` 即可接入主流 coding agent。
2. **避免技术过载**：学习优秀竞品时汲取「设计概念与时序逻辑」，落地时优先选最轻量、最稳定的原生方案（视频选择：IntersectionObserver 替代整套 video scrub 管线）。
3. **打造多 Agent 分工生产线**：研发实证 Agent（Codex）与线上运维 Agent（Hermes）物理隔离，配合结构化交接文档（调查报告/代码改动/数据规则/构建命令/注意事项），生产环境先验环境再整合。
4. **把重心转向不可复制的资产**：停止依赖「纯功能实现」做业务壁垒，深耕内容积累、品牌心智、数据资产与产品审美。

---

## 验证与勘误记录

| 视频声称 | 验证结果 | 来源 |
|----------|----------|------|
| 标题「21 万星」/ 口播「192K stars」（2:35） | ✅ 快照口径。GitHub API 2026-10-15 查询：**241,287 stars**。录制时约 192K → 标题 21 万 → 次日 24.1 万，增速属实，数字是移动目标 | api.github.com/repos/morluto/rea |
| REA 封装 Ghidra 等专业分析工具与网页调试接口 | ✅ README 原文：原生分析可使用已有 Hopper/Ghidra/IDA 安装；另有 JS/Electron/ASAR、.NET、网站分析能力。口播 ASR 把 Ghidra 听成了「GPT」（4:47） | morluto/rea README |
| REA 发明了新反编译器？ | ✅（与口播一致）官方明言：没有凭空发明反编译器，而是把既有逆向分析能力编排成 MCP 调查工具 | morluto/rea README |
| PhotoCraft 是 Rust 写的 Photoshop 开源挑战者 | ✅ storytold/photocraft：「clean-room reimplementation of Adobe Photoshop, rebuilt in pure Rust」，61,063 stars，MIT OR Apache-2.0，early alpha，2026-09-30 创建 | GitHub API + README |
| 「7 个 Adobe 替代品，全部 Rust、代码全公开」 | ⚠️ 口播口径。ArtCraft（storytold org）公开仓库 62 个，Craft 套件 14+ 款；明确对标 Adobe 的至少 5 款（PS/AI/LR/AE/ID 对应），另覆盖 Word/Excel/PPT/Pro Tools/AutoCAD/PDF/视频剪辑。「7」取决于统计口径，套件实际更大 | orgs/storytold repos API |
| 开发者「BrandThomas」展示成果 | ❓ ASR 转写无法坐实此人名；仓库归属 storytold org（ArtCraft 社区），官网 getartcraft.com 未列个人名。不采信该拼写 | org API + 官网 |
| DX-Ball：AI 还原「控制挡板移动」的计算规则 | ❌ 官方案例还原的是**声像（sound-pan）计算**——砖块被击中时屏幕坐标 → 左右声道声像值（pan = x×1.5625 − 500 再乘缩放）。挡板/球物理官方明说仍在进行中。Insights 与口播同源同误 | rea.tools/showcase/dx-ball/ |
| DX-Ball：325 组行为测试全部通过 | ❓ 官方数据中未定位到「325」。项目级：45,380 次直接用例对比（含声像测试所在的 gameplay owner 13,689 例）；函数级：声像函数重编译 **63 字节完全匹配** ✅（此数字属实）；全部函数累计精确字节 3,404、33 个函数字节级匹配。口播后段「3,000 sets」（5:24）疑为 3,404 或 45,380 的混串 | showcase 页 + N0zoM1z0/dx-ball ORACLES.md / PROGRESS.md |
| 马里奥进艾尔登法环（三段跳打 Boss）；蜘蛛侠在阿卡姆骑士的哥谭荡蛛丝 | ✅ 2026-10 一波 AI 辅助 mod 有媒体报道（pcmrace 2026-10-02：Spider-Man×Arkham Knight、Mario×Elden Ring、Tarkov×Skyrim、Minecraft×RDR2）；蜘蛛侠 mod 的实现是拉取 Arkham Knight 碰撞数据与摆荡点喂入蜘蛛侠引擎，双引擎同时运行。更早的 Mario mod 只是蘑菇皮肤 cosmetic，2026 新一波才是引擎级融合 | pcmrace.com / Reddit r/comicbooks |
| Apple 动效 = Group 时序机制 + Video Scrub | ✅ 与公开技术常识一致：Apple 产品页以预渲染视频按滚动进度 scrub 是社区周知机制；时序分组亦符合其公开页面的实现风格 | 公开技术常识（未做专页深挖） |
| 网站已升级上线 | ✅ wow-site-steel.vercel.app 在线，「精选 · WOW Insight」，数据快照 2026/10/14，378 期内容，含本期焦点排版 | web_extract 实测 |

### ASR 专有名词对照（字幕噪声，读转录稿时勿被带偏）

| 转录稿写法 | 实际是 | 依据 |
|------------|--------|------|
| Codix | Codex | 上下文（Mac Studio 上的研发 Agent） |
| HMIT / Hermith | Hermes | 上下文（Mac mini M4 常驻运维） |
| Replay | REA | 全文语境 + 描述链接 |
| DXB | DX-Ball | 官方案例页 |
| GPT（4:47 「professional tools like GPT」） | Ghidra | 上下文为逆向分析专业工具；README 确认 Ghidra |
| Inite（3:06 「Welcome back to Inite」） | Wow Insight（频道名） | 网站页脚「WOW Insight」 |
| TPW（10:55 Hermes 管线「calculating TPW」） | ❓ 无法核实 | ASR 未定项，不影响主线理解 |

---

## 参考资料

- [AI 开始逆向一切！21万星 REA 实测：软件的护城河正在消失？（视频）](https://www.youtube.com/watch?v=iDBDZoEBofQ)
- [morluto/rea — REA: Reverse Engineer Anything](https://github.com/morluto/rea)（241K stars，2026-10-15 快照）
- [REA 官网](https://rea.tools/) ／ [DX-Ball 声像计算官方案例](https://rea.tools/showcase/dx-ball/) ／ [REA Showcases](https://rea.tools/showcase/)
- [N0zoM1z0/dx-ball — DX-Ball C 重建项目](https://github.com/N0zoM1z0/dx-ball)（ORACLES.md 验证矩阵 / PROGRESS.md 进度台账）
- [storytold/photocraft — PhotoCraft](https://github.com/storytold/photocraft) ／ [ArtCraft 官网](https://getartcraft.com/)
- [wow-site-steel.vercel.app — 升级后的 WOW Insight 网站](https://wow-site-steel.vercel.app/)
- [pcmrace：AI mod 把蜘蛛侠带进阿卡姆骑士、马里奥带进艾尔登法环（2026-10-02）](https://www.pcmrace.com/2026/10/02/batman-arkham-knight-spider-man-mod-pc)

## 相关笔记

- [[M1 Mac mini 装 Omarchy - Jev Atlas 端到端 AI 工作流实测]] — 同频道（WOW Insight）前作，同一套 Codex 研发 + Hermes 运维流水线的更早实践
- [[Skill vs MCP - 为何有了Skill还需要MCP]] — REA 的本体就是一条 MCP 工具链，是「Agent 为什么需要 MCP」的又一实证
- [[Hermes Agent + Claude + Codex AI Agent Stack]] — 多 Agent 分工协作的同类架构实践

---

*文档生成时间：2026-10-15*
*基于视频（2026-10-14 发布）字幕一手素材 + GitHub API / 官方案例页 2026-10-15 快照交叉验证*
