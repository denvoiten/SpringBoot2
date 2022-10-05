package com.edu.ulab.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {
        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление пользователя")
    public void updateUser_Test() {
        Long userId = 1L;

        Person personFromDB = new Person();
        personFromDB.setId(userId);
        personFromDB.setFullName("old user name");
        personFromDB.setAge(11);
        personFromDB.setTitle("test user title");

        UserDto result = new UserDto();
        result.setId(userId);
        result.setAge(11);
        result.setFullName("old user name");
        result.setTitle("test user title");

        UserDto userDtoForUpdate = new UserDto();
        userDtoForUpdate.setId(userId);
        userDtoForUpdate.setAge(22);
        userDtoForUpdate.setFullName("new user name");
        userDtoForUpdate.setTitle("new user title");

        Person personForUpdate = new Person();
        personForUpdate.setId(userId);
        personForUpdate.setFullName("new user name");
        personForUpdate.setAge(22);
        personForUpdate.setTitle("new user title");

        Person savedUpdatePerson = new Person();
        savedUpdatePerson.setId(userId);
        savedUpdatePerson.setFullName("new user name");
        savedUpdatePerson.setAge(22);
        savedUpdatePerson.setTitle("new user title");

        UserDto userDtoResult = new UserDto();
        userDtoResult.setId(userId);
        userDtoResult.setAge(22);
        userDtoResult.setFullName("new user name");
        userDtoResult.setTitle("new user title");

        when(userMapper.userDtoToPerson(userDtoForUpdate)).thenReturn(personForUpdate);
        when(userRepository.findById(userId)).thenReturn(Optional.of(personFromDB));
        when(userRepository.save(personForUpdate)).thenReturn(personForUpdate);
        when(userMapper.personToUserDto(personForUpdate)).thenReturn(userDtoResult);

        UserDto userUpdateDtoResult = userService.updateUser(userDtoForUpdate, userId);
        assertEquals("new user name", userUpdateDtoResult.getFullName());
        assertEquals("new user title", userUpdateDtoResult.getTitle());
        assertEquals(22, userUpdateDtoResult.getAge());
    }

    @Test
    @DisplayName("Получение пользователя по ID")
    void getPersonByID_Test() {
        Long userId = 1L;

        Person person = new Person();
        person.setId(1L);
        person.setAge(11);
        person.setFullName("test user name");
        person.setTitle("test user title");

        UserDto resultDto = new UserDto();
        resultDto.setId(1L);
        resultDto.setAge(11);
        resultDto.setFullName("test user name");
        resultDto.setTitle("test user title");

        when(userRepository.findById(userId)).thenReturn(Optional.of(person));
        when(userMapper.personToUserDto(person)).thenReturn(resultDto);

        UserDto userDtoResult = userService.getUserById(userId);
        assertEquals(1L, userDtoResult.getId());
        assertEquals("test user name", userDtoResult.getFullName());
        assertEquals("test user title", userDtoResult.getTitle());
        assertEquals(11, userDtoResult.getAge());
    }

    @Test
    @DisplayName("Удаление пользователя по ID")
    void deleteUserByID_Test() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        userRepository.deleteById(userId);
        verify(userRepository, times(1)).deleteById(eq(userId));
    }

    @Test
    @DisplayName("Попытка удалить несуществующего пользователя. Будет брошено исключение.")
    public void deleteUserButNotExist_Test() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUserById(1L));
    }

    @Test
    @DisplayName("Попытка получить несуществующего пользователя. Будет брошено исключение.")
    public void getUserButNotExist_Test() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }
}
