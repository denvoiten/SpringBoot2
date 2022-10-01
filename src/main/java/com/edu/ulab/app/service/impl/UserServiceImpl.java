package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (Objects.isNull(userDto)
                || Strings.isBlank(userDto.getFullName())
                || Strings.isBlank(userDto.getTitle())
                || userDto.getAge() <= 0) {
            throw new IllegalArgumentException(
                    String.format("Not all fields for the user are filled in. Full Name: %s, Title: %s, Age: %s",
                            userDto.getFullName(), userDto.getTitle(), userDto.getAge()));
        }
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);

        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);

        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        if (Objects.isNull(userDto)) {
            throw new IllegalArgumentException("User for update is null");
        }
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);

        Optional<Person> userForUpdate = userRepository.findByIdForUpdate(userId);
        if (userForUpdate.isPresent()) {
            log.info("User before update: {}", userForUpdate);
            Person updatedUser = userForUpdate.get();

            updatedUser.setFullName(user.getFullName());
            updatedUser.setTitle(user.getTitle());
            updatedUser.setAge(user.getAge());

            userRepository.save(updatedUser);
            log.info("Updated user: {}", updatedUser);
            return userMapper.personToUserDto(updatedUser);
        }

        Person savedUser = userRepository.save(user);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Get User with ID: {}", id);
        Person user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found user with ID: " + id));
        log.info("The user - {} was found.", user);
        return userMapper.personToUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Not found user for delete with ID: " + id);
        }
        userRepository.deleteById(id);
        log.info("User with ID {} has been deleted", id);
    }

    @Override
    public Collection<UserDto> findAll() {
        log.info("Get all users");
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(userMapper::personToUserDto)
                .toList();
    }

}
