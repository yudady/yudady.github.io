---
title: restful-rule
author: tommy
tags:
  - principle
categories:
  - principle
toc: true
date: 2019-09-15 22:22:59
---

# 簡介

> 在RESTful架构中，每个网址代表一种资源（resource），所以网址中不能有动词，只能有名词，而且所用的名词往往与数据库的表格名对应。一般来说，数据库中的表都是同种记录的"集合"（collection），所以API中的名词也应该使用复数。

- 每一个URI代表一种资源；
- 客户端和服务器之间，传递这种资源的某种表现层；
- 客户端通过四个HTTP动词，对服务器端资源进行操作，实现"表现层状态转化"。

```
GET /zoos：列出所有动物园
POST /zoos：新建一个动物园
GET /zoos/ID：获取某个指定动物园的信息
PUT /zoos/ID：更新某个指定动物园的信息（提供该动物园的全部信息）
PATCH /zoos/ID：更新某个指定动物园的信息（提供该动物园的部分信息）
DELETE /zoos/ID：删除某个动物园
GET /zoos/ID/animals：列出某个指定动物园的所有动物
DELETE /zoos/ID/animals/ID：删除某个指定动物园的指定动物
```

<!--more-->
# 內容

> 在一个RESTful系统里，客户端向服务端发起索取资源的操作只能通过HTTP协议语义来进行交互。最常用的HTTP协议语义有以下5个：

## 常用的HTTP动词有下面五个（括号里是对应的SQL命令）
- GET（SELECT）：从服务器取出资源（一项或多项）。
- POST（CREATE）：在服务器新建一个资源。
- PUT（UPDATE）：在服务器更新资源（客户端提供改变后的完整资源）。
- PATCH（UPDATE）：在服务器更新资源（客户端提供改变的属性）。
- DELETE（DELETE）：从服务器删除资源。
  
## 返回结果
- GET /collection：返回资源对象的列表（数组）
- GET /collection/resource：返回单个资源对象
- POST /collection：返回新生成的资源对象
- PUT /collection/resource：返回完整的资源对象
- PATCH /collection/resource：返回完整的资源对象
- DELETE /collection/resource：返回一个空文档


## GET
GET:发送一条或者多条GET请求都不会改变服务器里面的资源，同一个request发一遍和发两遍都将获得相同结果。这个交互行为是幂等的。
GET:从服务器取出资源（一项或多项）

```java
@ApiOperation(value="获取用户列表", notes="")
@RequestMapping(value={""}, method=RequestMethod.GET)
public List<User> getUserList() {
    List<User> r = new ArrayList<User>(users.values());
    return r;
}
 
@ApiOperation(value="获取用户详细信息", notes="根据url的id来获取用户详细信息")
@ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
@RequestMapping(value="/{id}", method=RequestMethod.GET)
public User getUser(@PathVariable Long id) {
    return users.get(id);
}
```

## POST
POST:发送的POST请求中会包含希望创建的新资源。收到POST请求后，服务器会新增资源。这个交互行为是非幂等的。
POST：在服务器新建一个资源

```java
@ApiOperation(value="创建用户", notes="根据User对象创建用户")
@ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
@RequestMapping(value="", method=RequestMethod.POST)
public String postUser(@RequestBody User user) {
    users.put(user.getId(), user);
    return "success";
}
```


## PUT
PUT:客户端发送PUT请求对资源进行修改 (更新），此时服务端的资源发生变化，这种请求是幂等的。
PUT:在服务器更新资源（客户端提供完整资源数据）。

```java
@ApiOperation(value="更新用户详细信息", notes="根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
@ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
})
 
@RequestMapping(value="/{id}", method=RequestMethod.PUT)
public String putUser(@PathVariable Long id, @RequestBody User user) {
    User u = users.get(id);
    u.setName(user.getName());
    u.setAge(user.getAge());
    users.put(id, u);
    return "success";
}
```


## DELETE
DELETE:发送Delete请求之后，服务端资源将消失。当再次发送一条Delete请求，客户端获得的资源状态和第一次发送Delete请求后的状态是一致的。这个请求是幂等的。
DELETE:从服务器删除资源

```java
@ApiOperation(value="删除用户", notes="根据url的id来指定删除对象")
@ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
public String deleteUser(@PathVariable Long id) {
    users.remove(id);
    return "success";
}
```

## HEAD
HEAD:当不知道使用何种请求方式获取客户 端资源时，可以使用HEAD方式，此时客户端返回的不是具体的资源信息，只是HTTP状态码和报头。此方式是幂等的.
HEAD:从服务器获取报头信息（不是资源）。


#　最常见的一种设计错误，就是URI包含`动词`
> POST /accounts/1/transfer/500/to/2    ❌
> POST /accounts/1/transaction/500/to/2 ⭕


# 状态码（Status Codes）
- 200 OK - [GET]：服务器成功返回用户请求的数据，该操作是幂等的（Idempotent）。
- 201 CREATED - [POST/PUT/PATCH]：用户新建或修改数据成功。
- 202 Accepted - [*]：表示一个请求已经进入后台排队（异步任务）
- 204 NO CONTENT - [DELETE]：用户删除数据成功。
- 400 INVALID REQUEST - [POST/PUT/PATCH]：用户发出的请求有错误，服务器没有进行新建或修改数据的操作，该操作是幂等的。
- 401 Unauthorized - [*]：表示用户没有权限（令牌、用户名、密码错误）。
- 403 Forbidden - [*] 表示用户得到授权（与401错误相对），但是访问是被禁止的。
- 404 NOT FOUND - [*]：用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的。
- 406 Not Acceptable - [GET]：用户请求的格式不可得（比如用户请求JSON格式，但是只有XML格式）。
- 410 Gone -[GET]：用户请求的资源被永久删除，且不会再得到的。
- 422 Unprocesable entity - [POST/PUT/PATCH] 当创建一个对象时，发生一个验证错误。
- 429 Too Many Requests – 因為負載限制，拒絕request時。
- 500 INTERNAL SERVER ERROR - [*]：服务器发生错误，用户将无法判断发出的请求是否成功。


# 參考資料
- [RESTful API 编写规范](https://www.jqhtml.com/down/7604.html)

