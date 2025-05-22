package ru.mart.pioneer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mart.pioneer.exception.BusinessException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountService accountService;

    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId.equals(toUserId)) {
            throw new BusinessException("Cannot transfer to yourself");
        }
        accountService.transfer(fromUserId, toUserId, amount);
    }
}
