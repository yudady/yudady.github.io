# Modules: ECMAScript modules
[Node.js 如何处理 ES6 模块](https://www.ruanyifeng.com/blog/2020/08/how-nodejs-use-es6-module.html)

ES6 模块和 CommonJS 模块有很大的差异。

- CommonJS 模块使用require()加载和module. exports输出，
- ES6 模块使用import和export。

默認情況下，Node.js將JavaScript代碼視為CommonJS模塊。 
作者可以通過`.mjs`文件擴展名，package.json“類型”字段或--input-type標誌，
告訴Node.js將JavaScript代碼視為ECMAScript模塊。 
有關更多詳細信息，請參見模塊：軟件包。

## package.json use ES6
>   "type": "module"

## nvm
nvm list available
nvm install 15.10.0