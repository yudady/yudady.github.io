# [web](http://localhost:8080/web/resource/fruit)





[@ConcurrencyManagement](https://www.baeldung.com/java-ee-singleton-session-bean)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)


[Singleton Session Bean in Jakarta EE](https://www.baeldung.com/java-ee-singleton-session-bean)

https://github.com/eugenp/tutorials/blob/master/spring-ejb/spring-ejb-client/src/main/java/com/baeldung/springejbclient/SpringEjbClientApplication.java
## Types of Session Beans (lifecycles)
### Stateful Session Beans
有狀態會話Bean維護與其正在通信的客戶端的會話狀態。 每個客戶端都會創建一個新的有狀態Bean實例，並且不會與其他客戶端共享。 當客戶端和Bean之間的通信結束時，會話Bean也終止。
### Stateless Session Beans
無狀態會話Bean不與客戶端保持任何對話狀態。 Bean僅在方法調用期間才包含特定於客戶端的狀態。 與有狀態會話Bean不同，連續方法調用是獨立的。 該容器維護一個無狀態Bean池，並且這些實例可以在多個客戶端之間共享。
### Singleton Session Beans
Singleton Session Bean在應用程序的整個生命週期內維護Bean的狀態。 Singleton Session Bean與無狀態Session Bean相似，但是在整個應用程序中僅創建Singleton Session Bean的一個實例，並且在應用程序關閉之前不會終止。 Bean的單個實例在多個客戶端之間共享，並且可以同時訪問。




## 並發
EJB提供了兩種方法來實現對Singleton Session Bean的並發訪問：容器管理的並發和Bean管理的並發。 
註釋javax.ejb.ConcurrencyManagement定義方法的並發策略。 默認情況下，EJB容器使用容器管理的並發。 
@ConcurrencyManagement批註採用javax.ejb.ConcurrencyManagementType值。 
選項為：

- ConcurrencyManagementType.CONTAINER，用於容器管理的並發。 
- ConcurrencyManagementType.BEAN用於Bean管理的並發。

```java
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER) 
public class CountryStateContainerManagedBean implements CountryState { 
 
    private final Map<String, List<String> countryStatesMap = new HashMap<>();
 
    @Lock(LockType.READ) 
    public List<String> getStates(String country) { 
        return countryStatesMap.get(country);
    }
 
    @Lock(LockType.WRITE)
    public void setStates(String country, List<String> states) {
        countryStatesMap.put(country, states);
    }
}
```

```java
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN) 
public class CountryStateBeanManagedBean implements CountryState { 
   ... 
}
```





