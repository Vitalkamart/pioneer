package ru.mart.pioneer.utils;

import ru.mart.pioneer.model.Account;
import ru.mart.pioneer.model.EmailData;
import ru.mart.pioneer.model.PhoneData;
import ru.mart.pioneer.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static ru.mart.pioneer.util.CollectionUtils.mutableSetOf;

public class TestEntityObjectsUtil {
    public static User createUser(int i) {
        User user = new User()
                .setName(String.format("user_%d", i))
                .setDateOfBirth(LocalDate.now().minusDays(i))
                .setPassword(String.format("password_%d", i));

        user.setAccount(createAccount(user));
        user.setPhones(createPhones(user, i));
        user.setEmails(createEmails(user, i));
        return user;
    }

    private static Account createAccount(User user) {
        return new Account()
                .setBalance(BigDecimal.valueOf(1000))
                .setInitialDeposit(BigDecimal.valueOf(1000))
                .setUser(user);
    }

    private static Set<PhoneData> createPhones(User user, int i) {
        return mutableSetOf(new PhoneData()
                .setPhone(String.format("7929555667%d", i))
                .setUser(user));
    }

    private static Set<EmailData> createEmails(User user, int i) {
        return mutableSetOf(new EmailData()
                .setEmail(String.format("test.email%d@mail.com", i))
                .setUser(user));
    }
}
