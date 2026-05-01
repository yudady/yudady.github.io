---
title: GitHub Trending Weekly #29 - 35个开源项目速报
aliases: [GTW29, GitHub Trending 2026-04]
tags: [github-trending, status/active, area/distill, type/doc, topic-opensource]
source: ["https://www.youtube.com/watch?v=lTh0Zcu3EaM"]
author: Github Awesome
created: 2026-04-07 20:45
updated: 2026-04-07 20:45
description: |
  2026-04-06 发布的 GitHub Trending 周报第 29 期，涵盖 AI Agent 工具、开发者效率、安全、前端等多个类别共 35 个开源项目。
level: beginner
stars: 3
---

# GitHub Trending Weekly #29

> 2026-04-06，35 个开源项目快速概览。本期重点：AI Agent 生态工具爆发，Claude Code 相关项目密集出现。

---

## AI Agent 工具

| 项目                                                                     | 说明                          | 亮点                                                      |
| ---------------------------------------------------------------------- | --------------------------- | ------------------------------------------------------- |
| [WAZA](https://github.com/tw93/waza)                                   | Claude Code 高级技能集合（tw93 出品） | 旗舰是 Claude Health，6 层审计 AI 配置                           |
| [AutoAgent](https://github.com/kevinrgu/autoagent)                     | AI 自改进 agent harness        | 读 program.md → 运行 benchmark → 改代码 → 循环，Docker 隔离        |
| [Research Companion](https://github.com/andrehuang/research-companion) | Claude Code 研究插件            | 3 个 agent：idea critic、研究策略师、跨领域头脑风暴                     |
| [Caveman](https://github.com/JuliusBrussee/caveman)                    | Claude Code 技能：让 AI 用穴居人语言  | 减少 75% 输出 token，技术准确度不变，纯成本优化                           |
| [Claude Howto](https://github.com/luongnv89/claude-howto)              | Claude Code 可视化指南           | 基础 → 高级 agent/MCP，带自评命令                                 |
| [Autoskills](https://github.com/midudev/autoskills)                    | 自动检测技术栈并安装 AI 技能            | `npx autoskills` 一键，支持 React/Nex.js/SwiftUI/Spring Boot |
| [Compozy](https://github.com/compozy/compozy)                          | Go CLI：PRD → 代码全流程编排        | 跨 Claude Code/Codex/Cursor 等 40+ agent IDE              |
| [OpenHarness](https://github.com/HKUDS/OpenHarness)                    | 轻量 agent harness            | 11,733 行 = Claude Code 512,664 行的 2.3%，全功能兼容            |
| [Agent Files](https://github.com/Railly/agentfiles)                    | Obsidian 插件：统一管理 agent 技能文件 | 三栏视图，聚合 Claude Code/Cursor/Codex 等所有工具的技能               |
| [Hermes WebUI](https://github.com/nesquena/hermes-webui)               | Hermes Agent 浏览器界面          | 纯 Python + vanilla JS，三栏布局，SSH tunnel 访问                |
| [Cabinet](https://github.com/hilash/cabinet)                           | 纯 markdown 的 AI 团队管理        | 20 个预置 agent 模板，cron 调度，git 版本控制                        |
| [Iron Proxy](https://github.com/ironsh/iron-proxy)                     | AI agent 出站防火墙              | 默认拒绝，边界注入 API 密钥，agent 看不到真实 key                        |

---

## 开发者效率

| 项目 | 说明 | 亮点 |
|------|------|------|
| [GitReverse](https://github.com/filiksyos/gitreverse) | 反向工程 GitHub 项目为 prompt | URL 技巧：把 github.com 换成 gitreverse 即可 |
| [JSON Alexander](https://github.com/wesbos/JSON-Alexander) | 浏览器扩展：JSON 美化 | 暴露 `window.data`，控制台直接操作 API 响应 |
| [Sheets](https://github.com/maaslalani/sheets) | 终端电子表格（Go） | Vim 风格导航 HJKL，读写 CSV，支持命令行 sed 编辑 |
| [Port Whisperer](https://github.com/LarsenCundric/port-whisperer) | 端口占用排查 CLI | 交互式菜单，干净排版，找到即杀 |
| [Mark Native](https://github.com/liyown/marknative) | Markdown → 图片（无浏览器） | 直接解析 → Skia canvas 渲染，确定性 PNG/SVG 输出 |

---

## 安全

| 项目 | 说明 | 亮点 |
|------|------|------|
| [Supply Chain Monitor](https://github.com/elastic/supply-chain-monitor) | npm/pypi 实时供应链监控 | 自动下载新版本 → diff → AI 分类是否恶意，捕获了 Axios 攻击 |

---

## 前端 / UI

| 项目 | 说明 | 亮点 |
|------|------|------|
| [Boneyard](https://github.com/0xGF/boneyard) | 骨架屏自动生成 | 从实际组件派生，永远与真实 UI 同步 |
| [Math Curve Loaders](https://github.com/Paidax01/math-curve-loaders) | 数学曲线加载动画 | 玫瑰线/心脏线/卡西尼卵形线，可预览公式和代码 |
| [Nothing Design Skill](https://github.com/dominikmartn/nothing-design-skill) | Nothing Phone 设计语言技能 | 说「nothing style」agent 就生成对应风格 UI |

---

## 基础设施 / 底层

| 项目 | 说明 | 亮点 |
|------|------|------|
| [rvLLM](https://github.com/m0at/rvllm) | Rust 重写的 vLLM | 12,312 tok/s @128 流，20x 冷启动，31x 更小二进制 |
| [cuLA](https://github.com/inclusionAI/cuLA) | 线性注意力 CUDA 内核 | Blackwell 上 KDA 1.45x，Lightning Attention 1.86x |
| [Apfel](https://github.com/Arthur-Ficial/apfel) | Apple Silicon 本地模型 CLI | 解锁 Siri 背后的 3B 模型，Neural Engine 运行，OpenAI 兼容 API |

---

## 通讯 / 协作

| 项目 | 说明 | 亮点 |
|------|------|------|
| [Mail App (Exo)](https://github.com/ankitvgupta/mail-app) | AI 原生桌面邮件客户端 | 自动分析/优先级/草拟回复，Electron + React |
| [Jot](https://github.com/badlogic/jot) | 为人和 agent 设计的协作编辑器 | 完整 CLI + HTTP API，agent 可直接操作笔记和评论 |
| [Tuitter](https://github.com/bddicken/tuitter) | 终端 X 客户端（TypeScript） | OAuth 2.0，Kitty 图片协议，终端内刷时间线 |

---

## 应用 / 趣味

| 项目 | 说明 | 亮点 |
|------|------|------|
| [EmDash](https://github.com/emdash-cms/emdash) | TypeScript + Astro + Cloudflare 重写的 WordPress | Worker 沙箱插件，JSON 内容，内置 MCP server |
| [Career Ops](https://github.com/santifer/career-ops) | 自动求职系统 | 扫描 45+ 公司，14 种 CV 模式，ATS 优化 PDF，700+ 申请已验证 |
| [OmniVoice](https://github.com/k2-fsa/OmniVoice) | TTS 模型（扩散语言模型） | 600 语言，0.25 RTF（40x 实时），语音克隆，离线 |
| [Pika Skills Open](https://github.com/Pika-Labs/Pika-Skills) | Google Meet AI 代开会 | 语音克隆头像 + 工作区上下文 + 自动纪要 |
| [Any Buddy](https://github.com/cpaczek/any-buddy) | Claude Code 宠物选择器 | 暴力搜索 salt 字符串，<100ms 找到指定宠物 |
| [See-through](https://github.com/shitagaki-lab/see-through) | 动漫插图自动分层（SIGGRAPH 2026） | 一张图 → 24 个语义层 PSD |
| [3.wasm](https://github.com/mrdoob/three.wasm) | three.js 的 WebAssembly 版 | 10KB 二进制，480+ FPS，零依赖 |
| [RefugiOS](https://github.com/Ganso/refugiOS) | USB 启动离线 OS | 离线 Wiki + 地图 + AI 助手（Llama 3.4 mini）+ 加密文档库 |

---

## 本期趋势观察

1. **AI Agent 生态工具爆发**：35 个项目中 ~1/3 与 AI Agent 直接相关，Claude Code 技能/插件成为新常态
2. **成本优化受关注**：Caveman（减 token）和 rvLLM（Rust 重写提速）都指向同一方向
3. **Agent 安全意识提升**：Iron Proxy 解决 agent 终端访问的安全问题，Supply Chain Monitor 解决依赖安全
4. **Karpathy 效应持续**：AutoAgent 直接实现 Karpathy 的「AI 自改进 harness」理念

---

## 参考资料
- [视频源 (YouTube)](https://www.youtube.com/watch?v=lTh0Zcu3EaM)
- [文字版](https://githubawesome.com/github-trending-weekly-29/)

## 相关笔记
- [[../../200-Distill/200-AI-Tools/Hermes Agent - 自改进AI代理框架]]
- [[../../200-Distill/200-AI-Tools/claude/LLM Knowledge Base - 为Claude Code构建自进化记忆系统]]
