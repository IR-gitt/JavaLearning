package com.bankApp.dto;

import com.bankApp.dto.UserDto;
import com.bankApp.entity.AppUser;
import org.mapstruct.Mapper;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    default UserDto toDto(AppUser user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getDateOfBirth(),
                user.getEmails().stream().map(e -> e.getEmail()).collect(Collectors.toList()),
                user.getPhones().stream().map(p -> p.getPhone()).collect(Collectors.toList()),
                user.getAccount().getBalance()
        );
    }
}
