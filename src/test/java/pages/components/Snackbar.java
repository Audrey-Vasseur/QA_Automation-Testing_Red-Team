package pages.components;

import org.openqa.selenium.By;
import pages.BasePage;

import java.time.Duration;

public class Snackbar extends BasePage {
    private static final By TEXT = id("snackbar_text");
    private static final By ACTION = id("snackbar_action");

    public boolean appearsWith(String expectedMessage) {
        return isShown(idWithText("snackbar_text", expectedMessage), Duration.ofSeconds(15));
    }

    public String message() {
        return textOf(TEXT);
    }

    public String actionLabel() {
        return textOf(ACTION);
    }

    public void tapAction() {
        click(ACTION);
    }

    public boolean disappears() {
        return isGone(TEXT, Duration.ofSeconds(15));
    }
}
