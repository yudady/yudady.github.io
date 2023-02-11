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
[voodoodyne/subethasmtp: SubEtha SMTP is a Java library for receiving SMTP mail](https://github.com/voodoodyne/subethasmtp)

---
[HTTP](https://zh.m.wikipedia.org/wiki/%E8%B6%85%E6%96%87%E6%9C%AC%E4%BC%A0%E8%BE%93%E5%8D%8F%E8%AE%AE "超文本傳輸協議")、[SMTP](https://zh.m.wikipedia.org/wiki/%E7%AE%80%E5%8D%95%E9%82%AE%E4%BB%B6%E4%BC%A0%E8%BE%93%E5%8D%8F%E8%AE%AE "簡單郵件傳輸協議")、[SNMP](https://zh.m.wikipedia.org/wiki/%E7%AE%80%E5%8D%95%E7%BD%91%E7%BB%9C%E7%AE%A1%E7%90%86%E5%8D%8F%E8%AE%AE "簡單網絡管理協議")、[FTP](https://zh.m.wikipedia.org/wiki/%E6%96%87%E4%BB%B6%E4%BC%A0%E8%BE%93%E5%8D%8F%E8%AE%AE "文件傳輸協議")、[Telnet](https://zh.m.wikipedia.org/wiki/Telnet "Telnet")、[SIP](https://zh.m.wikipedia.org/wiki/%E4%BC%9A%E8%AF%9D%E5%8F%91%E8%B5%B7%E5%8D%8F%E8%AE%AE "會話發起協議")、[SSH](https://zh.m.wikipedia.org/wiki/Secure_Shell "Secure Shell")、[NFS](https://zh.m.wikipedia.org/wiki/%E7%BD%91%E7%BB%9C%E6%96%87%E4%BB%B6%E7%B3%BB%E7%BB%9F "網絡文件系統")、[RTSP](https://zh.m.wikipedia.org/wiki/RTSP "RTSP")、[XMPP](https://zh.m.wikipedia.org/wiki/XMPP "XMPP")、[Whois](https://zh.m.wikipedia.org/wiki/WHOIS "WHOIS")、[ENRP](https://zh.m.wikipedia.org/w/index.php?title=ENRP&action=edit&redlink=1 "ENRP（頁面不存在）")（英語：[Endpoint_Handlespace_Redundancy_Protocol](https://en.wikipedia.org/wiki/Endpoint_Handlespace_Redundancy_Protocol "en:Endpoint Handlespace Redundancy Protocol")）、[TLS](https://zh.m.wikipedia.org/wiki/%E5%82%B3%E8%BC%B8%E5%B1%A4%E5%AE%89%E5%85%A8%E6%80%A7%E5%8D%94%E5%AE%9A "傳輸層安全性協定")


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
