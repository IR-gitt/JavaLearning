package com.bankApp.dto;

import java.math.BigDecimal;

public record TransferRequest(Long toUserId, BigDecimal amount) {}

