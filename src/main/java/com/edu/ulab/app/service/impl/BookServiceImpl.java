package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);

        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        log.info("Get book with ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found book with id: " + id));
        log.info("The book - {} was found.", book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new NotFoundException("Not found book for delete with ID: " + id);
        }
        bookRepository.deleteById(id);
        log.info("Book with ID {} has been deleted.", id);
    }

    public Collection<BookDto> getAllBooks() {
        log.info("Get all books");
        return StreamSupport.stream(bookRepository.findAll().spliterator(), false)
                .map(bookMapper::bookToBookDto)
                .toList();
    }

    @Override
    public Collection<BookDto> getBooksByUserId(Long userId) {
        log.info("Get all books by User ID: {}");
        return bookRepository.findByPersonId(userId).stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }
}
