---
title: Claude Code 核心觀念新手指南 — 借我 40 分鐘搞懂所有名詞
aliases: ["借我 40 分鐘，讓我告訴你關於 Claude Code 你需要知道的所有觀念", "Claude Code 全觀念", "Claude Code 新手入門"]
tags:
  - claude-code
  - ai-agent
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=29F96QJEf6o"
author: Gary Chen（頻道）；功能聲稱已對照 Anthropic 官方文件驗證
created: 2026-10-29
updated: 2026-10-29
description: Gary Chen 40 分鐘 Claude Code 新手概論：Terminal、工作資料夾、Context Window、CLAUDE.md、Skills、MCP、Hooks、Subagent、Checkpoint、Worktree 與計費，全觀念地圖 + 官方文件逐條驗證。
level: beginner
stars: 4
---

# Claude Code 核心觀念新手指南

> Gary Chen 2026-10-22 的 Claude Code 新手向概論（40:15，觀看 15.8 萬 / 按讚 6,127，2026-10-29 快照）。影片用最白話的方式把 Claude Code 的每個名詞各配一個實際例子，從 Terminal 一路講到 Worktree 與計費。本筆記以影片機翻字幕 + Content Insights 為骨架，**所有指令與功能聲稱逐條對照 code.claude.com 官方文件驗證**，並補上影片沒講到的原生功能（如 `--worktree` 啟動參數）。

> [!info] 頻道性質
> Gary Chen 屬付費社區型頻道（描述區主推 Skool 社群，完整文章放在付費牆內，無免費配套文章）。本次逐條驗證其技術轉述**全部通過**，內容照常收錄；行銷成分僅此標註。

## 目錄

