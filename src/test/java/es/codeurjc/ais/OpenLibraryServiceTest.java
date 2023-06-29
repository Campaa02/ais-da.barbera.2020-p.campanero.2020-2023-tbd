package es.codeurjc.ais;


import es.codeurjc.ais.book.OpenLibraryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;


/**
 * OpenLibraryServiceTest  is a class that contains all the integrity tests required
 *
 * @author Pablo Campanero Arévalo
 */

@DisplayName("Integrity testing")
public class OpenLibraryServiceTest {

    private OpenLibraryService openLibraryService;


    @BeforeEach
    public void setUp() {
        openLibraryService = new OpenLibraryService();
    }



    @DisplayName("If the topic is urjc we get 0 books")
    @Test

    public void limitTest() {
        Assertions.assertEquals(0, openLibraryService.searchBooks("urjc", 15).size());


    }

    @DisplayName("Getting a non existing book by its ID throws exceptions")
    @Test
    public void recoverBookTest() {
        String id = "urjc";
        Assertions.assertThrows(HttpClientErrorException.class, () -> {
            openLibraryService.getBook(id);
        });

    }



}
