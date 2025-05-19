package com.bankApp.dao;

import com.bankApp.entity.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    // Метод для поиска по номеру телефона
    Optional<PhoneData> findByPhone(String phone);

}
