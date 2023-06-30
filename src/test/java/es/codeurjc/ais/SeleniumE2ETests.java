package es.codeurjc.ais;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("The website")
public class SeleniumE2ETests {

    private static final String BASE_URI = "http://localhost:";
    private String BASE_URL;
    private static FirefoxOptions options; 
    @LocalServerPort
    private int port;
    private static WebDriver driver;

    @BeforeAll
    public static void setUp() {
        // Open driver in headless mode 
        WebDriverManager.firefoxdriver().setup();
        options = new FirefoxOptions(); 
        options.setHeadless(true); 
    }

    @BeforeEach
    public void setUpTest() {
        BASE_URL = BASE_URI + Integer.toString(port) + "/";
        driver = new FirefoxDriver(options);
        // Set default timeout
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    @AfterEach
    public void tearDownTest() {
        if (driver != null)
            driver.quit();
    }

    @Test
    @DisplayName("should be able to search by topic, display the results and navigate to the correct book detail page")
    public void testSearchBySubject() {
        final String queryTopic = "drama";
        // Go to home page
        driver.get(BASE_URL);
        // Find search form
        By formSelector = By.cssSelector(".ui.action.input");
        WebElement form = driver.findElement(formSelector);
        // Search by topic
        By inputSelector = By.tagName("input");
        form.findElement(inputSelector).sendKeys(queryTopic);
        By buttonSelector = By.id("search-button");
        form.findElement(buttonSelector).click();
        Assertions.assertEquals(driver.getCurrentUrl(), BASE_URL + "?topic=" + queryTopic);
        // Find results
        WebElement results = driver.findElement(By.cssSelector(".ui.link.list"));
        // Click on first result
        results.findElement(By.tagName("a")).click();
        // Assert that subject tag is present
        By tagSelector = By.id(queryTopic);
        Assertions.assertNotNull(driver.findElement(tagSelector));
    }

    @Test
    @DisplayName("should be able to navigate to the book details page and add a review")
    public void testSearchBookByTopicAndLeaveReview() {
        final String queryTopic = "epic fantasy";
        final String bookTitle = "The Way of Kings";

        // Go to home page
        driver.get(BASE_URL);
        // Find search form
        By formSelector = By.cssSelector(".ui.action.input");
        WebElement form = driver.findElement(formSelector);
        // Search by topic
        By inputSelector = By.tagName("input");
        form.findElement(inputSelector).sendKeys(queryTopic);
        By buttonSelector = By.id("search-button");
        form.findElement(buttonSelector).click();
        Assertions.assertEquals(driver.getCurrentUrl(), BASE_URL + "?topic=epic+fantasy");
        // Find results
        WebElement results = driver.findElement(By.cssSelector(".ui.link.list"));

        // Click on result matching book title
        By resultSelector = By.id(bookTitle);
        results.findElement(resultSelector).click();
        // Find review form
        By reviewFormSelector = By.tagName("form");
        WebElement reviewForm = driver.findElement(reviewFormSelector);
        // Fill review form
        WebElement nicknameInput = reviewForm.findElement(By.name("nickname"));
        final String author = "Test User " + Long.toString(System.currentTimeMillis());
        nicknameInput.sendKeys(author);
        By reviewBodySelector = By.tagName("textarea");
        WebElement reviewBodyInput = reviewForm.findElement(reviewBodySelector);
        final String reviewBody = "This is a test review " + Long.toString(System.currentTimeMillis());
        reviewBodyInput.sendKeys(reviewBody);
        // Submit review
        By submitButtonSelector = By.id("add-review");
        reviewForm.findElement(submitButtonSelector).click();

        // Assert that review is present
        By reviewSelector = By.className("comment");
        List<WebElement> reviews = driver.findElements(reviewSelector);
        Assertions.assertNotNull(reviews);
        // From the list of reviews, get the one that matches the author and body
        WebElement review = reviews.stream()
                .filter(r -> r.findElement(By.className("author")).getText().equals(author))
                .filter(r -> r.findElement(By.className("text")).getText().equals(reviewBody))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(review);
    }

    @Test
    @DisplayName("should not accept reviews with an empty body content")
    public void testEmptyReviewBody() {
        final String queryTopic = "epic fantasy";
        final String bookTitle = "Words of Radiance";

        // Go to home page
        driver.get(BASE_URL);
        // Find search form
        By formSelector = By.cssSelector(".ui.action.input");
        WebElement form = driver.findElement(formSelector);
        // Search by topic
        By inputSelector = By.tagName("input");
        form.findElement(inputSelector).sendKeys(queryTopic);
        By buttonSelector = By.id("search-button");
        form.findElement(buttonSelector).click();
        Assertions.assertEquals(driver.getCurrentUrl(), BASE_URL + "?topic=epic+fantasy");
        // Find results
        WebElement results = driver.findElement(By.cssSelector(".ui.link.list"));

        // Click on result matching book title
        By resultSelector = By.id(bookTitle);
        results.findElement(resultSelector).click();
        // Find review form
        By reviewFormSelector = By.tagName("form");
        WebElement reviewForm = driver.findElement(reviewFormSelector);
        // Fill review form
        WebElement nicknameInput = reviewForm.findElement(By.name("nickname"));
        final String author = "Test User " + Long.toString(System.currentTimeMillis());
        nicknameInput.sendKeys(author);
        By reviewBodySelector = By.tagName("textarea");
        WebElement reviewBodyInput = reviewForm.findElement(reviewBodySelector);
        reviewBodyInput.sendKeys("");
        // Submit review
        By submitButtonSelector = By.id("add-review");
        reviewForm.findElement(submitButtonSelector).click();

        // Assert that the review is not present
        By reviewSelector = By.className("comment");
        List<WebElement> reviews = driver.findElements(reviewSelector);
        Assertions.assertNotNull(reviews);
        // From the list of reviews, get the one that matches the author and body
        WebElement review = reviews.stream()
                .filter(r -> r.findElement(By.className("author")).getText().equals(author))
                .findFirst()
                .orElse(null);
        Assertions.assertNull(review);

        // Assert that the error message is present
        By errorMessageSelector = By.id("error-message");
        WebElement errorMessage = driver.findElement(errorMessageSelector);
        Assertions.assertNotNull(errorMessage);
    }
}