1. [[#1. Claude Code 是什麼：對話即操作的本機 Agent|基礎定位]]
2. [[#2. 工作資料夾：Agent 的世界邊界|工作資料夾]]
3. [[#3. Tools 與 Agentic Loop：大腦與手腳|Tools 與 Agentic Loop]]
4. [[#4. Prompt、截圖與權限|Prompt 與權限]]
5. [[#5. 對話與記憶：Context / History / Memory 三層|對話與記憶]]
6. [[#6. 個人化配置：模型、CLAUDE.md、Skills、Flags|個人化配置]]
7. [[#7. 進階擴充：MCP / Hooks / Plugins / Subagents|進階擴充]]
8. [[#8. 版本安全三層防線：Checkpoint / Git / Worktree|版本安全]]
9. [[#9. 計費：訂閱制 vs API|計費]]
10. [[#10. 新手漸進式學習路徑|學習路徑]]
11. [[#11. 驗證與勘誤記錄|驗證與勘誤]]
12. [[#12. 參考資料|參考資料]]

---

## 1. Claude Code 是什麼：對話即操作的本機 Agent

網頁版 Claude 是「你問它答」；Claude Code 則是**住在你的電腦裡的 Agent**——下達任務後，它自己建檔案、改程式碼、在終端機執行指令、看結果、再驗證。名字裡有 Code，但用途不限寫程式：整理訪談資料、彙報告、處理文件都可以。

| 維度 | 網頁版 Claude | Claude Code |
|------|--------------|-------------|
| 住在哪 | 雲端對話框 | 你的電腦（本機 Agent） |
| 能做什麼 | 產生文字 | 直接讀寫檔案、執行指令、跑測試 |
| 資料邊界 | 對話上下文 | 工作資料夾（Working Directory） |
| 風險 | 說錯話而已 | 會真的動到你的檔案 → 需要權限控管 |

三個入口，本質同一個工具：

| 入口 | 適合場景 | 備註 |
|------|---------|------|
| **Terminal（終端機）** | 最核心的執行介面 | 裝好後輸入 `claude` 啟動；`ESC` 隨時中斷 |
| **IDE 擴充**（VS Code 等） | 邊看程式碼邊讓 AI 改 | 檔案樹 + 編輯區 + 側邊 Claude |
| **桌面版 App** | 想要視覺化操作 | 功能相同，入口不同 |

```bash
claude        # 在專案資料夾內啟動對話
# ESC         # 隨時中斷 Claude 正在做的操作
```

---

## 2. 工作資料夾：Agent 的世界邊界

Agent 只「看得到」你召喚它的那個資料夾。工作資料夾就是它的起點與邊界：

```text
你的電腦
└── interview-project/        ← cd 進來再啟動 claude
    ├── interview-01.txt
    ├── interview-02.txt
    └── (Claude 的產出會存這裡)
```

✅/❌ 新手清單：

- ✅ 先建專案資料夾 → 檔案放進去 → `cd` 進去 → 再啟動 `claude`
- ✅ 相關資料集中放，明確告訴它「讀哪些檔、結果存哪」
- ❌ 在根目錄或一堆雜物的環境裡直接召喚——它會在你看不見的地方亂動檔案
- ❌ 把不相關任務的檔案混在同一個工作資料夾

---

## 3. Tools 與 Agentic Loop：大腦與手腳

模型（Sonnet / Opus / Haiku / Fable）只是**大腦**；**Tools** 才是手腳。入門只需認識三類：

1. **讀取**檔案
2. **寫入 / 修改**檔案
3. **執行指令**（跑程式、跑測試）

你不用指定每一步用哪個工具——講清楚目標，它自己選。核心循環：

```text
        ┌──────────────────────────────────────────┐
        │                                          │
        ▼                                          │
  下達目標 → 執行（跑程式） → 捕獲錯誤訊息 → 修改程式碼
                                                   │
        ▲                                          │
        └──────── 再執行檢驗，直到錯誤消失 ──────────┘
                    這就是 Agentic Loop
```

工具會把真實執行結果回饋給模型——它因此知道上一步成沒成功、該不該繼續改。沒有 Tools，模型就只是紙上談兵。

---

## 4. Prompt、截圖與權限

**Prompt 要有明確目標與邊界。**「幫我做一個網站」太模糊；「做一個週末攝影課的報名頁，含上課時間、地點、作品範例，報名按鈕連到我的表單」——連「第一次看到的人能不能判斷適不適合報名」這個頁面目的都講清楚，它排版取捨才會對。細節沒想好可以反過來讓它先問你（要不要接金流？表單送出後顯示什麼？）。

**跑版、樣式問題直接給截圖**，比文字描述快得多——但要註明參照點（是字體、留白、還是文圖排列），只丟圖說「照這樣做」它還是得猜。

**權限（Permissions）：看不懂就拒絕。** AI 有能力刪改本機檔案，權限彈窗跳出來時：

- ✅ 看懂了、影響範圍確認過 → 同意
- ✅ 看不懂 → 拒絕，反問「你剛想做什麼、為什麼」——邊用邊學
- ❌ 無腦全點同意
- ❌ 因為怕而全點拒絕然後棄用

---

## 5. 對話與記憶：Context / History / Memory 三層

影片把「記憶」拆成三層，這是全片最值得內化的框架：

| 層 | 是什麼 | 生命週期 | 類比 |
|----|--------|---------|------|
| **Context Window（上下文）** | 本輪對話中模型正在用的資訊：你的要求、它讀的文件、工具結果 | 當前對話，有容量上限 | 短期記憶 / 工作檯 |
| **Chat History（對話歷程）** | 自動存檔的過去對話 | 磁碟上，可隨時載回 | 錄音帶 |
| **Memory（經驗記憶）** | 自動整理的「經驗筆記」：這專案的測試指令要這樣跑、你糾正過它的認知 | 跨對話複用，**預設綁定專案** | 筆記本 |

**Token 消耗邏輯**：輸入（含它讀的所有文件與歷史）、內部推理、輸出全都計費。「幫我總結這十份訪談」這句話本身很短，但它要讀完十份文件（input tokens）再生出完整報告（output tokens）——**別只用自己的輸入字數估算用量**。另外 prompt 寫太簡略反而害模型亂繞路、多燒 token；寫太死又限制它的能力發揮。

**四個命令管理對話**：

| 命令 | 作用 | 什麼時候用 |
|------|------|-----------|
| `claude -c` | 無縫接續**同資料夾**最近一次對話 | 昨天做到一半，今天回來 |
| `/resume` | 列出歷史對話，挑一個載入 | 要回到更早的某次對話 |
| `/compact` | 把冗長對話提煉成摘要（保留目標、關鍵決策、進度），釋放上下文 | 同一任務做了很久、上下文快滿 |
| `/clear` | 清空當前短期記憶，開全新任務 | 換到完全不相關的任務 |

```text
要切換到不相關的新任務嗎？
├── 是 → /clear（開新對話，不帶舊記憶）
└── 否，同一任務但上下文太滿
        ├── 重要結論先存檔（大綱、進度寫進實體檔案）
        └── /compact（提煉摘要，輕裝繼續）
```

> [!tip] 影片的務實建議
> `/compact` 的摘要必然丟失細節——壓縮前先把確認過的大綱、資料來源、進度存成實體檔案，下次直接叫它讀檔續作。

**Memory 的維護**：可以隨時叫它「列出你目前記得什麼」，過期的就讓它修正或移除——舊方法留著會在背景干擾未來決策。注意它預設綁定專案，換專案、換電腦不會帶走。

---

## 6. 個人化配置：模型、CLAUDE.md、Skills、Flags

### 6.1 模型梯隊與 Effort

影片口徑「Haiku 快、Sonnet 均衡、Opus/Fable 旗艦」，官方現行產品線（2026-10 查證）：

| 模型 | 官方定位 | API 價格（每百萬 token，輸入/輸出） | 預設 Effort |
|------|---------|-----------------------------------|------------|
| **Haiku 5.5** | 最低成本、最快 | $0.10 起 / $0.50 起 | medium |
| **Sonnet 5.5** | 速度與智能的最佳平衡 | $2 / $10 | high |
| **Opus 5.5** | 長跑型 agentic 編碼 | $4 / $20 | medium |
| **Fable 5.1** | 最高檔：難度推理、長程 agent | $10 / $50 | high |

四款上下文窗口均為 1M tokens。Fable 是 Anthropic 最強的 Mythos 級模型的公眾封裝版（另有受信任組織專用的 Mythos）；**影片把 Opus 和 Fable 併稱旗艦是粗粒度說法，實際 Fable 高一檔**。選型經驗：官方建議若 evals 顯示 Opus 過不了品質關，才上 Fable；免費檔只有 Haiku/Sonnet，Pro/Max 才解鎖 Opus/Fable。

**Effort（推理深度）**：`/effort` 可調模型對單一任務投入多少推理——改個檔名開低，反覆出現的疑難 Bug 開高。越高越費時費額度，且**不保證答案正確**；新手先用預設，遇到難題再手動拉高比較結果。

### 6.2 CLAUDE.md：白紙黑字的規則手冊

把每次對話都要重複交代的硬性規範寫進專案資料夾的 `CLAUDE.md`，每次啟動自動載入：

```markdown
# CLAUDE.md 範例結構
- 一律用繁體中文回答
- 完成的檔案存到 /output/ 資料夾
- 每次改完程式必須跑 npm test 驗證
- 客戶品牌色：#1a73e8
```

**層級劃分**：

| 層級 | 放什麼 | 例子 |
|------|--------|------|
| 全域個人級（家目錄） | 通用偏好 | 「永遠用中文回答」「口語化語氣」 |
| 專案級（專案資料夾） | 該專案特有的規格 | 客戶品牌規範、原始資料路徑、驗證指令 |

不知道怎麼下筆就輸入 `/init`——它會分析專案現況自動生成初版，**生成後仍要人工審閱**再補上你在意的細節。CLAUDE.md 與 Memory 的分工：Memory 是它上班學到的經驗；CLAUDE.md 是你開班前就立好的校規。

### 6.3 Skills：漸進式披露的操作手冊

CLAUDE.md 是「時時刻刻都要知道的事」；**Skills 是「進入特定階段才需要讀的完整 SOP」**——例如審稿五步流程（邏輯 → 病句 → 錯字）。核心是一個 `SKILL.md` 檔：說明什麼情況用、逐步怎麼做，還可以附範例、參考資料、輔助腳本。

```text
載入時機（Progressive Disclosure 漸進式披露）：

啟動 ──► 只載入所有 Skills 的「簡介」幾行字
              │
任務觸發 ──► 「這是審稿任務！」
              │
              ▼
        才載入完整 SKILL.md 內容
   （保護珍貴的 Context Window）
```

| 維度 | CLAUDE.md | Skills |
|------|-----------|--------|
| 載入時機 | 每次啟動全量常駐 | 平時只載簡介，觸發才載全文 |
| 放什麼 | 短小的硬性規範 | 完整多步驟流程、SOP |
| 體積原則 | 越精簡越好 | 長素材幾乎零成本（不用不載） |

觸發方式兩種：直接下指令指定某個 Skill，或讓它依任務與簡介自行判斷載入。它跑歪了就回頭改 SKILL.md——Skills 本身就是可迭代的。

### 6.4 啟動 Flags 與安全防線

```bash
claude -c                          # 接續上次對話（-c 是啟動 flag 的一種）
claude --verbose                   # 顯示每一步系統級執行日誌（不是要它回答更長）
claude --worktree feature-auth     # 原生 worktree 隔離啟動（影片沒講，見第 8 節）
claude --dangerously-skip-permissions   # ⚠️ 跳過所有權限確認
```

`--dangerously-skip-permissions`（急速模式）：所有原本要問你的操作直接執行。**只用在隨時可整個丟棄的隔離環境**——空白專案、虛擬機；日常工作區用了，改錯檔案就是不可逆災難。

**settings.json 敏感檔案隔離**：工作資料夾裡可能有資料庫密碼、客戶個資，可以在 `settings.json` 配排除清單限制 Claude 的檔案讀取。**但影片點出一個關鍵盲區**：只擋檔案讀取工具、卻允許它跑終端指令的話——很多終端指令一樣能把檔案內容印出來，等於後門沒關。

✅/❌ 安全清單：

- ✅ 根本解：把任務真正需要的資料收進一個乾淨的專案資料夾
- ✅ 敏感檔排除 + 同時收緊 Bash 指令白名單
- ✅ 不會寫設定就叫 Claude 列建議排除清單、解釋意圖，確認後再套用
- ❌ 以為擋掉單一讀檔工具就萬無一失

---

## 7. 進階擴充：MCP / Hooks / Plugins / Subagents

四個擴充機制的分工：

```text
                  Claude Code 本體
                        │
   ┌───────────┬────────┴───────┬──────────────┐
   ▼           ▼                ▼              ▼
  MCP        Hooks           Plugins       Subagents
外部工具管道  事件觸發器      打包成套安裝    平行外包小助手
```

| 機制 | 一句話定位 | 例子 |
|------|-----------|------|
| **MCP** | 連接外部雲端服務的標準協定 | 讀寫 Google Calendar、Jira、Notion |
| **Hooks** | 特定事件發生時自動執行預設動作 | 存檔自動格式化、結束前強制安全掃描 |
| **Plugins** | Skills + Hooks + MCP 打包的擴充套件 | 市集一鍵安裝「終極寫作套件」 |
| **Subagents** | 獨立上下文的平行外包 | 派小助手讀三份長文件、查三家競品 |

**Skill vs MCP vs Hooks（影片的黃金三分法）**：

| | 提供什麼 | 例子 |
|--|---------|------|
| Skill | 方法論（教大腦怎麼做） | 「每週五寫週報前，先彙整本週會議記錄」 |
| MCP | 真實工具（手伸出去拿資料） | 實際去行事曆把會議記錄撈回來的工具 |
| Hooks | 確定性觸發（死規則交給程式碼） | 存檔瞬間自動掃禁用標點，不勞模型逐行看 |

Skills 寫得出工作流，但**寫不出「登入行事曆的能力」**——那要靠 MCP 給真實工具；死板的重複檢查交給 Hooks，用確定性程式碼替代高成本的模型反覆檢查。Hooks 進階玩法：觸發後再呼叫另一個 AI 模型。

**Subagents 調度準則**：

- ✅ 講死回傳格式：只要條列結論、資料來源、發現的矛盾
- ✅ 多個完全獨立的任務 → 派多個 subagent 平行跑，主對話彙整成對照表
- ✅ 可與 Skills 組合：「派研究 subagent，嚴格按我的學術查證 Skill 執行」
- ❌ 只說「讀這份文件」——它可能把全文貼回來，瞬間塞爆主對話上下文
- ❌ 改個標題也派 subagent——背景交代、等待啟動、整合答案全都要花額外的時間與 token

**Subagent 的核心價值是上下文隔離**：研究過程中的雜訊（讀過的長文、失效的搜尋）全部留在它自己的記憶裡，主對話只收割提煉後的結論。

---

## 8. 版本安全三層防線：Checkpoint / Git / Worktree

### 8.1 短線後悔藥：Checkpoints（`/rewind`）

Claude 每次用檔案編輯工具改代碼時，背景自動記錄狀態快照。輸入 `/rewind`（或提示列空白時連按兩次 `Esc`）打開回溯選單，可選：

- **Restore code only**——只退回檔案
- **Restore conversation only**——只退回對話記憶
- **Restore both**——兩者同時倒轉
- （官方還有 Summarize 選項：把選定點之後/之前的對話壓成摘要——影片未提）

**`/rewind` 撤不回的東西**（影片聲稱，官方文件證實並更精確）：

- 只被**終端指令**改過的檔案（從未被編輯工具碰過 → 根本不在追蹤範圍）
- 你自己在 Claude Code 之外手改的、其他軟體改的
- 已經送出去的外部 API 請求（事實就是事實）
- Subagent 的編輯通常也不在 checkpoint 內
- symlink / hard-link 檔案會被跳過

官方補充（影片沒講的增量）：每個 session 保留**最近 100 個 checkpoint**，快照約 30 天後自動清理（`cleanupPeriodDays` 可調）；resume 回來的 session 照樣能 `/rewind`。

### 8.2 長線里程碑：Git / GitHub

- **Checkpoint**：工作中隨手後悔用；**Git commit**：功能完成點的正式快照
- Git = 本機版本控制軟體；GitHub = 微軟旗下的雲端托管服務——只用 Git 不上 GitHub 完全可以
- Claude Code 可以幫你比對改動、寫規範的 commit message、代跑 commit 指令
- ❌ 不是開著 Claude Code 就每秒自動進 Git 歷史——正確工作流：完成一個小目標 → 檢視改動 → 確認 → 主動叫它打包成一個 commit

### 8.3 平行開發隔離：Git Worktrees

開兩個 Claude Code 視窗同時改同一個資料夾，會互相覆蓋。Worktree 讓**同一個 Git 專案長出多個獨立實體目錄**（各自對應一個 branch），共用同一套版本歷史，完成後安全合併：

```bash
# Claude Code 原生支援（影片只提到 Cursor 有此功能，實際 claude 本身就有）：
claude --worktree feature-auth     # 建隔離工作樹並啟動，預設在 .claude/worktrees/ 下
# 另一個終端機再開一個不同名字 = 第二個隔離 session
# 或對話中直接說 "work in a worktree"，它會自己建
```

再提醒影片的收尾警告：**worktree 只隔離本機檔案**——兩邊代碼最終要合回同一個專案；若兩邊都讀寫同一張雲端資料庫表，資料庫的變更不會因為 worktree 而隔離。

**三層防線決策樹**：

```text
出了問題 / 想同時做多件事？
├── 剛剛改壞的，幾分鐘內 → /rewind（checkpoint 快速回滾）
├── 幾天前改壞的、要找哪次改壞的 → Git log 比對 + revert
├── 功能完成要存檔 → git commit（叫 Claude 代勞）
└── 要兩個 Claude 同時改不同功能 → git worktree（實體隔離）
```

---

## 9. 計費：訂閱制 vs API

| | 訂閱制（Pro / Max） | API 按量計費 |
|--|-------------------|-------------|
| 付費方式 | 固定月費 | 按 token 用量 |
| 用量 | 有上限 / 限速，**不是自助餐** | 用多少付多少，按模型檔次計價 |
| 適合 | 每天重度使用的一般人 | 程式串接、批次任務 |
| 認證路徑 | claude.ai 帳號登入 | Console API key（獨立路徑） |

- **兩套帳務完全分開**：買了 Pro 不代表能免費無限打 API——混著用，帳單來了會頭痛
- 訂閱檔位對應模型解鎖：Free 只有 Haiku/Sonnet；Pro/Max 解鎖 Opus/Fable 與更大額度
- **Subagent 也吃額度**——省你的時間，但來回調度可能更貴；評估成本要看「完成整個任務的總成本」，含出錯重跑的試錯成本
- `/usage` 隨時看當前 session 花費、方案剩餘額度與活動統計（別名 `/cost`、`/stats`）

---

## 10. 新手漸進式學習路徑

影片的核心建議：**第一天不需要學會全部**。按痛點出現的順序逐步導入：

| 階段 | 你遇到的痛點 | 該學的 |
|------|-------------|--------|
| 1 | 剛開始 | Terminal 啟動、基礎 Prompt、專案資料夾隔離、看懂權限彈窗 |
| 2 | 對話越來越亂 | `-c` / `/resume` / `/compact` / `/clear` |
| 3 | 每次重複交代同樣要求 | `CLAUDE.md`（`/init` 起步） |
| 4 | 規範太長塞爆上下文 | Skills |
| 5 | 資料在雲端服務裡 | MCP |
| 6 | 重複做死板檢查 | Hooks |
| 7 | 長文件研究塞爆主對話 | Subagents |
| 8 | 多線程開發 | Git Worktrees |

---

## 11. 驗證與勘誤記錄

> 素材為 webReader 機翻字幕（中→英）+ 用戶 Content Insights；以下逐條對照官方文件驗證（2026-10-29）。

| 影片聲稱 | 驗證結果 | 來源 / 說明 |
|---------|---------|------------|
| 模型家族為 Haiku、Sonnet、Opus、Fable | ✅ | 官方選型指南同款四分法；Fable 為 Mythos 級公眾封裝 |
| Opus 和 Fable 併稱旗艦 | ⚠️ 粗粒度 | 官方檔位 Fable > Opus（$10/$50 vs $4/$20 MTok）；另有受信任組織專用 Mythos |
| `/effort` 可調推理深度、模型設定內可調 | ✅ | 官方 commands：`/model`、`/effort`；各模型預設 effort 不同（Fable/Sonnet=high，Opus/Haiku=medium） |
| `claude -c` 接續對話；`/resume` 選歷史 | ✅ | `--continue` flag 見官方文件；`/resume` returns to an earlier conversation |
| `/compact` 提煉摘要；`/clear` 清記憶不刪檔 | ✅ | 官方：`/clear` starts fresh... while keeping project memory |
| `/init` 自動生成 CLAUDE.md 初版 | ✅ | 官方：generate a starter CLAUDE.md（另可 `/memory` 精修，影片未提） |
| Skill 檔名 skill.md、漸進式披露 | ⚠️ 檔名 | 正名 **SKILL.md**（大寫）；漸進披露屬實（body 只在使用時載入），且屬 Agent Skills 開放標準 |
| `--dangerously-skip-permissions` 跳過權限 | ✅ | 真實 flag；官方建議僅隔離環境使用，與影片一致 |
| settings.json 排除敏感檔；Bash 是後門 | ✅ | 官方權限系統 Read/Bash 分類審批，與影片盲區警告吻合 |
| `/rewind` 可選僅檔案 / 僅記憶 / 兩者 | ✅ | 官方 rewind 選單三選項 + 影片未提的 Summarize 選項 |
| rewind 撤不回終端指令改動、外部 API | ✅ | 官方精確口径：**只**被 Bash 改過的檔案不被追蹤；外部修改不追蹤；保留最近 100 個 checkpoint（約 30 天） |
| Worktree 實體隔離、共享版本史、不隔離雲端 DB | ✅ | 官方 worktrees 文件；影片還漏講 Claude Code 原生 `--worktree` flag 與 EnterWorktree 工具 |
| 訂閱制與 API 計費完全分開 | ✅ | 訂閱登入與 Console API key 為兩條獨立認證/計費路徑 |
| `/usage` 看花費與配額 | ✅ | 官方：session cost + plan usage limits + activity stats；別名 `/cost`、`/stats` |
| 訂閱舉例（機翻寫作 "Cursor Pro"） | ❓ | 機翻疑雲：上下文與 Insights 判斷為 Claude Pro；不影響「訂閱 vs API 分開」的結論 |

---

## 12. 參考資料

- [Commands — Claude Code Docs](https://code.claude.com/docs/en/commands)（`/usage`、`/effort`、`/init`、`/compact`、`/clear`、`/resume` 全表）
- [Checkpointing — Claude Code Docs](https://code.claude.com/docs/en/checkpointing)（`/rewind` 選項與限制）
- [Run parallel sessions with worktrees — Claude Code Docs](https://code.claude.com/docs/en/worktrees)（`--worktree` flag、EnterWorktree、`.worktreeinclude`）
- [Configure permissions — Claude Code Docs](https://code.claude.com/docs/en/permissions)（Read/Bash 分類審批、settings 檔案）
- [Extend Claude with skills — Claude Code Docs](https://code.claude.com/docs/en/skills)（SKILL.md、漸進披露、Agent Skills 標準）
- [Models overview — Claude Platform Docs](https://platform.claude.com/docs/en/about-claude/models/overview)（Fable/Opus/Sonnet/Haiku 5.x 定價與規格）
- [Choosing the right Claude model — Claude](https://claude.com/resources/tutorials/choosing-the-right-claude-model)（四模型選型 + 訂閱檔位解鎖）
- [原始影片 — 借我 40 分鐘，讓我告訴你關於 Claude Code 你需要知道的所有觀念](https://www.youtube.com/watch?v=29F96QJEf6o)

---

## 13. 相關筆記

- [[Claude-Code-持久化记忆系统]] — Claude-Mem 插件：把「記憶」做到跨會話持久化的第三方方案（本篇第 5 節 Memory 的進階版）
- [[AI-Plan-Mode-四步工作流]] — Plan Mode 的正確用法：動手前先對齊規劃
- [[2026-06-08-matt-van-horn-agentic-engineering-hacks]] — Agentic Engineering 進階技巧（CLAUDE.md / 自動化的實戰調校）

---

*文檔生成時間：2026-10-29*
*基於 Gary Chen 影片（2026-10-22）+ Anthropic 官方文件驗證（Claude Code v2.1.x 文件現況）*
