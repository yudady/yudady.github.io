---
title: Spring Boot Reference Documentation
tags: []
aliases: [Spring Boot Reference Documentation, springboot]
created_date: 2022-10-05 11:25
updated_date: 2022-10-06 17:47
---

# Spring Boot Reference Documentation

- Link: [Spring REST Docs](https://spring.io/projects/spring-restdocs#samples)

## reading list

- [x] [Documentation Overview](https://docs.spring.io/spring-boot/docs/current/reference/html/documentation.html#documentation) : 總覽
	- spring-cli 可以直接 run groovy
- [x] [Getting Started](https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started) : Introducing Spring Boot, System Requirements, Servlet Containers, Installing Spring Boot, and Developing Your First Spring Boot Application
- [x] [Using Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using) : Build Systems, Structuring Your Code, Configuration, Spring Beans and Dependency Injection, DevTools, and more. : [開發一個springboot流程](開發一個springboot流程.md)
- [x] [Core Features](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features) :Profiles, Logging, Security, Caching, Spring Integration, Testing, and more.
	- ApplicationContextEvent 不能用 `@Bean` 創建
	- META-INF/spring.factories : **org.springframework.context.ApplicationListener**=com.example.project.MyListener
	- ApplicationRunner or CommandLineRunner
	- `@DataElasticsearchTest` , `@DataJpaTest` , `@DataJdbcTest`
- [ ] [Web](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web) :Servlet Web, Reactive Web, GraphQL, Embedded Container Support, Graceful Shutdown, and more.
	- mvc
		- Spring MVC 自動配置
			- `ContentNegotiatingViewResolver` and `BeanNameViewResolver`
			- static resources, including support for WebJars
			- `Converter`, `GenericConverter`, and `Formatter` beans
			- `HttpMessageConverters`
			- `MessageCodesResolver`
			- `ConfigurableWebBindingInitializer`
		- ErrorViewResolver :  @ExceptionHandler , @ControllerAdvice
	- shutdown
	- web security
	- web sesion
- [ ] [Data](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data) :SQL and NOSQL data access.
	- jpa
	- jdbc
	- jooq
	- r2dbc
	- redis
	- mongodb
	- neo4j
	- elasicsearch
	- couchbase
	- ldap
	- infucdb
- [ ] [IO](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io) Caching, Quartz Scheduler, REST clients, Sending email, Spring Web Services, and more.
	- cache provider
		- generic : `CacheManager` : org.springframework.cache.Cache
		- jcache : `JCacheCacheManager` : spring.cache.jcache.provider
		- quartz
		- email
		- restTemplate
		- webClient
		- JTA
- [ ] [Messaging](https://docs.spring.io/spring-boot/docs/current/reference/html/messaging.html#messaging) :JMS, AMQP, Apache Kafka, RSocket, WebSocket, and Spring Integration.
- [x] [Container Images](https://docs.spring.io/spring-boot/docs/current/reference/html/container-images.html#container-images) : build docker image , fat jar
- [ ] [Production-ready Features](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator) : 
	- 監控 Monitoring, 
	- Metrics, 
	- Auditing, and more.
- [ ] [Deploying Spring Boot Applications](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment) :Deploying to the Cloud, and Installing as a Unix application.
	- k8s : Kubernetes Container Lifecycle
	- Heroku

## [“How-to” Guides](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto)

- [ ] [1. Spring Boot Application](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.application)
	- FailureAnalyzer 用來分析啟動錯誤
		- **org.springframework.boot.diagnostics.FailureAnalyzer**=com.example.ProjectConstraintViolationFailureAnalyzer
		- If you need access to the `BeanFactory` or the `Environment`, your `FailureAnalyzer` can implement `BeanFactoryAware` or `EnvironmentAware` respectively.
	- 經驗法則
		- 查找調用的類 `*AutoConfiguration` 並閱讀它們的來源。請特別注意 `@Conditional*` 註釋以了解它們啟用了哪些功能以及何時啟用。
		- 添加 --debug 到命令行或系統屬性 -Ddebug 以在控制台上獲取在您的應用程序中做出的所有自動配置決策的日誌。
		- 在啟用了執行器的正在運行的應用程序中，查看 conditions 端點（/actuator/conditions 或 JMX 等效項）以獲取相同的信息。
		- 查找類@ConfigurationProperties（例如 ServerProperties）並從那裡讀取可用的外部配置選項。
		- @ConfigurationProperties 註釋有一個 name 作為外部屬性前綴的屬性。因此，ServerPropertieshasprefix="server" 及其配置屬性是 server.port、server.address 和其他。在啟用執行器的運行應用程序中，查看 configprops 端點。
		- 尋找該 bind 方法的用途，以輕鬆的方式 Binder 顯式地將配置值拉出。Environment 它通常與前綴一起使用。
		- 查找@Value 直接綁定到 Environment.
		- 查找@ConditionalOnExpression 響應 SpEL 表達式打開和關閉功能的註釋，通常使用從 Environment.
- [ ] [2. Properties and Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.properties-and-configuration)
	- maven gradle 設定的屬性可以傳入 application.yaml 中
	- 設定檔會全部合併
- [ ] [3. Embedded Web Servers](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.webserver)
	- Undertow
	- response 壓縮設定
- [ ] [4. Spring MVC](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.spring-mvc)
- [ ] [5. Jersey](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.jersey)
- [ ] [6. HTTP Clients](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.http-clients)
- [ ] [7. Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.logging)
- [ ] [8. Data Access](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-access)
- [ ] [9. Database Initialization](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization)
- [ ] [10. Messaging](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.messaging)
- [ ] [11. Batch Applications](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.batch)
- [ ] [12. Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.actuator)
- [ ] [13. Security](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.security)
- [ ] [14. Hot Swapping](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.hotswapping)
- [ ] [15. Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.testing)
- [ ] [16. Build](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.build)
- [ ] [17. Traditional Deployment](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.traditional-deployment)
