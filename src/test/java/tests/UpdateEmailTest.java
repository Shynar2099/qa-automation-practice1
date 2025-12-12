package tests;

import factory.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class UpdateEmailTest extends BaseTest {

    private final String oldEmail = "shynar.largess+koel950@testpro.io";
    private final String password = "TestPassword!123Q1";
    private final String newEmail = "shynar.largess+koel960@testpro.io";

    @Test
    public void userCanUpdateEmailAndLoginWithNewEmailOnly() throws InterruptedException {

        WebDriver driver = getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // ---------- 1. LOGIN WITH OLD EMAIL ----------
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='email'][placeholder='Email Address']")));
        WebElement passwordField = driver.findElement(
                By.cssSelector("input[type='password'][placeholder='Password']"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        emailField.sendKeys(oldEmail);
        passwordField.sendKeys(password);
        loginButton.click();

        // Wait for home page → avatar / profile link
        WebElement avatar = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#userBadge.profile img.avatar")
                )
        );

        // ---------- 2. GO TO PROFILE ----------
        avatar.click(); // open profile menu

        WebElement profileLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[data-testid='view-profile-link']")
                )
        );
        profileLink.click();

        // Ensure we're on profile page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputProfileEmail")));

        // ---------- 3. UPDATE EMAIL ----------
        WebElement currentPasswordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("inputProfileCurrentPassword")));
        currentPasswordInput.sendKeys(password);

        WebElement emailInput = driver.findElement(By.id("inputProfileEmail"));
        emailInput.clear();
        emailInput.sendKeys(newEmail);

        WebElement saveButton = driver.findElement(By.cssSelector("button.btn-submit"));
        saveButton.click();

        // ---------- 4. WAIT FOR SUCCESS TOAST TO APPEAR + DISAPPEAR ----------
        By successToast = By.cssSelector("div.success");
        wait.until(ExpectedConditions.visibilityOfElementLocated(successToast));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(successToast));

        // ---------- 5. LOG OUT ----------
        avatar = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("#userBadge.profile img.avatar")
                )
        );
        avatar.click();

        WebElement logoutLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[data-testid='btn-logout']") // "Log student out"
                )
        );
        logoutLink.click();

        // Back on login page
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='email'][placeholder='Email Address']"))
        );

        // ---------- 6. TRY LOGIN WITH OLD EMAIL (SHOULD FAIL) ----------
        emailField = driver.findElement(
                By.cssSelector("input[type='email'][placeholder='Email Address']"));
        passwordField = driver.findElement(
                By.cssSelector("input[type='password'][placeholder='Password']"));
        loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        emailField.sendKeys(oldEmail);
        passwordField.sendKeys(password);
        loginButton.click();

        // Give the app a moment to react
        Thread.sleep(2000);

        // Check that avatar is NOT present → user NOT logged in with old email
        boolean avatarPresentAfterOldLogin =
                !driver.findElements(By.cssSelector("#userBadge.profile img.avatar")).isEmpty();

        Assert.assertFalse(
                avatarPresentAfterOldLogin,
                "Old email SHOULD NOT be able to log in, but avatar is visible!"
        );

        // ---------- 7. LOGIN WITH NEW EMAIL (SHOULD SUCCEED) ----------

        // 🔄 REFRESH to reset the login form completely
        driver.navigate().refresh();

        // Wait for clean login form
        emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='email'][placeholder='Email Address']")));
        passwordField = driver.findElement(
                By.cssSelector("input[type='password'][placeholder='Password']"));
        loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        // Now type only the NEW email + password
        emailField.sendKeys(newEmail);
        passwordField.sendKeys(password);
        loginButton.click();

        // Success = avatar visible again
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#userBadge.profile img.avatar")
        ));
    }
}
