package ru.mart.pioneer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@MockBean(TaskScheduler.class)
public abstract class AbstractIntegrationTest {
    private static final String DOCKER_IMAGE_NAME = "postgres:15-alpine";
    private static final String DATABASE_NAME = "testdb";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpass";

    @Container
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DOCKER_IMAGE_NAME)
                    .withDatabaseName(DATABASE_NAME)
                    .withUsername(USERNAME)
                    .withPassword(PASSWORD);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @PersistenceContext
    protected EntityManager entityManager;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @BeforeEach
    void cleanDatabase() {
        entityManager.clear(); // Очистка кэша 1го уровня

        jdbcTemplate.execute("TRUNCATE TABLE emails RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE phones RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE accounts RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");

        jdbcTemplate.execute("ALTER SEQUENCE users_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE emails_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE phones_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART WITH 1");
    }
}
