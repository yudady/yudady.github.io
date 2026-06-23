# baoyu-skills 笔记

> 仓库: https://github.com/JimLiu/baoyu-skills
> 作者: 宝玉 (JimLiu)
> 定位: AI Agent 技能集，适用于 Claude Code、Codex 等
> 运行时: TypeScript via Bun (无构建步骤)

## 一、概览

baoyu-skills 是一套面向 AI 编码 Agent 的技能插件，分为三大类:

1. **内容技能 (Content Skills)** — 生成或发布内容
2. **AI 生成技能 (AI Generation Skills)** — 图像生成后端
3. **工具技能 (Utility Skills)** — 内容处理

每个技能包含 `SKILL.md`(YAML front matter + 文档)、可选的 `scripts/`、`references/`、`prompts/`。所有技能使用 `baoyu-` 前缀，支持通过 `EXTEND.md` 自定义扩展。

## 二、安装

```bash
# 快速安装（推荐）
npx skills add jimliu/baoyu-skills

# Codex 项目级安装（只复制需要的 skill 到 .agents/skills/）
# 推荐公众号最小组合: baoyu-cover-image + baoyu-article-illustrator + baoyu-post-to-wechat

# 环境配置
mkdir -p ~/.baoyu-skills
# 在 ~/.baoyu-skills/.env 或 <project>/.baoyu-skills/.env 中配置 API 密钥
```

配置优先级: 命令行环境变量 > process.env > 项目 .env > 用户 ~/.baoyu-skills/.env

## 三、内容技能

### 3.1 baoyu-xhs-images — 小红书图片卡片

将内容拆解为 1-10 张卡通风格图片卡片，三维系统: **风格 x 布局**，可选配色覆盖。

```bash
/baoyu-xhs-images article.md --style notion --layout dense --palette macaron
/baoyu-xhs-images article.md --yes   # 非交互模式
```

**12 种风格**: cute(默认), fresh, warm, bold, minimal, retro, pop, notion, chalkboard, study-notes, screen-print, sketch-notes

**8 种布局**: sparse(封面), balanced(常规), dense(知识卡), list(清单), comparison(对比), flow(流程), mindmap(概念图), quadrant(四象限)

**3 种配色**: macaron, warm, neon

**预设 (Preset)**: 如 `--preset knowledge-card` = notion + dense; `--preset cute-share` = cute + balanced

**核心特性 — image-1 anchor chain**: 先生成封面(不加 ref)，然后把封面作为 --ref 传给后续所有图片，保持视觉一致性。

---

### 3.2 baoyu-infographic — 信息图生成

21 种布局 x 22 种风格，分析内容后推荐组合。

```bash
/baoyu-infographic content.md --layout pyramid --style technical-schematic
```

**21 种布局**: linear-progression, binary-comparison, comparison-matrix, hierarchical-layers, tree-branching, hub-spoke, structural-breakdown, bento-grid(默认), iceberg, bridge, funnel, isometric-map, dashboard, periodic-table, comic-strip, story-mountain, jigsaw, venn-diagram, winding-roadmap, circular-flow, dense-modules

**22 种风格**: craft-handmade(默认), claymation, kawaii, storybook-watercolor, chalkboard, cyberpunk-neon, bold-graphic, aged-academia, corporate-memphis, technical-schematic, origami, pixel-art, ui-wireframe, subway-map, ikea-manual, knolling, lego-brick, pop-laboratory, morandi-journal, retro-pop-grid, hand-drawn-edu, retro-popup-pop

---

### 3.3 baoyu-diagram — SVG 图表生成

直接输出自包含 SVG 代码(非图像生成)，支持深色模式。

```bash
/baoyu-diagram "JWT 认证流程" --type sequence
/baoyu-diagram "微服务架构" --type structural --lang zh
```

**10 种类型**: Architecture, Flowchart, Sequence, Structural, Mind Map, Timeline, Illustrative, State Machine, Data Flow, Class Diagram

特点: 手写 SVG 坐标、内嵌 `<style>` 含 `@media (prefers-color-scheme: dark)`、自动转换 @2x PNG

---

### 3.4 baoyu-cover-image — 文章封面图

五维定制系统: **类型 x 配色 x 渲染 x 文字 x 氛围**

