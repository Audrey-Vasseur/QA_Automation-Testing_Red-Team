package pages;

import core.AppConfig;
import core.DriverFactory;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public abstract class BasePage {
    protected static final String APP_ID = "org.wordpress.android:id/";

    protected final AndroidDriver driver;
    private final Duration timeout;

    protected BasePage() {
        this.driver = DriverFactory.get();
        this.timeout = Duration.ofSeconds(AppConfig.getInt("wait.seconds", 20));
    }

    protected static By id(String resourceId) {
        return AppiumBy.id(APP_ID + resourceId);
    }

    protected static By androidId(String resourceId) {
        return AppiumBy.id("android:id/" + resourceId);
    }

    protected static By a11y(String accessibilityId) {
        return AppiumBy.accessibilityId(accessibilityId);
    }

    protected static By text(String visibleText) {
        return uiSelector("new UiSelector().text(" + quote(visibleText) + ")");
    }

    protected static By textContains(String part) {
        return uiSelector("new UiSelector().textContains(" + quote(part) + ")");
    }

    protected static By idWithText(String resourceId, String visibleText) {
        return uiSelector("new UiSelector().resourceId(" + quote(APP_ID + resourceId) + ").text(" + quote(visibleText) + ")");
    }

    protected static By uiSelector(String expression) {
        return AppiumBy.androidUIAutomator(expression);
    }

    protected static String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    protected WebDriverWait waiter() {
        return new WebDriverWait(driver, timeout);
    }

    protected WebDriverWait waiter(Duration customTimeout) {
        return new WebDriverWait(driver, customTimeout);
    }

    protected WebElement visible(By locator) {
        try {
            return waiter().until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException firstTry) {
            if (AppPopups.dismissIfPresent(driver)) {
                return waiter().until(ExpectedConditions.visibilityOfElementLocated(locator));
            }
            throw firstTry;
        }
    }

    protected void click(By locator) {
        visible(locator);
        waiter().until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String value) {
        WebElement field = visible(locator);
        field.click();
        field.clear();
        field.sendKeys(value);
    }

    protected void typeWithKeyboard(By locator, String value) {
        click(locator);
        if (isPlainAscii(value)) {
            new Actions(driver).sendKeys(value).perform();
        } else {
            driver.setClipboardText(value);
            driver.pressKey(new KeyEvent(AndroidKey.PASTE));
        }
    }

    private static boolean isPlainAscii(String value) {
        return value.chars().allMatch(c -> c >= 32 && c < 127);
    }

    protected void clearWithKeyboard(By locator) {
        WebElement field = visible(locator);
        int length = field.getText().length();
        field.click();
        driver.pressKey(new KeyEvent(AndroidKey.MOVE_END));
        for (int i = 0; i < length; i++) {
            driver.pressKey(new KeyEvent(AndroidKey.DEL));
        }
    }

    protected String textOf(By locator) {
        return visible(locator).getText();
    }

    protected boolean isShown(By locator, Duration within) {
        try {
            waiter(within).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean isGone(By locator, Duration within) {
        try {
            return waiter(within).until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected WebElement scrollToText(String visibleText) {
        return visible(uiSelector("new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().text(" + quote(visibleText) + "))"));
    }

    protected Object shell(String command, String... args) {
        return driver.executeScript("mobile: shell", Map.of("command", command, "args", List.of(args)));
    }

    public void pressBack() {
        driver.navigate().back();
    }
}
