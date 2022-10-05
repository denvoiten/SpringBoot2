package com.edu.ulab.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Получение книги по ID")
    void getBookById_Test() {
        Person person = new Person();
        person.setId(1L);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(result);

        BookDto bookDtoResult = bookService.getBookById(1L);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals(1L, bookDtoResult.getUserId());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals(1000, bookDtoResult.getPageCount());
    }

    @Test
    @DisplayName("Получение книги по User ID")
    void getBooksByUserId_Test() {
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto bookDto2 = new BookDto();
        bookDto.setId(2L);
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author2");
        bookDto.setTitle("test title2");
        bookDto.setPageCount(1000);

        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book book2 = new Book();
        book.setId(2L);
        book.setPageCount(1000);
        book.setTitle("test title2");
        book.setAuthor("test author2");
        book.setPerson(person);

        List<BookDto> bookDtos = List.of(bookDto, bookDto2);

        when(bookRepository.findByPersonId(1L)).thenReturn(List.of(book, book2));
        when(bookMapper.bookToBookDto(book)).thenReturn(bookDto);
        when(bookMapper.bookToBookDto(book2)).thenReturn(bookDto2);

        Collection<BookDto> booksByUserId = bookService.getBooksByUserId(1L);
        assertTrue(booksByUserId.containsAll(bookDtos));
        assertEquals(2, booksByUserId.size());
    }

    @Test
    @DisplayName("Удаление книги по ID")
    void deleteBookByID_Test() {
        Long bookId = 1L;

        when(bookRepository.existsById(bookId)).thenReturn(true);

        bookRepository.deleteById(bookId);
        verify(bookRepository, times(1)).deleteById(eq(bookId));
    }

    @Test
    @DisplayName("Попытка удалить несуществующую книгу. Будет брошено исключение.")
    public void deleteBookButNotExist_Test() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.deleteBookById(1L));
    }

    @Test
    @DisplayName("Попытка получить несуществующую книгу. Будет брошено исключение.")
    public void getBookButNotExist_Test() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.getBookById(1L));
    }
}
