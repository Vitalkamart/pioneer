package ru.mart.pioneer.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mart.pioneer.exception.InsufficientFundsException;
import ru.mart.pioneer.model.Account;
import ru.mart.pioneer.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("Check that EntityNotFoundException is thrown when sender is not found")
    void transferShouldThrowWhenSenderNotFound() {
        when(accountRepository.findByUserIdWithLock(1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                accountService.transfer(1L, 2L, new BigDecimal("100")));
    }

    @Test
    @DisplayName("Check that EntityNotFoundException is thrown when recipient is not found")
    void transferShouldThrowWhenRecipientNotFound() {
        when(accountRepository.findByUserIdWithLock(2L))
                .thenReturn(Optional.empty());
        when(accountRepository.findByUserIdWithLock(1L))
                .thenReturn(Optional.of(new Account()));

        assertThrows(EntityNotFoundException.class, () ->
                accountService.transfer(1L, 2L, new BigDecimal("100")));
    }

    @Test
    void transferShouldThrowWhenInsufficientFunds() {
        Account fromAccount = new Account();
        fromAccount.setBalance(new BigDecimal("50.00"));

        Account toAccount = new Account();

        when(accountRepository.findByUserIdWithLock(1L))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(2L))
                .thenReturn(Optional.of(toAccount));

        assertThrows(InsufficientFundsException.class, () ->
                accountService.transfer(1L, 2L, new BigDecimal("100")));
    }
}
