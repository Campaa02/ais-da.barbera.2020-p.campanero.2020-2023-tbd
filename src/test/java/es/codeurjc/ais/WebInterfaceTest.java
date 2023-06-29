package es.codeurjc.ais;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;


/**
 * WebInterfaceTest is a class that contains all the Selenium tests required
 *
 * @author Pablo Campanero Arévalo
 */

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootApplication
@DisplayName("Selenium tests")
public class WebInterfaceTest {
    @LocalServerPort
    int port;

    private WebDriver driver;
    private Duration duration;
    private WebDriverWait wWait;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.edgedriver().setup();
    }

    @BeforeEach
    public void setup() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new EdgeDriver(options);
        driver.get("http://localhost:" + this.port + "/");
        this.duration = Duration.ofSeconds(3);
        this.wWait = new WebDriverWait(driver,duration);

    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @DisplayName("Clicks in 'The Two Towers' checks we can get it and the title is correct")
    @Test
    public void theTwoTowersClickTest(){

        driver.findElement(By.name("topic")).sendKeys("The Lord of the Rings");
        wWait.until(presenceOfElementLocated(By.name("topic")));
        driver.findElement(By.id("search-button")).click();
        driver.findElement(By.id("The Two Towers")).click();
        wWait.until(presenceOfElementLocated(By.id("bookTitle")));
        Assertions.assertEquals("The Two Towers", driver.findElement(By.id("bookTitle")).getText());


    }
    @DisplayName("Clicks in 'The Return of the King', checks we cannot get the detail")
    @Test
    public void theReturnOfTheKingTest(){

        driver.findElement(By.name("topic")).sendKeys("The Lord of the Rings");
        wWait.until(presenceOfElementLocated(By.name("topic")));
        driver.findElement(By.id("search-button")).click();
        driver.findElement(By.id("The Return of the King")).click();
        wWait.until(presenceOfElementLocated(By.id("error-message")));
        Assertions.assertEquals("Error when retrieving a book: unsupported format", driver.findElement(By.id("error-message")).getText());


    }
    @DisplayName("Clicks in 'A Game of Thrones', creates a review, deletes it, and checks it does not exist")
    @Test
    public void reviewCreatedAndErasedTest() {

        driver.findElement(By.name("topic")).sendKeys("epic fantasy");
        wWait.until(presenceOfElementLocated(By.name("topic")));
        driver.findElement(By.id("search-button")).click();
        driver.findElement(By.id("A Game of Thrones")).click();
        String tester = "tester";
        driver.findElement(By.name("nickname")).sendKeys(tester);
        String reviewMessage = "This is a review";
        driver.findElement(By.name("content")).sendKeys(reviewMessage);
        driver.findElement(By.id("add-review")).click();
        WebElement reviewCreated = driver.findElement(By.className("author"));
        Assertions.assertEquals(tester, reviewCreated.getText());
        driver.findElement(By.id("tester-delete")).click();
        Assertions.assertThrows(NoSuchElementException.class, () ->
            driver.findElement(By.className("author"))
        );


    }

}
