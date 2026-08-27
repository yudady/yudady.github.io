# 001-TODO → 100-InBox 归档实施计划

> **For Hermes:** Use subagent-driven-development skill to implement this plan task-by-task.

**Goal:** 把 `docs/001-TODO` 下 85 个 ob 档案按主题移入 `docs/100-InBox` 的 7 个既有分类资料夹（AI-Agent/AI-Models/AI-Tools/Hermes/Local-LLM/NotebookLM/misc），001-TODO 保留为空收件箱（.gitkeep 留守）。

**Architecture:** 沿用上一轮（commit `8744d3ab`）验证过的流程：人工判定写 manifest → 子代理 git mv → spec review + quality review 两阶段审查 → 修复 → commit。分类规则与上轮一致：Hermes → NotebookLM → Local-LLM → AI-Models（模型发布/评测）→ AI-Agent（架构/工作流/方法论）→ AI-Tools（具体工具/项目/周报）→ misc（非 AI 杂项）。

**Tech Stack:** bash + python（manifest 断言）、git mv、Obsidian vault（根在 docs/）。

---

## 现状（已探查确认，2026-08-23）

- `docs/001-TODO/`：85 个档案（不含 .gitkeep），分布在根目录 + 5 个子目录（`cli/` 12 档、`skills/` 4 档、`旅游/` 2 档、`U出金/` 1 档、`images/` 1 jpg）
- `docs/100-InBox/`：7 个一层资料夹（AI-Agent 95 / AI-Tools 44 / Hermes 39 / misc 12 / Local-LLM 14 / AI-Models 12 / NotebookLM 6 = 222，为 commit `8744d3ab` 后实际值）
- 档名冲突：0（85 个档名与 100-InBox 现有档名零重叠）
- `git status --porcelain -- docs/001-TODO docs/100-InBox`：干净
- `images/youtube-thumb-viIASQJHmSY 1.jpg`：全库无引用（misc/24小时重启系统 引用的是不带 " 1" 的另一个档名，属存量断链，本次不处理）
- 001-TODO 是「新学习笔记默认落点」（用户既有约定），移动后必须保留资料夹与 .gitkeep

## 完整分类映射（85 档，已按 tags + 标题逐档人工判定）

### Hermes（7）
```
2026-07-03-hermes-agent-moa-architecture.md
Hermes Agent - learn 与 goal 八大超能力.md
Hermes Agent v0.20 Herald Release 更新解析.md
Hermes Agent 百大自动化技能精选拆解.md
Hermes-Agent-v0.20-Herald-Release-更新详解.md
Hermes-Master-Agent-改造筆記.md
Prime Agent VS Hermes - AI Agent 学习哲学对比.md
```

### NotebookLM（2）
```
notebooklm-py - 把NotebookLM搬进命令行.md
notebooklm-py - 用开源 CLI 把 NotebookLM 变成 Claude Code 的零 Token 大脑.md
```

### Local-LLM（13）
```
Best Local AI Models by VRAM 2026.md
DeepSeek-V4-Flash-0731-本地部署全解析.md
Gemma 4 12B Coder 本地模型开发实测.md
LM Studio Bionic — 本地開源模型的 AI 代理工作站.md
Mac-Mini-M4-本地开源AI模型指南.md
Ornith-1.0-本地端AI編碼模型實測筆記.md
Ornith-1.5 35B MoE Mac 本地实测.md
Qwen3.8 27B 本地推理实测 — Ollama MLX 后端 4x 加速解析.md
TurboFieldfare - 8GB Mac 跑通 26B 大模型的端侧 AI 革命.md
Unsloth Desktop vs Ollama LM Studio Open WebUI 深度对比.md
llama-cpp-MoE-6GB-GPU-调优指南.md
本地 AI 模型 - GPU 显存分级选型指南.md
背包级AI工作站 - RTX PRO 4000 SFF 极限组装与 M3 Ultra 对决.md
```

### AI-Models（4）
```
Fable 5 泄露与AI Agent架构演进.md
Kimi K3 vs DeepSeek - 跑分陷阱与真实成本实测.md
Muse Glimmer 30B - Meta本地AI Agent战略分析.md
ReactBench - AI 編程的薛定諤綠燈.md
```

