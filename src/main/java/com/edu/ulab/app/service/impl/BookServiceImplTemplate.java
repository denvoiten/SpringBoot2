package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.BookRowMapper;
import com.edu.ulab.app.service.BookService;
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

import static com.edu.ulab.app.web.constant.SQLQueryBookConstant.*;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;
    private final BookMapper bookMapper;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate,
                                   BookMapper bookMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        if (Objects.isNull(bookDto)) {
            throw new IllegalArgumentException("Book for save is null");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getUserId());
                    return ps;
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Saved book: {}", bookDto);
        return bookDto;
    }

    @Override
    public BookDto getBookById(Long id) {
        log.info("Get Book with ID: {}", id);
        Book book = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, new BookRowMapper(), id);
        log.info("The book - {} was found.", book);
        if (Objects.isNull(book)) {
            throw new NotFoundException(String.format("Book with id %s not found", id));
        }
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        if (jdbcTemplate.update(DELETE_BY_ID_SQL, id) != 0) {
            log.info("Book with ID {} has been deleted", id);
        } else {
            throw new NotFoundException(String.format("Book with id %s not found", id));
        }
    }

    public void deleteBookByUserId(Long userId) {
        if (jdbcTemplate.update(DELETE_BY_USER_ID_SQL, userId) != 0) {
            log.info("Book with ID {} has been deleted", userId);
        } else {
            throw new NotFoundException(String.format("Book with userId %s not found", userId));
        }
    }

    @Override
    public Collection<BookDto> getBooksByUserId(Long userId) {
        List<Book> books = jdbcTemplate.query(SELECT_BY_USER_ID_SQL,
                new BeanPropertyRowMapper(Book.class), userId);
        return books.stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }
}
