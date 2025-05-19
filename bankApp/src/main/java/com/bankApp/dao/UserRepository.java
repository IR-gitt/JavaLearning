package com.bankApp.dao;

import com.bankApp.entity.AppUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {

    Optional<AppUser> findByEmails_Email(String email);
    Optional<AppUser> findByPhones_Phone(String phone);

    @EntityGraph(attributePaths = {"emails", "phones"}) // добавьте нужные коллекции
    Optional<AppUser> findById(Long id);
}


