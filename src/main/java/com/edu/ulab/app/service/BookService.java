package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;

import java.util.Collection;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    BookDto updateBook(BookDto bookDto, Long bookId);

    BookDto getBookById(Long id);

    void deleteBookById(Long id);

    void deleteBookByUserId(Long userId);

    Collection<BookDto> getBooksByUserId(Long userId);
}
