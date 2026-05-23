---
title: 用代码写视频 — AI + HTML 视频生成方案全览
aliases:
  - HTML 视频生成
  - Remotion HyperFrames
  - AI 做视频
tags:
  - ai/video-generation
  - frontend/react
  - tool/remotion
  - tool/hyperframes
  - status/active
  - type/notes
  - source/youtube
source:
  - "https://www.youtube.com/watch?v=1S7EW8IRWaE"
author: 轩元（YouTube 频道）
created: 2026-05-23
updated: 2026-05-23
description: 三种用 AI + HTML/React 代码生成动画视频的方案：Remotion、HyperFrames、SvgAnimate，覆盖技术派和无代码用户
level: beginner
stars: 3
note: 无字幕，基于 NotebookLM 转录 + 外部资料综合整理
---

# 用代码写视频 — AI + HTML 视频生成方案全览

> 讲义/科普类视频有大量文字、线条、图表、动效，需要精准控制——这恰好是前端的舒适区。AI 写前端已经非常成熟，所以「AI 写代码 → 转换为视频」成为了一条清晰的路径。

---

## 目录

- [为什么用代码做视频](#為什麼用代碼做視頻)
- [方案一：Remotion — React 写视频](#方案一remotion--react-寫視頻)
- [方案二：HyperFrames — 原生 HTML 写视频](#方案二hyperframes--原生-html-寫視頻)
- [方案三：SvgAnimate — 无代码在线创作](#方案三svganimate--無代碼在線創作)
- [三种方案对比](#三種方案對比)
- [核心技术原理](#核心技術原理)
- [知识区 UP 主的启示](#知識區-up-主啟示)

---

## 为什么用代码做视频

当前 AI 视频生成（如可灵、Kling）擅长叙事类视频，但对**讲义/科普类视频**力有未逮——这类视频的特点：

```
講義/科普視頻的關鍵需求          傳統 AI 視頻的弱項
─────────────────────────────────────────────
大量精確文字                     AI 生成文字常有幻觉
線條與圖表                      难以精准控制布局
動效與時間軸                     时间控制粗糙
數據可視化                      无法生成准确图表
風格一致性                      每帧可能有差异
```

**而前端恰好是这些需求的舒适区**——CSS、SVG、Canvas、WebGL 天生就擅长这些。加上 AI 写前端已经非常成熟，路径就很清晰了：

```
AI 寫前端代碼 → 瀏覽器渲染 → 錄製/導出為 MP4
```

---

## 方案一：Remotion — React 写视频

**定位**：用 React 组件创建视频的开源框架。

| 属性 | 详情 |
|------|------|
| 官网 | [remotion.dev](https://www.remotion.dev/) |
| GitHub | [remotion-dev/remotion](https://github.com/remotion-dev/remotion) |
| Stars | 31K+ |
| 技术栈 | React + TypeScript |
| 输出 | MP4 视频 |
| 许可证 | MIT |

### 工作原理

```
React Component → 瀏覽器渲染每一幀 → Puppeteer 捕獲 → FFmpeg 編碼為 MP4
```

### 使用方式

```bash
# 创建项目
npx create-video@latest

# 本地开发预览
npm start

# 渲染输出视频
npm run build
```

### AI Agent 集成

Remotion 官方已经内置了对 AI Coding Agent 的支持：

```bash
# Claude Code 中安装 skill
# Remotion 提供了官方 system prompt 供 LLM 使用
# https://www.remotion.dev/docs/ai/system-prompt
```

支持的 Agent：Claude Code、Codex、OpenCode 等。

> ⚠️ **注意**：Remotion 存在于 AI 出现之前，以前只有前端程序员能用。现在有了 AI，普通人也能用了——但门槛仍然偏高。

---

## 方案二：HyperFrames — 原生 HTML 写视频

**定位**：HeyGen 开源的 HTML 视频渲染框架，不需要 React，直接写原生 HTML。

> ⚠️ **产品归属澄清**：视频中称为 "Hyperfence"，实际产品名为 **HyperFrames**，由 HeyGen（heygen.com）开源，非视频作者开发。

| 属性 | 详情 |
|------|------|
| 官网 | [hyperframes.heygen.com](https://hyperframes.heygen.com) |
| GitHub | [heygen-com/hyperframes](https://github.com/heygen-com/hyperframes) |
| NPM | `hyperframes` |
| 技术栈 | TypeScript + HTML/CSS/JS |
| 输出 | MP4 视频（确定性渲染） |
| 许可证 | Apache-2.0 |
| 版本 | 0.4.41 |

### 核心哲学：Video = HTML

```
傳統方式                              HyperFrames
─────────────────────────────────────────────
學習專屬 DSL                         用 HTML/CSS/JS 寫視頻
React 組件封裝                       原生 HTML 元素 + data-* 屬性
需要 React 知識                     任何前端開發者都能上手
```

### HTML 视频结构示例

```html
<div id="stage" data-composition-id="my-video"
     data-start="0" data-width="1920" data-height="1080">

  <!-- 背景视频轨道 -->
  <video data-start="0" data-duration="5"
         data-track-index="0" src="bg.mp4" muted></video>

  <!-- 文字标题轨道 -->
  <h1 class="clip" data-start="1" data-duration="4"
      data-track-index="1">Welcome</h1>

  <!-- 音频轨道 -->
  <audio data-start="0" data-duration="5"
         data-track-index="2" data-volume="0.5"
         src="music.wav"></audio>
</div>
```

### 关键特性

```
                    HyperFrames 架構
    ┌────────────────────────────────────┐
    │           HTML 定義結構            │
    │   data-start, data-duration,       │
    │   data-track-index                 │
    ├────────────────────────────────────┤
    │        多運行時動畫支持             │
    │   GSAP / Lottie / Three.js /      │
    │   CSS Animation / Web Animations   │
    ├────────────────────────────────────┤
    │         確定性渲染                  │
    │   同樣輸入 = 同樣輸出              │
    │   Puppeteer 捕獲 → FFmpeg 編碼     │
    ├────────────────────────────────────┤
    │         AI Agent 一等公民           │
    │   npx skills add heygen-com/       │
    │   hyperframes                      │
    │   支持 Claude Code / Codex /       │
    │   Gemini CLI / Cursor              │
    ├────────────────────────────────────┤
    │        內建 50+ 組件庫             │
    │   社交媒體覆蓋 / 著色器轉場 /      │
    │   數據可視化 / 電影特效             │
    └────────────────────────────────────┘
```

### 使用方式

```bash
# 初始化项目
npx hyperframes init my-video

# 预览（浏览器实时播放）
npx hyperframes preview

# 渲染输出 MP4
npx hyperframes render

# 安装 Agent skill
npx skills add heygen-com/hyperframes
```

### Remotion vs HyperFrames

| 维度 | Remotion | HyperFrames |
|------|----------|-------------|
| 语言 | React + TypeScript | 原生 HTML/CSS/JS |
| 学习曲线 | 需要 React 知识 | 更低，会 HTML 即可 |
| 动画运行时 | React 生态 | GSAP/Lottie/Three.js |
| AI 友好度 | 高 | 更高（LLM 天生擅长 HTML） |
| 组件库 | React 组件 | 50+ HTML 组件 |
| 确定性渲染 | 是 | 是 |
| 成熟度 | 更成熟（更早出现） | 较新（v0.4.x） |

---

## 方案三：SvgAnimate — 无代码在线创作

> ⚠️ **产品归属澄清**：视频中称为 "SVGimate"，实际产品名为 **SvgAnimate**（svganimate.ai），由视频作者轩元开发，非开源项目。

**定位**：面向非技术用户的 AI 动画/视频在线创作平台。

| 属性 | 详情 |
|------|------|
| 官网 | [svganimate.ai](https://svganimate.ai) |
| 类型 | SaaS 在线平台 |
| 技术门槛 | 无需编程 |
| 付费模式 | 积分制（注册送 50 积分） |

### HTML 视频功能流程

```
1. 輸入提示詞
   "幫我做一個講解大模型訓練過程的視頻"

2. AI 自動生成：
   ├── 旁白文案
   ├── 分鏡腳本（每段旁白 → 對應畫面）
   └── 分鏡時間軸

3. 用戶確認/修改腳本

4. 一鍵生成：
   ├── 每個分鏡 → 帶動畫的 HTML 頁面
   ├── 所有畫面按序拼接 + 轉場過渡
   └── 配上 AI 生成旁白音頻

5. 輸出 → 完整動畫視頻
```

### 可配置项

| 配置 | 选项 |
|------|------|
| 视觉风格 | 十几套预设风格（科技、商务、手绘等） |
| 音色 | 十几种 AI 音色（可关闭旁白） |
| 画面比例 | 横屏 / 竖屏 |

### 其他动画类型

平台还支持多种动画创作：

```
動畫類型          適用場景
─────────────────────────────
3D 動畫          抽象概念可視化、產品演示、科幻視覺
手繪動畫          溫暖內容、兒童教育、繪本風格
地圖動畫          地理/旅行內容
數據可視化        圖表動畫
流程圖動畫        過程講解
產品演示          產品展示
文字動畫          標題/口號
Logo 動畫         品牌展示
火柴人動畫        趣味解說
加載動畫          UI 組件
網頁小遊戲        互動內容
```

---

## 三种方案对比

| 维度 | Remotion | HyperFrames | SvgAnimate |
|------|----------|-------------|------------|
| **目标用户** | 前端开发者 | 开发者 + AI Agent | 所有人 |
| **编程需求** | React/TS | HTML/CSS/JS | 无需编程 |
| **AI 集成** | 官方支持 | 官方一等公民 | 内置 |
| **开源** | 是（MIT） | 是（Apache-2.0） | 否（SaaS） |
| **自由度** | 最高 | 高 | 受限于预设 |
| **学习成本** | 高 | 中 | 低 |
| **适合场景** | 复杂程序化视频 | AI Agent 自动化 | 快速出片 |

### 选择决策树

```
你需要做什麼類型的視頻？
│
├─ 精確控制每一幀 + React 技術棧
│   └─ → Remotion
│
├─ AI Agent 自動化生產 + 不想綁 React
│   └─ → HyperFrames
│
├─ 不會寫代碼 + 想快速出片
│   └─ → SvgAnimate
│
└─ 批量生產 + 需要確定性
    └─ → HyperFrames 或 Remotion
```

---

## 核心技术原理

所有三种方案共享一个底层渲染逻辑：

```
           ┌──────────────┐
           │  編寫代碼     │
           │ HTML/React   │
           └──────┬───────┘
                  │
           ┌──────▼───────┐
           │  瀏覽器渲染    │
           │ 逐幀播放      │
           └──────┬───────┘
                  │
       ┌──────────┼──────────┐
       │          │          │
  ┌────▼────┐ ┌──▼───┐ ┌────▼────┐
  │Puppeteer│ │Playwright│ │ Headless │
  │  截幀   │ │  截幀   │ │ Chrome  │
  └────┬────┘ └──┬───┘ └────┬────┘
       │         │         │
       └─────────┼─────────┘
                 │
          ┌──────▼───────┐
          │  FFmpeg 編碼  │
          │  輸出 MP4     │
          └──────────────┘
```

### 关键概念：确定性渲染

```
❌ 生成式 AI 視頻              ✅ 代碼渲染視頻
─────────────────────────────────────────────
每次生成可能不同                同樣輸入 = 同樣輸出
難以精確控制                    幀幀可控
不適合批量生產                  天然支持批量
風格一致性低                    風格 100% 一致
```

确定性渲染对**自动化流水线**至关重要——AI Agent 批量生成 100 个视频时，每个视频必须精确符合预期。

---

## 知识区 UP 主启示

这类 HTML 视频方案对知识区创作者的意义：

```
傳統知識區視頻製作                  AI + HTML 方案
─────────────────────────────────────────────
AE/PR 手動製作（數小時）          AI 自動生成（5 分鐘）
每個視頻單獨製作                    批量生成流水線
改一個字 = 重做整個視頻             改一個字 = 重新渲染
風格遷移成本高                     換一套 CSS = 換風格
```

### 最佳实践建议

```
✅ 適合用 HTML 視頻的場景：
  - 科普/講義/教學
  - 數據可視化
  - 流程圖/架構圖動畫
  - 產品功能演示
  - 信息圖/信息動畫

❌ 不太適合的場景：
  - 真人出鏡
  - 自然場景攝影
  - 複雜 3D 場景
  - 情感驅動的敘事
```

---

## 参考资料

- [Remotion 官网](https://www.remotion.dev/) | [GitHub](https://github.com/remotion-dev/remotion)
- [HyperFrames 官网](https://hyperframes.heygen.com) | [GitHub](https://github.com/heygen-com/hyperframes)
- [HyperFrames 深度解析 - silenceper](https://silenceper.com/article/2026-05-02-hyperframes-html-video-rendering/)
- [Remotion AI Agent 文档](https://www.remotion.dev/docs/ai/coding-agents)
- [SvgAnimate HTML 视频功能](https://svganimate.ai/en/html-video)
