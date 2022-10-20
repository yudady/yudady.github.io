# SpringBoot中的事件機制

[SpringBoot中的事件機制](https://www.itread01.com/content/1541057234.html)

> ApplicationListener

- ApplicationStartingEvent：在Spring最開始啟動的時候觸發
- ApplicationEnvironmentPreparedEvent：在Spring已經準備好上下文但是上下文尚未建立的時候觸發
- ApplicationContextInitializedEvent is sent when the ApplicationContext is prepared and ApplicationContextInitializers have been called but before any bean definitions are loaded.
- ApplicationPreparedEvent：在Bean定義載入之後、重新整理上下文之前觸發
- ApplicationStartedEvent：在重新整理上下文之後、呼叫application命令之前觸發
- ApplicationReadyEvent：在呼叫applicaiton命令之後觸發
- ApplicationFailedEvent：在啟動Spring發生異常時觸發


