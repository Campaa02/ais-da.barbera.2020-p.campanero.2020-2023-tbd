package es.codeurjc.ais;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import es.codeurjc.ais.book.Book;
import es.codeurjc.ais.book.BookDetail;
import es.codeurjc.ais.review.Review;
import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("The REST API")
public class RestAssuredE2ETests {    
    
    @LocalServerPort private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = this.port;  
    }

    @Test
    @DisplayName("should return at most 10 books after searching by topic 'drama'")
    public void testSearchByTopic() {
        given()
            .queryParam("topic", "drama")
        .when()
            .get("/api/books/")
        .then()
            .assertThat()
            .statusCode(200)
            .and()
            .body("size()", is(lessThanOrEqualTo(10)));        
    }

    @Test
    @DisplayName("should be able to create a new review")
    public void testCreateReview() {
        Book book = given()
            .queryParam("topic", "fantasy")
        .when()
            .get("/api/books/")
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("size()", is(greaterThan(1)))
            .extract().jsonPath().getObject("[0]", Book.class);
        
        Assertions.assertNotNull(book);
        
        final String reviewNickname = "Dani :)";
        final String reviewContent = "This book is awesome!";
        Review review = given()
            .contentType(JSON)
            .body(new HashMap<String, String>() {{
                put("nickname", reviewNickname);
                put("content", reviewContent);
            }})
        .when()
            .post("/api/books/{bookId}/review", book.getId())
        .then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value())
            .extract().jsonPath().getObject("", Review.class);
            
        Assertions.assertNotNull(review);
        Assertions.assertAll(
            () -> Assertions.assertEquals(review.getNickname(), reviewNickname),
            () -> Assertions.assertEquals(review.getContent(), reviewContent),
            () -> Assertions.assertEquals(review.getBookId(), book.getId())
        );
    }

    @Test
    @DisplayName("should be able to delete a review")
    public void testDeleteReview() {
        final String bookId = "OL15358691W";
        BookDetail bookDetail = 
        when()
            .get("/api/books/{bookId}", bookId)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .extract().jsonPath().getObject("", BookDetail.class);
        
        Assertions.assertNotNull(bookDetail);
        
        final String reviewNickname = "Dani :)";
        final String reviewContent = "This book is awesome!";
        Review review = given()
            .contentType(JSON)
            .body(new HashMap<String, String>() {{
                put("nickname", reviewNickname);
                put("content", reviewContent);
            }})
        .when()
            .post("/api/books/{bookId}/review", bookDetail.getId())
        .then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value())
            .extract().jsonPath().getObject("", Review.class);
            
        Assertions.assertNotNull(review);

        // Delete the review
        when()
            .delete("/api/books/{bookId}/review/{reviewId}", bookDetail.getId(), review.getId())
        .then()
            .assertThat()
            .statusCode(HttpStatus.NO_CONTENT.value());
        
        // Verify that the review has been deleted
        when()
            .delete("/api/books/{bookId}/review/{reviewId}", bookDetail.getId(), review.getId())
        .then()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
