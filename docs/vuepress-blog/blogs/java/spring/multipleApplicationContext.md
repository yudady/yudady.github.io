---
title: multipleApplicationContext
author: tommy
tags:
  - spring
categories:
  - java
toc: true
date: 2018-10-13 10:38:40
---

# 簡介

> 使用多個spring-AnnotationConfigApplicationContext

[源碼](https://github.com/yudady/applicationContextTest)

```java
AnnotationConfigApplicationContext ctx01 = new AnnotationConfigApplicationContext();
AnnotationConfigApplicationContext ctx02 = new AnnotationConfigApplicationContext();
AnnotationConfigApplicationContext ctx03 = new AnnotationConfigApplicationContext();

	/**
	 * Set the parent of this application context.
	 * <p>The parent {@linkplain ApplicationContext#getEnvironment() environment} is
	 * {@linkplain ConfigurableEnvironment#merge(ConfigurableEnvironment) merged} with
	 * this (child) application context environment if the parent is non-{@code null} and
	 * its environment is an instance of {@link ConfigurableEnvironment}.
	 * @see ConfigurableEnvironment#merge(ConfigurableEnvironment)
	 */
	@Override
	public void setParent(@Nullable ApplicationContext parent) {
		this.parent = parent;
		if (parent != null) {
			Environment parentEnvironment = parent.getEnvironment();
			if (parentEnvironment instanceof ConfigurableEnvironment) {
				getEnvironment().merge((ConfigurableEnvironment) parentEnvironment);
			}
		}
	}
```
<!--more-->
# 內容



```java
public class ApplicationContextTest01 {

	public static void main(String[] args) throws InterruptedException {

		AnnotationConfigApplicationContext parentCtx = new AnnotationConfigApplicationContext();
		parentCtx.setDisplayName("parentCtx");
		AnnotationConfigApplicationContext childCtx = new AnnotationConfigApplicationContext();
		childCtx.setDisplayName("childCtx");
		AnnotationConfigApplicationContext childCtx2 = new AnnotationConfigApplicationContext();
		childCtx2.setDisplayName("childCtx2");

		parentCtx.registerBean(Config01.class);
		childCtx.registerBean(Config02.class);
		childCtx2.registerBean(Config03.class);

		// parentCtx is ROOT CTX
		childCtx.setParent(parentCtx); // parent-child
		childCtx2.setParent(childCtx); // parent-child

		parentCtx.refresh(); // parent
		childCtx.refresh(); //child
		childCtx2.refresh(); //child2

		Car car = parentCtx.getBean(Car.class);
		Dog dog = childCtx.getBean(Dog.class);
		Egg egg = childCtx2.getBean(Egg.class);

		System.out.println(car);
		System.out.println(dog);
		System.out.println(egg);
		System.out.println("----子容器可以看到ROOT容器中定义的beans，反之不行------------");
		//	System.out.println(parentCtx.getBean(Car.class));
		//	System.out.println(parentCtx.getBean(Dog.class));
		//	System.out.println(parentCtx.getBean(Egg.class));
		System.out.println(childCtx2.getBean(Car.class));
		System.out.println(childCtx2.getBean(Dog.class));
		System.out.println(childCtx2.getBean(Egg.class));
		childCtx2.close();
	}
}
```

---
```java
public class ApplicationContextTest02 {

		public static void main(String[] args) throws InterruptedException {

		AnnotationConfigApplicationContext parentCtx = new AnnotationConfigApplicationContext(
			Config01.class);// parentCtx is ROOT CTX
		parentCtx.setDisplayName("parentCtx");
		Car car = parentCtx.getBean(Car.class);
		System.out.println(car);

		System.out.println("parentCtx啟動完成，準備加入一個新的AnnotationConfigApplicationContext");
		AnnotationConfigApplicationContext childCtx = new AnnotationConfigApplicationContext();
		childCtx.setDisplayName("childCtx");
		childCtx.registerBean(Config02.class);

		childCtx.setParent(parentCtx); // parent-child
		childCtx.refresh(); //child
		Dog dog = childCtx.getBean(Dog.class);
		System.out.println(dog);
		System.out.println("childCtx啟動完成，準備加入一個新的AnnotationConfigApplicationContext");

		AnnotationConfigApplicationContext childCtx2 = new AnnotationConfigApplicationContext();
		childCtx2.setDisplayName("childCtx2");
		childCtx2.registerBean(Config03.class);
		childCtx2.setParent(childCtx); // parent-child
		childCtx2.refresh(); //child2

		Egg egg = childCtx2.getBean(Egg.class);
		System.out.println(car);
		System.out.println(dog);
		System.out.println(egg);
		System.out.println("----子容器可以看到根容器中定义的beans，反之不行------------");
		//	System.out.println(parentCtx.getBean(Car.class));
		//	System.out.println(parentCtx.getBean(Dog.class));
		//	System.out.println(parentCtx.getBean(Egg.class));
		System.out.println(childCtx2.getBean(Car.class));
		System.out.println(childCtx2.getBean(Dog.class));
		System.out.println(childCtx2.getBean(Egg.class));

		childCtx2.close();
		System.out.println("把childCtx2 destroy 調用childCtx2.close()");
		System.out.println("==============");
		System.out.println(parentCtx.getDisplayName() + parentCtx.isActive());
		System.out.println(childCtx.getDisplayName() + childCtx.isActive());
		System.out.println(childCtx2.getDisplayName() + childCtx2.isActive());
		System.out.println("==============");
		System.out.println(childCtx.getDisplayName());
		System.out.println(childCtx.getBean(Dog.class));
		System.out.println("把childCtx存在Dog");

		childCtx2.close();
		System.out.println("把childCtx2 destroy 調用childCtx2.close()");

		System.out.println(childCtx);
		System.out.println(childCtx.getBean(Dog.class));
		System.out.println("把childCtx存在Dog");

		System.out.println("parentCtx destroy parentCtx.close()");
		parentCtx.close();
		System.out.println("==============");
		System.out.println("parentCtx.isActive()" + " isActive= " + parentCtx.isActive());
		System.out.println("childCtx.isActive()" + " isActive= " + childCtx.isActive());
		System.out.println("childCtx2.isActive()" + " isActive= " + childCtx2.isActive());
		System.out.println("==============");
		System.out.println(childCtx.getBean(Dog.class));
		System.out.println("把childCtx存在Dog");

		childCtx2 = new AnnotationConfigApplicationContext();
		parentCtx.setDisplayName("childCtx2");
		childCtx2.registerBean(Config03.class);
		childCtx2.setParent(childCtx); // parent-child
		childCtx2.refresh(); //child2
		System.out.println("==============");
		System.out.println(parentCtx.getDisplayName() + " isActive= " + parentCtx.isActive());
		System.out.println(childCtx.getDisplayName() + " isActive= " + childCtx.isActive());
		System.out.println(childCtx2.getDisplayName() + " isActive= " + childCtx2.isActive());
		System.out.println("==============");
		System.out.println(childCtx2.getDisplayName());
		System.out.println(childCtx2.getBean(Dog.class));
		System.out.println(childCtx2.getBean(Egg.class));
		System.out.println("把childCtx2存在Egg");

		parentCtx = new AnnotationConfigApplicationContext();
		parentCtx.registerBean(Config01.class);
		parentCtx.setDisplayName("parentCtx");
		parentCtx.setParent(childCtx2);
		parentCtx.refresh();
		System.out.println(parentCtx.getBean(Car.class));
		System.out.println("==============");
		System.out.println(parentCtx.getDisplayName() + " isActive= " + parentCtx.isActive());
		System.out.println(childCtx.getDisplayName() + " isActive= " + childCtx.isActive());
		System.out.println(childCtx2.getDisplayName() + " isActive= " + childCtx2.isActive());
		System.out.println("==============");
	}
}

```

---

```java
@Configurable
public class Config01 {
	@Bean
	public Car car(){
		return new Car();
	}
}

@Configurable
public class Config02 {
	@Bean public Dog dog() {
		return new Dog();
	}
}

@Configurable 
public class Config03 {
	@Bean 
	public Egg egg() {
		return new Egg();
	}
}


public class Car {
	private int id;
	private String name;

}
public class Dog {
	private int id;
	private String name;

}
public class Egg {
	private int id;
	private String name;

}
```



# 參考資料