### AI-Agent（30）
```
14行業AI應用痛點訪談.md
AI 改 Code 一直改 A 壞 B - Brownfield 專案五步工作流.md
AI 编程规划框架 — Grilling Brainstorming Explore.md
AI代码审查之争 - Uncle Bob vs Vibe Sled.md
Agent 三层解耦架构 — Harness Loop Graph 工程实践.md
Agent 技能设计 — 从技能地狱逃生的工程指南.md
Agentic Engineering 范式转移 - 从 Vibe Coding 到系统化 AI 开发.md
Agent自动合并代码的评估体系 — 六道门.md
Claude Code Loop 工程设计 - 四种模式与边界控制.md
Cross-Model Review 互審機制.md
GLM-5.2-GPT-5.5-分層AI編程工作流.md
Harness 时代 — AI 编程的底盤戰爭.md
I-HAVE-ADHD - 让 AI 回复不再废话的 Claude Code 技能.md
Matt Pocock - Prototype 替代 Spec 驱动开发.md
Matt Pocock Skills - AI Coding 完整工作流.md
Matt Pocock Skills v1.1 - AI 辅助编程 SDLC 工作流.md
Self-Harness 自我演进框架.md
Wayfinder 漸進式規劃與 Frontier 邊界思維.md
cli/MCP 之死 - 命令列才是 AI 工具整合的正確路徑.md
cli/多模型混合 Coding Agent 工作流 - Claude 规划 Gemini 设计 UI.md
cli/為 AI 重寫 CLI - 從 Agent 角度拆解命令列工具設計的未來.md
mattpocock-skills-tutorial-notes.md
skills/AI Agent 四大核心Skill架构.md
skills/Ponytail - AI极简编程提示词架构.md
skills/baoyu-skills.md
skills/matt-pocock-skills.md
企业级AI-Agent四步架构.md
多模型编排 — Fugu 与 Fusion.md
拒绝AI盲目生成-四阶段工程实践.md
跨代理审计工作流 — Cross-Agent Adversarial Review.md
```

### AI-Tools（24）
```
Ego-Lite-AI-Agent-浏览器自动化.md
GitHub一周热点127 - Agent 基础设施与可审计知识图谱.md
Graphify 知识图谱挑战 AI Coding 极限.md
LangGraph 企业级 AI Agent 状态管理.md
OfficeCLI - AI Agent 专用的 Office 自动化 CLI.md
Oh-My-Pi 终端编程代理深度拆解.md
Omnigent - AI 编码智能体的 Meta-Harness.md
OpenCode Desktop 入门指南 - 20 分钟掌握 80% 核心功能.md
OpenHuman - Local-First 个人 AI 工作台.md
OpenSpec - 轻量级AI编程框架深度拆解.md
Orca - 开源 AI Agent 编排开发环境.md
Prime Agent — 自我改进的开源 RLM 编码代理.md
WSL2-AI-Agent-开发完全攻略.md
cli/CLI 復興浪潮 - CLI Anything 與 OpenCLI 深度解析.md
cli/CLI-Anything 實戰 - Claude Code 運行任意軟體（比 MCP 便宜 30 倍）.md
cli/OpenClaw - 開源 AI Agent Gateway 與 ACP 協議深度解析.md
cli/PaddleOCR - 百度開源 OCR 工具包與文件 AI 引擎.md
cli/Scrapling - AI 自適應網頁爬蟲框架.md
cli/SkillOpt - 微软文本空间Agent技能优化框架.md
cli/Tolaria - 基於 Git 的 Markdown 知識庫桌面應用.md
cli/Understand-Anything - 程式碼知識圖譜互動探索工具.md
codebase-memory-mcp.md
本周热门开发工具项目汇总 - AI Agent 与基础设施.md
热门 AI Agent 项目速览 2026-W26.md
```

### misc（5）
```
U出金/加密貨幣出金與報稅攻略.md
images/youtube-thumb-viIASQJHmSY 1.jpg
旅游/新北坪林-金瓜寮魚蕨步道.md
旅游/雲森瀑布一日遊攻略.md
簡化程式碼控制流.md
```

合计：7+2+13+4+30+24+5 = 85 ✓

判定说明（边界案例）：
- `Muse Glimmer 30B`：tag `ai-model` 且主题是模型战略分析 → AI-Models（虽涉本地）
- `ReactBench`：评测/榜单类，同上轮 Agent-Arena 先例 → AI-Models
- `LangGraph`/`SkillOpt`/`codebase-memory-mcp`：具名框架/工具 → AI-Tools（区别于方法论类的 AI-Agent）
- `Prime Agent VS Hermes` 含 Hermes 对比 → Hermes；`Prime Agent` 产品介绍 → AI-Tools
- `簡化程式碼控制流`：clean-code 非 AI → misc
- `WSL2-AI-Agent-开发完全攻略`：环境搭建指南 → AI-Tools（同上轮 Cloudflare 教學先例）

