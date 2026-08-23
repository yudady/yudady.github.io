# Matt Van Horn：我所知道的全部 Agentic Engineering 技巧（2026年6月）

> 📺 [Why QQ](https://youtu.be/i0dQoe7KSEc) · 2026-06-08 · Video ID: `i0dQoe7KSEc`
> 原文：Matt Van Horn《Every Agentic Engineering Hack I Know (June 2026)》

## TL;DR

Matt Van Horn（June 创始人、Lyft 联合创始人）一个人用 Claude Code 维护两个合计 34000+ Star 的开源项目，同时成为 Python 核心贡献者。他的核心理念：**不用 IDE，只用 `plan.md` + 语音**。传统开发 80% 写代码、20% 规划；Agentic Engineering 把它反过来了——思考全压进计划里，执行交给 Agent。

---

## 核心工作流：计划驱动

### Hack 1. 有想法立刻 `/ce-plan`
- 使用 [Compound Engineering](https://github.com/everyinc/compound-engineering-plugin) 插件
- 支持输入：截图、bug 链接、报错信息、设计稿、Slack 对话
- 模糊想法先用 `/ce-brainstorm`，清晰后再 `/ce-plan`
- 内部机制：并行扇出多个研究型 Agent，分别分析代码库、历史方案、外部文档
- 输出：结构化 plan.md（问题、方案、涉及文件、验收标准 checkbox、参考模式）

### Hack 2. 别读那个 plan.md
> "做计划。信计划。别读计划。"

- 计划是给 Agent 看的，强制它不偷懒（承诺方案、写验收标准、逐条达成）
- 有疑问当场追问：`TLDR?`、`eli5 这个计划`、"为什么用这个思路？"

### Hack 3. 给计划做个计划（非代码工作）
> "直接叫它出成品，它会偷工减料；叫它先规划'我将如何产出这个成品'、再去执行那份规划，它每次都会给你下足功夫的深度版。"

适用于：策略文档、产品规格书、竞品分析、董事会汇报

---

## 环境配置

### Hack 4. 语音输入
- Mac：Monologue 或 Wispr Flow + 鹅颈麦
- 手机：苹果自带领写（LLM 能猜出识别错误，无需完美转录）

### Hack 5. cmux 多标签页
- 常开 4-6 个标签页，每个跑不同任务
- 一个写计划、一个搭建、一个跑研究工具、一个修 bug
- 一个在研究时，切换到另一个已就绪的继续搭建

### Hack 6. 终端默认打开 Claude
```
# ~/.config/ghostty/config 加一行：
command = ~/.local/bin/claude-launcher.sh

# ~/.local/bin/claude-launcher.sh：
# 运行 claude --dangerously-skip-permissions
# 退出后打印提示 + 进入交互式 zsh
# chmod +x
```

### Hack 7. 远程控制 + 邮箱触发
- **远程控制**：每个会话从手机 App 触达
  ```json
  "remoteControlAtStartup": true
  ```
- **AgentMail**：发邮件触发新 Claude 会话
  - 守护进程 WebSocket 监听收件箱
  - 白名单过滤 + DKIM/SPF 验证
  - 开源：`github.com/mvanhorn/agentmail-to-claude-code`

### Hack 8. YOLO 模式（跳过权限确认）
```json
// ~/.claude/settings.json
{
  "permissions": {
    "allow": ["WebSearch", "WebFetch", "Bash", "Read", "Write", "Edit", "Glob", "Grep", "Task", "TodoWrite"],
    "deny": [],
    "defaultMode": "bypassPermissions"
  },
  "skipDangerousModePermissionPrompt": true
}
```
声音钩子（非 negotiable，6 个会话必须能听到完成通知）：
```json
{
  "hooks": {
    "Stop": [{"hooks": [{"type": "command", "command": "afplay /System/Library/Sounds/Blow.aiff"}]}]
  }
}
```

---

## 工具链

### Hack 9. Claude 规划 + Codex 搭建
- **Codex**：reasoning xhigh，fast mode ON
- **Claude Code**：reasoning xhigh，fast mode OFF（避免额外收费）
- 委托方式：Codex IDE 扩展、`/ce-work --codex`、Printing Press 提示词末尾加 `codex`

### Hack 10. last30days（研究工具）
- 在 `/ce-plan` 之前跑 `/last30days <主题>`
- 并行搜索 Reddit、X、YouTube、TikTok、Instagram、HN、Polymarket、GitHub

### Hack 11. Granola（会议转录）
> "诀窍在于原始。我不先做摘要，把整段乱糟糟的转录原样丢进去——连那些关于寿司的跑题也一起——让 Claude 对着我真实的代码库和我写过的每一份历史策略计划去做提取。"

### Hack 12. HyperFrames（视频制作）
- 视频当 HTML 做，Agent 能写
- 每个视频 = 文件夹 + 逐场景 `script.md` + 动态文字排版

### Hack 13. 笔记即知识库
- Bear（配 Bear CLI）、Obsidian、gbrain（跨机器同步）、supermemory（Agent 记忆层）

### Hack 14. Proof（分享计划）
- 把 `.md` 丢进 Proof，发链接给同事
- 普通人能干净阅读、逐行评论
- 评论回流进 Agent 循环

---

## 进阶技巧

### Hack 15. 自己写 Skills
> "任何我做超过两次的事，我都把它变成一个 skill。"
```
看看 Compound Engineering 那个 skill，帮我照它做一个用于〔X〕的。
```

### Hack 16. 开源贡献方法论
- 用 `/ce-plan` + `/ce-work` 循环给项目贡献
- **PR 让你进门，人，才是你留下来的原因**
- X 付费订阅（$1-3/月）可发"付费用户"特别通知

### Hack 17. 随处办公
- Mosh（糟糕 wifi 下比 SSH 稳定）、Tmux（断线重连）、Hermes + Agent Cookie

### Hack 18. 笔记本配置
- M5 Max 64GB，永不休眠：`sudo pmset -a disablesleep 1`
- 随身 Anker 充电宝 + 车里备充电器

### Hack 19. Printing Press + Agent Cookie
- Agent Cookie：把真实浏览器会话交给 CLI，以你的身份行事
- 用例：特斯拉预热、Instacart 下单、ESPN 比分轮询、机票查询

---

## 心态

### Hack 20. 人类信号
> "当你同时跑六个 Agent 时，你的工作不是去干活，而是去当那个信号。Agent 提供产量，你提供品味、方向，以及'看一眼—再纠偏'的循环。"
>
> "去当品味，让它们当手。"

### Hack 21. AI 精神病
- 用 Agent 搭东西容易上瘾——"有史以来最棒的电子游戏"
- 歇一歇，去外面走走，和爱的人说话，做人们真正想要的东西

---

## 核心循环总结

```
Research → Plan → Build
   ↑            ↓
   └──  Iterate ──┘
```

1. `/last30days <topic>` 做研究
2. `/ce-plan` 生成计划
3. `/ce-work` 执行构建
4. 多终端并行，人在中间做"信号"

---

## 相关链接
- 原文（LinkedIn）：https://www.linkedin.com/pulse/every-agentic-engineering-hack-i-know-june-2026-matt-van-horn-e2dkc
- 中文翻译：https://superpung.com/agentic-engineering-2026/
- last30days：https://github.com/mvanhorn/last30days
- Printing Press：https://printingpress.dev
- Compound Engineering：https://github.com/everyinc/compound-engineering-plugin
