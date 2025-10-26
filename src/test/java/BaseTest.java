// package your.package;

import factory.BrowserFactory; // if you have one; else use pickBrowser below
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import java.time.Duration;

public class BaseTest {

    private static final ThreadLocal<WebDriver> TL = new ThreadLocal<>();

    protected static WebDriver getDriver() { return TL.get(); }
    private static void setDriver(WebDriver d) { TL.set(d); }
    private static void unloadDriver() { TL.remove(); }

    protected WebDriverWait wait;
    protected FluentWait<WebDriver> fluentWait;
    protected Actions actions;

    @BeforeMethod(alwaysRun = true)
    @Parameters({"BaseURL","Browser","Target"})
    public void setupBrowser(String baseURL, String browser, String target) throws Exception {
        // Let BrowserFactory / pickBrowser know where to run
        if (target != null && !target.isBlank()) System.setProperty("target", target); // "local" or "cloud"

        // If you built BrowserFactory.create(browser), use that; else call pickBrowser(browser) below
        WebDriver driver = BrowserFactory.create(browser);
        // WebDriver driver = pickBrowser(browser); // <- use this if you don’t have BrowserFactory

        setDriver(driver);

        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        getDriver().manage().window().maximize();

        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        fluentWait = new FluentWait<>(getDriver())
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(2));
        actions = new Actions(getDriver());

        getDriver().get(baseURL);
        // If a landing page shows first, click "Log in"
        var links = getDriver().findElements(By.linkText("Log in"));
        if (!links.isEmpty() && links.get(0).isDisplayed()) {
            links.get(0).click();
        }

// Wait for the login form's email field
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));

    }

    @AfterMethod(alwaysRun = true)
    public void teardown(ITestResult result) {
        try {
            WebDriver d = getDriver();
            if (d instanceof JavascriptExecutor js) {
                js.executeScript("lambda-status=" + (result.isSuccess() ? "passed" : "failed"));
            }
            if (d != null) d.quit();         // quit, not close
        } finally {
            unloadDriver();                  // clear ThreadLocal
        }
    }
}

