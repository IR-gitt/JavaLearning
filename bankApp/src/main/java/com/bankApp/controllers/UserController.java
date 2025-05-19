package com.bankApp.controllers;

import com.bankApp.config.AuthUser;
import com.bankApp.dto.UpdateUserRequest;
import com.bankApp.dto.UserDto;
import com.bankApp.dto.UserMapper;
import com.bankApp.entity.AppUser;
import com.bankApp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController @RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * PATCH /self
     * Пользователь может изменить свои email и phone
     */
    @PatchMapping("/self")
    public ResponseEntity<Void> updateSelf(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UpdateUserRequest request) {

        AppUser user = userService.getById(authUser.id());
        userService.updateUserSelf(user, request.emails(), request.phones());
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /search
     * Поиск пользователей по фильтрам:
     *   name (like), email (точно), phone (точно), dateOfBirth > ...
     * Плюс пагинация (page, size)
     */
    @GetMapping("/search")
    public Page<UserDto> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return userService.search(name, email, phone, dateOfBirth, pageable)
                .map(userMapper::toDto);
    }
}
