---
title: weblogic-spring-jpa
author: tommy
tags:
  - weblogic
categories:
  - java
toc: true
date: 2019-05-25 11:59:43
---

# 簡介

spring jpa

<!--more-->
# 內容


```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:jpa="http://www.springframework.org/schema/data/jpa"
       xmlns:repository="http://www.springframework.org/schema/data/repository"
       xmlns:jee="http://www.springframework.org/schema/jee" xmlns:beans="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa-1.3.xsd
		http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.0.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd
        http://www.springframework.org/schema/data/repository http://www.springframework.org/schema/data/repository/spring-repository.xsd
        http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd">

    <!-- 配置自动扫描的包 -->
    <context:component-scan base-package="core.*.model , lott.*.model"></context:component-scan>

    <!-- 1. 配置数据源 -->
    <bean id="dataSource"
          class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="username" value="core"></property>
        <property name="password" value="core"></property>
        <property name="driverClassName" value="oracle.jdbc.OracleDriver"></property>
        <property name="url" value="jdbc:oracle:thin:@DB.PF2DEV1-OOB.COM:1521/UB8.pf2dev1.com"></property>

        <!-- 配置其他属性 -->
    </bean>

    <!-- 2. 配置 JPA 的 EntityManagerFactory -->
    <bean id="entityManagerFactory"
          class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <!--<property name="persistenceXmlLocation" value=""></property>-->
        <property name="dataSource" ref="dataSource"></property>
        <property name="loadTimeWeaver">
            <bean class="org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver"/>
        </property>

        <property name="jpaVendorAdapter">
            <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
                <property name="database" value="ORACLE"/>
                <property name="generateDdl" value="false"/>
                <property name="showSql" value="true"/>
            </bean>
        </property>
        <property name="packagesToScan">
            <list>
                <value>core.*</value>
                <value>lott.*</value>
            </list>
        </property>
        <property name="jpaProperties">
            <props>
                <!-- 二级缓存相关 -->
                <!--
                <prop key="hibernate.cache.region.factory_class">org.hibernate.cache.ehcache.EhCacheRegionFactory</prop>
                <prop key="net.sf.ehcache.configurationResourceName">ehcache-hibernate.xml</prop>
                -->
                <!-- 生成的数据表的列的映射策略
                <prop key="hibernate.ejb.naming_strategy">org.hibernate.cfg.ImprovedNamingStrategy</prop>
                -->
                <!-- hibernate 基本属性 -->
                <prop key="hibernate.dialect">org.hibernate.dialect.Oracle10gDialect</prop>
                <prop key="hibernate.show_sql">true</prop>
                <prop key="hibernate.format_sql">true</prop>
                <prop key="hibernate.hbm2ddl.auto">none</prop>
            </props>
        </property>
    </bean>

    <!-- 3. 配置事务管理器 -->
    <bean id="transactionManager"
          class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"></property>
    </bean>

    <!-- 4. 配置支持注解的事务 -->
    <tx:annotation-driven transaction-manager="transactionManager"/>


    <!-- 5. 配置 SpringData -->
    <!-- 加入  jpa 的命名空间 -->
    <!-- base-package: 扫描 Repository Bean 所在的 package -->
    <jpa:repositories base-package="core.*.persistence , lott.*.persistence"
                      entity-manager-factory-ref="entityManagerFactory">
        <!--<repository:exclude-filter type="regex" expression=".*IJpsSuperConfigRepository"/>-->
        <!--<repository:exclude-filter type="regex" expression=".*ILgsMessageRecoveryRepository"/>-->
    </jpa:repositories>

    <bean id="customerQueryRepository" class="core.user.persistence.CustomerQueryRepository"/>

    <bean id="customerProfileRepository" class="core.user.persistence.CustomerProfileRepository"/>
    <!--<bean id="customerGroupRepository" class="core.user.persistence.CustomerGroupRepository" />-->



    <beans:properties id="jndiEnvironment">
        <prop key="java.naming.factory.initial">weblogic.jndi.WLInitialContextFactory</prop>
        <prop key="java.naming.provider.url">t3://localhost:8080</prop>
        <prop key="java.naming.security.principal">weblogic</prop>
        <prop key="java.naming.security.credentials">1qaz2wsx</prop>
    </beans:properties>

    <bean id="casualGameLottGroupSeriesServiceRemote" class="org.springframework.jndi.JndiObjectFactoryBean">
        <property name="jndiName" value="bpm.ejb.CasualGameLottGroupSeriesService#core.bpm.service.settings.CasualGameLottGroupSeriesServiceRemote"></property>
        <property name="jndiEnvironment" ref="jndiEnvironment" />
    </bean>

</beans>

```

