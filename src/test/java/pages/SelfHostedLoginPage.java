package pages;

import org.openqa.selenium.By;

import java.time.Duration;

/**
 * Formulaire "Log In" d'un site WordPress auto-heberge (adresse du site + bouton Continue).
 *
 * Cet ecran est ecrit en Jetpack Compose et n'expose AUCUN resource-id (verifie avec
 * l'arbre XML de l'ecran). On utilise donc les seuls reperes stables disponibles :
 * le type de champ (un seul EditText a l'ecran), les textes affiches et le libelle "Back".
 */
public class SelfHostedLoginPage extends BasePage {

    private static final By ADDRESS_FIELD = By.className("android.widget.EditText");
    private static final By INSTRUCTIONS = textContains("Enter the address of the WordPress site");
    /** Le bouton cliquable est le parent du texte "Continue" : c'est lui qui porte l'etat enabled. */
    private static final By CONTINUE_BUTTON = By.xpath("//android.view.View[./android.widget.TextView[@text='Continue']]");
    private static final By BACK = a11y("Back");

    public SelfHostedLoginPage waitUntilLoaded() {
        visible(INSTRUCTIONS);
        return this;
    }

    public SelfHostedLoginPage enterSiteAddress(String address) {
        type(ADDRESS_FIELD, address);
        return this;
    }

    public String siteAddress() {
        return textOf(ADDRESS_FIELD);
    }

    public SelfHostedLoginPage clearSiteAddress() {
        visible(ADDRESS_FIELD).clear();
        return this;
    }

    public boolean isContinueEnabled() {
        return Boolean.parseBoolean(visible(CONTINUE_BUTTON).getAttribute("enabled"));
    }

    public SelfHostedLoginPage tapContinue() {
        visible(CONTINUE_BUTTON).click();
        return this;
    }

    /** Attend un message d'erreur sous le champ (verification faite par le serveur, donc asynchrone). */
    public boolean showsError(String expectedMessage) {
        return isShown(text(expectedMessage), Duration.ofSeconds(30));
    }

    public MySitePage leave() {
        click(BACK);
        // "Back" ramene a "Choose site", puis "Navigate up" ramene a "My Site"
        click(a11y("Navigate up"));
        return new MySitePage().waitUntilLoaded();
    }
}
