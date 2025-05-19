package com.bankApp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UserDto(
        Long id,
        String name,
        LocalDate dateOfBirth,
        List<String> emails,
        List<String> phones,
        BigDecimal balance
) {}

