---
title: Scrapling - AI 自適應網頁爬蟲框架
aliases: [Scrapling, D4Vinci/Scrapling]
tags:
  - web-scraping
  - python
  - adaptive-parser
  - anti-bot
  - status/active
  - type/learning-note
source:
  - "https://youtu.be/xryFsADR5P4"
  - "https://github.com/D4Vinci/Scrapling"
author: Karim Shoair (D4Vinci)
created: 2026-06-07
updated: 2026-06-07
description: Scrapling 是一個 Python 自適應爬蟲框架，核心賣點是自適應元素追蹤——網站改版後自動重新定位元素，無需手動修復選擇器。
level: intermediate
stars: 5
note: 無字幕，基於 GitHub README + Medium 作者文章 + 第三方評測綜合整理
---

# Scrapling - AI 自適應網頁爬蟲框架

> Scrapling 解決爬蟲開發最大的痛點：**網站改版導致選擇器失效**。它的自適應解析器能記住元素的特徵指紋，在頁面結構變化後自動重新定位元素。同時整合反偵測繞過、多種 Fetcher 後端、類 Scrapy Spider 框架，形成一站式爬蟲解決方案。

## 目錄

- [核心架構：四層設計](#核心架構四層設計)
- [自適應元素追蹤（核心差異化）](#自適應元素追蹤核心差異化)
- [Fetcher 體系：三種後端](#fetcher-體系三種後端)
- [Spider 框架：類 Scrapy 但更現代](#spider-框架類-scrapy-但更現代)
- [性能基準測試](#性能基準測試)
- [實戰程式碼示例](#實戰程式碼示例)
- [選擇器替代方案](#選擇器替代方案)
- [MCP Server 與 AI 整合](#mcp-server-與-ai-整合)
- [安裝與部署](#安裝與部署)
- [優缺點評估](#優缺點評估)

---

## 核心架構：四層設計

```
+---------------------------------------+
|          MCP Server (AI 整合)          |  <- Claude/Cursor 等 AI 工具
+---------------------------------------+
|        Spider Framework (爬蟲框架)      |  <- 並發、多 session、暫停/恢復
+---------------------------------------+
|    Fetchers (三種 HTTP 後端)            |  <- HTTP / Playwright / Stealth
+---------------------------------------+
|    Adaptive Parser (自適應解析器)        |  <- CSS/XPath/文字/正則 + 自適應追蹤
+---------------------------------------+
```

每層可獨立使用，也可組合使用。例如只需要解析器可以 `pip install scrapling`，需要完整功能才拉 Playwright 和瀏覽器依賴。

---

## 自適應元素追蹤（核心差異化）

### 問題場景

傳統爬蟲用固定 CSS/XPath 選擇器定位元素，一旦網站改版（class 名改變、DOM 結構調整），爬蟲就會全部報廢。

### 解決方案：Automatch

1. **首次抓取**時用 `auto_save=True` 保存元素的完整特徵指紋（文字內容、屬性、結構位置等多維度）
2. **網站改版**後用 `auto_match=True`，Scrapling 用相似度算法在整個頁面中找到與原指紋最匹配的元素
3. 整個過程**不需要 AI**，用本地相似度算法完成，無 token 成本

### 跨版本驗證實例

```python
from scrapling import Fetcher

selector = '#hmenus > div:nth-child(1) > ul > li:nth-child(1) > a'

Fetcher.configure(auto_match=True, automatch_domain='stackoverflow.com')

# 2010 年的 StackOverflow（archive.org）
page = Fetcher.get("https://web.archive.org/web/20100102003420/http://stackoverflow.com/")
element1 = page.css_first(selector, auto_save=True)

# 現在的 StackOverflow
page = Fetcher.get("https://stackoverflow.com/")
element2 = page.css_first(selector, auto_match=True)

# 即使 DOM 結構完全不同，仍能找到同一元素
print(element1.text == element2.text)
# Output: True
```

### 決策樹：何時用 Automatch

```
目標網站會頻繁改版？
  ├── 是 → 用 Automatch（auto_save + auto_match）
  └── 否 → 用普通選擇器即可
      元素有穩定的 ID/class？
        ├── 是 → CSS/XPath 選擇器
        └── 否 → 見下方「選擇器替代方案」
```

### 局限性

- 適合**增量改動**（class 改名、小幅結構調整）
- **重大重新設計**可能仍會匹配失敗
- 降低維護成本，但不消除維護需求

---

## Fetcher 體系：三種後端

| Fetcher | 底層 | 適用場景 | 反偵測能力 |
|---------|------|----------|-----------|
| `Fetcher` | httpx（HTTP） | 靜態頁面、API 請求 | TLS 指紋偽裝、HTTP/3 |
| `DynamicFetcher` | Playwright（Chromium） | JS 渲染頁面、SPA | 基礎隱身模式 |
| `StealthyFetcher` | Camoufox（改裝 Firefox） | 重度反爬站（Cloudflare Turnstile） | 通過幾乎所有已知偵測 |

**共同特性：**
- 持久化 Session（`FetcherSession` / `DynamicSession` / `StealthySession`）
- 異步支援
- 內建 `ProxyRotator` 自動代理輪換
- 域名封鎖（瀏覽器類 Fetcher）
- **單一 Spider 混用多種 Session** — 受保護頁面走 Stealth，普通頁面走 HTTP

### 選擇決策樹

```
目標網站有反爬機制？
  ├── 無或輕微 → Fetcher（最快）
  ├── 中等（JS 渲染、簡單驗證） → DynamicFetcher
  └── 重度（Cloudflare Turnstile、指紋偵測） → StealthyFetcher
```

---

## Spider 框架：類 Scrapy 但更現代

遵循 Scrapy 的 `start_urls` → 異步 `parse` 回呼 → `yield items/requests` 模式，但增加多項現代化功能：

### vs Scrapy 對比

| 特性 | Scrapy | Scrapling |
|------|--------|-----------|
| 自適應元素追蹤 | ❌ | ✅ Automatch |
| 多種 Fetcher 後端 | 僅 HTTP | HTTP + Playwright + Stealth |
| 單 Spider 混用 Session | ❌ | ✅ |
| 暫停/恢復 | 需外部工具 | 內建 Checkpoint（Ctrl+C） |
| Stream 模式 | ❌ | ✅ `async for item in spider.stream()` |
| 反偵測繞過 | 需外掛 | 內建（StealthyFetcher） |
| AI 整合 | ❌ | MCP Server |
| 生態成熟度 | ✅ 極成熟 | ⚠️ 成長中（10.6k stars） |
| 依賴輕量 | ✅ | ⚠️ Full 安裝較重 |

### Spider 基本範例

```python
from scrapling import Spider, Request

class MySpider(Spider):
    start_urls = ['https://example.com/products']

    async def parse(self, response):
        for product in response.css('.product-card'):
            yield {
                'name': product.css('h2::text'),
                'price': product.css('.price::text'),
            }
        next_page = response.css('a.next-page::attr(href)')
        if next_page:
            yield response.follow(next_page, self.parse)

# 暫停/恢復
# spider.run(crawldir='./crawl_state')
# Ctrl+C → 自動保存進度
# 重新執行同命令 → 自動恢復
```

---

## 性能基準測試

### 文字提取速度（5000 嵌套元素，100+ 次平均）

```
Scrapling     ████████████████████████████████  2.02ms  (1.0x, 基準)
Parsel/Scrapy ████████████████████████████████  2.04ms  (1.01x)
Raw lxml      ████████████████████████████████  2.54ms  (1.26x)
PyQuery       ████████                           24.17ms (12x)
Selectolax    ███                                82.63ms (41x)
BS4 + lxml    ▏                                  1584ms  (784x)
BS4 + html5lib ▏                                  3392ms  (1679x)
```

### 元素相似度搜尋

| 庫 | 時間 | 倍率 |
|---|------|------|
| **Scrapling** | **2.39ms** | 1.0x |
| AutoScraper | 12.45ms | 5.2x |

> 結論：解析性能與 Scrapy/Parsel 持平，相似度搜尋比 AutoScraper 快 5 倍。

---

## 實戰程式碼示例

### CLI 快速提取（零程式碼）

```bash
# 提取為 Markdown
scrapling fetch https://example.com -o output.md

# 提取純文字
scrapling fetch https://example.com -o output.txt

# 提取 HTML
scrapling fetch https://example.com -o output.html

# 互動式爬蟲 Shell
scrapling shell
```

### 用正則提取價格（無需穩定選擇器）

```python
# 場景：網站 class 名隨機生成，無法用 CSS 選擇器
price_element = page.find_by_regex(r'£[\d\.,]+', first_match=True)

# 找到價格後，向上定位商品容器
container = price_element.find_ancestor(
    lambda ancestor: ancestor.has_class('product')
)

# 從容器生成穩定的 CSS 選擇器
selector = container.generate_css_selector
```

### 混合 Session 爬蟲

```python
class MixedSpider(Spider):
    start_urls = ['https://protected-site.com']

    async def parse(self, response):
        # 用 StealthyFetcher 處理受保護頁面
        yield Request('https://protected-site.com/data',
                      fetcher_type='stealthy',
                      callback=self.parse_protected)
        # 用普通 Fetcher 處理 API
        yield Request('https://api.protected-site.com/v1/items',
                      fetcher_type='http',
                      callback=self.parse_api)
```

---

## 選擇器替代方案

當網站設計糟糕（無穩定 ID/class、隨機命名）時：

| 方法 | 用途 | 示例 |
|------|------|------|
| `find_by_text()` | 用文字內容定位 | `page.find_by_text('下一頁')` |
| `find_by_regex()` | 用正則匹配定位 | `page.find_by_regex(r'\$[\d,]+')` |
| 類比尋找 | 找到一個，Scrapling 找其餘同類 | 基於元素特徵相似度 |
| 條件過濾 | 自定義條件匹配元素 | `lambda el: el.attrib.get('role') == 'button'` |

---

## MCP Server 與 AI 整合

Scrapling 提供 MCP Server，可與 Claude、Cursor 等 AI 工具整合。核心思路：

- **先在本地提取目標內容**，只把精簡結果傳給 AI
- 減少 token 消耗（整頁 HTML 送給 AI 成本極高）
- AI 負責理解語意，Scrapling 負責高效提取

適合場景：AI Agent 需要從大量網頁中提取結構化資料。

---

## 安裝與部署

```bash
# 最小安裝（僅解析器，無瀏覽器依賴）
pip install scrapling

# 完整安裝（含瀏覽器 + 指紋偽裝）
pip install scrapling[fetch]
scrapling install          # 下載瀏覽器二進制

# Docker
docker pull d4vinci/scrapling
```

> Python 3.10+，BSD-3-Clause 開源協議。

---

## 優缺點評估

### ✅ 優點

- **自適應元素追蹤是真正創新**：解決爬蟲最大痛點，降低維護成本
- **一站式框架**：解析、抓取、爬蟲、反偵測、AI 整合全部整合
- **高性能**：解析速度與 Scrapy 持平，比 BS4 快 700-1600 倍
- **模組化安裝**：可按需引入，不必拉全部依賴
- **活躍開發**：10.6k stars、38 releases、持續更新

### ❌ 缺點與風險

- **依賴較重**：Full 安裝含 Playwright + 瀏覽器二進制，鏡像較大
- **Bus Factor 高**：僅 5 位貢獻者，絕大部分工作來自作者一人
- **自適應非萬能**：重大重新設計仍會匹配失敗
- **法律灰色地帶**：StealthyFetcher 的 Cloudflare 繞過功能
- **生態不如 Scrapy 成熟**：社群、外掛、教學資源較少

---

## 参考资料

- [D4Vinci/Scrapling GitHub](https://github.com/D4Vinci/Scrapling)
- [Scrapling 官方文檔](https://scrapling.readthedocs.io/en/latest/)
- [Creating Self-Healing Spiders with Scrapling (Medium)](https://medium.com/@d4vinci/creating-self-healing-spiders-with-scrapling-in-python-without-ai-web-scraping-4042a16ec4a5)
- [Scrapling 評測 - DarkWebInformer](https://darkwebinformer.com/scrapling-an-adaptive-web-scraping-framework-that-handles-everything-from-single-requests-to-full-scale-crawls/)
- [Scrapling v0.4 發佈 (Reddit)](https://www.reddit.com/r/webscraping/comments/1r5712p/scrapling_v04_is_here_effortless_web_scraping_for/)

## 相关笔记

- [[Web Scraping 工具對比]]
