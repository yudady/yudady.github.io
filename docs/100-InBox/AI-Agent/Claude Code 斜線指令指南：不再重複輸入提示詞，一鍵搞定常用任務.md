---
title: "Claude Code 斜線指令指南：不再重複輸入提示詞，一鍵搞定常用任務"
source: "https://haosquare.com/claude-code-slash-commands/"
author:
  - "[[好豪]]"
published: 2025-11-28
created: 2026-03-30
description: "Claude Code 斜線指令，把常用工作流程濃縮成一個指令，別再重複輸入相同提示詞。"
tags:
  - "clippings"
---
> 你是否也厭倦了反覆輸入相同的提示詞？

你用 Claude Code 寫程式，一定遇過這個情況：每次想讓 Claude 幫你產生 Git 提交訊息，都要重新輸入「請根據我的程式碼變更，產生一個符合團隊 Commit 規範的提交訊息」；每次想做程式碼審查，又得打一長串「請審查這段程式碼的安全性、效能、可讀性」。

重複輸入這些提示詞不只浪費時間，更糟的是，你可能每次措辭都略有不同，導致 Claude 的回應品質不穩定。

像我就偶爾會忘記審查的時候要加入「可讀性」 (ｰ ｰ;)

**斜線指令** （Slash Commands）就是為了解決這個痛點而存在。它讓你把常用的提示詞「打包」成一個以斜線為開頭的簡單指令，只要輸入 `/commit` 或 `/review` ，就能立刻啟動完整的工作流程。這不只是節省打字時間的小技巧，更是掌握 Vibe Coding 節奏、提升 AI 寫程式效率的重要習慣。

在這篇文章中，我會帶你從零開始理解 Claude Code 斜線指令的核心概念、三大重要價值，並手把手教你建立第一個客製化指令。最後，我們還會從 SuperClaude 這個熱門專案學習高手如何設計並運用客製化斜線指令。

---

AI 工具、聊天機器人與虛擬助理

商務處理流程

開發工具

電腦硬體

電腦

寫作資源

機器學習與人工智慧

Data-Analytics

圖書與文學

---

## 什麼是 Claude Code 斜線指令？一分鐘搞懂核心概念

斜線指令本質上就是 **預先寫好的「快捷鍵」** 。

想像你的手機有個回家快捷鍵，只按一下就自動開啟導航、傳訊息給家人、調整冷氣溫度。Claude Code 的斜線指令就是這個概念：把複雜的多步驟操作，濃縮成一個簡單的指令。

