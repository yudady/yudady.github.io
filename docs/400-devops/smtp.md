---
title: smtp
tags: [2022-11, devops, go]
aliases: [smtp]
created_date: 2022-11-17 17:56
updated_date: 2022-11-17 17:58
---

# smtp

- **🏷️Tags** :   #2022-11 #devops #go
- Link: [smtpmock package - github.com/mocktools/go-smtp-mock/v2 - Go Packages](https://pkg.go.dev/github.com/mocktools/go-smtp-mock/v2@v2.0.0#section-readme)
[使用 telnet 檢查 SMTP 是否正常提供服務 - Yowko's Notes](https://blog.yowko.com/telnet-check-smtp/)
[SMTP server/command/port](https://www.evo-mailserver.com.tw/smtp.php)

## 緣起

- 

## 是什麼

- 

## 去哪下載

- 

## 📝 怎麼玩

### SMTP 指令

**SMTP** 常見的指令 (SMTP command) 包括下面幾種：

**HELO**：開始與 SMTP Server 送出的第一個指令，通常為 MUA 或 MTA 與伺服器打招呼用，通常使用 HELO 表示客戶端使用 SMTP 協議，而指令後跟隨的網域名稱，常被利用在反垃圾信的用途之上，所以該網域名稱必須能解析為 IP 位址並且與目前連線的客戶端 IP 位址相同．

**EHLO**：開始與 eSMTP Server 送出的第一個指令，通常為 MUA 或 MTA 與伺服器打招呼用，通常使用 EHLO 表示客戶端使用 eSMTP 協議，而指令後跟隨的網域名稱，常被利用在反垃圾信的用途之上，所以該網域名稱必須能解析為 IP 位址並且與目前連線的客戶端 IP 位址相同，除此之外，SMTP 伺服器端還會送出目前伺服器支援的 eSMTP 功能列表，例如 STARTTLS

**STARTTLS**：MUA 或 MTA 透過 EHLO 指令確認伺服器支援 TLS 後，可以送出 STARTTLS 指令要求進行 TLS 握手交涉．

**MAIL FROM**：MUA 或 MTA 告知 SMTP 伺服器 ENVELOPE 的寄件人，可以與 Message 寄件人不同．

**RCPT TO**：MUA 或 MTA 告知 SMTP 伺服器 ENVELOPE 的收件人，可以與 Message 收件人不同，可執行多次以指定多個收件人．

**DATA** 告知 SMTP 伺服器即將開始傳輸郵件內容．

**QUIT** 告知 SMTP 伺服器即將說再見，許多 SMTP 甚至將此指令作為是否為垃圾信的參考．
