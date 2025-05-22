package ru.mart.pioneer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mart.pioneer.AbstractIntegrationTest;
import ru.mart.pioneer.model.User;
import ru.mart.pioneer.security.JwtTokenProvider;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerTest extends AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    protected String generateTestToken(User user) {
        return jwtTokenProvider.generateToken(
                user.getEmails().iterator().next().getEmail(),
                user.getId()
        );
    }
}
