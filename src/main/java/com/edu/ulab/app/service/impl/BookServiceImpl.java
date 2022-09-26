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
import java.util.Objects;
import java.util.Optional;
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
    public BookDto updateBook(BookDto bookDto, Long bookId) {
        if (Objects.isNull(bookDto)) {
            throw new IllegalArgumentException("Book for update is null");
        }
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);

        Optional<Book> bookForUpdate = bookRepository.findByIdForUpdate(bookId);
        if (bookForUpdate.isPresent()) {
            log.info("Book before update: {}", bookForUpdate);
            Book updatedBook = bookForUpdate.get();

            updatedBook.setTitle(book.getTitle());
            updatedBook.setAuthor(book.getAuthor());
            updatedBook.setPageCount(book.getPageCount());
            updatedBook.setUserId(book.getUserId());

            bookRepository.save(updatedBook);
            log.info("Updated book: {}", updatedBook);
            return bookMapper.bookToBookDto(updatedBook);
        }

        Book savedBook = bookRepository.save(book);
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
        return bookRepository.findAllByUserId(userId).stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }

    @Override
    public void deleteBookByUserId(Long userId) {
        log.info("Delete all books by User ID: {}");
        bookRepository.deleteByUserId(userId);
        log.info("All Books with User ID: {} has been deleted.", userId);
    }
}
