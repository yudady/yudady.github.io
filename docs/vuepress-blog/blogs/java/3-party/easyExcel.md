---
title: easyExcel阿里
author: tommy
tags:
  - java
categories:
  - java
toc: true
date: 2018-10-08 13:55:56
---

# 簡介

> 簡單的生成excel讀寫，使用annotion
> github，alibaba/easyexcel

<!--more-->
# 內容

[EasyExcel](https://github.com/alibaba/easyexcel)
```
<!-- https://mvnrepository.com/artifact/com.alibaba/easyexcel -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>1.0.4</version>
</dependency>

```
## 讀Excel

```java
public void noModelMultipleSheet() {
    InputStream inputStream = getInputStream("2007NoModelMultipleSheet.xlsx");
    try {
        ExcelReader reader = new ExcelReader(inputStream, ExcelTypeEnum.XLSX, null,
            new AnalysisEventListener<List<String>>() {
                @Override
                public void invoke(List<String> object, AnalysisContext context) {
                    System.out.println(
                        "当前sheet:" + context.getCurrentSheet().getSheetNo() + " 当前行：" + context.getCurrentRowNum()
                            + " data:" + object);
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {

                }
            });

        reader.read();
    } catch (Exception e) {
        e.printStackTrace();

    } finally {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

## 寫Excel
```java
@Test
public void test1() throws FileNotFoundException {
    OutputStream out = new FileOutputStream("/Users/jipengfei/78.xlsx");
    try {
        ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
        //写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
        Sheet sheet1 = new Sheet(1, 0,ExcelPropertyIndexModel.class);
        writer.write(getData(), sheet1);
        writer.finish();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

# 參考資料
[google](http://www.google.com)

