package es.codeurjc.ais;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.codeurjc.ais.book.OpenLibraryService;

@DisplayName("The OpenLibrary service")
public class OpenLibraryServiceIntegrationTests {

    private static OpenLibraryService openLibraryService;

    @BeforeEach
    public void setUp() {
        openLibraryService = new OpenLibraryService();
    }

    @Test
    @DisplayName("should return only books with the specified topic, and exactly fifteen of each")
    public void testSearchFifteenBooksByTopic() {
        // Act
        String[] queryTopics = { "drama", "fantasy", "magic" };
        for (String queryTopic : queryTopics) {
            List<OpenLibraryService.BookData> books = openLibraryService.searchBooks(queryTopic, 15);
            // Assert
            Assertions.assertNotNull(books);
            Assertions.assertEquals(15, books.size());
            Assertions.assertTrue(
                    books.stream().allMatch(b -> Arrays.stream(b.subject).anyMatch(s -> s.equalsIgnoreCase(queryTopic))));
        }
    }


    @Test
    @DisplayName("should return \"The Name of the Wind\" when searching by its id")
    public void testSearchBookById() {
        // Act
        OpenLibraryService.BookData book = Assertions.assertDoesNotThrow(() -> openLibraryService.getBook("OL8479867W"));
        Assertions.assertNotNull(book);
        Assertions.assertEquals("The Name of the Wind", book.title);
    }
}
