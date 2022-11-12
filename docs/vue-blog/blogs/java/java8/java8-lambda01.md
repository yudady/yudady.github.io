---
title: java8-函數式編程01
author: tommy
tags:
  - java
categories:
  - java
toc: true
date: 2018-10-18 10:01:38
---

# 簡介
- 使用函數式邊程，抽取模版
- 類名-靜態方法名


<!--more-->
# 內容

## 原始程式

```java
// DAO 
// FunctionalInterface 格式 接收兩個參數，返回一個值
public class OhActivityDAO {
	public static List<OhActivity> findAll(Connection conn,Map<String,Object> map) throws SQLException {
		String sql = "SELECT * FROM  OH_ACTIVITY where 1=1 ";
		return DBQueryRunner.getBeanList(conn, OhActivity.class, sql);
  }
}

// Service
// 抽取模版
public class ActivityBO {
	public static List<OhActivity> findAll() throws SQLException {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getReadConnection();
			return OhActivityDAO.findAll(conn,new HashMap());
		} finally {
			DbUtils.close(conn);
		}
  }
}
```


## 修改後

```java
// 函數式接口
@FunctionalInterface
public interface BiFunctionUnchecked<K, T, V> {
	V applyDao(K k, T t) throws SQLException; // 因為Dao會丟出SQLException
}

// 模版
public class DefaultBO {
	public static <R> Optional<R> readOptional(Map map, BiFunctionUnchecked<Connection, Map, R> function)
		throws Exception {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getReadConnection();
			R r = function.applyDao(conn, map); // 傳入2個參數，返回一個值
			return Optional.ofNullable(r);
		} finally {
			DbUtils.close(conn);
		}
	}

	public static <R> Optional<R> writeOptional(Map map, BiFunctionUnchecked<Connection, Map, R> function)
		throws Exception {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getWriteConnection();
			conn.setAutoCommit(false);
			R r = function.applyDao(conn, map);
			conn.commit();
			return Optional.ofNullable(r);
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			DbUtils.close(conn);
		}
	}

	public static <R> R read(Map map, BiFunctionUnchecked<Connection, Map, R> function) throws Exception {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getReadConnection();
			R r = function.applyDao(conn, map);
			return r;
		} finally {
			DbUtils.close(conn);
		}
	}

	public static <R> R write(Map map, BiFunctionUnchecked<Connection, Map, R> function) throws Exception {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getWriteConnection();
			conn.setAutoCommit(false);
			R r = function.applyDao(conn, map);
			conn.commit();
			return r;
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			DbUtils.close(conn);
		}
	}
}


// DAO
public class BeanDao {
	public static List<OhSystemConfig> currentSql(Connection conn, Map<String, Object> map) throws SQLException {
		String sql = "select * from  OH_SYSTEM_CONFIG where TYPE = ? ORDER BY DESCR ASC ";
		return DBQueryRunner.getBeanList(conn, OhSystemConfig.class, sql, map.get("type"));
	}
	public static List<OhSystemConfig> errorSql(Connection conn, Map<String, Object> map) throws SQLException {
		String sql = "select *!!!!!!!!!!! from  OH_SYSTEM_CONFIG where TYPE = ? ORDER BY DESCR ASC ";
		return DBQueryRunner.getBeanList(conn, OhSystemConfig.class, sql, map.get("type"));
	}
}

// 測試
public class TestBo {
	@Test
	public void testSimpleBoMethod() throws Exception {

		Map<String, Object> map = new HashMap<>();
		String type = "ACTIVITY_FIELD_LIST";
		map.put("type", type);
    List<OhSystemConfig> ohSystemConfigs = TestBo.simpleBoMethod(map); //
		ohSystemConfigs.stream().map(ToStringBuilder::reflectionToString).forEach(System.out::println);
	}
	private static List<OhSystemConfig> simpleBoMethod(Map<String, Object> map) throws Exception {
		return DefaultBO.read(map, BeanDao::currentSql); // 類名-靜態方法名
	}
}

```


# 參考資料


