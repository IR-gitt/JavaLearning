package com.bankApp.dao;

import com.bankApp.entity.Account;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * DAO-слой для работы с таблицей <code>account</code>.
 * <p>
 *  ▸ {@link #findAllForUpdate()} – используется плановым заданием,
 *  начисляющим проценты раз в 30 секунд (pessimistic write-lock на все строки).
 *  ▸ {@link #findByUserIdForUpdate(Long)} – для потокобезопасного
 *    перевода средств <i>от</i>/<i>к</i> конкретному пользователю.
 *  ▸ {@link #findByUserId(Long)} – обычное чтение без блокировки.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /* --- массовое обновление ---------------------------------------------------------------- */

    /** Лочим все строки accounts для последующего инкремента баланса. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a")
    List<Account> findAllForUpdate();

    /* --- операции над одним пользователем --------------------------------------------------- */

    /** Ищем аккаунт пользователя и сразу ставим PESSIMISTIC_WRITE-lock. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.user.id = :userId")
    Optional<Account> findByUserIdForUpdate(@Param("userId") Long userId);

    /** То же самое, но без блокировки (например, для READ-операций). */
    Optional<Account> findByUserId(Long userId);
}
