package com.bankApp.service;

import com.bankApp.dao.EmailDataRepository;
import com.bankApp.dao.PhoneDataRepository;
import com.bankApp.dao.UserRepository;
import com.bankApp.entity.AppUser;
import com.bankApp.entity.EmailData;
import com.bankApp.entity.PhoneData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис работы с пользователями:
 *   • поиск с фильтрами и пагинацией;
 *   • изменение собственных email/phone;
 *   • кэширование (Redis) c инвалидацией при апдейте.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final EmailDataRepository emailRepo;
    private final PhoneDataRepository phoneRepo;

    /* =======================================================================
       READ: получить пользователя по id (результат кэшируется)               */

    @Cacheable(value = "users", key = "#id")
    public AppUser getById(Long id) {
        log.debug("Загрузка пользователя по ID: {}", id);
        return userRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id={} не найден", id);
                    return new IllegalArgumentException("Пользователь не найден");
                });
    }

    /* =======================================================================
       UPDATE: пользователь меняет СВОИ email/phone-списки                    */

    @CacheEvict(value = "users", key = "#user.id")
    @Transactional
    public void updateUserSelf(AppUser user,
                               List<String> rawEmails,
                               List<String> rawPhones) {
        Long userId = user.getId();
        log.info("Обновление email/phone для userId={}", userId);

        List<String> newEmails = Optional.ofNullable(rawEmails).orElse(List.of());
        List<String> newPhones = Optional.ofNullable(rawPhones).orElse(List.of());

        if (newEmails.isEmpty() || newPhones.isEmpty()) {
            log.warn("Попытка обновления с пустыми списками: emails={}, phones={}", newEmails, newPhones);
            throw new IllegalArgumentException("Нужен минимум один email и один телефон");
        }

        ensureNoDuplicates(newEmails, "email");
        ensureNoDuplicates(newPhones, "phone");

        checkEmailUniqueness(userId, newEmails);
        checkPhoneUniqueness(userId, newPhones);

        syncEmails(user, newEmails);
        syncPhones(user, newPhones);

        log.info("Контакты пользователя обновлены: userId={}, emails={}, phones={}",
                userId, newEmails, newPhones);
    }

    /* =======================================================================
       SEARCH: фильтрация + пагинация                                          */

    public Page<AppUser> search(String name, String email, String phone,
                                LocalDate dateOfBirthAfter, Pageable pageable) {
        log.debug("Поиск пользователей: name={}, email={}, phone={}, dobAfter={}, page={}",
                name, email, phone, dateOfBirthAfter, pageable);
        Specification<AppUser> spec =
                UserSearchSpecification.filter(name, email, phone, dateOfBirthAfter);
        return userRepo.findAll(spec, pageable);
    }

    /* -----------------------------------------------------------------------
       ==============  PRIVATE HELPERS  ===================================== */

    private static void ensureNoDuplicates(List<String> list, String field) {
        Set<String> uniq = new HashSet<>(list);
        if (uniq.size() != list.size()) {
            log.warn("Обнаружены дубликаты в списке {}: {}", field, list);
            throw new IllegalArgumentException("В списке " + field + " есть дубликаты");
        }
    }

    private void checkEmailUniqueness(Long userId, List<String> emails) {
        for (String e : emails) {
            emailRepo.findByEmail(e).ifPresent(ed -> {
                if (!ed.getUser().getId().equals(userId)) {
                    log.warn("Email {} уже занят другим пользователем", e);
                    throw new IllegalArgumentException("Email «" + e + "» уже занят другим пользователем");
                }
            });
        }
    }

    private void checkPhoneUniqueness(Long userId, List<String> phones) {
        for (String p : phones) {
            phoneRepo.findByPhone(p).ifPresent(pd -> {
                if (!pd.getUser().getId().equals(userId)) {
                    log.warn("Телефон {} уже занят другим пользователем", p);
                    throw new IllegalArgumentException("Телефон «" + p + "» уже занят другим пользователем");
                }
            });
        }
    }

    private void syncEmails(AppUser user, List<String> desired) {
        Set<String> current = user.getEmails().stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toSet());

        log.debug("syncEmails: userId={}, current={}, desired={}", user.getId(), current, desired);

        user.getEmails().removeIf(ed -> !desired.contains(ed.getEmail()));

        desired.stream()
                .filter(e -> !current.contains(e))
                .forEach(e -> {
                    EmailData ed = new EmailData();
                    ed.setUser(user);
                    ed.setEmail(e);
                    user.getEmails().add(ed);
                });
    }

    private void syncPhones(AppUser user, List<String> desired) {
        Set<String> current = user.getPhones().stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toSet());

        log.debug("syncPhones: userId={}, current={}, desired={}", user.getId(), current, desired);

        user.getPhones().removeIf(pd -> !desired.contains(pd.getPhone()));

        desired.stream()
                .filter(p -> !current.contains(p))
                .forEach(p -> {
                    PhoneData pd = new PhoneData();
                    pd.setUser(user);
                    pd.setPhone(p);
                    user.getPhones().add(pd);
                });
    }
}

