---
title: weblogic-ejb
author: tommy
tags:
  - weblogic
categories:
  - java
toc: true
date: 2019-05-25 11:53:30
---

# 簡介

weblogic 測試ejb

<!--more-->
# 內容

```java
package tk.ejb.casual;


import core.bpm.model.workflow.TaskVariable;
import core.bpm.model.workflow.WorkflowTask;
import core.bpm.service.workflow.engine.WorkflowEngineService;
import core.bpm.service.workflow.engine.WorkflowEngineServiceRemote;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Before;
import org.junit.Test;


/**
 * {@link WorkflowEngineServiceRemote}
 */
public class 任務流Test {
    Properties prop = new Properties();
    InitialContext ctx;

    @Before
    public void before() throws NamingException {
        prop.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        prop.put(Context.PROVIDER_URL, "t3://localhost:8080");
        prop.put(Context.SECURITY_PRINCIPAL, "weblogic");
        prop.put(Context.SECURITY_CREDENTIALS, "1qaz2wsx");
        ctx = new InitialContext(prop);
    }

    @Test
    public void test01() throws NamingException {
        Object obj = ctx.lookup("bpm/ejb/WorkflowEngineService#core.bpm.service.workflow.engine.WorkflowEngineServiceRemote");
        System.out.println(obj);
        System.out.println(obj.getClass().getName());
        WorkflowEngineService obj1 = (WorkflowEngineServiceRemote) obj;
        System.out.println("[LOG]" + obj);
        System.out.println("[LOG]" + obj1);
        WorkflowTask wf = obj1.findTask("CS190419140546667");
        System.out.println("[LOG]" + ToStringBuilder.reflectionToString(wf, ToStringStyle.DEFAULT_STYLE));
        TaskVariable taskVariable = wf.getTaskVariable();
        System.out.println("[LOG]" + ToStringBuilder.reflectionToString(taskVariable, ToStringStyle.DEFAULT_STYLE));


    }
}

```

# 參考資料


