package ru.mart.pioneer.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.mart.pioneer.AbstractIntegrationTest;
import ru.mart.pioneer.model.EmailData;
import ru.mart.pioneer.model.User;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mart.pioneer.utils.TestEntityObjectsUtil.createUser;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("User is found by email")
    void shouldFindUserByEmail() {
        User user = createUser(1);
        String expectedEmail = user.getEmails().iterator().next().getEmail();

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmails_Email(expectedEmail);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getName(), foundUser.get().getName());
        assertFalse(foundUser.get().getEmails().isEmpty());

        Set<EmailData> foundEmailDataSet = foundUser.get().getEmails();
        Optional<EmailData> foundEmailData = foundEmailDataSet.stream().findFirst();

        assertEquals(expectedEmail, foundEmailData.get().getEmail());
    }

    @Test
    @DisplayName("Check that user can be found by email for security load by username")
    void findUserByEmail() {
        User user = createUser(1);
        String expectedEmail = user.getEmails().iterator().next().getEmail();

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmailOrPhone(expectedEmail);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getName(), foundUser.get().getName());
        assertFalse(foundUser.get().getEmails().isEmpty());

        Set<EmailData> foundEmailDataSet = foundUser.get().getEmails();
        Optional<EmailData> foundEmailData = foundEmailDataSet.stream().findFirst();

        assertEquals(expectedEmail, foundEmailData.get().getEmail());
        assertEquals(user.getId(), foundUser.get().getId());
        assertEquals(user.getName(), foundUser.get().getName());
    }
}