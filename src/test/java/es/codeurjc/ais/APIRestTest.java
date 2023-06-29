package es.codeurjc.ais;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.core.IsEqual.equalTo;


/**
 * APIRestTest is a class that contains all the API Rest tests required
 *
 * @author Pablo Campanero Arévalo
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Rest tests")
public class APIRestTest {

    @LocalServerPort
    int port;


    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("Recovers the number of urjc books and checks that it's 0 ")
    @Test
    public void getZeroDramaBooksTest() throws JSONException {

        Response response = when().get("/api/books/?topic=urjc");
        JSONArray dramaJSON = new JSONArray(
                response.thenReturn().getBody().asString());

        Assertions.assertEquals(0, dramaJSON.length());
        response.then().assertThat().statusCode(200);
    }

    //COMPROBAR CON UN GET LA EXISTENCIA DE REVIEW(0,25) -> no necesario
    @DisplayName("Recovers fantasy books, creates a review with no content for the first one and checks it is not created")
    @Test
    public void createReviewTest() throws JSONException {
        JSONObject body = new JSONObject();
        String tester = "tester";
        JSONArray jsonBody = new JSONArray(
                given().contentType(ContentType.JSON).
                        when().get("/api/books/?topic=fantasy").
                        thenReturn().getBody().asString());

        body.put("nickname", tester);
        given().contentType(ContentType.JSON).body(body.toString()).
                when().post("/api/books/{id}/review", jsonBody.getJSONObject(0).getString("id")).
                then().assertThat().
                statusCode(500).
                assertThat().body("nickname", equalTo(null)).body("content", equalTo(null));

    }

    @DisplayName("Gets a book with an urjc id, checks it exists, and gets a notification that it does not")
    @Test
    public void recoverUrjcBookTest() {
        JSONObject body = new JSONObject();
        given().contentType(ContentType.JSON).body(body.toString()).
                when().get("/api/books/{id}", "urjc").then().assertThat().
                statusCode(404).
                extract().response().andReturn();

    }
}