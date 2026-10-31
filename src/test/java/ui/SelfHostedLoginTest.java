package ui;

import core.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pages.SelfHostedLoginPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Formulaire de connexion a un site auto-heberge ("Add self-hosted site").
 * Ce formulaire est accessible sans se deconnecter du compte de test.
 */
@Tag("ui")
class SelfHostedLoginTest extends BaseUiTest {

    @Test
    @DisplayName("UI-02 [Negatif] Adresse d'un site qui n'est pas WordPress : message d'erreur et on reste sur le formulaire")
    void UI_02_nonWordPressAddress_showsError() {
        SelfHostedLoginPage login = mySite.openSelfHostedLogin()
                .enterSiteAddress("example.com");   // site reel, mais pas un site WordPress

        assertTrue(login.isContinueEnabled(), "Une adresse bien formee doit activer Continue");
        login.tapContinue();

        assertAll("Erreur affichee",
                () -> assertTrue(login.showsError("The site does not appear to be a WordPress site."),
                        "Le message d'erreur exact doit s'afficher sous le champ"),
                () -> assertEquals("example.com", login.siteAddress(), "La saisie doit etre conservee pour correction"));
    }

    @ParameterizedTest(name = "UI-03 [Validation] adresse \"{0}\" -> Continue actif = {1}")
    @DisplayName("UI-03 [Validation de champ] Le bouton Continue n'est actif que pour une adresse au bon format")
    @CsvSource(delimiter = '|', nullValues = "EMPTY", value = {
            "EMPTY          | false",   // champ obligatoire vide
            "invalid url @@ | false",   // espaces et caracteres interdits
            "example.com    | true",    // nom de domaine valide
            "https://wordpress.org | true"
    })
    void UI_03_siteAddressFormat_controlsContinueButton(String address, boolean expectedEnabled) {
        SelfHostedLoginPage login = mySite.openSelfHostedLogin();
        if (address == null) {
            login.clearSiteAddress();
        } else {
            login.enterSiteAddress(address);
        }

        if (expectedEnabled) {
            assertTrue(login.isContinueEnabled(), "Continue doit etre actif pour : " + address);
        } else {
            assertFalse(login.isContinueEnabled(), "Continue doit rester desactive pour : " + address);
        }
    }
}
