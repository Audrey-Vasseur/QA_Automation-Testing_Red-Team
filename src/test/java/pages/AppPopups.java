package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public final class AppPopups {
    private static final By JETPACK_CONTINUE_WITHOUT = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"org.wordpress.android:id/secondary_button\").text(\"Continue without Jetpack\")");

    private AppPopups() {
    }

    public static boolean dismissIfPresent(AndroidDriver driver) {
        Duration previous = driver.manage().timeouts().getImplicitWaitTimeout();
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ZERO);
            List<WebElement> buttons = driver.findElements(JETPACK_CONTINUE_WITHOUT);
            if (buttons.isEmpty()) {
                return false;
            }
            buttons.get(0).click();
            return true;
        } finally {
            driver.manage().timeouts().implicitlyWait(previous);
        }
    }
}
