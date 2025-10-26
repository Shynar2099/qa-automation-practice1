import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    private final By email = By.id("email");
    private final By password = By.id("password");
    private final By submit = By.cssSelector("button[type='submit']");
    private final By errorBanner = By.cssSelector("[data-test='login-error'], .error, .alert-danger");
    private final By dashboardMarker = By.cssSelector("[data-test='dashboard'], .dashboard, .user-avatar");

    @Test
    public void loginPositive_correctCreds () {
        getDriver().findElement(email).sendKeys("shynar@testpro.io");
        getDriver().findElement(password).sendKeys("Javashynar890@");
        getDriver().findElement(submit).click();
        Assert.assertTrue(getDriver().findElement(dashboardMarker).isDisplayed(), "Dashboard should appear.");
    }

    @Test
    public void loginNegative_wrongPassword() throws InterruptedException {
        getDriver().findElement(email).sendKeys("shynar@testpro.io");
        getDriver().findElement(password).sendKeys("WrongPass!123");
        getDriver().findElement(submit).click();
        String err = getDriver().findElement(errorBanner).getText().toLowerCase();
        Assert.assertTrue(err.contains("invalid") || err.contains("incorrect") || err.contains("wrong"),
                "Expected invalid password message. Actual: " + err);
    }

    @Test
    public void loginNegative_wrongEmail() throws InterruptedException {
        getDriver().findElement(email).sendKeys("nope+" + System.currentTimeMillis() + "@example.com");
        getDriver().findElement(password).sendKeys("Javashynar890@");
        getDriver().findElement(submit).click();
        String err = getDriver().findElement(errorBanner).getText().toLowerCase();
        Assert.assertTrue(err.contains("invalid") || err.contains("not found") || err.contains("unrecognized") || err.contains("wrong"),
                "Expected user-not-found/invalid email message. Actual: " + err);
    }
}

