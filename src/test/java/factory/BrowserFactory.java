package factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BrowserFactory {

    public static WebDriver create(String browser) {
        if (browser == null || browser.isBlank()) {
            browser = "chrome";
        }

        switch (browser.toLowerCase()) {
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                return new ChromeDriver(options);
        }
    }

    private BrowserFactory() {
        // utility class
    }
}
