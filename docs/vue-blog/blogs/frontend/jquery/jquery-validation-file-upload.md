---
title: jquery驗證與檔案上傳
author: tommy
tags:
  - jquery
categories:
  - frontend
toc: true
date: 2018-10-02 14:51:09
---

# 簡介
使用jquery Validation and Form 实现档案上传
- [jQuery Validation Plugin](http://jqueryvalidation.org/)
- [jQuery Form Plugin](http://malsup.com/jquery/form/)
- apache ServletFileUpload


<!--more-->
# 內容

## html
```
<form id="formData" method="post" enctype="multipart/form-data">
    <input type="text" name="first" value="Bob" />
    <input type="file" name="file01"  />
    <input type="file" name="file02"  />
    <button>Submit</button>
</form>
```

## js 

- `validate` 
- `fieldSerialize` ， `ajaxSubmit`


```
$("#formData").validate({   // 
    rules: {
        first: {
            required: true
        }
    },
    submitHandler: function () {
        let url = "";
        let data = $("#formData").fieldSerialize();
        $('#formData').ajaxSubmit({  // 
            url: url,
            data: data,
            success: function (resultObj) {
                // TODO
            }
        });


        return false;
    }
});
```

## java controller

- apache fileupload
  
```
DiskFileItemFactory factory = new DiskFileItemFactory();
factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
ServletFileUpload sfu = new ServletFileUpload(factory);
sfu.setHeaderEncoding("UTF-8");
List<FileItem> fis = sfu.parseRequest(request);

Iterator<FileItem> it = fis.iterator();
while (it.hasNext()) {
  FileItem fi = (FileItem) it.next();
  if (!fi.isFormField()) { // 档案上传

    String uploadFileName = fi.getName();
    String fieldName = fi.getFieldName();
    String extName = FilenameUtils.getExtension(uploadFileName);
    log.debug("fieldName=" + fieldName + " ，uploadFileName = " + uploadFileName
      + " ，extName = " + extName);
    byte[] bytes = IOUtils.toByteArray(fi.getInputStream());



  } else {  // input资料

    String fieldName = fi.getFieldName();
    String fieldValue = fi.getString("UTF-8");
    log.debug("fieldName = " + fieldName + " ，fieldValue = " + fieldValue);

  }
}

```

## 图片base64

```
private String getMimeType(byte data[]) throws Exception {
  InputStream is = new BufferedInputStream(new ByteArrayInputStream(data));
  String mimeType = URLConnection.guessContentTypeFromStream(is);
  return mimeType;
}

private String getPicString(byte[] bytes) throws Exception {
  String mimeType = getMimeType(bytes);
  boolean mimeTypeHasError = true;

  if (StringUtils.containsIgnoreCase(mimeType, "png")) {
    mimeTypeHasError = false;
  }
  if (StringUtils.containsIgnoreCase(mimeType, "jpg")) {
    mimeTypeHasError = false;
  }
  if (StringUtils.containsIgnoreCase(mimeType, "jpeg")) {
    mimeTypeHasError = false;
  }

  if (mimeTypeHasError) {
    throw new RuntimeException(PNG_OR_JPG);
  }

  return String.format(format, mimeType,
    new String(Base64.encodeBase64(bytes), StandardCharsets.UTF_8));
}

```

## 检查图片宽高

```
private void checkImageSize(byte[] bytes, Integer w, Integer h, String name) throws IOException {
  BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
  int width = bufferedImage.getWidth();
  int height = bufferedImage.getHeight();

  boolean hasError = false;
  if (Objects.nonNull(w)) {
    if (width != w.intValue()) {
      hasError = true;
    }
  }
  if (Objects.nonNull(h)) {
    if (height != h.intValue()) {
      hasError = true;
    }
  }

  if (hasError) {
    throw new RuntimeException(name + "，图片格式宽度必须为：" + w + "px" + "，图片格式高度必须为：" + h + "px");
  }

}
```


## image base64格式

```

data:[<mime type>][;base64],<data>

data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADo

// html
<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADo" >

```


# 參考資料


