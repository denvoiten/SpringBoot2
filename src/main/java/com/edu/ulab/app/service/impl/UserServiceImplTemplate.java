package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.PersonRowMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.edu.ulab.app.web.constant.SQLQueryUserConstant.*;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;

    private final UserMapper userMapper;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate,
                                   UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (Objects.isNull(userDto)) {
            throw new IllegalArgumentException("User for save is null");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Saved user: {}", userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        if (Objects.isNull(userDto)) {
            throw new IllegalArgumentException("User for update is null");
        }

        if (jdbcTemplate.update(
                UPDATE_SQL,
                userDto.getFullName(), userDto.getTitle(), userDto.getAge(), userId) != 0) {
            log.info("Updated user: {}", userDto);
            return userDto;
        } else {
            throw new NotFoundException(String.format("User with ID %s not found", userId));
        }
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Get User with ID: {}", id);
        Person user;
        try {
            user = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, new PersonRowMapper(), id);
        } catch (Exception e) {
            throw new NotFoundException(String.format("User with id %s not found", id));
        }
        log.info("The user - {} was found.", user);
        return userMapper.personToUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        if (jdbcTemplate.update(DELETE_BY_ID_SQL, id) != 0) {
            log.info("User with ID {} has been deleted", id);
        } else {
            throw new NotFoundException(String.format("User with id %s not found", id));
        }
    }

    @Override
    public Collection<UserDto> findAll() {
        List<Person> users = jdbcTemplate.query(SELECT_ALL_SQL,
                new BeanPropertyRowMapper(Person.class));
        return users.stream()
                .map(userMapper::personToUserDto)
                .toList();
    }
}
