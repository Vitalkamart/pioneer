package ru.mart.pioneer.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mart.pioneer.dto.UserDto;
import ru.mart.pioneer.dto.UserFilter;
import ru.mart.pioneer.dto.UserUpdateDto;
import ru.mart.pioneer.model.EmailData;
import ru.mart.pioneer.model.PhoneData;
import ru.mart.pioneer.model.User;
import ru.mart.pioneer.repository.UserRepository;
import ru.mart.pioneer.repository.specification.UserSpecifications;
import ru.mart.pioneer.util.StringDateConverter;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        log.info("Fetching user from DB: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDto> searchUsers(UserFilter filter, Pageable pageable) {
        return userRepository.findAll(
                UserSpecifications.withNameLike(filter.getName())
                        .and(UserSpecifications.withDateAfter(filter.getDateOfBirth()))
                        .and(UserSpecifications.withExactPhone(filter.getPhone()))
                        .and(UserSpecifications.withExactEmail(filter.getEmail())),
                pageable
        ).map(this::convertToDto);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserDto updateUser(Long userId, UserUpdateDto updateDto) {
        log.info("Updating user (cache evict): {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.updateEmails(updateDto.getEmails());
        user.updatePhones(updateDto.getPhones());

        log.info("user with id: {} has been updated with emails: {} and phones: {}",
                userId, updateDto.getEmails(), updateDto.getPhones());

        return convertToDto(userRepository.saveAndFlush(user));
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .dateOfBirth(StringDateConverter.convertToString(user.getDateOfBirth()))
                .emails(user.getEmails().stream()
                        .map(EmailData::getEmail)
                        .collect(Collectors.toSet()))
                .phones(user.getPhones().stream()
                        .map(PhoneData::getPhone)
                        .collect(Collectors.toSet()))
                .balance(user.getAccount().getBalance())
                .build();
    }
}
