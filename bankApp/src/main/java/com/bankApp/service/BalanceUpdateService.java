package com.bankApp.service;

import com.bankApp.entity.Account;
import com.bankApp.dao.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceUpdateService {

    private final AccountRepository accountRepository;

    /**
     * Раз в 30 секунд увеличивает баланс каждого аккаунта на 10%,
     * но не больше чем 207% от начального депозита.
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void applyInterest() {
        log.info("Запуск начисления процентов всем аккаунтам");

        List<Account> accounts = accountRepository.findAll();

        for (Account acc : accounts) {
            BigDecimal max = acc.getInitialDeposit().multiply(BigDecimal.valueOf(2.07));
            BigDecimal current = acc.getBalance();

            if (current.compareTo(max) < 0) {
                BigDecimal increased = current.multiply(BigDecimal.valueOf(1.10));
                if (increased.compareTo(max) > 0) {
                    increased = max;
                }

                log.debug("Повышение баланса: userId={}, {} → {}", acc.getUser().getId(), current, increased);
                acc.setBalance(increased);
            }
        }
    }
}
