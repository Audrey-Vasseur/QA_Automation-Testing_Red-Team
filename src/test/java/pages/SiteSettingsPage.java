package pages;

import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import pages.components.ConfirmDialog;

/**
 * Reglages du site. On y utilise le champ numerique "Posts per page"
 * (un selecteur de nombre Android limite a une plage de valeurs).
 */
public class SiteSettingsPage extends BasePage {

    private static final String POSTS_PER_PAGE = "Posts per page";
    private static final By SETTINGS_LIST = androidId("list");
    private static final By NUMBER_INPUT = androidId("numberpicker_input");

    /** Valeur affichee sous "Posts per page" (le "summary" de la meme ligne). */
    private static final By POSTS_PER_PAGE_VALUE = uiSelector(
            "new UiSelector().resourceId(\"android:id/title\").text(\"" + POSTS_PER_PAGE + "\")"
                    + ".fromParent(new UiSelector().resourceId(\"android:id/summary\"))");

    public SiteSettingsPage waitUntilLoaded() {
        visible(SETTINGS_LIST);
        return this;
    }

    public int postsPerPage() {
        scrollToText(POSTS_PER_PAGE);
        return Integer.parseInt(textOf(POSTS_PER_PAGE_VALUE).trim());
    }

    /**
     * La valeur est enregistree sur le serveur apres OK : l'affichage peut se mettre a jour
     * avec un leger retard. On attend donc qu'elle atteigne la valeur attendue.
     */
    public boolean postsPerPageBecomes(int expected) {
        scrollToText(POSTS_PER_PAGE);
        return isShown(uiSelector(
                "new UiSelector().resourceId(\"android:id/title\").text(\"" + POSTS_PER_PAGE + "\")"
                        + ".fromParent(new UiSelector().resourceId(\"android:id/summary\").text(\"" + expected + "\"))"),
                java.time.Duration.ofSeconds(10));
    }

    /**
     * Saisit une valeur au clavier dans le selecteur de nombre puis valide avec OK.
     *
     * Le selecteur ne prend en compte la saisie qu'apres la touche Entree
     * (constate pendant le developpement : sans Entree, la valeur tapee etait ignoree).
     *
     * @return la valeur que le selecteur affichait juste avant OK (apres sa propre validation)
     */
    public int typePostsPerPage(int value) {
        scrollToText(POSTS_PER_PAGE).click();
        type(NUMBER_INPUT, String.valueOf(value));
        driver.pressKey(new KeyEvent(AndroidKey.ENTER));
        String shownInPicker = textOf(NUMBER_INPUT);
        new ConfirmDialog().confirm();
        return Integer.parseInt(shownInPicker.trim());
    }
}
