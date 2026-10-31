package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

/**
 * Ferme les fenetres que l'appli affiche a des moments imprevisibles.
 *
 * Observe pendant l'exploration : WordPress affiche parfois, au hasard, une fenetre
 * "Your site has the Jetpack plugin" qui recouvre l'ecran. Elle ne fait pas partie
 * des scenarios testes, mais si on ne la ferme pas, le test suivant echoue
 * sur un element "introuvable". On la ferme en choisissant l'option qui ne change rien
 * ("Continue without Jetpack").
 */
public final class AppPopups {

    private static final By JETPACK_CONTINUE_WITHOUT = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"org.wordpress.android:id/secondary_button\").text(\"Continue without Jetpack\")");

    private AppPopups() {
    }

    /** @return vrai si une fenetre a ete fermee */
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
