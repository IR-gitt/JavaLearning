package com.bankApp;

import com.bankApp.config.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc // включает поддержку MockMvc (эмуляция HTTP-запросов без запуска сервера)
public class TransferControllerIT {

    // создаём PostgreSQL контейнер (используем официальный образ)
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    // динамически подставляем настройки в Spring Boot
    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired MockMvc mockMvc;           // HTTP клиент для тестов
    @Autowired JdbcTemplate jdbc;         // прямой доступ к БД
    @Autowired JwtTokenService jwtTokenService; // генератор JWT токенов

    String token; // будет хранить JWT токен для авторизации
    Long user1Id; // ID пользователя-отправителя
    Long user2Id; // ID пользователя-получателя

    @BeforeEach
    void setUp() {
        jdbc.execute("DELETE FROM phone_data");
        jdbc.execute("DELETE FROM email_data");
        jdbc.execute("DELETE FROM account");
        jdbc.execute("DELETE FROM app_user");

        // вставка пользователей без указания ID — генерируются автоматически
        jdbc.execute("INSERT INTO app_user(name, password) VALUES ('A', 'p')");
        jdbc.execute("INSERT INTO app_user(name, password) VALUES ('B', 'p')");



        // получаем ID вставленных пользователей
        user1Id = jdbc.queryForObject("SELECT id FROM app_user WHERE name = 'A'", Long.class);
        user2Id = jdbc.queryForObject("SELECT id FROM app_user WHERE name = 'B'", Long.class);

        // создаём аккаунты с начальными балансами
        jdbc.update("INSERT INTO account(user_id, balance, initial_deposit) VALUES (?, ?, ?)",
                user1Id, new BigDecimal("100.00"), new BigDecimal("100.00"));

        jdbc.update("INSERT INTO account(user_id, balance, initial_deposit) VALUES (?, ?, ?)",
                user2Id, new BigDecimal("50.00"), new BigDecimal("50.00"));

        jdbc.update("INSERT INTO email_data(user_id, email) VALUES (?, ?)", user1Id, "a@mail.com");
        jdbc.update("INSERT INTO phone_data(user_id, phone) VALUES (?, ?)", user1Id, "+79991112233");

        // генерируем JWT токен для user1
        token = "Bearer " + jwtTokenService.generateToken(user1Id);
    }

    @Test
    void transfer_success() throws Exception {
        // выполняем HTTP-запрос на перевод 40 единиц от user1 к user2
        mockMvc.perform(post("/transfer")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "toUserId": %s,
                                  "amount": 40.00
                                }
                                """.formatted(user2Id)))
                .andExpect(status().isOk()); // ожидаем успешный HTTP 200 OK

        // проверяем финальные балансы через прямой SQL
        BigDecimal balance1 = jdbc.queryForObject("SELECT balance FROM account WHERE user_id = ?", BigDecimal.class, user1Id);
        BigDecimal balance2 = jdbc.queryForObject("SELECT balance FROM account WHERE user_id = ?", BigDecimal.class, user2Id);

        // убеждаемся, что перевод выполнен корректно
        assertThat(balance1).isEqualByComparingTo("60.00");  // 100 - 40
        assertThat(balance2).isEqualByComparingTo("90.00");  // 50 + 40
    }

    @Test
    void transfer_fail_when_not_enough_balance() throws Exception {
        // пытаемся перевести больше, чем есть
        mockMvc.perform(post("/transfer")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "toUserId": %s,
                                  "amount": 999.99
                                }
                                """.formatted(user2Id)))
                .andExpect(status().isBadRequest());
    }
}
