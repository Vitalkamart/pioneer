package ru.mart.pioneer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.mart.pioneer.AbstractIntegrationTest;
import ru.mart.pioneer.dto.UserDto;
import ru.mart.pioneer.dto.UserFilter;
import ru.mart.pioneer.dto.UserUpdateDto;
import ru.mart.pioneer.model.Account;
import ru.mart.pioneer.model.EmailData;
import ru.mart.pioneer.model.PhoneData;
import ru.mart.pioneer.model.User;
import ru.mart.pioneer.repository.EmailDataRepository;
import ru.mart.pioneer.repository.PhoneDataRepository;
import ru.mart.pioneer.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.mart.pioneer.util.CollectionUtils.mutableSetOf;
import static ru.mart.pioneer.utils.TestEntityObjectsUtil.createUser;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailDataRepository emailDataRepository;

    @Autowired
    private PhoneDataRepository phoneDataRepository;

    @Test
    @DisplayName("Check that emails are added to user")
    void shouldAddEmailsToUser() {
        Set<String> newEmails = Set.of("test1@example.com", "test2@example.com");

        User user = persistUser();

        Set<String> phones = user.getPhones().stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toSet());

        UserUpdateDto updateDto = new UserUpdateDto().setEmails(newEmails).setPhones(phones);
        UserDto result = userService.updateUser(user.getId(), updateDto);

        assertThat(result.getEmails()).containsExactlyInAnyOrderElementsOf(newEmails);

        User dbUser = userRepository.findById(user.getId()).orElseThrow();

        assertThat(dbUser.getEmails()).hasSize(2);
        dbUser.getEmails().forEach(email -> assertTrue(newEmails.contains(email.getEmail())));
    }

    @Test
    @DisplayName("Check that phones are added to user")
    void shouldAddPhonesToUser() {
        Set<String> newPhones = Set.of("79991112233", "79994445566");
        User user = persistUser();
        Set<String> emails = user.getEmails().stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toSet());

        UserUpdateDto updateDto = new UserUpdateDto().setEmails(emails).setPhones(newPhones);
        UserDto result = userService.updateUser(user.getId(), updateDto);

        assertThat(result.getPhones()).containsExactlyInAnyOrderElementsOf(newPhones);

        User dbUser = userRepository.findById(user.getId()).orElseThrow();

        assertThat(dbUser.getPhones()).hasSize(2);
        dbUser.getPhones().forEach(email -> assertTrue(newPhones.contains(email.getPhone())));
    }

    @Test
    @DisplayName("Check that emails and phone data are deleted cascade from user")
    void shouldCascadeDeleteEmailsAndPhonesWhenUserDeleted() {
        User user = persistUser();

        assertEquals(1, user.getPhones().size());
        assertEquals(1, user.getEmails().size());

        Long emailId = user.getEmails().iterator().next().getId();
        Long phoneId = user.getPhones().iterator().next().getId();

        userRepository.delete(user);

        assertThat(emailDataRepository.findById(emailId)).isEmpty();
        assertThat(phoneDataRepository.findById(phoneId)).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("nameSearchProvider")
    @DisplayName("Check that search users result is filtered by by name")
    void shouldFindUsersByNameLike(String searchTerm, int expectedCount, String[] expectedNames) {
        persistUsersForSearch();

        UserFilter filter = new UserFilter();
        filter.setName(searchTerm);

        Page<UserDto> result = userService.searchUsers(filter, PageRequest.of(0, 10));

        assertThat(result.getContent())
                .hasSize(expectedCount)
                .extracting(UserDto::getName)
                .containsExactlyInAnyOrder(expectedNames);
    }

    @ParameterizedTest
    @MethodSource("dateSearchProvider")
    @DisplayName("Check that search users result is filtered by date")
    void shouldFilterByBirthDate(LocalDate date, int expectedCount) {
        persistUsersForSearch();

        UserFilter filter = new UserFilter();
        filter.setDateOfBirth(date);

        Page<UserDto> result = userService.searchUsers(filter, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(expectedCount);
    }

    @ParameterizedTest
    @MethodSource("combinedFilterProvider")
    @DisplayName("Check that search users combine filter is correct")
    void shouldApplyCombinedFilters(String name, LocalDate date, String email, String phone, int expectedCount) {
        persistUsersForSearch();

        UserFilter filter = new UserFilter();
        filter.setName(name);
        filter.setDateOfBirth(date);
        filter.setEmail(email);
        filter.setPhone(phone);

        Page<UserDto> result = userService.searchUsers(filter, PageRequest.of(0, 10));

        assertThat(result.getContent())
                .hasSize(expectedCount)
                .doesNotHaveDuplicates();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("Check search users filter work correct with null and empty input")
    void shouldHandleEmptyNameFilters(String nameFilter) {
        persistUsersForSearch();

        UserFilter filter = new UserFilter();
        filter.setName(nameFilter);

        Page<UserDto> result = userService.searchUsers(filter, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(3);
    }

    @ParameterizedTest
    @ValueSource(strings = {"иван", "ИВАН", "иВаН"})
    @DisplayName("Check search users filter work correct with different case letters input")
    void shouldBeCaseInsensitive(String searchTerm) {
        persistUsersForSearch();

        UserFilter filter = new UserFilter();
        filter.setName(searchTerm);

        Page<UserDto> result = userService.searchUsers(filter, PageRequest.of(0, 10));

        assertThat(result.getContent())
                .hasSize(2)
                .extracting(UserDto::getName)
                .containsExactlyInAnyOrder("Иван Петров", "Петр Иванов");
    }

    @ParameterizedTest
    @MethodSource("paginationProvider")
    @DisplayName("Check search users pagination")
    void shouldHandlePaginationCorrectly(int page, int size, int expectedContentSize, long expectedTotal) {
        persistUsersForSearch();

        UserFilter filter = new UserFilter();
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));

        Page<UserDto> result = userService.searchUsers(filter, pageable);

        assertThat(result.getContent()).hasSize(expectedContentSize);
        assertThat(result.getTotalElements()).isEqualTo(expectedTotal);
    }

    private User persistUser() {
        return userRepository.save(createUser(1));
    }

    private void persistUsersForSearch() {
        User user1 = createUserWithContacts(
                "Иван Петров",
                LocalDate.of(1990, 5, 15),
                "ivan@example.com",
                "79151234567"
        );

        User user2 = createUserWithContacts(
                "Петр Иванов",
                LocalDate.of(1985, 10, 20),
                "petr@example.com",
                "79261234567"
        );

        User user3 = createUserWithContacts(
                "Алексей Сидоров",
                LocalDate.of(1995, 3, 10),
                "alex@example.com",
                "79371234567"
        );

        userRepository.saveAll(mutableSetOf(user1, user2, user3));
    }

    private User createUserWithContacts(String name, LocalDate dob, String email, String phone) {
        User user = new User()
                .setName(name)
                .setDateOfBirth(dob)
                .setPassword("password");

        EmailData emailData = new EmailData()
                .setEmail(email)
                .setUser(user);

        PhoneData phoneData = new PhoneData()
                .setPhone(phone)
                .setUser(user);

        user.setAccount(createAccount(user))
                .setEmails(mutableSetOf(emailData))
                .setPhones(mutableSetOf(phoneData));
        return userRepository.save(user);
    }

    private Account createAccount(User user) {
        return new Account()
                .setBalance(BigDecimal.valueOf(1000))
                .setInitialDeposit(BigDecimal.valueOf(1000))
                .setUser(user);
    }


    private static Stream<Arguments> nameSearchProvider() {
        return Stream.of(
                Arguments.of("Иван", 2, new String[]{"Иван Петров", "Петр Иванов"}),
                Arguments.of("Петр", 2, new String[]{"Иван Петров", "Петр Иванов"}),
                Arguments.of("Алексей", 1, new String[]{"Алексей Сидоров"}),
                Arguments.of("Сидоров", 1, new String[]{"Алексей Сидоров"}),
                Arguments.of("ов", 3, new String[]{"Иван Петров", "Петр Иванов", "Алексей Сидоров"}),
                Arguments.of("Несуществующий", 0, new String[]{})
        );
    }

    private static Stream<Arguments> dateSearchProvider() {
        return Stream.of(
                Arguments.of(LocalDate.of(1980, 1, 1), 3),
                Arguments.of(LocalDate.of(1988, 1, 1), 2),
                Arguments.of(LocalDate.of(1993, 1, 1), 1),
                Arguments.of(LocalDate.of(2000, 1, 1), 0)
        );
    }

    private static Stream<Arguments> combinedFilterProvider() {
        return Stream.of(
                Arguments.of("Иван", null, null, null, 2),
                Arguments.of(null, LocalDate.of(1990, 1, 1), null, null, 2),
                Arguments.of(null, null, "ivan@example.com", null, 1),
                Arguments.of(null, null, null, "79151234567", 1),
                Arguments.of("Иван", LocalDate.of(1990, 1, 1), null, null, 1),
                Arguments.of(null, LocalDate.of(1980, 1, 1), "ivan@example.com", "79151234567", 1),
                Arguments.of("Несуществующий", null, null, null, 0)
        );
    }

    private static Stream<Arguments> paginationProvider() {
        return Stream.of(
                Arguments.of(0, 1, 1, 3L),
                Arguments.of(0, 2, 2, 3L),
                Arguments.of(1, 2, 1, 3L),
                Arguments.of(0, 10, 3, 3L),
                Arguments.of(2, 1, 1, 3L)
        );
    }
}