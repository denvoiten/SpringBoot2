package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImplTemplate userService;
    private final BookServiceImplTemplate bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImplTemplate userService,
                          BookServiceImplTemplate bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        if (Objects.isNull(userBookRequest.getUserRequest()) || Objects.isNull(userBookRequest.getBookRequests())) {
            throw new IllegalArgumentException(String.format("UserRequest is %s, BookRequest is %s",
                    userBookRequest.getUserRequest(), userBookRequest.getBookRequests()));
        }
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("Mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book IDs: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse updateUser(UserBookRequest userBookRequest, Long userId) {
        if (Objects.isNull(userBookRequest.getUserRequest()) || Objects.isNull(userBookRequest.getBookRequests())) {
            throw new IllegalArgumentException(String.format("UserRequest is %s, BookRequest is %s",
                    userBookRequest.getUserRequest(), userBookRequest.getBookRequests()));
        }
        log.info("Got user book update request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto updatedUser = userService.updateUser(userDto, userId);
        log.info("Updated user: {}", updatedUser);

        List<Long> addedBooks = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(userId))
                .peek(mappedBookDto -> log.info("Mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .filter(Objects::nonNull)
                .map(BookDto::getId)
                .toList();
        log.info("IDs of added books: {}", addedBooks);

        List<Long> bookIdList = bookService.getBooksByUserId(userId)
                .stream()
                .map(BookDto::getId)
                .toList();
        log.info("Collected book IDs: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(userId)
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        UserDto userDto = userService.getUserById(userId);
        if (Objects.isNull(userDto)) {
            throw new NotFoundException("User with id: " + userId + " not found");
        }
        log.info("Got userDto: {}", userDto);
        List<Long> bookIdList = bookService.getBooksByUserId(userId)
                .stream()
                .map(BookDto::getId)
                .toList();
        log.info("Collected book IDs: {}", bookIdList);
        return UserBookResponse.builder()
                .userId(userDto.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public List<UserBookResponse> getAll() {
        Collection<UserDto> allUsers = userService.findAll();
        log.info("Get all users: {}", allUsers);
        return allUsers.stream()
                .filter(Objects::nonNull)
                .map(userDto -> UserBookResponse
                        .builder()
                        .userId(userDto.getId())
                        .booksIdList(
                                bookService.getBooksByUserId(userDto.getId())
                                        .stream()
                                        .map(BookDto::getId)
                                        .toList())
                        .build())
                .toList();
    }

    public void deleteUserWithBooks(Long userId) {
        bookService.getBooksByUserId(userId)
                .forEach(bookDto -> bookService.deleteBookById(bookDto.getId()));
        userService.deleteUserById(userId);
        log.info("Deleted user with ID: {}", userId);
    }
}

