package com.bankApp.service;

import com.bankApp.dao.AccountRepository;
import com.bankApp.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
@Slf4j
@Service @RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;

    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        log.info("Запрос на перевод: от {} к {} сумма {}", fromUserId, toUserId, amount);

        if (fromUserId.equals(toUserId)) {
            log.warn("Попытка перевести самому себе: userId={}", fromUserId);
            throw new IllegalArgumentException("Нельзя переводить самому себе");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Некорректная сумма перевода: {}", amount);
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        Account from = accountRepository.findByUserIdForUpdate(fromUserId)
                .orElseThrow(() -> {
                    log.error("Отправитель не найден: {}", fromUserId);
                    return new IllegalArgumentException("Отправитель не найден");
                });

        Account to = accountRepository.findByUserIdForUpdate(toUserId)
                .orElseThrow(() -> {
                    log.error("Получатель не найден: {}", toUserId);
                    return new IllegalArgumentException("Получатель не найден");
                });

        if (from.getBalance().compareTo(amount) < 0) {
            log.warn("Недостаточно средств: userId={}, баланс={}, сумма={}", fromUserId, from.getBalance(), amount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недостаточно средств");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        log.info("Трансфер завершен: {} → {} сумма {}", fromUserId, toUserId, amount);
    }
}