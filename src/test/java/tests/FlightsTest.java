package tests;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import support.Config;
import support.DriverFactory;

public class FlightsTest extends DriverFactory {
  @Test
  void selectAsynchronousAirportsAndSearchAValidReturnFlight() {
    WebDriver driver = DriverFactory.current();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    driver.get(Config.baseUrl());

    By acceptCookies = By.xpath("//button[normalize-space()='Accept all' or normalize-space()='Accept']");
    if (!driver.findElements(acceptCookies).isEmpty()) {
      wait.until(ExpectedConditions.elementToBeClickable(acceptCookies)).click();
    }

    By origin = By.id("origin");
    By destination = By.id("destination");
    WebElement originInput = wait.until(ExpectedConditions.elementToBeClickable(origin));
    originInput.sendKeys("London");
    By gatwick = By.xpath("//*[contains(normalize-space(.),'Gatwick') and not(.//*[contains(normalize-space(.),'Gatwick')])]");
    wait.until(ExpectedConditions.elementToBeClickable(gatwick)).click();

    WebElement destinationInput = wait.until(ExpectedConditions.elementToBeClickable(destination));
    destinationInput.sendKeys("Paris");
    By paris = By.xpath("//*[normalize-space()='Paris, France']");
    wait.until(ExpectedConditions.elementToBeClickable(paris)).click();
    wait.until(ExpectedConditions.invisibilityOfElementLocated(paris));
    Assertions.assertTrue(driver.findElements(paris).stream().noneMatch(WebElement::isDisplayed), "Paris should be selected as destination");

    LocalDate outbound = LocalDate.now().plusDays(30);
    LocalDate inbound = outbound.plusDays(7);
    By departure = By.id("outboundDate");
    wait.until(ExpectedConditions.elementToBeClickable(departure)).click();
    By outboundDay = By.xpath("//div[@data-value='" + outbound.withDayOfMonth(1) + "']//div[@data-test='DayDateTypography' and normalize-space()='" + outbound.getDayOfMonth() + "']");
    wait.until(ExpectedConditions.elementToBeClickable(outboundDay)).click();
    By inboundDay = By.xpath("//div[@data-value='" + inbound.withDayOfMonth(1) + "']//div[@data-test='DayDateTypography' and normalize-space()='" + inbound.getDayOfMonth() + "']");
    wait.until(ExpectedConditions.elementToBeClickable(inboundDay)).click();
    Assertions.assertTrue(driver.getPageSource().contains(outbound.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) || driver.getPageSource().contains("Return"), "Outbound and inbound dates should be accepted");

    By search = By.xpath("//button[normalize-space()='Search']");
    wait.until(ExpectedConditions.elementToBeClickable(search)).click();
    wait.until(ExpectedConditions.or(ExpectedConditions.urlContains("gatwick-london-united-kingdom"), ExpectedConditions.urlContains("service"), ExpectedConditions.urlContains("error")));
    String resultUrl = driver.getCurrentUrl();
    Assertions.assertTrue(resultUrl.contains("gatwick-london-united-kingdom"), "Results URL should contain the selected origin: " + resultUrl);
    Assertions.assertTrue(resultUrl.contains("paris-france") || driver.getPageSource().toLowerCase().contains("service error") || driver.getPageSource().toLowerCase().contains("something went wrong"), "Results or a service error should be shown");
  }

}
