package pages.components;

import org.openqa.selenium.By;
import pages.BasePage;

import java.time.Duration;

/**
 * Boite de dialogue Android standard (titre, message, boutons OK/ANNULER).
 * Utilisee par WordPress pour "Delete post?", "Delete this image?", "Status & Visibility"...
 *
 * button1 = bouton positif (OK, DELETE), button2 = bouton negatif (CANCEL) :
 * ce sont les identifiants fixes du systeme Android, donc tres stables.
 */
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

    /** Pour les dialogues a choix unique (ex : statut de l'article). */
    public void chooseOption(String option) {
        click(option(option));
    }

    /** L'option est-elle cochee (selectionnee) dans la liste ? */
    public boolean isOptionChecked(String option) {
        return Boolean.parseBoolean(visible(option(option)).getAttribute("checked"));
    }

    private static By option(String label) {
        return uiSelector("new UiSelector().resourceId(\"android:id/text1\").text(" + quote(label) + ")");
    }
}
