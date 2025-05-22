package ru.mart.pioneer.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.mart.pioneer.model.Account;
import ru.mart.pioneer.repository.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountInterestApplier {
    private final AccountRepository accountRepository;

    @Retryable(retryFor = CannotAcquireLockException.class,
            maxAttempts = 10,
            backoff = @Backoff(delay = 100, maxDelay = 300))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void applyInterestToAccount(long accountId) {
        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Account with id: '%d' was not found", accountId)));

        BigDecimal maxAllowed = account.getInitialDeposit()
                .multiply(new BigDecimal("2.07"));

        BigDecimal newBalance = account.getBalance()
                .multiply(new BigDecimal("1.1"));

        if (newBalance.compareTo(maxAllowed) > 0) {
            log.debug("Capping interest for account {} (max allowed: {})", account.getId(), maxAllowed);
            newBalance = maxAllowed;
        }

        BigDecimal oldBalance = account.getBalance();

        account.setBalance(newBalance.setScale(2, RoundingMode.FLOOR));

        log.debug("Applied interest to account {}. Old balance: {}, New balance: {}",
                account.getId(), oldBalance, account.getBalance());
    }
}