## 測試
```java
package tk.dao.test03;

import com.yx.cgs.to.TopCasualGameModel;
import core.ac.model.AcsAccount;
import core.ac.persistence.IAcsAccountRepository;
import core.bp.model.BpsBankFeeHistory;
import core.bp.model.BpsCustomerEwallet;
import core.bp.persistence.IBpsBankFeeHistoryRepository;
import core.bp.persistence.IBpsBankLocationRepository;
import core.bp.persistence.IBpsCustomerEwalletRepository;
import core.bpm.service.settings.CasualGameLottGroupSeriesServiceRemote;
import core.user.model.CustomerDeletion;
import core.user.persistence.CustomerProfileRepository;
import core.user.persistence.CustomerQueryRepository;
import core.user.persistence.ICustomerDeletionRepository;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import lott.lg.model.LgsGameGroup;
import lott.lg.persistence.ILgsGameGroupRepository;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;


import static org.apache.commons.lang3.builder.ToStringStyle.DEFAULT_STYLE;

/**
 * 需要在core.gradle添加依赖
 * <p>
 * testImplementation "org.springframework:spring-test:${springVersion}"
 * testImplementation "junit:junit:4.12"
 * </p>
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(value = "classpath*:applicationContext.xml")
@EnableSpringDataWebSupport
public class SpringDataJpaUsingXmlTest {

    @Autowired
    private ApplicationContext ctx;
//casualGameLottGroupSeriesService

    @Test
    public void test014() throws Exception {

        CasualGameLottGroupSeriesServiceRemote bean = ctx.getBean(CasualGameLottGroupSeriesServiceRemote.class);
        System.out.println(bean);

        TopCasualGameModel topCasualGame = bean.getTopCasualGame(1, "WEB");
        System.out.println("[LOG]" + ToStringBuilder.reflectionToString(topCasualGame, ToStringStyle.DEFAULT_STYLE));

    }

    @Test
    public void test013() {

        IBpsBankLocationRepository bean = ctx.getBean(IBpsBankLocationRepository.class);
        System.out.println(bean);
        List<Object[]> list = bean.getAllActiveBankLocations();
        System.out.println(list.size());
        list.stream().forEach(e -> {
            System.out.println("[LOG]" + ToStringBuilder.reflectionToString(e, DEFAULT_STYLE));
        });
    }

    /**
     * <code>
     * QueryDslJpaRepository
     * <p>
     * public interface IAcsAccountRepository extends
     * JpaRepository<AcsAccount, Integer>,
     * JpaSpecificationExecutor<AcsAccount>,
     * QueryDslPredicateExecutor<AcsAccount>
     * </code>
     */
    @Test
    public void test012() {

        IAcsAccountRepository iLgsGameGroupRepository = ctx.getBean(IAcsAccountRepository.class);
        System.out.println(iLgsGameGroupRepository);
        List<AcsAccount> acsAccounts = iLgsGameGroupRepository.findByCustomerId(102);
        System.out.println(acsAccounts.size());


    }

    @Test
    public void test011() {

        ILgsGameGroupRepository iLgsGameGroupRepository = ctx.getBean(ILgsGameGroupRepository.class);
//        LGS_GAME_GROUP
        List<LgsGameGroup> gameGroupList = iLgsGameGroupRepository.findAll();
        for (LgsGameGroup gameGroup : gameGroupList) {
            System.out.println(gameGroup);
        }
    }


    @Test
    public void test05() throws Exception {
        ICustomerDeletionRepository iCustomerDeletionRepository = ctx.getBean(ICustomerDeletionRepository.class);
        List<CustomerDeletion> all = iCustomerDeletionRepository.findAll();
        System.out.println(all.size());
    }

    @Test
    public void test04() throws Exception {
        System.out.println(ctx);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void test03() throws Exception {

        IBpsCustomerEwalletRepository bean = ctx.getBean(IBpsCustomerEwalletRepository.class);
        List<BpsCustomerEwallet> all = bean.findAll();
        System.out.println(all.size());
        System.out.println(all.size());
        System.out.println(all.size());
    }


    @Test
    public void testJpa() throws SystemException, NotSupportedException {
        IBpsBankFeeHistoryRepository iBpsBankFeeHistoryRepository = ctx.getBean(IBpsBankFeeHistoryRepository.class);
        System.out.println(iBpsBankFeeHistoryRepository);
        List<BpsBankFeeHistory> byFeeId = iBpsBankFeeHistoryRepository.findByFeeId(72L);
        System.out.println(byFeeId);
    }

    @Test
    public void testDataSource() throws SQLException {
        DataSource dataSource = ctx.getBean(DataSource.class);
        System.out.println(dataSource.getConnection());
    }


    @Test
    public void test09() {
        CustomerProfileRepository customerProfileRepository = ctx.getBean(CustomerProfileRepository.class);
        setEm(customerProfileRepository, CustomerProfileRepository.class);
        String levelId = customerProfileRepository.findLevelId(325);
        System.out.println(levelId);
    }

    private void setEm(Object bean, Class clazz) {
        Field field = ReflectionUtils.findField(clazz, "em");
        field.setAccessible(true);
        EntityManager em = ctx.getBean(EntityManager.class);
        ReflectionUtils.setField(field, bean, em);
    }

    @Test
    public void test08() throws Exception {
        CustomerProfileRepository customerProfileRepository = ctx.getBean(CustomerProfileRepository.class);
        System.out.println(customerProfileRepository);
    }


    @Test
    public void test06() throws Exception {
        CustomerQueryRepository customerQueryRepository = ctx.getBean(CustomerQueryRepository.class);
        System.out.println(customerQueryRepository);
        setEm(customerQueryRepository, CustomerQueryRepository.class);
        boolean b = customerQueryRepository.checkCustomerVIP(5980);
        System.out.println(b);
    }

}

```





