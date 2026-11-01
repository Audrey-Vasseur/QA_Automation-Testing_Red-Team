package pages;

import org.openqa.selenium.By;

public class MySitePage extends BasePage {
    private static final By SITE_TITLE = id("my_site_title_label");
    private static final By SWITCH_SITE = id("switch_site");

    public MySitePage waitUntilLoaded() {
        AppPopups.dismissIfPresent(driver);
        visible(SITE_TITLE);
        return this;
    }

    public String siteTitle() {
        return textOf(SITE_TITLE);
    }

    public PostsListPage openPosts() {
        openMenuItem("Posts");
        return new PostsListPage().waitUntilLoaded();
    }

    public MediaLibraryPage openMedia() {
        openMenuItem("Media");
        return new MediaLibraryPage().waitUntilLoaded();
    }

    public SiteSettingsPage openSiteSettings() {
        openMenuItem("Site Settings");
        return new SiteSettingsPage().waitUntilLoaded();
    }

    public SelfHostedLoginPage openSelfHostedLogin() {
        click(SWITCH_SITE);
        click(a11y("Add a site"));
        click(a11y("Add self-hosted site"));
        return new SelfHostedLoginPage().waitUntilLoaded();
    }

    private void openMenuItem(String label) {
        click(idWithText("my_site_item_primary_text", label));
    }
}
