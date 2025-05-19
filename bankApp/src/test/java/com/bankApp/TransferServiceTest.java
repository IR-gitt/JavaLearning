package com.bankApp;

import com.bankApp.dao.AccountRepository;
import com.bankApp.entity.Account;
import com.bankApp.service.TransferService;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    private final AccountRepository repo = mock(AccountRepository.class);
    private final TransferService service = new TransferService(repo);

    @Test
    void transfer_ok() {
        // ─── Arrange ──────────────────────────────────────────────────────────────
        Account from = new Account(); from.setId(1L); from.setBalance(BigDecimal.valueOf(200));
        Account to   = new Account(); to.setId(2L);   to.setBalance(BigDecimal.valueOf(50));

        when(repo.findByUserIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(repo.findByUserIdForUpdate(2L)).thenReturn(Optional.of(to));

        // ─── Act ──────────────────────────────────────────────────────────────────
        service.transfer(1L, 2L, BigDecimal.valueOf(100));

        // ─── Assert ───────────────────────────────────────────────────────────────
        assertThat(from.getBalance()).isEqualByComparingTo("100");
        assertThat(to.getBalance()).isEqualByComparingTo("150");

        // проверяем, что оба аккаунта были заблокированы (PESSIMISTIC_WRITE) в правильном порядке
        InOrder io = inOrder(repo);
        io.verify(repo).findByUserIdForUpdate(1L);
        io.verify(repo).findByUserIdForUpdate(2L);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void transfer_fail_if_not_enough_money() {
        Account from = new Account(); from.setId(1L); from.setBalance(BigDecimal.valueOf(30));
        Account to   = new Account(); to.setId(2L);   to.setBalance(BigDecimal.ZERO);

        when(repo.findByUserIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(repo.findByUserIdForUpdate(2L)).thenReturn(Optional.of(to));

        assertThatThrownBy(() ->
                service.transfer(1L, 2L, BigDecimal.valueOf(50))
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Недостаточно средств");

        assertThat(from.getBalance()).isEqualByComparingTo("30");
        assertThat(to.getBalance()).isEqualByComparingTo("0");
    }
}
