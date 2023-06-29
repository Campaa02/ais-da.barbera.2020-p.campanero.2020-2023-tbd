package es.codeurjc.ais;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import es.codeurjc.ais.book.Book;
import es.codeurjc.ais.book.BookDetail;
import es.codeurjc.ais.book.BookService;
import es.codeurjc.ais.book.OpenLibraryService;
import es.codeurjc.ais.notification.NotificationService;

@DisplayName("The book service")
public class BookServiceUnitTests {

    @Test
    @DisplayName("should return books and display a notification when searching by subject")
    public void testFindAll() {
        // Arrange        
        NotificationService notificationService = mock(NotificationService.class); 
        OpenLibraryService openLibraryService = mock(OpenLibraryService.class);
        when(openLibraryService.searchBooks("magic", 10)).thenReturn(List.of(
            new OpenLibraryService.BookData("The Book Of Magic", "TBOM/TBOM/TBOM", "magic", new Integer[] {1}, new String[] {"magic"}),
            new OpenLibraryService.BookData("The Book Of Magic 2", "TBOM2/TBOM2/TBOM2", "magic", new Integer[] {1}, new String[] {"magic"})
        ));
        BookService bookService = new BookService(openLibraryService, notificationService); // SUT
        // Act
        List<Book> books = bookService.findAll("magic");
        // Assert
        verify(notificationService, times(1)).info("The books have been loaded: 2 books | query: magic");
        Assertions.assertNotNull(books);
        Assertions.assertEquals(2, books.size());
    }

    @Test
    @DisplayName("should return no books and display an error notification when searching by inexistent id")
    public void testFindByIDWithBadID() {
        // Arrange
        NotificationService notificationService = new NotificationService(); 
        OpenLibraryService openLibraryService = mock(OpenLibraryService.class);
        when(openLibraryService.getBook("inexistentId")).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        BookService bookService = new BookService(openLibraryService, notificationService); // SUT
        // Act
        Optional<BookDetail> bookDetail = bookService.findById("inexistentId");
        // Assert
        Assertions.assertFalse(bookDetail.isPresent());
    }
}
