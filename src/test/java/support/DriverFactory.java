package support;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {
  private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

  @BeforeEach
  public void createDriver() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--window-size=1280,720");
    options.addArguments("--headless=new");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    WebDriver driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(Duration.ZERO);
    DRIVER.set(driver);
  }

  @AfterEach
  public void closeDriver() {
    WebDriver driver = DRIVER.get();
    if (driver != null) {
      driver.quit();
      DRIVER.remove();
    }
  }

  public static WebDriver current() {
    WebDriver driver = DRIVER.get();
    if (driver == null) {
      throw new IllegalStateException("WebDriver is not active for this test");
    }
    return driver;
  }
}
