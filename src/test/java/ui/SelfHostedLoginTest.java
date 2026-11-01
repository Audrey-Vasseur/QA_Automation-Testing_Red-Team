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

@Tag("ui")
class SelfHostedLoginTest extends BaseUiTest {
    @Test
    @DisplayName("UI-02 [Negative] Address of a non-WordPress site: error message, user stays on the form")
    void UI_02_nonWordPressAddress_showsError() {
        SelfHostedLoginPage login = mySite.openSelfHostedLogin()
                .enterSiteAddress("example.com");

        assertTrue(login.isContinueEnabled(), "A well-formed address should enable Continue");
        login.tapContinue();

        assertAll("Error displayed",
                () -> assertTrue(login.showsError("The site does not appear to be a WordPress site."),
                        "The exact error message should be shown under the field"),
                () -> assertEquals("example.com", login.siteAddress(), "The typed address should be kept for correction"));
    }

    @ParameterizedTest(name = "UI-03 [Validation] address \"{0}\" -> Continue enabled = {1}")
    @DisplayName("UI-03 [Field validation] Continue is enabled only for a well-formed address")
    @CsvSource(delimiter = '|', nullValues = "EMPTY", value = {
            "EMPTY          | false",
            "invalid url @@ | false",
            "example.com    | true",
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
            assertTrue(login.isContinueEnabled(), "Continue should be enabled for: " + address);
        } else {
            assertFalse(login.isContinueEnabled(), "Continue should stay disabled for: " + address);
        }
    }
}
