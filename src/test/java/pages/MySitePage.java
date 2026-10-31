package pages;

import org.openqa.selenium.By;

/**
 * Ecran d'accueil de l'appli : "My Site" (titre du site + menu Posts, Media, Site Settings...).
 * C'est l'ecran sur lequel chaque test demarre (l'appli est relancee a chaque session).
 */
public class MySitePage extends BasePage {

    private static final By SITE_TITLE = id("my_site_title_label");
    private static final By SWITCH_SITE = id("switch_site");

    /** Attend que l'ecran soit charge (et ferme une eventuelle fenetre promotionnelle). */
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

    /** Ecran "Choose site" -> bouton "+" -> "Add self-hosted site" (formulaire de connexion). */
    public SelfHostedLoginPage openSelfHostedLogin() {
        click(SWITCH_SITE);
        click(a11y("Add a site"));
        click(a11y("Add self-hosted site"));
        return new SelfHostedLoginPage().waitUntilLoaded();
    }

    private void openMenuItem(String label) {
        // Les entrees du menu partagent le meme id : on cible l'id + le texte affiche.
        click(idWithText("my_site_item_primary_text", label));
    }
}
