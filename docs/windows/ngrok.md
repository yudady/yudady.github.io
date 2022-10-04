---
title: ngrok
tags: []
date: 2022-10-04 14:07
modified: 2022-10-04 14:47
aliases: [ngrok]
linter-yaml-title-alias: ngrok
---

# ngrok

- 一個臨時伺服器

## 緣起

- github webhook 提及，用來測試 callback
- source-url :: [Creating webhooks - GitHub Docs](https://docs.github.com/en/developers/webhooks-and-events/webhooks/creating-webhooks)

## 是什麼

**免費** **免費** **免費** 很重要寫 3 次

公網 domain : https://1bee-210-66-180-104.jp.ngrok.io

用法很簡單 `ngrok http 8080`

### 開發機器人

- line boot
- telegram bot 
- github webhook
- slack bot

## 去哪下載

- source-url :: [ngrok - Online in One Line](https://ngrok.com/)
- source-url :: [ngrok - download](https://ngrok.com/download)

```powershell
Windows PowerShell
Copyright (C) Microsoft Corporation. 著作權所有，並保留一切權利。

請嘗試新的跨平台 PowerShell https://aka.ms/pscore6

PS C:\WINDOWS\system32> choco install ngrok
Chocolatey v1.1.0
Installing the following packages:
ngrok
.....
PS C:\WINDOWS\system32>
```

## 怎麼玩

- 1. 直接點 google 賬號註冊
- 2. Connect your account 可以拿到密碼
- 3. Fire it up

Read [the documentation](https://ngrok.com/docs) on how to use ngrok. Try it out by running it from the command line:

```
ngrok help
```

To start a HTTP tunnel forwarding to your local port 80, run this next:

```
$ ngrok http 8080




ngrok                                                                                                   (Ctrl+C to quit)                                                                                                                        Visit http://localhost:4040/ to inspect, replay, and modify your requests                                                                                                                                                                       Session Status                online                                                                                    Account                       lin tommy (Plan: Free)                                                                    Version                       3.1.0                                                                                     Region                        Japan (jp)                                                                                Latency                       42ms                                                                                      Web Interface                 http://127.0.0.1:4040                                                                     Forwarding                    https://1bee-210-66-180-104.jp.ngrok.io -> http://localhost:8080                                                                                                                                                  Connections                   ttl     opn     rt1     rt5     p50     p90                                                                             0       2       0.00    0.00    0.00    0.00                                                                                                                                                                      HTTP Requests                                                                                                           -------------                                                                                                                                                                                                                                   GET /                          200                                                                                      GET /                          200                                                                                      GET /favicon.ico               404                                                                                                                                   
```