這些快捷鍵甚至不用你自己設計，Anthropic 官方幫你寫好一堆好用的斜線指令了！你可以在 [官方文件](https://code.claude.com/docs/en/slash-commands) 看到完整列表，或者更好用的方式，是在 Claude Code 內輸入 `/help` 指令，工作到一半想查詢的時候隨時都有小抄能看到自己有什麼好用快捷鍵可以用，不需要死記硬背。

![[70898cca38e7893c4465d7d315a66e93_MD5.png]]

Claude Code 內查找斜線指令（製圖：好豪）

而斜線指令的運作方式超好理解：你在一個 Markdown 檔案裡寫下提示詞，Claude Code 會把這個檔案的名稱變成指令。當你在對話中輸入 `/指令名稱` ，Claude 就會自動讀取那個 Markdown 檔案的內容，像「複製貼上」一樣把整段提示詞注入到當前對話中。

這是一個 **純粹的字串替換** （string substitution）過程：

```
[你輸入 /commit] 
    ↓
[讀取 commit.md 檔案內容] 
    ↓
[注入提示到對話中] 
    ↓
[Claude 執行任務]
```

這種純粹字串替換的運作方式會讓我想到 [C++ 的 #define Macro](https://en.cppreference.com/w/cpp/preprocessor/replace.html)

---

## 為什麼 Claude Code 的斜線指令非學不可？三個你不能錯過的理由

### ⏰ 節省時間，讓工作「可重複」

想必每位開發者的工作流程都有一些自己的 *儀式* ，可能是產生 Git 提交訊息的固定格式、或是 debug 時習慣先檢查的幾個項目。斜線指令讓你把這些流程 **標準化** ，確保每次執行都遵循相同的最佳做法。

**實例** ：建立一個 `/commit` 指令

```markdown
根據當前的程式碼變更，產生一個符合團隊 Commit 規範的提交訊息。

格式：<type>(<scope>): <subject>

提交訊息是英文，請確保訊息清晰、簡潔、字數越少越好，並且說明「為什麼」做這個改變。
```

之後每次要提交程式碼，只要輸入 `/commit` ，Claude 就會按照這個標準流程幫你產生訊息。不用再擔心格式不一致，也不用每次都重新解釋你要的規範。

### 🧠 給 Claude 記憶，永遠不會忘記專案脈絡

這裡特別要提的是 `/init` 跟 `/memory` 兩個記憶指令。

大型語言模型的本質是 *無狀態* 的，每次對話都是新的開始、幾天前討論過的內容都不算數。但開發專案不是如此，你的程式碼架構、命名慣例、測試策略，這些專案知識如果每次都會被 LLM 忘記、都要重新說明，實在太浪費時間。

為了解決這個問題，我們可以透過內建指令 `/init` 與 `/memory` 、結合 `CLAUDE.md` 使用，啟動 AI 的 **記憶** 能力。

執行 `/init` 後，Claude Code 會掃描你的整個專案，自動產生一個名為 `CLAUDE.md` 的檔案。這個檔案是專案的記憶大腦，記錄了：

- 專案的整體架構
- 重要的執行指令（如 `npm run build` ）
- 程式碼風格慣例
- 核心技術棧（Tech Stack）

每次你啟動 Claude Code，它都會自動載入這個 `CLAUDE.md` ，讓 Claude 從一開始就「記得」你的專案脈絡。你不用再重複說明「我們用 React + TypeScript」或「測試框架是 Jest」，用 `/init` 創造 `CLAUDE.md` 後，Claude 都已經知道了。

專案天天都在計劃、記憶天天都要更新，這個 `CLAUDE.md` 當然也可以隨時編輯。你除了可以直接找到 `CLAUDE.md` 檔案用 VSCode 來編輯內容以外，Claude Code 內部也可以使用 `/memory` 在終端機內部直接編輯記憶內容，不用脫離現有工作流、很方便。

**想深入了解 `CLAUDE.md` 如何讓 Claude 發揮記憶的威力？** 我寫了兩篇專文詳細介紹：

- \[入門\] [Claude Code 的記憶大腦：CLAUDE.md 讓 AI 記住你的寫程式偏好](https://haosquare.com/claude-code-claude-md-intro/)
- \[進階\] [CLAUDE.md 實戰技巧整理：10 個讓 Claude Code 更聰明的進階設定](https://haosquare.com/claude-code-claude-md-advanced-tips/)

### 🎯 精準控制，行動你說了算

AI 工具最大的問題之一，就是 **不可預測** 。有時候你只想讓 Claude 做 A，它卻自作主張做了 B。

有趣的是，AI 的「自作主張」在 Claude Skills 的設計反而算是一種優點，我們下一段再補充。

斜線指令必須要手動觸發，你得親手寫出 `/指令名稱` AI 才會有動作，這個特性讓你 **完全掌控執行時機** 。需要程式碼 Pull-Request 審查？輸入 `/review` ；需要資安檢查？輸入 `/security-review` 。Claude 不會猜測你的意圖，也不會在不該行動的時候擅自行動。

這種精準控制，在以下場景特別重要：

- **程式碼提交前的檢查流程** ：你希望按照 **固定順序** 執行 Lint → Unit Test → Review
- **安全性審查** ：你需要確保每次都檢查相同的資安項目
- **文件生成** ：你希望文件格式完全一致，不要有意外驚喜
	（最基本的：不要讓 AI 在文件內一下寫英文、一下寫中文！）

---

## 斜線指令與 Skills 有什麼不同？

斜線指令（Slash Commands）與技能（Skills）有什麼不同？這個問題超常見，連 Anthropic 都在官方文件、部落格、YouTube 影片等平台反覆解釋，因為真的很容易搞混。

簡單來說： **斜線指令是你手動觸發，Skills 是 Claude 自主決定** 。

Skills 的好處在於 AI 幫你決定什麼時候運用某個技能最有效率，例如請 AI 寫技術文件，它自動在寫完後啟動 `article-refine` 技能來檢查文法與用詞錯誤。然而自主決定的缺點是：AI 也有可能 **忘記啟動技能** ！行動究竟會不會執行反而不好預測。例如 [這位仁兄](https://scottspence.com/posts/how-to-make-claude-code-skills-activate-reliably) 還得測試各種 Hook 努力提高技能被啟動的機率。

因此，你現在大概可以理解：為什麼斜線指令是「手動觸發」很重要？

斜線指令的關鍵特色是 **你必須明確輸入才會執行** 。這跟 Claude Skills 不同，技能是 Claude 自己判斷要不要用，而斜線指令是 **啟用時機你說了算** 。這種精準控制，讓你在需要明確掌握執行時機的場景（如程式碼提交、安全審查）時，不會被 AI 的「自作主張」打亂節奏。

（延伸閱讀： [Claude Skills 完整說明與使用教學](https://haosquare.com/claude-skills/) ）

---

## 客製化斜線指令（Custom slash commands）：指令的超多元功能

Claude Code 的斜線指令分為兩大類：不只有上面介紹過的內建指令、還有 **客製化** 斜線指令（Custom slash commands）。

| 類型 | 儲存位置 | 適用範圍 | 說明 |
| --- | --- | --- | --- |
| **內建指令**   (Built-in Commands) | Claude Code 內建 | 全部範圍 | 官方提供，用於管理對話、模型選擇、專案初始化等等 |
| **客製化．專案指令**   (Project Commands) | `.claude/commands/` | 單一專案，可團隊共享 | 存在 **專案資料夾** ，透過 Git 與團隊成員共享 |
| **客製化．個人指令**   (Personal Commands) | `~/.claude/commands/` | 所有專案 | 存在你的 **個人電腦主目錄** ，適用於所有專案的個人習慣 |

**內建指令** 是 Claude Code 預設提供的工具，像是 `/clear` 可以清空對話歷史、 `/compact` 可以精簡目前對話以節省 Token 成本、 `/init` 可以初始化專案記憶體。

接著，客製化（自定義）的斜線指令，依照適用範圍還可分成專案指令與個人指令。

**專案指令** 適合團隊協作。你可以在 `.claude/commands/` 資料夾中建立 *客製化* 指令，然後把這個資料夾加入 Git 版本控制。這樣 **整個團隊都能用相同的工作流程** ，例如能夠確保每個人的程式碼審查標準、提交訊息格式都一致。

**個人指令** 則是你的 *秘密* 武器。可能是你個人習慣的 debug 流程、或是你特別喜歡的文件撰寫格式。這些指令存在 `~/.claude/commands/` ，只有你能用，但 **適用於你所有的專案** 。

```
=========================================
 📘 專案級配置範圍 (Project Level)
=========================================
📁 你的專案/ <-- [通常有 Git 控制]
│
└── 📁 .claude/
    │
    ├── 🔵 📂 commands/ 👥  <-- [團隊共享的指令]
    │   │
    │   ├── 📄 commit.md
    │   └── 📄 review.md
    │
    └── (其他專案檔案...)

=========================================
 📙 個人級配置範圍 (Personal Level)
=========================================
📁 你的主目錄 (~/)/
│
└── 📁 .claude/
    │
    └── 🟠 📂 commands/ 👤  <-- [個人專用的指令]
        │
        ├── 📄 fix-issue.md
        └── 📄 my-workflow.md
```

---

---

## 手把手教學：打造你的第一個客製化斜線指令

理解客製化斜線指令的概念後，讓我們動手做一個。

### 建立一個簡單的 /optimize 指令

**步驟 1：建立目錄**

```bash
mkdir -p .claude/commands
```

**步驟 2：建立 Markdown 檔案**

```bash
echo "分析這段程式碼的效能問題，並提出三個具體最佳化建議：" > .claude/commands/optimize.md
```

這段只是在 `.claude/commands/` 資料夾內創立一個新檔案而已，你當然也可以選擇直接用 VSCode 來建立檔案、不一定要透過終端機指令。

**步驟 3：使用指令** 在 Claude Code 中輸入：

```
/optimize
```

就這麼簡單！你已經建立了第一個客製化指令。

提醒：如果你正開著 Claude Code，新創立的斜線指令要重新啟動 Claude Code 才會生效

### 進階功能：設計出彈性更高的指令

基礎版的斜線指令已經很實用，但如果你想讓指令變得更靈活有彈性，可以用以下三個進階功能：

#### 1\. 參數傳遞（$ARGUMENTS）

讓單一指令模板適用於不同情境。

**範例客製化指令** ： `fix-issue.md`

```markdown
修復 Issue #$ARGUMENTS，並確保符合最佳實踐。

請先分析問題根源，再提出解決方案。
```

**使用時** ：

```
/fix-issue 123
```

這樣 AI 就會聚焦在 Issue #123。

**為什麼好用** ：你不需要為每個 Issue 重新建立一個指令，像是 `/fix-issue-123` 、 `/fix-issue-321` 等等。一個範本，搭配動態參數，就能處理所有情況。

#### 2\. Bash 命令執行（!）

在提示詞中嵌入 Bash 命令，透過驚嘆號 `!Bash命令` 來做到。我們直接用一個實例來介紹使用 Bash 的好處：**`!git status` 讓 Claude 即時獲取專案狀態** 。

**範例客製化指令** ： `commit.md`

```markdown
---

allowed-tools: Bash(git*)

---

根據以下 Git 狀態產生提交訊息：

!git status

!git diff HEAD
```

**為什麼好用** ：Claude 不再是憑空猜測、或是根據對話紀錄來推論，而是基於「當下這一秒」的程式碼變更來產生訊息，確保提交訊息的準確性。

#### 3\. 檔案引用（@）

直接將特定檔案內容納入上下文，透過小老鼠符號 `@檔案位置` 來達成。

**範例客製化指令** ： `security-review.md`

```markdown
審查這個檔案的安全性：

@src/auth/login.js

遵照 @docs/security-rules.md 此文件條列的所有規範。

請特別注意 SQL Injection 和 XSS 風險。
```

**為什麼好用** ：不用手動複製貼上程式碼，也不用擔心遺漏任何檔案。Claude 會自動讀取並分析。

### 完整進階範例（結合三種功能）

現在我們把三個功能結合在一起，建立一個精準的「Commit 提交訊息產生器」：

```markdown
---

allowed-tools: Bash(git*)

description: Commit 提交訊息產生器

---

根據 Issue #$ARGUMENTS 的需求，分析以下變更：

!git diff HEAD

並審查相關檔案：

@src/components/Button.js

審查必須遵照 @docs/security-rules.md 此文件條列的所有規範。

產生符合團隊 Commit 規範的提交訊息。

格式：<type>(<scope>): <subject>
```

**使用時** ：

```
/smart-commit 456
```

Claude Code 會：

1. 讀取 Issue #456 的需求（透過 `$ARGUMENTS` ）
2. 執行 `git diff HEAD` 查看實際變更（透過 `!`）
3. 審查 `Button.js` 的程式碼（透過 `@` 來找到關鍵 js 檔案、並參考重要文件）
4. 產生一個完整、準確、符合規範的提交訊息
```
Issue #456 ($ARGUMENTS)
        ↓
    git diff (!指令)  ──┐
        ↓             　│
Button.js (@引用)  ─────┤──→  整合成完整提示  ──→  Claude 產生提交訊息
```

---

## 專家示範：向 SuperClaude 學習客製化指令的威力

看完基礎和進階功能後，你可能還想要看更多高手們使用客製化斜線指令的範例？

[SuperClaude](https://github.com/SuperClaude-Org/SuperClaude_Framework) 是一個為了強化 Claude Code 功能而開發的開源框架，提供 16+ 專業化斜線指令，涵蓋從開發、測試到文件的完整工作流程。讓我們以 `/sc:improve` 為例，看看進階客製化指令可以做到什麼程度。

SuperClaude 需要額外安裝才能使用，但你不需要急著安裝也沒關係！這個段落的目的是學習高手怎麼寫客製化斜線指令， [此連結](https://github.com/SuperClaude-Org/SuperClaude_Framework/blob/master/src/superclaude/commands/improve.md) 是 `/sc:improve` 指令的完整內容，以下我們只擷取一小段看看：

```markdown
## Behavioral Flow

1. **Analyze**: Examine codebase for improvement opportunities and quality issues

2. **Plan**: Choose improvement approach and activate relevant personas for expertise

3. **Execute**: Apply systematic improvements with domain-specific best practices

4. **Validate**: Ensure improvements preserve functionality and meet quality standards

5. **Document**: Generate improvement summary and recommendations for future work

Key behaviors:

- Multi-persona coordination (architect, performance, quality, security) based on improvement type

- Framework-specific optimization via Context7 integration for best practices

- Systematic analysis via Sequential MCP for complex multi-component improvements

- Safe refactoring with comprehensive validation and rollback capabilities
```

從這段我們可以看到：

1. `/sc:improve` 不只是「看一眼程式碼然後給建議」，而是遵循一個結構化的流程： `程式碼分析 → 計畫並討論解決方案 → 執行 → 驗證檢查 → 文件紀錄` ，每個步驟都有明確的目標和檢查點，確保改進是「基於證據」而非「憑感覺」
2. 自動啟用專業角色（Persona），例如當你執行 `/sc:improve --type security` 時，框架會自動啟用「Security Persona」，讓 Claude 以安全專家的視角來審查程式碼

這段 Markdown 只是片段，但光是學習這個片段， `/sc:improve` 已經證明了客製化斜線指令可以：

- **標準化複雜的多步驟工作流程** ：不只是「提示詞範本」，而是完整的自動化流程
- **根據不同情境自動調整行為** ：透過旗標（Flag）和參數（ `$ARGUMENT` ），單一指令勝過十個固定指令
- **整合專業知識** ：透過自動啟用的角色系統，讓 AI 以專家視角工作

[SuperClaude](https://github.com/SuperClaude-Org/SuperClaude_Framework) 目前提供 16+ 類似的專業指令（如 `/sc:analyze` 、 `/sc:build` 、 `/sc:test` ），涵蓋開發、測試、文件等各個環節，都是透過相同的客製化斜線指令機制建構而成。這些指令都展示出斜線指令怎麼從「簡單快捷鍵」進化到「專業工作流程引擎」，所以 SuperClaude 是我學習運用斜線指令的重要模範（之一）。

---

## 結語

看完這篇文章，相信你已經理解：

- **斜線指令是 Claude Code 的基礎** ：從簡單的快捷鍵到複雜的工作流程，都建立在這個機制上
- **斜線指令讓你掌控 AI 行為** ：不再依賴 AI 的隨機發揮，而是按照你的規則執行
- **客製化與其進階功能讓指令變得更加智慧** ：參數傳遞、Bash 執行、檔案引用，三者結合能創造出強大的自動化流程

今天就開始試試吧：

- 超簡單第一步：在 Claude Code 輸入 `/help` ，看一遍所有內建指令，肯定有一個適合你用的
- 客製化指令也不太難：從建立一個簡單的 `/optimize` 或 `/commit` 指令開始，不用追求完美，先讓它能用、再逐步改進。你會發現，每增加一個客製化指令，你的 Vibe Coding 節奏就會更流暢一點
  

參考資料：

- [Scott Spence: How to Make Claude Code Skills Activate Reliably](https://scottspence.com/posts/how-to-make-claude-code-skills-activate-reliably)
- [SuperClaude: Transform Claude Code into a Structured Development Platform](https://github.com/SuperClaude-Org/SuperClaude_Framework)
- [Claude Code Doc: Slash Commands](https://code.claude.com/docs/en/slash-commands)

---

如果這篇文章對你有幫助，歡迎追蹤我的 [Facebook 粉絲專頁](https://haosquare.com/social/facebook/) 或 [Threads 帳號](https://haosquare.com/social/threads/) ，我會持續分享 Claude 和 AI 工具的學習心得。