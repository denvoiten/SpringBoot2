package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto getUserById(Long id);

    void deleteUserById(Long id);

    Collection<UserDto> findAll();
}
