package com.bankApp.service;


import com.bankApp.entity.AppUser;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSearchSpecification {

    public static Specification<AppUser> filter(String name, String email, String phone, LocalDate dateOfBirth) {
        return Specification.where(dateOfBirthAfter(dateOfBirth))
                .and(nameStarts(name))
                .and(emailEquals(email))
                .and(phoneEquals(phone));
    }

    private static Specification<AppUser> dateOfBirthAfter(LocalDate dob) {
        return dob == null ? null :
                (root, query, cb) -> cb.greaterThan(root.get("dateOfBirth"), dob);
    }

    private static Specification<AppUser> nameStarts(String name) {
        return name == null ? null :
                (root, query, cb) -> cb.like(root.get("name"), name + "%");
    }

    private static Specification<AppUser> emailEquals(String email) {
        return email == null ? null :
                (root, query, cb) -> cb.equal(root.join("emails").get("email"), email);
    }

    private static Specification<AppUser> phoneEquals(String phone) {
        return phone == null ? null :
                (root, query, cb) -> cb.equal(root.join("phones").get("phone"), phone);
    }
}

