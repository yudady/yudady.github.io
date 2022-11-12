---
title: java8
author: tommy
tags:
  - java
categories:
  - java
toc: true
date: 2018-10-03 15:11:30
---

# 簡介

- Stream
- Collectors

<!--more-->
# 內容


## 从数组创建, Arrays.stream
```java
Arrays.stream(new int[] {1, 1, 2, 3, 5});
Arrays.stream(new long[] {1, 1, 2, 3, 5});
Arrays.stream(new double[] {1, 1, 2, 3, 5});
Arrays.stream(new String[] {"foo", "bar"});
```


## 从容器创建, Collection.stream
```java
Arrays.asList(1, 2, 3).stream();
```

## Stream上的静态方法
```java
Stream.of(1, 2, 3);
Stream.of(1);

var s1 = Stream.of(1, 2, 3);
var s2 = Stream.of(4, 5, 6);
Stream.concat(s1 , s2);
```

## Stream
```java
public class StreamApi {

	public static class Sample {

		public int score;
		public int type;

		public Sample(int score, int type) {
			this.type = type;
			this.score = score;
		}

		@Override
		public String toString() {
			return "Sample{" + "score=" + score + ", type=" + type + '}';
		}
	}

	@Test
	public void test01() {

		// groupingBy，对Stream中元素进行分组

		List<Sample> samples = Arrays.asList(//
			new Sample(90, 1), //
			new Sample(80, 1), //
			new Sample(80, 1), //
			new Sample(70, 2), //
			new Sample(60, 2), //
			new Sample(50, 3)//
		);

		// 简单分组
		// {1=[Sample{score=90, type=1}, Sample{score=80, type=1}, Sample{score=80,
		// type=1}], 2=[Sample{score=70, type=2}, Sample{score=60, type=2}],
		// 3=[Sample{score=50, type=3}]}
		Map<Integer, List<Sample>> one = samples.stream().collect(Collectors.groupingBy(x -> x.type));
		System.out.println(one);

		// 二级分组，先按类别分组，再按分数分组
		// {1={80=[Sample{score=80, type=1}, Sample{score=80, type=1}],
		// 90=[Sample{score=90, type=1}]}, 2={70=[Sample{score=70, type=2}],
		// 60=[Sample{score=60, type=2}]}, 3={50=[Sample{score=50, type=3}]}}
		Map<Integer, Map<Integer, List<Sample>>> second = samples.stream()
			.collect(Collectors.groupingBy(x -> x.type, Collectors.groupingBy(x -> x.score)));
		System.out.println(second);

		// 配合其余Collectors里的接口使用
		// 分组后求和
		// {1=250, 2=130, 3=50}
		Map<Integer, Integer> third = samples.stream()
			.collect(Collectors.groupingBy(x -> x.type, Collectors.summingInt(x -> x.score)));
		System.out.println(third);

		// 分组后获取最大值
		// {1=Optional[Sample{score=90, type=1}], 2=Optional[Sample{score=70, type=2}],
		// 3=Optional[Sample{score=50, type=3}]}
		Map<Integer, Optional<Sample>> forth = samples.stream()
			.collect(Collectors.groupingBy(x -> x.type, Collectors.maxBy((x, y) -> x.score - y.score)));
		System.out.println(forth);

		// 分组后reduce
		// {1=Optional[250], 2=Optional[130], 3=Optional[50]}
		Map<Integer, Optional<Integer>> fifth = samples.stream().collect(Collectors.groupingBy(x -> x.type,
			Collectors.mapping(x -> x.score, Collectors.reducing((x, y) -> x + y))));
		System.out.println(fifth);

		// ----------------------------------
		// ----------------------------------
		// ----------------------------------

		List<Integer> nums = Arrays.asList(1, 1, 2, 3, 5);
		List<String> words = Arrays.asList("foo", "bar", "aaa", "BBB");

		// collect，进行collect操作，构建结果
		nums.stream().collect(Collectors.toList());
		nums.stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

		// averagingInt/averagingLong/averagingDouble，计算平均值
		nums.stream().collect(Collectors.averagingInt(x -> x));

		// counting，统计数目
		nums.stream().collect(Collectors.counting());

		// maxBy，获取最大元素
		nums.stream().collect(Collectors.maxBy((x, y) -> x - y)).ifPresent(System.out::println);

		// minBy，获取最小元素
		nums.stream().collect(Collectors.minBy((x, y) -> x - y)).ifPresent(System.out::println);

		// summingInt/summingLong/summingDouble
		nums.stream().collect(Collectors.summingInt(x -> x));
		nums.stream().collect(Collectors.summingLong(x -> x));
		nums.stream().collect(Collectors.summingDouble(x -> x));

		// joining，拼接Stream中元素
		words.stream().collect(Collectors.joining());
		words.stream().collect(Collectors.joining(", "));
		words.stream().collect(Collectors.joining(", ", "prefix, ", ", suffix"));

		// toList，将Stream转化成List，实际类型是ArrayList
		List<Integer> collectList = nums.stream().collect(Collectors.toList());

		// toSet，将Stream转化成Set，实际类型是HashSet
		Set<Integer> collectSet = nums.stream().collect(Collectors.toSet());

		// toMap，将Stream转化成Map，实际类型是HashMap
		Map<Integer, Integer> collectMap = nums.stream().collect(Collectors.toMap(x -> x, x -> x * x));

		// toConcurrentMap，将Stream转化为ConcurrentHashMap
		ConcurrentMap<Integer, Integer> collectConcurrentMap = nums.stream()
			.collect(Collectors.toConcurrentMap(x -> x, x -> x * x));

		// toCollection，将Stream转化为指定类型的容器，通过提供supplier来实现
		TreeSet<Integer> collectTreeSet = nums.stream().collect(Collectors.toCollection(TreeSet::new));

		// collectingAndThen，在传入的Collector基础之上再执行一个finisher操作
		String collectCollectingAndThen = nums.stream()
			.collect(Collectors.collectingAndThen(Collectors.averagingInt(x -> x), x -> "" + (x * x)));

		// mapping, 适配一个已有的Collector，对Stream中元素执行mapper操作后再进行处理
		List<Integer> collectCollectorsMapping = words.stream()
			.collect(Collectors.mapping(x -> x.length(), Collectors.toList()));
		// --------------
		// --------------

		// forEach, 遍历并进行操作
		nums.stream().forEach(System.out::print);

		// forEachOrdered, 有序遍历进行操作
		nums.stream().forEachOrdered(System.out::print);

		// allMatch，判断是否每一个元素都满足条件
		nums.stream().allMatch(x -> x > 0);

		// anyMatch，判断是否有一个元素满足条件
		nums.stream().anyMatch(x -> x > 3);

		// noneMatch，判断是否没有元素满足条件
		nums.stream().noneMatch(x -> x > 5);

		// limit，保留指定个数元素
		nums.stream().limit(4).forEach(System.out::println);

		// findFirst，返回首个元素
		nums.stream().findFirst().ifPresent(System.out::println);

		// findAny，返回任意一个元素
		nums.stream().findAny().ifPresent(System.out::println);

		// distinct，返回不包含重复元素的Stream
		nums.stream().distinct().forEach(System.out::println);

		// count，统计Stream中元素个数
		nums.stream().count();

		// max，获取最大元素
		nums.stream().max((x, y) -> x - y).ifPresent(System.out::println);

		// min，获取最小元素
		nums.stream().min((x, y) -> x - y).ifPresent(System.out::println);

		// 自然排序
		nums.stream().sorted().forEach(System.out::println);
		// 通过comparator指定排序规则
		nums.stream().sorted((x, y) -> y - x).forEach(System.out::println);

		// map，对每一个元素进行转换，生成新的Stream

		words.stream().map(String::toUpperCase).forEach(System.out::println);

		// flatMap，对每一个元素进行转换，将其变成一个Stream类型数据
		words.stream().flatMap(x -> Arrays.asList(x, x).stream()).forEach(System.out::println);

		// filter，过滤Stream上操作
		nums.stream().filter(x -> x > 2).forEach(System.out::println);

		// reduce，对Stream进行reduce操作，获得计算结果
		nums.stream().reduce((x, y) -> x + y).ifPresent(System.out::println);
		// 传入初始参数，参数类型与Stream中需要一致
		nums.stream().reduce(100, (x, y) -> x + y);
		words.stream().reduce("", (x, y) -> x + y);
		// 传入出事参数，参数类型可以不一致需要满足，combiner.apply(u, accumulator.apply(identity, t)) ==
		// accumulator.apply(u, t)
		nums.stream().reduce("Foo", (x, y) -> x + y, (x, y) -> x + y);

	}
}
```