---

### Task 1: 生成 manifest

**Objective:** 产出 85 条 filename → 目标资料夹的机器可校验映射。

**Files:**
- Create: `.hermes/plans/todo-to-inbox-manifest.json`

**Step 1:** 控制器直接执行（不派子代理——判定已完成，只需落盘）。写 `/tmp/classify_todo.py`，内嵌上面 85 条 MAP（键 = 001-TODO 相对路径，值 = 目标资料夹），校验逻辑与上轮相同：
- `VALID = {AI-Agent, AI-Models, Local-LLM, Hermes, AI-Tools, NotebookLM, misc}`
- 磁盘扫描 `docs/001-TODO`（排除 .DS_Store 和 .gitkeep）与 MAP 双向 diff，断言 missing/extra 均为空
- 输出分布计数，必须等于 7/2/13/4/30/24/5

**Step 2:** 运行 `python3 /tmp/classify_todo.py`，Expected: `OK manifest: 85 files` + 分布一致。

### Task 2: 子代理执行移动与校验

**Objective:** git mv 85 档 + 清空子目录 + 七项校验。

**Step 1:** 派实施子代理，上下文要点：
1. `git status --porcelain -- docs/001-TODO docs/100-InBox` 有输出即停
2. baseline：档数（85）、`git rev-parse HEAD`、wikilink+embed 基线数（用下方检查脚本，/tmp 可能已清空，脚本内容随上下文给全）
3. 按 manifest 逐条 `git mv docs/001-TODO/<rel> docs/100-InBox/<dest>/`
4. 移动后 `rmdir` 五个子目录（U出金/images/cli/skills/旅游，只删空的；非空=有遗漏，停下报告）；`.gitkeep` 不动，001-TODO 保留
5. 校验并汇报原文：
   - a. `find docs/001-TODO -type f ! -name ".DS_Store" ! -name ".gitkeep" | wc -l` → 0；`.gitkeep` 仍在
   - b. `find docs/001-TODO -type d` → 只有 `.` 本身
   - c. 100-InBox 档案总数 222+85 = 307；顶层仍 7 个资料夹；无二层目录；根无散档
   - d. wikilink/embed 断链数与 baseline 相同（同口径对比）
   - e. 档名守恒：移动前后 001-TODO 档名集合 ⊆ 100-InBox 新增档名集合
   - f. `git status --porcelain -- docs/001-TODO docs/100-InBox` 状态字母只有 R
6. 不 commit 不 push

link 检查脚本（上轮 /tmp/qa_links.py 逻辑，凭上下文重写）：
```python
import os, re, pathlib, sys, urllib.parse
VAULT = "/Users/tommy/Documents/work.nosync/yudady/yudady.github.io/docs"
out_json = sys.argv[1]
wikilinks = re.compile(r"\[\[([^\]|#]+)")
md_embeds = re.compile(r"!\[\[([^\]|]+)")
files = {}
for dp, dns, fns in os.walk(VAULT):
    dns[:] = [d for d in dns if d != ".obsidian"]
    for fn in fns:
        if fn != ".DS_Store":
            p = pathlib.Path(dp, fn)
            files[urllib.parse.unquote(p.name)] = p
notes = {k: v for k, v in files.items() if k.endswith(".md")}
wiki_broken, embed_broken = [], []
for name, path in notes.items():
    text = path.read_text(encoding="utf-8", errors="ignore")
    for m in wikilinks.finditer(text):
        t = urllib.parse.unquote(m.group(1).strip())
        if t and t not in notes:
            wiki_broken.append((str(path.relative_to(VAULT)), m.group(0)))
    for m in md_embeds.finditer(text):
        t = urllib.parse.unquote(m.group(1).strip())
        if t and t not in files:
            embed_broken.append((str(path.relative_to(VAULT)), m.group(0)))
import json
json.dump({"wiki_broken": wiki_broken, "embeds_broken": embed_broken},
          open(out_json, "w"), ensure_ascii=False, indent=1)
print(f"wiki broken: {len(wiki_broken)} | embeds broken: {len(embeds_broken)}")
```
注意：md_embeds 会先于 wikilinks 匹配到 `![[x]]`，wikilinks 计数含 embed——无所谓，只需 pre/post 同口径对比。

