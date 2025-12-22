package tests;

import factory.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class ArtistsSongsListTest extends BaseTest {

    // Reuse your working login selectors
    private final By email = By.cssSelector("input[type='email'][placeholder='Email Address']");
    private final By password = By.cssSelector("input[type='password'][placeholder='Password']");
    private final By submit = By.cssSelector("button[type='submit']");
    private final By avatar = By.cssSelector("#userBadge.profile img.avatar");

    // Artists navigation
    private final By artistsNav = By.cssSelector("a[href='#!/artists']");

    // ✅ Click artist by name link directly (from your DevTools it exists)
    private final By artistNameLinks = By.cssSelector("article[data-test='artist-card'] a.name");

    // ✅ Robust song locator (works for different Koel layouts)
    private final By songItems = By.cssSelector(
            "#mainContent table tbody tr, " +
                    "#mainContent tr, " +
                    "#mainContent .song, " +
                    "#mainContent .song-item, " +
                    "#mainContent .track"
    );

    @Test
    public void artistSongsListDisplaysWhenArtistOpened() {

        WebDriver driver = getDriver();

        // 1) Login
        wait.until(ExpectedConditions.visibilityOfElementLocated(email))
                .sendKeys("shynar.largess+koel960@testpro.io");
        driver.findElement(password).sendKeys("TestPassword!123Q1");
        driver.findElement(submit).click();

        // 2) Wait for logged-in marker
        wait.until(ExpectedConditions.visibilityOfElementLocated(avatar));

        // 3) Open Artists page
        wait.until(ExpectedConditions.elementToBeClickable(artistsNav)).click();

        // 4) Click FIRST artist name link (do NOT click the card first)
        WebElement firstArtistLink = wait.until(ExpectedConditions.elementToBeClickable(artistNameLinks));
        String artistName = firstArtistLink.getText().trim();
        firstArtistLink.click();

        // 5) Confirm we navigated to artist page
        wait.until(ExpectedConditions.urlContains("#!/artist/"));
        Assert.assertTrue(driver.getCurrentUrl().contains("#!/artist/"),
                "Did not open artist details page.");

        // 6) Wait until at least 1 visible song item exists, then assert
        wait.until(d -> d.findElements(songItems).stream().anyMatch(WebElement::isDisplayed));

        long visibleSongs = driver.findElements(songItems).stream()
                .filter(WebElement::isDisplayed)
                .count();

        Assert.assertTrue(visibleSongs > 0, "No songs found for artist: " + artistName);
    }
}


