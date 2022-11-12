---
title: git-command
author: tommy
tags:
  - git
categories:
  - tools
toc: true
date: 2019-01-19 04:08:30
---

# 簡介

git命令

## 只拉取了dev

> git clone --single-branch git@github.com:yudady/concurrency.git --branch dev develop

```shell
Cloning into 'develop'...
remote: Enumerating objects: 242, done.
remote: Counting objects: 100% (242/242), done.
remote: Compressing objects: 100% (101/101), done.
Receiving objectsremote: Total 242 (delta 124), reused 239 (delta 124), pack-reused 0
Receiving objects: 100% (242/242), 46.11 KiB | 214.00 KiB/s, done.
Resolving deltas: 100% (124/124), done.
```

> git branch --set-upstream branch-name origin/branch-name

## 移除未加入版控的檔案(-f) & 目錄(-d)
> git clean -f -d   

## 本地undo:退版(reset) 
> git reset --hard 09d528c7f608c1a5485cbde431fe5d0705bde8e7

## 以遠端為主:重拉pull　
> git reset --hard origin/master

## 以本地為主:強迫push
> git push --force　

## 合併未push的commit(壓縮commit)(衍合操作)
> git rebase -i 1c76461a21a8
- 第一條pick其他全部改为squash


<!--more-->
# 內容


## 設定 git config 
```git
git config --global user.name "yudady"
git config --global user.email "tommy@gmail.com"

git config --local user.name "tommy"
git config --local user.email "tommy@gmail.com"
```

---

## 命令

### 取消分支追蹤
> git branch --unset-upstream [分支名]
### 刪除遠程跟蹤分支：
> git branch -d -r origin/master


### 加入遠端的 repository
> git remote add upstream https://github.com/otheruser/repo.git
### 開始追蹤master
> git branch --set-upstream-to=origin/master master



### 回退到某個版本
> git reset 09d528c7f608c1a5485cbde431fe5d0705bde8e7

### 強制更新遠端分支(很可能會不小心把別人上傳的 code 整個覆蓋掉)
> git push -f

### 假設你回覆之前的版本後，又想回復到最新提交的版本，就可以用
> git reset --hard commit_id


### git push origin master:cat
> 把本地的 master 分支的內容，推一份到 origin 上，並且在 origin 上建立一個 cat 分支

### git pull origin master:cat
> 把遠程的master拉下來到本地的cat分支
```shell
$ git pull origin master:cat
From github.com:yudady/concurrency
 * [new branch]      master     -> cat
Already up to date.

```
----------------------


### 如何修改/取消上一次的 commit
1. git commit --amend 修改上一次的 commit 訊息。
2. git commit --amend 檔案1 檔案2... 將檔案1、檔案2加入上一次的 commit。
3. git reset HEAD^ --soft 取消剛剛的 commit，但保留修改過的檔案。
4. git reset HEAD^ --hard 取消剛剛的 commit，回到再上一次 commit的 乾淨狀態。


### 分支基本操作(branch)
- git branch 列出所有本地端的 branch。
- git branch -r 列出所有遠端的 branch。
- git branch -a 列出所有本地及遠端的 branch。
- git branch "branch名稱" 建立一個新的 branch。
- git checkout -b "branch名稱" 建立一個新的 branch 並切換到該 branch。
- git branch branch名稱 起始點 以起始點作為基準建立一個新的 branch，起始點可以是一個 tag，branch 或是 commit。
- git branch --track branch名稱 遠端branch 建立一個 tracking 遠端 branch 的 branch，這樣以後 push/pull都會直接對應到該遠端的branch。
- git branch --set-upstream branch 遠端branch 將一個已存在的 branch 設定成 tracking 遠端的branch。
- git branch -d "branch 名稱" 刪除 branch。
- git -r -d 遠端branch 刪除一個 tracking 的遠端 branch，例如git branch -r -d wycats/master
- git push repository名稱 :遠端branch 刪除一個 repository 的 branch，通常用在刪除遠端的 branch，例如git push origin :old_branch_to_be_deleted。
- git checkout branch名稱 切換到另一個 branch(所有修改過程會被保留)。

### 遠端操作(remote)
- git remote add remote名稱 remote網址 加入一個 remote repository，例如 git remote add github git://github.com/gogojimmy/test.git
- git push remote名稱 :branch名稱 刪除遠端 branch，例如 git push origin :somebranch。
- git pull remote名稱 branch名稱 下載一個遠端的 branch 並合併(注意是下載遠端的 branch 合併到目前本地端所在的 branch)。
- git push 類似於 pull 操作，將本地端的 branch 上傳到遠端。

```
預設應該只會有 origin 這個 remote：
origin https://github.com/user/repo.git (fetch)
origin https://github.com/user/repo.git (push)

我們用下面這個命令來加入遠端的 repository，在這邊的情境也就是比較新的、上游的 repository
upstream 是 remote name、可以自己取名，不要重複就好，但後面我都用會 upstream 做示範
而後面那串網址是 repository 位置：
$ git remote add upstream https://github.com/otheruser/repo.git

如果再看一次現有的remote端應該會發現多了兩組 upstream (fetch & push)：
$ git remote -v
origin https://github.com/user/repo.git (fetch)
origin https://github.com/user/repo.git (push)
upstream https://github.com/otheruser/repo.git (fetch)
upstream https://github.com/otheruser/repo.git (push)
```


### 暫存操作(stash)
- git stash 將目前所做的修改都暫存起來。
- git stash apply 取出最新一次的暫存。
- git stash pop 取出最新一次的暫存並將他從暫存清單中移除。
- git stash list 顯示出所有的暫存清單。
- git stash clear 清除所有暫存。

### 常見問題：
- 我的 code 改爛了我想全部重來，我要如何快速回到乾淨的目錄?
  - git reset --hard 這指令會清除所有與最近一次 commit 不同的修改。
- merge 過程中發生 confict 我想放棄 merge，要如何取消 merge？
  - 一樣使用 git reset --hard 可以取消這次的 merge。
- 如何取消這次的 merge 回到 merge 前的狀態?
  - git reset --hard ORIG_HEAD 這指令會取消最近一次成功的 merge 以及所有你在這次 merge 後所做的修改。
- 如何回復單獨檔案到原本 commit 的狀態?
  - git checkout 檔案名稱 這指令會將已經被修改過的檔案回復到最近一次 commit 的樣子。





# 參考資料

- [git中利用rebase来压缩多次提交](https://blog.csdn.net/itfootball/article/details/44154121)
