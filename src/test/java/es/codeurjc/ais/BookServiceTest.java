package es.codeurjc.ais;


import es.codeurjc.ais.book.Book;
import es.codeurjc.ais.book.BookDetail;
import es.codeurjc.ais.book.BookService;
import es.codeurjc.ais.book.OpenLibraryService;
import es.codeurjc.ais.notification.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * BookServiceTest is a class that contains all the unitary tests required
 *
 * @author Pablo Campanero Arévalo
 */

@DisplayName("Unit testing")
public class BookServiceTest {

    private OpenLibraryService openLibraryService;
    private NotificationService notificationService;
    private BookService bookService;


    @BeforeEach
    public void setUp() {
        openLibraryService = mock(OpenLibraryService.class);
        notificationService = mock(NotificationService.class);
        bookService = new BookService(openLibraryService, notificationService);

    }

    @DisplayName("Returns an empty collection of books with urjc subject")
    @Test
    public void notUrjcBooksTest() {
        String urjc = "urjc";
        String message = "The books have been loaded: 0 books | query: urjc";
        when(openLibraryService.searchBooks(urjc, 10)).thenReturn(Collections.emptyList());
        List<Book> encontrados = bookService.findAll(urjc);
        Assertions.assertEquals(0,encontrados.size());
        Assertions.assertEquals(Collections.emptyList(),encontrados);
        verify(openLibraryService).searchBooks(urjc,10);
        verify(notificationService).info(message);

    }

    @DisplayName("When we recover a book using BookService by its ID it must be returned")
    @Test
    public void existentBookServiceTest() {
        Integer[] covers = new Integer[3];
        String[] subjects = {"a","b"};
        String message = "The book has been loaded: test | id: OL8479867W";
        BookDetail expected = new BookDetail();
        expected.setTitle("test");
        expected.setId("OL8479867W");
        expected.setDescription("this is a description test");
        expected.setSubjects(subjects);
        expected.setImageUrl("https://covers.openlibrary.org/b/id/null-M.jpg");
        when(openLibraryService.getBook("OL8479867W")).thenReturn(new OpenLibraryService.BookData("test","ASZ254G/00434ER1/OL8479867W","this is a description test", covers,subjects));
        Optional<BookDetail> optional = bookService.findById("OL8479867W");
        Assertions.assertEquals(optional.get().getTitle(), expected.getTitle());
        Assertions.assertEquals(optional.get().getId(), expected.getId());
        Assertions.assertEquals(optional.get().getDescription(), expected.getDescription());
        Assertions.assertArrayEquals(optional.get().getSubjects(), expected.getSubjects());
        Assertions.assertEquals(optional.get().getImageUrl(), expected.getImageUrl());
        Assertions.assertEquals(optional.get().getReviews(), expected.getReviews());
        verify(openLibraryService).getBook("OL8479867W");
        verify(notificationService).info(message);


    }

}
