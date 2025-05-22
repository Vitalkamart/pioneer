package ru.mart.pioneer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.mart.pioneer.repository.AccountRepository;
import ru.mart.pioneer.repository.EmailDataRepository;
import ru.mart.pioneer.repository.PhoneDataRepository;
import ru.mart.pioneer.repository.UserRepository;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration," +
                "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,",
        "spring.flyway.enabled=false"
})
@MockBeans({@MockBean(UserRepository.class), @MockBean(EmailDataRepository.class),
        @MockBean(PhoneDataRepository.class), @MockBean(AccountRepository.class)
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PioneerApplicationTests {

    @Test
    void contextLoads() {
    }

}
