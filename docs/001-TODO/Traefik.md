---
title: Traefik
tags: [2022-11, devops, docker]
aliases: [Traefik]
created_date: 2022-11-11 15:08
updated_date: 2022-11-11 15:57
---

# Traefik

- **🏷️Tags** :   #2022-11 #devops  #docker
- Link: 
- [[Go 語言 + Docker] 用 Traefik 搭配 Docker 快速架設服務 - YouTube](https://www.youtube.com/watch?v=0GkJb6-CDUU)
- [[Docker 教學] 用 Traefik 搭配 Let's Encrypt 憑證服務 - YouTube](https://www.youtube.com/watch?v=ddJxBXShkZg)
- [使用 Traefik 的 Docker 反向代理 | Accesto 博客](https://accesto.com/blog/docker-reverse-proxy-using-traefik/)
- [新一代LB - Traefik · Docker學習筆記](https://peihsinsu.gitbooks.io/docker-note-book/content/xin-yi-dai-lb-traefik.html)

### docker label

```yml
    labels:
      - "traefik.docker.network=web"
      - "traefik.enable=true"
      - "traefik.basic.frontend.rule=Host:domain2.com"
      - "traefik.basic.port=8080"
      - "traefik.basic.protocol=http"
```



## ref
- [Is this the BEST Reverse Proxy for Docker? // Traefik Tutorial - YouTube](https://www.youtube.com/watch?v=wLrmmh1eI94&t=100s)
- [Let's Encrypt Explained: Free SSL - YouTube](https://www.youtube.com/watch?v=jrR_WfgmWEw&list=RDCMUCFe9-V_rN9nLqVNiI8Yof3w&start_radio=1&rv=jrR_WfgmWEw&t=11)
- [[Docker 教學] 用 Traefik 搭配 Let's Encrypt 憑證服務 - YouTube](https://www.youtube.com/watch?v=ddJxBXShkZg)
- 


