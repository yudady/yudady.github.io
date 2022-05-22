// package tk.tommy;
//
// import java.util.Map;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.jdbc.core.JdbcTemplate;
//
// @SpringBootApplication
// public class App implements CommandLineRunner {
//
// 	@Autowired
// 	JdbcTemplate jdbcTemplate;
//
// 	public static void main(String[] args) {
// 		SpringApplication.run(App.class, args);
//
// 	}
//
// 	@Override
// 	public void run(String... args) throws Exception {
// //		 String sql = "SELECT column_name FROM USER_COL_COMMENTS WHERE table_name = 'py_user' ";
// //		String sql = "SELECT * FROM user_tab_cols WHERE table_name = 'PY_USER' ";
// 		String sql = "SELECT sysdate FROM dual ";
//
//
//
//
//
// 		Map<String, Object> map = jdbcTemplate.queryForMap(sql);
// 		System.out.println(map);
// 		System.out.println(map);
//
// 	}
// }
