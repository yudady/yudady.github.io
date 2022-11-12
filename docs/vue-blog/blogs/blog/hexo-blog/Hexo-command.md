---
title: Hexo command
author: tommy
categories:
  - blog
tags: 
  - hexo
toc: true
date: 2018-09-28 12:32:13
---

# 命令

## 建立一篇新文章
```
hexo new post "文章名称"

相同于 hexo new "文章名称"

ex :  hexo new post "Hexo command"
```
> 使用scaffolds里面的模板来建立页面

<!--more-->

## drafts 命令
```

hexo new drafts "文章名称"   // 还未完成的工作可以用drafts

hexo publish "文章名称" //从drafts转移到post里面

// 要这样启动才看得到
hexo server --drafts

```


## new page 命令

```
hexo new page "文章名称" // 会创建一个新的folder

url  => http://localhost:4000/"文章名称"/index.html

```


## 布局
```
themes/landscape/
  _config.yml 
  languages
  layout/
    layout.ejs => 布局文件
    index.ejs  => layout的    <%- body %>
  scripts
  source
```
# 参考资料

[youtube 教学 Hexo](https://www.youtube.com/watch?v=Kt7u5kr_P5o&list=PLLAZ4kZ9dFpOMJR6D25ishrSedvsguVSm)



```
{% youtube UNTk5XkXKdQ %}
```

{% youtube UNTk5XkXKdQ %}


