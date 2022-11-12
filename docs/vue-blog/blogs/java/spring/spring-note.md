---
title: spring-note
author: tommy
tags:
  - spring
categories:
  - java
toc: true
date: 2018-10-03 22:26:05
---

# 簡介

> 記錄下spring系列的注意和擴展點

<!--more-->
# 內容

# Spring



## 動態注入一個bean到spring容器
```java
@Component
public class DynamicRegisterBean2SpringContainer {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ApplicationContext applicationContext;

	public static String getCamelCase(Class clazz, String refer) {
		String re = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1)
			+ refer;

		// System.out.println(re);
		return re;
	}

	public void dynamicCreateBean(Class clazz, String refer) {
		dynamicCreateBean(clazz, null, null, refer);
	}

	public void dynamicCreateBeanByValue(Class clazz, Map<String, Object> propertyValue, String refer) {
		dynamicCreateBean(clazz, propertyValue, null, refer);
	}

	public void dynamicCreateBeanByReference(Class clazz, Map<String, String> propertyReference,
		String refer) {
		dynamicCreateBean(clazz, null, propertyReference, refer);
	}

	public void dynamicCreateBean(Class clazz, Map<String, Object> propertyValue,
		Map<String, String> propertyReference, String refer) {

		// 获取BeanFactory
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext
			.getAutowireCapableBeanFactory();

		// 创建bean信息.
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);

		if (Objects.nonNull(propertyValue)) {
			Iterator<Map.Entry<String, Object>> iterator = propertyValue.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> pair = iterator.next();
				String key = pair.getKey();
				Object value = pair.getValue();

				// addPropertyValue
				beanDefinitionBuilder.addPropertyValue(key, value);
			}

		}

		if (Objects.nonNull(propertyReference)) {
			Iterator<Map.Entry<String, String>> iterator = propertyReference.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> pair = iterator.next();
				String key = pair.getKey();
				String value = pair.getValue();

				// addPropertyReference
				beanDefinitionBuilder.addPropertyReference(key, value);
			}

		}

		// 动态注册bean.
		defaultListableBeanFactory.registerBeanDefinition(getCamelCase(clazz, refer),
			beanDefinitionBuilder.getBeanDefinition());

	}

	public void dynamicDeleteBean(Class clazz, String refer) {

		// 获取BeanFactory
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext
			.getAutowireCapableBeanFactory();

		// 删除bean.
		defaultListableBeanFactory.removeBeanDefinition(getCamelCase(clazz, refer));
	}

}

```




## BeanDefinitionRegistryPostProcessor
```java
@Component
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	/**
	 * 功能：注册bean到spring容器
	 * 
	 * @param registry
	 * @throws BeansException
	 */
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println(
			"动态注册 listener ， spring 容器初始化完后 postProcessBeanDefinitionRegistry.postProcessBeanDefinitionRegistry : "
				+ registry);

		for (int i = 0; i < 10; i++) {

			BeanDefinitionBuilder bdf = BeanDefinitionBuilder.rootBeanDefinition(RegistryBean.class);
			bdf.addPropertyValue("name", "name-tommy" + i);

			registry.registerBeanDefinition("registryBean" + i, bdf.getBeanDefinition());
		}

	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println(
			"spring 容器初始化完后 postProcessBeanDefinitionRegistry.postProcessBeanFactory : " + beanFactory);
	}
}


```



## ApplicationEventPublisher ApplicationListener 
- 事件發佈，事件監聽
```java
@FunctionalInterface
public interface ApplicationEventPublisher {
	/**
	 * Notify all <strong>matching</strong> listeners registered with this
	 * application of an event.
	 * <p>If the specified {@code event} is not an {@link ApplicationEvent},
	 * it is wrapped in a {@link PayloadApplicationEvent}.
	 * @param event the event to publish
	 * @since 4.2
	 * @see PayloadApplicationEvent
	 */
	void publishEvent(Object event);

}

@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

	/**
	 * Handle an application event.
	 * @param event the event to respond to
	 */
	void onApplicationEvent(E event);

}
```
# 组件注册
- packagescan
- @Bean
- @Import
  - ImportSelector
  - ImportBeanDefinitionRegistrar
- public interface FactoryBean
  - id前面加上&，可以拿到工廠bean


## annotion
- @Import
- @Conditional
- @Primary
- @Profile
- @TransactionalEventListener
- @EventListener
- @Async
- @Order(42)