### Task 3: Spec review 子代理（只读）

**Objective:** 独立复验 85 条 manifest 逐条落位。

检查项：
1. manifest 每条按 basename 验证在 `docs/100-InBox/<dest>/` 下，mismatch=0；反向：100-InBox 档案集合 - 移动前 222 档集合 = 85 个新档名，且每个都能对回 manifest
2. 001-TODO 只剩 .gitkeep，子目录全清
3. 100-InBox：307 档、7 顶层资料夹、无二层、无根散档
4. `git diff --cached --name-status` R 行数 = 85（.gitkeep 无 diff），无非 R 行
5. `git diff --cached -- docs/001-TODO` 无 D 行（.gitkeep 未被动）

### Task 4: Quality review 子代理（只读）

**Objective:** 上轮同类质量维度。

检查项：
1. **附件 embed**：pre/post 同尺对比（`git archive HEAD` 导出基线树），embed 断链净增必须为 0；重点确认 001-TODO 内没有类似上轮 `![[images/...]]` 带相对前缀的 embed（探查已确认 jpg 无引用，但子目录笔记可能有其他 embed，必须扫）
2. **wikilink delta**：净变化 0（允许宿主迁移导致的成对新增/消失）
3. **rename 质量**：85 条全 R100
4. **misc 抽查**：misc 应为 12+5=17 档，新增 5 档符合非 AI 兜底定位
5. 输出 Critical/Important/Minor + APPROVED/REQUEST_CHANGES

### Task 5: 修复循环（如 review 有阻塞项）→ 最终验证 → commit

**Step 1:** 有 Critical/Important 则派修复子代理（同上轮模式），修复后控制器用同口径 link 脚本复审
**Step 2:** commit（只 add docs/001-TODO docs/100-InBox）：
```bash
git add docs/001-TODO docs/100-InBox
git commit -m "refactor(docs): archive 85 notes from 001-TODO into 100-InBox topic folders"
```
**Step 3:** 验证：`git log --oneline -1`；`git status --porcelain -- docs/001-TODO docs/100-InBox` 干净；不 push

## Files likely to change

- 移动：`docs/001-TODO/` 下 85 个档案 → `docs/100-InBox/<7 资料夹>/`
- 删除（清空后 rmdir）：`docs/001-TODO/{cli,skills,旅游,U出金,images}/`
- 保留：`docs/001-TODO/.gitkeep`
- 100-InBox 预期新分布：AI-Agent 125 / AI-Tools 68 / Hermes 46 / Local-LLM 27 / misc 17 / AI-Models 16 / NotebookLM 8 = 307

## Tests / validation

见 Task 2 Step 1 校验 a-f、Task 3、Task 4。核心验收：
1. 001-TODO 清空（仅 .gitkeep），100-InBox 307 档
2. 7 个一层资料夹结构不被破坏（无二层、无根散档）
3. wikilink/embed 断链净增 0
4. 全程 R100 rename，一次 commit，可 revert

## Risks / tradeoffs / open questions

1. **001-TODO 语义变化**：移动后它变成空收件箱。用户约定「新学习笔记默认存 001-TODO」，保留资料夹 + .gitkeep 即可维持工作流；若用户其实想废弃该资料夹，删 .gitkeep 即可（本计划不做）
2. **旅游笔记去向**：misc 只是过渡。vault 外层可能有更合适的家（如 300-閱讀筆記），但用户指令限定「移动到 100-InBox 下面的分类资料夹」，故入 misc；open question：是否后续给旅游/理财单独建夹
3. **存量断链**：misc/24小时重启系统 引用的 `youtube-thumb-viIASQJHmSY.jpg`（无 " 1"）与本次移入的 `...viIASQJHmSY 1.jpg` 档名差一个 " 1"。可选增强：把 jpg 改名去掉 " 1" 并改宿主引用为裸档名即可修复该存量断链——默认不做，列为 open question
4. **同名档风险**：已验证 0 冲突；git mv 若遇意外冲突会报错停下，不会静默覆盖
5. **分类边界**：LangGraph/SkillOpt/codebase-memory-mcp 归 AI-Tools、Muse Glimmer 归 AI-Models 等判定见上文说明；manifest 落盘后仍可人工微调再执行