```java
package tk.dao.test02;

import core.user.persistence.CustomerGroupRepository;
import core.user.persistence.CustomerProfileRepository;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import lott.lg.model.LgsGameGroup;
import lott.lg.persistence.ILgsGameGroupRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.util.ReflectionUtils;

@Configuration
public class SpringInjectEntityManager2RepositoryTest {

    AnnotationConfigApplicationContext anno;



    // ---------------------------------------------
    @Bean
    public CustomerProfileRepository customerProfileRepository() {
        return new CustomerProfileRepository();
    }


    @Test
    public void test01() {
        System.out.println(anno);
        CustomerProfileRepository bean = getBean(CustomerProfileRepository.class);
        String levelId = bean.findLevelId(325);
        System.out.println(levelId);
    }


    // ---------------------------------------------


    @Bean
    public CustomerGroupRepository CustomerGroupRepository() {
        return new CustomerGroupRepository();
    }


    @Test
    public void test02() {
        CustomerGroupRepository bean = getBean(CustomerGroupRepository.class);
        Map<Integer, String> nameByID = bean.findNameByID(Arrays.asList(new Integer[]{1}));
        System.out.println(nameByID);
    }

    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------

    /**
     * set em to bean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T getBean(Class<T> clazz) {
        T bean = anno.getBean(clazz);
        EntityManager em = anno.getBean(EntityManager.class);
        Field field = ReflectionUtils.findField(clazz, "em");
        field.setAccessible(true);
        ReflectionUtils.setField(field, bean, em);
        return bean;
    }


    @Before
    public void setUp() {
        this.anno = new AnnotationConfigApplicationContext(SpringInjectEntityManager2RepositoryTest.class);
    }

    @After
    public void tearDown() {
        this.anno.close();
    }

    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------
    // ---------------------------------------------


    @Bean
    public LocalEntityManagerFactoryBean entityManagerFactory() {
        LocalEntityManagerFactoryBean lemf = new LocalEntityManagerFactoryBean();
        lemf.setPersistenceUnitName("CoreServiceTest");
        return lemf;
    }


    @Bean
    @Primary
    public EntityManager entityManager(EntityManagerFactory CoreServiceTest) {
        EntityManager em = CoreServiceTest.createEntityManager();
        return em;
    }

    @Bean
    @Primary
    public EntityTransaction entityTransaction(EntityManager em) {
        return em.getTransaction();
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}

```






# 參考資料


