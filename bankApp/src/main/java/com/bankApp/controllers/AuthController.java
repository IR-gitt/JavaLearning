package com.bankApp.controllers;

import com.bankApp.config.AuthUser;
import com.bankApp.config.JwtTokenService;
import com.bankApp.dto.AuthRequest;
import com.bankApp.dto.AuthResponse;
import com.bankApp.service.AuthUserService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthUserService authUserService;
        private final JwtTokenService jwtTokenService;

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
            AuthUser user = authUserService.authenticate(request.login(), request.password());
            String token = jwtTokenService.generateToken(user.id());
            return ResponseEntity.ok(new AuthResponse(token));
        }
    }
