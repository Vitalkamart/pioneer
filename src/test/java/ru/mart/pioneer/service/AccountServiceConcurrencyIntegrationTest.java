package ru.mart.pioneer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import ru.mart.pioneer.AbstractIntegrationTest;
import ru.mart.pioneer.model.Account;
import ru.mart.pioneer.model.User;
import ru.mart.pioneer.repository.AccountRepository;
import ru.mart.pioneer.repository.UserRepository;
import ru.mart.pioneer.security.JwtTokenProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static ru.mart.pioneer.utils.TestEntityObjectsUtil.createUser;

@SpringBootTest
class AccountServiceConcurrencyIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(anyString())).thenReturn("testuser");

        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void transferShouldHandleConcurrentRequests() throws InterruptedException {
        User user1 = userRepository.save(createUser(1));
        User user2 = userRepository.save(createUser(2));

        Account account1 = user1.getAccount();
        account1.setBalance(new BigDecimal("1000.00"));
        accountRepository.save(account1);

        Account account2 = user2.getAccount();
        account2.setBalance(new BigDecimal("500.00"));
        accountRepository.save(account2);

        int threads = 10;
        try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
            CountDownLatch latch = new CountDownLatch(threads);

            for (int i = 0; i < threads; i++) {
                executor.execute(() -> {
                    try {
                        accountService.transfer(
                                user1.getId(),
                                user2.getId(),
                                new BigDecimal("10.00")
                        );
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(5, TimeUnit.SECONDS);
            executor.shutdown();
        }


        Optional<User> foundUser1 = userRepository.findById(user1.getId());
        Optional<User> foundUser2 = userRepository.findById(user2.getId());

        assertTrue(foundUser1.isPresent());
        assertTrue(foundUser2.isPresent());

        Account updatedAccount1 = foundUser1.get().getAccount();
        Account updatedAccount2 = foundUser2.get().getAccount();

        assertEquals(new BigDecimal("900.00"), updatedAccount1.getBalance().setScale(2, RoundingMode.FLOOR));
        assertEquals(new BigDecimal("600.00"), updatedAccount2.getBalance().setScale(2, RoundingMode.FLOOR));
    }
}
