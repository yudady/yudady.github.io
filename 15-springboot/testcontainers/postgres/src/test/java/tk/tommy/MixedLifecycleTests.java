package tk.tommy;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.junit.Assert.assertTrue;

@Testcontainers
class MixedLifecycleTests {

    // will be shared between test methods
    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:5.7");

    // will be started before and stopped after each test method
    @Container
    private PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:12")
        .withDatabaseName("foo")
        .withUsername("foo")
        .withPassword("secret");

    @Test
    void test() {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertTrue(postgresqlContainer.isRunning());
    }
}