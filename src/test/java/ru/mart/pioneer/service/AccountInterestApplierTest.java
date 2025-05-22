package ru.mart.pioneer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mart.pioneer.model.Account;
import ru.mart.pioneer.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountInterestApplierTest {
    private static final long ACCOUNT_ID = 1L;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountInterestApplier interestApplier;

    @Test
    @DisplayName("Check that apply interest multiply is correct")
    void applyInterestShouldCalculateCorrectBalance() {
        Account account = new Account();
        account.setBalance(new BigDecimal("100.00"));
        account.setInitialDeposit(new BigDecimal("100.00"));

        when(accountRepository.findByIdWithLock(eq(ACCOUNT_ID))).thenReturn(Optional.of(account));

        interestApplier.applyInterestToAccount(ACCOUNT_ID);

        assertEquals(new BigDecimal("110.00"), account.getBalance());
    }

    @Test
    @DisplayName("Check that apply interest multiply stop with maximum allowed value")
    void applyInterestShouldNotExceedMaxAllowed() {
        Account account = new Account();
        account.setBalance(new BigDecimal("200.00"));
        account.setInitialDeposit(new BigDecimal("100.00"));

        when(accountRepository.findByIdWithLock(eq(ACCOUNT_ID))).thenReturn(Optional.of(account));

        interestApplier.applyInterestToAccount(ACCOUNT_ID);

        assertEquals(new BigDecimal("207.00"), account.getBalance());
    }
}