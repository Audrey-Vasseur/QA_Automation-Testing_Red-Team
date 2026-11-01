package pages;

import org.openqa.selenium.By;

import java.time.Duration;

public class SelfHostedLoginPage extends BasePage {
    private static final By ADDRESS_FIELD = By.className("android.widget.EditText");
    private static final By INSTRUCTIONS = textContains("Enter the address of the WordPress site");
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

    public boolean showsError(String expectedMessage) {
        return isShown(text(expectedMessage), Duration.ofSeconds(30));
    }

    public MySitePage leave() {
        click(BACK);
        click(a11y("Navigate up"));
        return new MySitePage().waitUntilLoaded();
    }
}
