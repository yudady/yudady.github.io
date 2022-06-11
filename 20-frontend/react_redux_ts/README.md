## 目标

React + Redux + TS

```bash
npx create-react-app news --template typescript
```

<img src="TS 在 React 中的应用.assets/image-20220502174109205.png" alt="image-20220502174109205"  />

## 接口

1\. 获取频道列表，`http://geek.itheima.net/v1_0/channels`

2\. 获取频道新闻，`http://geek.itheima.net/v1_0/articles?channel_id=频道id&timestamp=时间戳`

## 界面

`styles/index.css`

```css
body {
    margin: 0;
    padding: 0;
}
*,
*:before,
*:after {
    box-sizing: inherit;
}

li {
    list-style: none;
}
dl,
dd,
dt,
ul,
li {
    margin: 0;
    padding: 0;
}

.no-padding {
    padding: 0px !important;
}

.padding-content {
    padding: 4px 0;
}

a:focus,
a:active {
    outline: none;
}

a,
a:focus,
a:hover {
    cursor: pointer;
    color: inherit;
    text-decoration: none;
}

b {
    font-weight: normal;
}

div:focus {
    outline: none;
}

.fr {
    float: right;
}

.fl {
    float: left;
}

.pr-5 {
    padding-right: 5px;
}

.pl-5 {
    padding-left: 5px;
}

.block {
    display: block;
}

.pointer {
    cursor: pointer;
}

.inlineBlock {
    display: block;
}
.catagtory {
    display: flex;
    overflow: hidden;
    overflow-x: scroll;
    background-color: #f4f5f6;
    width: 100%;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 999;
}
.catagtory li {
    padding: 0 15px;
    text-align: center;
    line-height: 40px;
    color: #505050;
    cursor: pointer;
    z-index: 99;
    white-space: nowrap;
}
.catagtory li.select {
    color: #f85959;
}
.list {
    margin-top: 60px;
}
.article_item {
    padding: 0 10px;
}
.article_item .img_box {
    display: flex;
    justify-content: space-between;
}
.article_item .img_box .w33 {
    width: 33%;
    height: 90px;
    display: inline-block;
}
.article_item .img_box .w100 {
    width: 100%;
    height: 180px;
    display: inline-block;
}
.article_item h3 {
    font-weight: normal;
    line-height: 2;
}
.article_item .info_box {
    color: #999;
    line-height: 2;
    position: relative;
    font-size: 12px;
}
.article_item .info_box span {
    padding-right: 10px;
}
.article_item .info_box span.close {
    border: 1px solid #ddd;
    border-radius: 2px;
    line-height: 15px;
    height: 12px;
    width: 16px;
    text-align: center;
    padding-right: 0;
    font-size: 8px;
    position: absolute;
    right: 0;
    top: 7px;
}
```

`Channel.tsx`

```ts
import React from 'react'

export default function Channel() {
    return (
        <ul className='catagtory'>
            <li className='select'>开发者资讯</li>
            <li>ios</li>
            <li>c++</li>
            <li>android</li>
            <li>css</li>
            <li>数据库</li>
            <li>区块链</li>
            <li>go</li>
            <li>产品</li>
            <li>后端</li>
            <li>linux</li>
            <li>人工智能</li>
            <li>php</li>
            <li>javascript</li>
            <li>架构</li>
            <li>前端</li>
            <li>python</li>
            <li>java</li>
            <li>算法</li>
            <li>面试</li>
            <li>科技动态</li>
            <li>js</li>
            <li>设计</li>
            <li>数码产品</li>
            <li>html</li>
            <li>软件测试</li>
            <li>测试开发</li>
        </ul>
    )
}
```

`Article.tsx`

```ts
import React from 'react'
import avatar from '../assets/back.jpg'
export default function Article() {
    return (
        <div className='list'>
            <div className='article_item'>
                <h3 className='van-ellipsis'>python数据预处理 ：数据标准化</h3>
                <div className='img_box'>
                    <img src={avatar} className='w100' alt='' />
                </div>
                <div className='info_box'>
                    <span>13552285417</span>
                    <span>0评论</span>
                    <span>2018-11-29T17:02:09</span>
                </div>
            </div>
        </div>
    )
}
```

## 配置

配置下路径别名和提示

```bash
yarn add -D @craco/craco
```

`craco.config.js`

```js
const path = require('path')

module.exports = {
    // webpack 配置
    webpack: {
        // 配置别名
        alias: {
            // 约定：使用 @ 表示 src 文件所在路径
            '@': path.resolve(__dirname, 'src')
        },
    },
}
```

`package.json`

```json
// 将 start/build/test 三个命令修改为 craco 方式
{
    "scripts": {
        "start": "craco start",
        "build": "craco build",
        "test": "craco test",
        "eject": "react-scripts eject"
    }
}
```

`path.tsconfig.json`

```json
{
    "compilerOptions": {
        "baseUrl": "./",
        "paths": {
            "@/*": ["src/*"]
        }
    }
}
```

`tsconfig.json` 中导入上面配置

```json
{
    // 导入配置文件
    "extends": "./path.tsconfig.json"
}
```

## 步骤

1\. 配置 Redux；

2\. 请求频道数据并渲染；

3\. 处理频道高亮；

4\. 请求文章数据并渲染；