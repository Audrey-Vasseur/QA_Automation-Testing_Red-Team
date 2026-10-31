package pages.components;

import org.openqa.selenium.By;
import pages.BasePage;

import java.time.Duration;

/**
 * Bandeau temporaire en bas de l'ecran ("Post sent to trash  UNDO").
 * C'est un element DYNAMIQUE : il apparait apres une action puis disparait seul
 * au bout de quelques secondes. On l'attend donc explicitement.
 */
public class Snackbar extends BasePage {

    private static final By TEXT = id("snackbar_text");
    private static final By ACTION = id("snackbar_action");

    /** Attend que le bandeau affiche ce message precis. */
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
