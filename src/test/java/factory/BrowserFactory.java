package factory;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BrowserFactory {

    // Default local Grid URL (override with -DgridUrl=... if needed)
    private static final String GRID_URL_DEFAULT = "http://192.168.1.152:4444/wd/hub";

    /** local (default) or cloud; can be set via -Dtarget=cloud */
    private static String getTarget() {
        String sys = System.getProperty("target");
        if (sys != null && !sys.isBlank()) return sys;
        String env = System.getenv("TARGET");
        return (env != null && !env.isBlank()) ? env : "local";
    }

    private static String getLocalGridUrl() {
        return System.getProperty("gridUrl", GRID_URL_DEFAULT);
    }

    /** Main entry point used by BaseTest */
    public static WebDriver create(String browser) {
        String target = getTarget().toLowerCase(Locale.ROOT);
        return switch (target) {
            case "cloud" -> createOnLambdaTest(browser);
            case "local" -> createOnLocalGrid(browser);
            default -> throw new IllegalArgumentException("Unknown target: " + target + " (use local or cloud)");
        };
    }

    // ----------------- LOCAL GRID -----------------
    private static WebDriver createOnLocalGrid(String browser) {
        try {
            Capabilities caps = switch (browser.toLowerCase(Locale.ROOT)) {
                case "chrome" -> {
                    ChromeOptions chrome = new ChromeOptions();
                    chrome.addArguments("--remote-allow-origins=*");
                    yield chrome;
                }
                case "firefox" -> new FirefoxOptions();
                default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
            };

            return new RemoteWebDriver(new URL(getLocalGridUrl()), caps);
        } catch (Exception e) {
            throw new RuntimeException("Could not start remote " + browser + " on LOCAL grid", e);
        }
    }

    // ----------------- LAMBDATEST CLOUD -----------------
    private static WebDriver createOnLambdaTest(String browser) {
        String user = System.getenv("LT_USERNAME");
        String key  = System.getenv("LT_ACCESS_KEY");
        if (user == null || key == null) {
            throw new IllegalStateException("Set LT_USERNAME and LT_ACCESS_KEY environment variables.");
        }

        try {
            String browserVersion = System.getProperty("browserVersion", "latest");
            String platformName   = System.getProperty("platformName", "Windows 11");

            Capabilities caps;
            if ("firefox".equalsIgnoreCase(browser)) {
                FirefoxOptions fx = new FirefoxOptions();
                fx.setBrowserVersion(browserVersion);
                fx.setPlatformName(platformName);
                fx.setCapability("LT:Options", ltOptions(user, key));
                caps = fx;
            } else { // default to chrome
                ChromeOptions chrome = new ChromeOptions();
                chrome.setBrowserVersion(browserVersion);
                chrome.setPlatformName(platformName);
                chrome.setCapability("LT:Options", ltOptions(user, key));
                caps = chrome;
            }

            return new RemoteWebDriver(new URL("https://hub.lambdatest.com/wd/hub"), caps);
        } catch (Exception e) {
            throw new RuntimeException("Could not start remote " + browser + " on LambdaTest", e);
        }
    }

    private static Map<String, Object> ltOptions(String user, String key) {
        Map<String, Object> lt = new HashMap<>();
        lt.put("user", user);            // do NOT hardcode secrets
        lt.put("accessKey", key);
        lt.put("project", System.getProperty("project", "HW25"));
        lt.put("build", System.getProperty("build", "HW25-" + System.currentTimeMillis()));
        lt.put("name", Thread.currentThread().getName());
        lt.put("w3c", true);
        return lt;
    }

    private BrowserFactory() {}
}
