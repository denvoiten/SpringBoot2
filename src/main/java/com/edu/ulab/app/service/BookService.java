package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;

import java.util.Collection;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    BookDto getBookById(Long id);

    void deleteBookById(Long id);

    Collection<BookDto> getBooksByUserId(Long userId);
}
