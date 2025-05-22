package ru.mart.pioneer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.mart.pioneer.dto.UserUpdateDto;
import ru.mart.pioneer.model.User;
import ru.mart.pioneer.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mart.pioneer.utils.TestEntityObjectsUtil.createUser;

class UserControllerTest extends AbstractControllerTest {

    private static final String TEST_USER_NAME = "Test User";
    private static final String TEST_USER_EMAIL = "test@mail.ru";

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User testUser;
    private String authToken;

    @BeforeEach
    void setUp() {
        testUser = createUser(1);
        testUser.getEmails().iterator().next().setEmail(TEST_USER_EMAIL);
        testUser.setName(TEST_USER_NAME);

        testUser = userRepository.save(testUser);

        authToken = generateTestToken(testUser);
    }

    @Test
    @WithMockUser(username = TEST_USER_NAME)
    void getUserByIdShouldReturnUser_WhenExists() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.name").value(TEST_USER_NAME))
                .andDo(print());
    }

    @Test
    void getUserByIdShouldReturnUnauthorized_WhenNoAuth() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 900L))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = TEST_USER_NAME)
    void getUserByIdShouldReturnNotFound_WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999L)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = TEST_USER_NAME)
    void searchUsersShouldReturnFilteredByName() throws Exception {
        User user2 = createUser(2);
        userRepository.save(user2);

        mockMvc.perform(get("/api/users/search")
                        .param("name", TEST_USER_NAME)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(testUser.getId()))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = TEST_USER_NAME)
    void searchUsersShouldReturnFilteredByDateOfBirth() throws Exception {
        User user2 = createUser(2);
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user2.setDateOfBirth(LocalDate.of(2000, 1, 1));
        userRepository.save(testUser);
        userRepository.save(user2);

        mockMvc.perform(get("/api/users/search")
                        .param("dateOfBirth", "02.01.1990")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value(user2.getName()))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = TEST_USER_NAME)
    void searchUsersShouldReturnBadRequest_WhenInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("dateOfBirth", "invalid-date"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = TEST_USER_NAME)
    void searchUsersShouldReturnEmptyPage_WhenNoMatches() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("email", "nonexistent@example.com")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = TEST_USER_NAME)
    void updateUserShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
        UserUpdateDto invalidDto = new UserUpdateDto();
        invalidDto.setEmails(Set.of("invalid-email"));
        invalidDto.setPhones(Set.of("79201234567"));

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = TEST_USER_NAME)
    void updateUserShouldReturnBadRequest_WhenInvalidPhone() throws Exception {

        UserUpdateDto invalidDto = new UserUpdateDto();
        invalidDto.setEmails(Set.of("valid@email.com"));
        invalidDto.setPhones(Set.of("123"));

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = TEST_USER_NAME)
    void searchUsersShouldReturnPaginatedResults() throws Exception {
        User user = createUser(2);
        userRepository.save(user);

        List<User> users = userRepository.findAll();

        // Проверяем первую страницу (1 элемент)
        mockMvc.perform(get("/api/users/search")
                        .param("page", "0")
                        .param("size", "1")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andDo(print());

        // Проверяем вторую страницу (1 элемент)
        mockMvc.perform(get("/api/users/search")
                        .param("page", "1")
                        .param("size", "1")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andDo(print());
    }
}
