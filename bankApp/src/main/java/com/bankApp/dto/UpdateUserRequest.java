package com.bankApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record UpdateUserRequest(
        @Size(max = 500) String name,
        @Past LocalDate dateOfBirth,
        List<@Email String> emails,
        List<@Pattern(regexp = "79\\d{9}") String> phones
) { }
