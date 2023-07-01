package es.codeurjc.ais;

import es.codeurjc.ais.book.BookDetail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;


@DisplayName("The Sanity Test")
public class SanityTest {



    @Test
    @DisplayName("Checks that The Two Towers has a reduced description")
    public void SanityTest() throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
        String url = System.getProperty("host");
        if (url == null || url.isEmpty()){
            Assertions.fail("La variable de sistema host está vacía. Debe pasar la variable de sistema host.");
        }

        Boolean testSuccessful = false;
        Integer tries = 1;
        while(testSuccessful == false){
            try{
                final String bookId = "OL27479W";
                BookDetail bookDescription = given().
                        contentType(JSON)
                        .when()
                        .get(url+"/api/books/{bookId}",bookId)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.OK.value())
                        .extract().jsonPath().getObject("", BookDetail.class);
                Assertions.assertNotNull(bookDescription);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(953, bookDescription.getDescription().length())
                );
                testSuccessful = true;
            }catch (AssertionError e){
                if (tries > 10){
                    throw e;
                }
                tries ++;
                TimeUnit.SECONDS.sleep(10);
            }
        }

    }

}