```bash
/baoyu-cover-image article.md --type conceptual --palette cool --rendering digital
/baoyu-cover-image article.md --quick   # 跳过确认
```

- **Type**: hero, conceptual, typography, metaphor, scene, minimal
- **Palette**: warm, elegant, cool, dark, earth, vivid, pastel, mono, retro, duotone, macaron (11 种)
- **Rendering**: flat-vector, hand-drawn, painterly, digital, pixel, chalk, screen-print (7 种)
- **Text**: none, title-only(默认), title-subtitle, text-rich
- **Mood**: subtle, balanced(默认), bold

77 种独特效果组合，11 种预设（如 blueprint, chalkboard 等）

---

### 3.5 baoyu-slide-deck — 幻灯片生成

从内容生成专业幻灯片，最终合并为 .pptx 和 .pdf。

```bash
/baoyu-slide-deck article.md --style corporate --slides 15
/baoyu-slide-deck article.md --outline-only   # 仅大纲
```

**17 种预设**: blueprint(默认), chalkboard, corporate, minimal, sketch-notes, hand-drawn-edu, watercolor, dark-atmospheric, notion, bold-editorial, editorial-infographic, fantasy-animation, intuition-machine, pixel-art, scientific, vector-illustration, vintage

预设由四维组合: 纹理(clean/grid/organic/pixel/paper) x 氛围 x 字体 x 密度

---

### 3.6 baoyu-comic — 知识漫画创作

画风 x 基调灵活组合，逐页生成，最终合并 PDF。

```bash
/baoyu-comic source.md --art manga --tone warm --layout cinematic
/baoyu-comic source.md --style ohmsha   # 使用预设(含特殊规则)
```

- **Art(6)**: ligne-claire(默认), manga, realistic, ink-brush, chalk, minimalist
- **Tone(7)**: neutral(默认), warm, dramatic, romantic, energetic, vintage, action
- **Preset(5)**: ohmsha(manga+neutral), wuxia(ink-brush+action), shoujo(manga+romantic), concept-story, four-panel
- **Layout(7)**: standard(默认), cinematic, dense, splash, mixed, webtoon, four-panel

---

### 3.7 baoyu-article-illustrator — 文章插图

三维系统: **类型 x 风格 x 色板**

```bash
/baoyu-article-illustrator article.md --type flowchart --style notion
```

- **Type(6)**: infographic, scene, flowchart, comparison, framework, timeline
- **Style(8)**: notion(默认), elegant, warm, minimal, blueprint, watercolor, editorial, scientific
- **Palette(3)**: macaron, warm, neon

---

### 3.8 baoyu-post-to-wechat — 发布微信公众号

```bash
# 文章模式
/baoyu-post-to-wechat 文章 --markdown article.md --theme grace

# 贴图模式
/baoyu-post-to-wechat 贴图 --markdown article.md --images ./photos/

# 发布方式: API(推荐) / 浏览器 / 远程 API
```

多账号支持(EXTEND.md 配置 accounts 列表)

---

### 3.9 baoyu-post-to-x — 发布到 X (Twitter)

```bash
/baoyu-post-to-x "Hello" --image photo.png
/baoyu-post-to-x --article article.md   # X 文章(长文)
```

使用真实 Chrome + CDP，用户需手动检查发布。

---

### 3.10 baoyu-post-to-weibo — 发布到微博

```bash
/baoyu-post-to-weibo "Hello Weibo!" --image photo.png
/baoyu-post-to-weibo --article article.md --cover cover.jpg   # 头条文章
```

---

## 四、AI 生成技能

### 4.1 baoyu-image-gen — 图像生成后端

基于 AI SDK，支持多服务商:

| Provider | 默认模型 |
|----------|----------|
| OpenAI | gpt-image-2 |
| Azure OpenAI | (部署名) |
| Google | gemini-3-pro-image |
| OpenRouter | google/gemini-3.1-flash-image |
| DashScope | qwen-image-2.0-pro |
| Z.AI | glm-image |
| MiniMax | image-01 |
| 即梦 (Jimeng) | jimeng_t2i_v40 |
| 豆包 (Seedream) | doubao-seedream-5-0-260128 |
| Replicate | google/nano-banana-2 |
| Codex CLI | (无需 API key，走 codex exec) |

