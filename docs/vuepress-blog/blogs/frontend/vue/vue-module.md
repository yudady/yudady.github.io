---
title: 開發Vue使用brower的module
author: tommy
tags:
  - vue
categories:
  - frontend
toc: true
date: 2018-09-30 09:36:38
---

# 在瀏覽器中，直接使用ES6的Module開發Vue

- 注意index.html `script` 使用 type="`module`"
- 瀏覽器直接編譯es6，沒有用到bebel

<!--more-->

## 代碼

### index.html
```
<!DOCTYPE html>
<html>
<head>
  <title>Vue.js Single-File JavaScript Component Demo</title>
  <script src="https://unpkg.com/vue"></script>
  <script type="module" src="SingleFileComponent.js"></script>
  <script type="module" src="app.js"></script>
  <script>
    window.onload = function () {
      let h1Msg = document.getElementById("h1Msg");
      console.log(h1Msg);
    }
  </script>
</head>
<body>
  <div id="app">
    <single-file-component></single-file-component>
  </div>
</body>
</html>
```

### app.js

```
import SingleFileComponent from './SingleFileComponent.js';

new Vue({
  el: '#app',
  components: {
    SingleFileComponent
  }
});
```
### SingleFileComponent.js

```
export default {
  template: `
    <div>
     <h1 id="h1Msg">這個是用 script type=module 產生的 message </h1>
     <p v-text="message" ></p>
    </div>
  `,
  data() {
    return {
      message: 'brower module'
    }
  }
}
```

## 參考資料
[Vue.js Single File JavaScript Components In The Browser](https://medium.com/js-dojo/vue-js-single-file-javascript-components-in-the-browser-c03a0a1f13b8)



