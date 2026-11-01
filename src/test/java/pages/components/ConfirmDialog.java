package pages.components;

import org.openqa.selenium.By;
import pages.BasePage;

import java.time.Duration;

public class ConfirmDialog extends BasePage {
    private static final By TITLE = id("alertTitle");
    private static final By MESSAGE = androidId("message");
    private static final By POSITIVE = androidId("button1");
    private static final By NEGATIVE = androidId("button2");

    public boolean isDisplayed() {
        return isShown(POSITIVE, Duration.ofSeconds(5));
    }

    public boolean isClosed() {
        return isGone(POSITIVE, Duration.ofSeconds(5));
    }

    public String title() {
        return textOf(TITLE);
    }

    public String message() {
        return textOf(MESSAGE);
    }

    public String positiveLabel() {
        return textOf(POSITIVE);
    }

    public void confirm() {
        click(POSITIVE);
    }

    public void cancel() {
        click(NEGATIVE);
    }

    public void chooseOption(String option) {
        click(option(option));
    }

    public boolean isOptionChecked(String option) {
        return Boolean.parseBoolean(visible(option(option)).getAttribute("checked"));
    }

    private static By option(String label) {
        return uiSelector("new UiSelector().resourceId(\"android:id/text1\").text(" + quote(label) + ")");
    }
}