```bash
/baoyu-image-gen --prompt "一只猫" --image cat.png --provider openai --ar 16:9 --quality 2k
/baoyu-image-gen --prompt "变成蓝色" --image out.png --ref source.png   # 参考图
/baoyu-image-gen --batchfile batch.json --jobs 4   # 批量
```

服务商自动选择: 有 ref 优先 Google > OpenAI > Azure > OpenRouter > Replicate > Seedream > MiniMax

### 4.2 baoyu-danger-gemini-web — Gemini Web 交互

通过浏览器 cookies 使用非官方 Gemini Web API，用于文本和图片生成。逆向工程，使用风险自负。

---

## 五、工具技能

| Skill | 用途 | 关键命令 |
|-------|------|----------|
| baoyu-youtube-transcript | YouTube 字幕下载 | 支持多语言、章节分段、说话人识别 |
| baoyu-url-to-markdown | URL 转 Markdown | 通过 Chrome CDP 抓取 |
| baoyu-danger-x-to-markdown | X 推文转 Markdown | 逆向工程 X API |
| baoyu-compress-image | 图片压缩 | 保持质量 |
| baoyu-format-markdown | Markdown 格式化 | 添加 frontmatter、层级标题等 |
| baoyu-markdown-to-html | Markdown 转 HTML | 公众号兼容主题、代码高亮 |
| baoyu-translate | 翻译(三模式) | quick / normal / refined |
| baoyu-wechat-summary | 微信群聊精华提取 | 话题提取、发言排行、群友画像 |
| baoyu-electron-extract | Electron 应用资源提取 | 从 app.asar 提取 JS、source-map 还原 |

---

## 六、架构与设计要点

### 统一的后端选择规则 (Image Generation Tools)

所有需要渲染图片的技能都遵循统一的后端选择链:
1. 用户当前请求指定 → 使用指定的
2. EXTEND.md `preferred_image_backend` → 使用保存的偏好
3. 自动选择: 运行时原生工具(Codex imagegen / Cursor GenerateImage / Hermes image_generate) > baoyu-image-gen > 多个时询问用户
4. 无可用 → 告知用户

### Prompt File 机制

所有技能在调用图像后端之前，必须将完整 prompt 保存为独立文件 `prompts/NN-{type}-{slug}.md`，作为可复现性记录。不允许在无 prompt 文件的情况下直接生成。

### 确认策略 (Confirmation Policy)

默认行为: 生成前确认。只有用户明确说"直接生成"、"不用确认"等才跳过。

### 批量生成策略 (Batch Generation Policy)

prompt 文件全部就绪后，按优先级: 后端原生批量 > 运行时并行工具调用 > 顺序生成。默认批量大小 4。

### EXTEND.md 自定义

所有技能支持通过 EXTEND.md 自定义，检查顺序: 项目级 `.baoyu-skills/<skill>/EXTEND.md` > XDG > 用户 `~/.baoyu-skills/<skill>/EXTEND.md`

### 文字修正策略

生成的图片中文字错误不允许用代码修补(SVG/ImageMagick/Pillow 等)，必须重新生成。旧图保留用于对比。

---

## 七、相关项目

- **baoyu-design** (独立项目): 把 Claude Design 打包成可移植 Agent Skill，生成 UI 稿、原型、线框图、落地页等自包含 HTML
  - 安装: `npx skills add JimLiu/baoyu-design`

- **《图解 Skill — AI 提效实战指南》**: 系统讲解 Skill 设计、编写、安装和迭代的图书
  - 配套仓库: https://github.com/JimLiu/Illustrated-Agent-Skills

---

## 八、环境变量速查

```bash
# 通用
OPENAI_API_KEY, GOOGLE_API_KEY, OPENROUTER_API_KEY, DASHSCOPE_API_KEY
ZAI_API_KEY, MINIMAX_API_KEY, REPLICATE_API_TOKEN
JIMENG_ACCESS_KEY_ID, JIMENG_SECRET_ACCESS_KEY, ARK_API_KEY

# 微信公众号
WECHAT_APP_ID, WECHAT_APP_SECRET

# X (Twitter)
X_AUTH_TOKEN, X_CT0
```

---

*整理日期: 2026-06-23*
