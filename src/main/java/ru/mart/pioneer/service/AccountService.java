package ru.mart.pioneer.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mart.pioneer.exception.InsufficientFundsException;
import ru.mart.pioneer.model.Account;
import ru.mart.pioneer.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private static final String LOCK_ACQUIRED = "[Thread: {}] Lock acquired for transfer from {} to {}";
    private static final String LOCK_RELEASED = "[Thread: {}] Lock released for transfer from {} to {}";
    private static final String TRANSFER_START = "[Thread: {}] Starting transfer of {} from {} to {}";

    private final AccountRepository accountRepository;
    private final AccountInterestApplier interestApplier;

    private final Lock lock = new ReentrantLock();

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void applyInterest() {
        log.info("Starting interest application process");

        accountRepository.findAllIds().forEach(interestApplier::applyInterestToAccount);

        log.info("Interest application process completed");
    }

    @Retryable(retryFor = CannotAcquireLockException.class,
            maxAttempts = 10,
            backoff = @Backoff(delay = 100, maxDelay = 300))
    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        String threadName = Thread.currentThread().getName();
        log.info(TRANSFER_START, threadName, amount, fromUserId, toUserId);

        lock.lock();

        try {
            log.debug(LOCK_ACQUIRED, threadName, fromUserId, toUserId);

            Account fromAccount = accountRepository.findByUserIdWithLock(fromUserId)
                    .orElseThrow(() -> {
                        log.error("Sender account not found for user ID: {}", fromUserId);
                        return new EntityNotFoundException("Sender account not found");
                    });

            Account toAccount = accountRepository.findByUserIdWithLock(toUserId)
                    .orElseThrow(() -> {
                        log.error("Recipient account not found for user ID: {}", toUserId);
                        return new EntityNotFoundException("Recipient account not found");
                    });

            log.debug("Current balances - From: {}, To: {}",
                    fromAccount.getBalance(), toAccount.getBalance());

            if (fromAccount.getBalance().compareTo(amount) < 0) {
                log.error("Insufficient funds in account {} (balance: {}, requested: {})",
                        fromUserId, fromAccount.getBalance(), amount);
                throw new InsufficientFundsException("Insufficient funds");
            }

            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));

            accountRepository.saveAll(List.of(fromAccount, toAccount));

            log.info("Transfer completed successfully. New balances - From: {}, To: {}",
                    fromAccount.getBalance(), toAccount.getBalance());
        } finally {
            lock.unlock();
            log.debug(LOCK_RELEASED, threadName, fromUserId, toUserId);
        }
    }
}
