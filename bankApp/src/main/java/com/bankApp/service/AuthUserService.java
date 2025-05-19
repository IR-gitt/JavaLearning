package com.bankApp.service;

import com.bankApp.config.AuthUser;
import com.bankApp.dao.UserRepository;
import com.bankApp.entity.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserService {

        private final UserRepository userRepo;

        public AuthUser authenticate(String login, String password) {
            log.info("Попытка входа: login={}", login);

            Optional<AppUser> opt = userRepo.findByEmails_Email(login);
            if (opt.isEmpty()) {
                opt = userRepo.findByPhones_Phone(login);
            }

            AppUser user = opt.orElseThrow(() -> {
                log.warn("Пользователь не найден: {}", login);
                return new UsernameNotFoundException("User not found");
            });

            if (!Objects.equals(user.getPassword(), password)) {
                log.warn("Неверный пароль для login={}", login);
                throw new BadCredentialsException("Неверный пароль");
            }

            log.info("Успешный вход: userId={}", user.getId());
            return new AuthUser(user.getId(), login, user.getPassword());
        }


    public AuthUser loadById(Long id) {
        AppUser user = userRepo.findById(id).orElseThrow();
        String login = !user.getEmails().isEmpty()
                ? user.getEmails().get(0).getEmail()
                : user.getPhones().get(0).getPhone();

        return new AuthUser(user.getId(), login, user.getPassword());
    }
}