## 类名::静态方法名
```java
//　List 有一個方法　=> default void sort(Comparator<? super E> c)
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);
}


List<Student> students =　...
// 比較成績
students.sort((o1, o2) -> o1.getScore() - o2.getScore());  

// 如果Student有一個static方法
public static int compareStudentByScore(Student o1,Student o2){
    return o1.getScore() - o2.getScore();
}

// 比較成績可以改寫
students.sort(Student::compareStudentByScore);

// 如果Student有一個方法
public int compareByScore(Student student){
    return this.getScore() - student.getScore();
}
// 比較成績可以改寫
students.sort(Student::compareStudentByScore);
```

```java

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
public class Student {

	private String name;
	private int score;

	public Student() {
		this.name = "default";
		this.score = 0;
	}

	public Student(String name) {
		this.name = name;
		this.score = 0;
	}

	public Student(String name, int score) {
		this.name = name;
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override public String toString() {
		return "Student{" + "name='" + name + '\'' + ", score=" + score + '}';
	}

	public static Student create(final Supplier<Student> supplier) {

		return supplier.get();
	}

	public static Student createStudent0() {
		Supplier<Student> c1 = Student::new;
		Student c11 = c1.get();
		//等价于
		Supplier<Student> c2 = () -> new Student();
		Student c22 = c2.get();
		return c22;
	}

	public static Student createStudent1(String n) {
		Function<String, Student> c1 = Student::new;
		Student c11 = c1.apply(n);
		//等价于
		Function<String, Student> c2 = (name) -> new Student(name);
		Student c22 = c2.apply(n);

		return c22;
	}

	public static Student createStudent2(String name, int score) {
		BiFunction<String, Integer, Student> c11 = Student::new;
		Student c1 = c11.apply(name, score);
		//等价于
		BiFunction<String, Integer, Student> c22 = (n, sc) -> new Student(n, sc);
		Student c2 = c22.apply(name, score);
		return c2;
	}

	public static void collide(final Student student) {
		System.out.println("Collided " + student.toString());
	}

	public void repair() {
		System.out.println("Repaired " + this.toString());
	}

	public void follow(final Student another) {
		System.out.println("Following the " + another.toString());
	}

	public static int compareStudentByScore(Student o1, Student o2) {
		return o1.getScore() - o2.getScore();
	}

	public int compareByScore(Student student) {
		return this.getScore() - student.getScore();
	}

	private static int coverStudentScore(List<Student> students, Function<Student, Integer> function) {
		List<Integer> list = new ArrayList<>();
		for (Student a : students) {
			/**
			 * @FunctionalInterface
			 * public interface Function<T, R> {
			 *    R apply (T t);
			 * }
			 */
			Integer apply = function.apply(a);// Function<Student, Integer> => Integer-Function-Student
			list.add(apply);
		}
		return list.stream().collect(Collectors.summingInt(Integer::intValue));
	}

	public static void main(String[] args) {
		Student student1 = Student.createStudent2("tommy", 100);
		Student student2 = Student.create(Student::new); // 构造器引用
		Student student4 = new Student("李四", 70);
		Student student5 = new Student("王五", 80);
		Student student6 = new Student("趙六", 90);
		List<Student> students = Arrays.asList(student1, student2, student4, student5, student6);
		students.forEach(Student::collide);//Class::static_method
		students.forEach(Student::repair);//Class::method
		students.forEach(Student::toString);//Class::method
		students.forEach(student1::follow);//instance::method

		students.sort((o1, o2) -> o1.getScore() - o2.getScore());

		/**
		 * (Student student) -> student.getScore()
		 * 等价于
		 * Student::getScore
		 */
		students.stream().map(student -> student.getScore()).forEach(System.out::print);
		students.stream().map(Student::getScore).forEach(System.out::print);

		students.sort(Student::compareStudentByScore);//Class::static_method

		StudentComparator studentComparator = new StudentComparator();
		students.sort(studentComparator::compareStudentByScore);

		students.sort(Student::compareByScore);

		students.forEach(student -> System.out.println(student.getScore()));

		coverStudentScore(students, Student::getScore);
	}
}
class StudentComparator {

	public int compareStudentByScore(Student o1, Student o2) {
		return o1.getScore() - o2.getScore();
	}
}
```

# 參考資料


