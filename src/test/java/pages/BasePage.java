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

/**
 * Classe mere de tous les ecrans (Page Object Model).
 *
 * Elle regroupe les actions communes avec des ATTENTES EXPLICITES :
 * on attend qu'un element soit visible/cliquable au lieu d'un Thread.sleep fixe.
 * Resultat : les tests sont plus rapides quand l'appli repond vite,
 * et plus stables quand elle est lente (reseau WordPress.com).
 *
 * Les tests ne manipulent jamais le driver directement : ils appellent des
 * methodes metier des pages (ex : postsList.openTab(PostTab.DRAFTS)).
 *
 * Ordre de preference des localisateurs (du plus stable au moins stable) :
 *   1. resource-id Android (ex : org.wordpress.android:id/post_status)
 *   2. accessibilityId / content-desc (ex : "More options")
 *   3. texte visible, seulement quand l'ecran n'expose pas d'id (ecrans Jetpack Compose)
 *   4. XPath RELATIF et semantique, uniquement pour "le bouton X de la ligne qui contient Y".
 * Jamais de XPath absolu (/hierarchy/android.widget.FrameLayout[1]/...) ni de coordonnees.
 */
public abstract class BasePage {

    protected static final String APP_ID = "org.wordpress.android:id/";

    protected final AndroidDriver driver;
    private final Duration timeout;

    protected BasePage() {
        this.driver = DriverFactory.get();
        this.timeout = Duration.ofSeconds(AppConfig.getInt("wait.seconds", 20));
    }

    // ------------------------------------------------------------------ localisateurs

    /** resource-id de l'appli WordPress (le plus stable, defini par les developpeurs). */
    protected static By id(String resourceId) {
        return AppiumBy.id(APP_ID + resourceId);
    }

    /** resource-id du systeme Android (boites de dialogue, selecteurs de date...). */
    protected static By androidId(String resourceId) {
        return AppiumBy.id("android:id/" + resourceId);
    }

    /** accessibilityId = content-desc, stable et lisible. */
    protected static By a11y(String accessibilityId) {
        return AppiumBy.accessibilityId(accessibilityId);
    }

    /** Texte exact affiche (utilise seulement quand l'ecran n'expose pas d'id). */
    protected static By text(String visibleText) {
        return uiSelector("new UiSelector().text(" + quote(visibleText) + ")");
    }

    protected static By textContains(String part) {
        return uiSelector("new UiSelector().textContains(" + quote(part) + ")");
    }

    /** Element de l'appli dont l'id ET le texte correspondent (ex : un titre precis dans une liste). */
    protected static By idWithText(String resourceId, String visibleText) {
        return uiSelector("new UiSelector().resourceId(" + quote(APP_ID + resourceId) + ").text(" + quote(visibleText) + ")");
    }

    protected static By uiSelector(String expression) {
        return AppiumBy.androidUIAutomator(expression);
    }

    /** Echappe le texte pour l'inserer dans une expression UiSelector. */
    protected static String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    // ------------------------------------------------------------------ attentes et actions

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
            // Une fenetre promotionnelle peut apparaitre a tout moment et masquer l'ecran :
            // on la ferme puis on reessaie UNE fois. Si l'element n'est toujours pas la,
            // le test echoue normalement (on ne cache pas un vrai defaut).
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

    /** Efface le champ puis saisit la valeur. */
    protected void type(By locator, String value) {
        WebElement field = visible(locator);
        field.click();
        field.clear();
        field.sendKeys(value);
    }

    /**
     * Saisie "comme un humain" : on donne le focus au champ puis on envoie de vraies frappes clavier.
     *
     * Necessaire pour l'editeur Gutenberg (React Native) : la saisie Appium standard (sendKeys)
     * remplace le texte d'un coup, ce que ces champs ignorent ou font recreer (element "stale").
     * Voir docs/failure-investigation.md.
     */
    protected void typeWithKeyboard(By locator, String value) {
        click(locator);
        if (isPlainAscii(value)) {
            new Actions(driver).sendKeys(value).perform();
        } else {
            // Les frappes clavier simulees ne savent produire que des caracteres ASCII
            // (constate : "accentué" s'arretait a "accentu"). Pour les accents, emojis, etc.,
            // on fait comme un utilisateur qui colle un texte : presse-papiers + "coller".
            driver.setClipboardText(value);
            driver.pressKey(new KeyEvent(AndroidKey.PASTE));
        }
    }

    private static boolean isPlainAscii(String value) {
        return value.chars().allMatch(c -> c >= 32 && c < 127);
    }

    /** Efface un champ touche par touche : curseur en fin de texte, puis "retour arriere" autant de fois que de caracteres. */
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

    /** Vrai si l'element apparait avant le delai donne, faux sinon (sans lever d'exception). */
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

    /** Fait defiler la premiere liste defilable jusqu'au texte demande (UiScrollable, cote Android). */
    protected WebElement scrollToText(String visibleText) {
        return visible(uiSelector("new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().text(" + quote(visibleText) + "))"));
    }

    /** Commande shell sur le telephone (necessite Appium lance avec --relaxed-security). */
    protected Object shell(String command, String... args) {
        return driver.executeScript("mobile: shell", Map.of("command", command, "args", List.of(args)));
    }

    public void pressBack() {
        driver.navigate().back();
    }
}
