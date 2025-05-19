package com.bankApp.controllers;

import com.bankApp.config.AuthUser;
import com.bankApp.dto.TransferRequest;
import com.bankApp.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<Void> transfer(
            @AuthenticationPrincipal AuthUser auth,
            @RequestBody TransferRequest dto) {
        transferService.transfer(auth.id(), dto.toUserId(), dto.amount());
        return ResponseEntity.ok().build();
    }
}
