package com.bankApp.dao;

import com.bankApp.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    // Метод для поиска по email
    Optional<EmailData> findByEmail(String email);

}