# XXXAware vs XXXProcessor
- ApplicationContextAware
- ApplicationContextAwareProcessor 
- ServletContextAware 
- ServletContextAwareProcessor
- WebApplicationContextServletContextAware
- WebApplicationContextServletContextAwareProcessor
```
postProcessBeforeInitialization
invokeAwareInterfaces
postProcessAfterInitialization
```
# Aware
- ApplicationEventPublisherAware 
- MessageSourceAware 
- ResourceLoaderAware 
- NotificationPublisherAware
- EnvironmentAware 
- BeanFactoryAware 
- EmbeddedValueResolverAware 
- ServletConfigAware 
- BootstrapContextAware
- LoadTimeWeaverAware
- BeanNameAware 
- BeanClassLoaderAware 




# spring mvc 

- org.springframework.web.servlet.FlashMapManager
- org.springframework.data.domain.Pageable
- org.springframework.web.context.support.WebApplicationContextUtils
- @GetMapping("/")
- @PostMapping("/regist")
- @PutMapping("/{id:\\d+}")
- @RequestBody







## DispatcherServlet
- doService
- doDispatch
```java
/**
	 * Process the actual dispatching to the handler.
	 * <p>The handler will be obtained by applying the servlet's HandlerMappings in order.
	 * The HandlerAdapter will be obtained by querying the servlet's installed HandlerAdapters
	 * to find the first that supports the handler class.
	 * <p>All HTTP methods are handled by this method. It's up to HandlerAdapters or handlers
	 * themselves to decide which methods are acceptable.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws Exception in case of any kind of processing failure
	 */
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerExecutionChain mappedHandler = null;
		boolean multipartRequestParsed = false;

		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

		try {
			ModelAndView mv = null;
			Exception dispatchException = null;

			try {
				processedRequest = checkMultipart(request);
				multipartRequestParsed = (processedRequest != request);

				// Determine handler for the current request.
				mappedHandler = getHandler(processedRequest);
				if (mappedHandler == null) {
					noHandlerFound(processedRequest, response);
					return;
				}

				// Determine handler adapter for the current request.
				HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

				// Process last-modified header, if supported by the handler.
				String method = request.getMethod();
				boolean isGet = "GET".equals(method);
				if (isGet || "HEAD".equals(method)) {
					long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
					if (logger.isDebugEnabled()) {
						logger.debug("Last-Modified value for [" + getRequestUri(request) + "] is: " + lastModified);
					}
					if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
						return;
					}
				}

				if (!mappedHandler.applyPreHandle(processedRequest, response)) {
					return;
				}

				// Actually invoke the handler.
				mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

				if (asyncManager.isConcurrentHandlingStarted()) {
					return;
				}

				applyDefaultViewName(processedRequest, mv);
				mappedHandler.applyPostHandle(processedRequest, response, mv);
			}
			catch (Exception ex) {
				dispatchException = ex;
			}
			catch (Throwable err) {
				// As of 4.3, we're processing Errors thrown from handler methods as well,
				// making them available for @ExceptionHandler methods and other scenarios.
				dispatchException = new NestedServletException("Handler dispatch failed", err);
			}
			processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
		}
		catch (Exception ex) {
			triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
		}
		catch (Throwable err) {
			triggerAfterCompletion(processedRequest, response, mappedHandler,
					new NestedServletException("Handler processing failed", err));
		}
		finally {
			if (asyncManager.isConcurrentHandlingStarted()) {
				// Instead of postHandle and afterCompletion
				if (mappedHandler != null) {
					mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
				}
			}
			else {
				// Clean up any resources used by a multipart request.
				if (multipartRequestParsed) {
					cleanupMultipart(processedRequest);
				}
			}
		}
	}
```

```java
/**
	* Initialize the strategy objects that this servlet uses.
	* <p>May be overridden in subclasses in order to initialize further strategy objects.
	*/
protected void initStrategies(ApplicationContext context) {
	initMultipartResolver(context);
	initLocaleResolver(context);
	initThemeResolver(context);
	initHandlerMappings(context);
	initHandlerAdapters(context);
	initHandlerExceptionResolvers(context);
	initRequestToViewNameTranslator(context);
	initViewResolvers(context);
	initFlashMapManager(context);
}
```

## MockMvc

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
	@Test
	public void whenDeleteSuccess() throws Exception {
		mockMvc.perform(delete("/user/1")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}
}

```

# spring security





## 認證




## 授權





# SpringBoot

# SpringCloud

## EnvironmentPostProcessor（META-INF/spring.factories）

# 參考資料


