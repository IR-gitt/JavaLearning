package com.bankApp.config;

import com.bankApp.dao.AccountRepository;
import com.bankApp.dao.EmailDataRepository;
import com.bankApp.dao.PhoneDataRepository;
import com.bankApp.dao.UserRepository;
import com.bankApp.entity.Account;
import com.bankApp.entity.AppUser;
import com.bankApp.entity.EmailData;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import com.bankApp.entity.PhoneData;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;

    public DataInitializer(UserRepository userRepository, AccountRepository accountRepository,
                           EmailDataRepository emailDataRepository, PhoneDataRepository phoneDataRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.emailDataRepository = emailDataRepository;
        this.phoneDataRepository = phoneDataRepository;
    }

    @PostConstruct
    public void initData() {
        createTestUser("John Doe", "john.doe@example.com", "79123456789", LocalDate.of(1990, 5, 15), new BigDecimal("1000"));
        createTestUser("Jane Smith", "jane.smith@example.com", "79234567890", LocalDate.of(1985, 7, 20), new BigDecimal("2000"));
        createTestUser("Alice Johnson", "alice.johnson@example.com", "79345678901", LocalDate.of(1992, 3, 10), new BigDecimal("1500"));
    }

    private void createTestUser(String name, String email, String phone, LocalDate dateOfBirth, BigDecimal initialBalance) {
        // Создание пользователя
        AppUser user = new AppUser();
        user.setName(name);
        user.setDateOfBirth(dateOfBirth);
        user.setPassword("password_hash"); // Простое значение пароля для теста

        userRepository.save(user);

        // Создание аккаунта для пользователя
        Account account = new Account();
        account.setUser(user);
        account.setBalance(initialBalance);
        account.setInitialDeposit(initialBalance);

        accountRepository.save(account);

        // Создание email для пользователя
        EmailData emailData = new EmailData();
        emailData.setUser(user);
        emailData.setEmail(email);

        emailDataRepository.save(emailData);

        // Создание phone для пользователя
        PhoneData phoneData = new PhoneData();
        phoneData.setUser(user);
        phoneData.setPhone(phone);

        phoneDataRepository.save(phoneData);
    }
}
