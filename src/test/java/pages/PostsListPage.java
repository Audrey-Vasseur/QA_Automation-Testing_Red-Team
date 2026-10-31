package pages;

import org.openqa.selenium.By;
import pages.components.ConfirmDialog;
import pages.components.Snackbar;

import java.time.Duration;
import java.util.List;

/**
 * Liste des articles (onglets Published / Drafts / Scheduled / Trashed).
 */
public class PostsListPage extends BasePage {

    private static final By CREATE_POST = a11y("Create a post");
    private static final By TABS = id("tabLayout");

    public PostsListPage waitUntilLoaded() {
        visible(TABS);
        return this;
    }

    public EditorPage createPost() {
        click(CREATE_POST);
        return new EditorPage().waitUntilLoaded();
    }

    /** Raccourci : nouvel article -> titre -> texte -> fermeture (= brouillon enregistre). */
    public PostsListPage createDraft(String title, List<String> bodyLines) {
        EditorPage editor = createPost().typeTitle(title);
        if (!bodyLines.isEmpty()) {
            editor.typeBody(bodyLines);
        }
        return editor.closeAndSaveDraft();
    }

    public PostsListPage openTab(PostTab tab) {
        click(a11y(tab.label()));
        return this;
    }

    /** Ligne de la liste dont le titre est exactement celui-ci. */
    private static By postTitle(String title) {
        return idWithText("title", title);
    }

    /**
     * Bouton "..." (More) de la ligne qui contient ce titre.
     * Le titre et le bouton ne sont pas freres directs dans l'arbre : on remonte au conteneur
     * de la ligne avec un XPath RELATIF base sur des ids, pas sur des positions.
     */
    private static By moreButtonOf(String title) {
        return By.xpath("//*[@resource-id='" + APP_ID + "container']"
                + "[.//*[@resource-id='" + APP_ID + "title' and @text=" + xpathLiteral(title) + "]]"
                + "//*[@resource-id='" + APP_ID + "more']");
    }

    /** L'article apparait-il dans l'onglet courant ? (le chargement passe par le reseau) */
    public boolean isPostListed(String title) {
        return isShown(postTitle(title), Duration.ofSeconds(20));
    }

    public boolean isPostGone(String title) {
        return isGone(postTitle(title), Duration.ofSeconds(15));
    }

    /** Date affichee sur la ligne de l'article (ex : "Nov 18"). */
    public String postInfoOf(String title) {
        return textOf(By.xpath("//*[@resource-id='" + APP_ID + "container']"
                + "[.//*[@resource-id='" + APP_ID + "title' and @text=" + xpathLiteral(title) + "]]"
                + "//*[@resource-id='" + APP_ID + "post_info']"));
    }

    public EditorPage openPost(String title) {
        click(postTitle(title));
        return new EditorPage().waitUntilLoaded();
    }

    /** Menu "..." d'un article -> Trash (aucune confirmation dans l'appli, un bandeau UNDO apparait). */
    public Snackbar trashPost(String title) {
        openMenuItemOf(title, "Trash");
        return new Snackbar();
    }

    /** Depuis l'onglet Trashed : menu "..." -> "Delete Permanently" -> ouvre une boite de confirmation. */
    public ConfirmDialog askPermanentDeletion(String title) {
        openMenuItemOf(title, "Delete Permanently");
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.isDisplayed();
        return dialog;
    }

    private void openMenuItemOf(String title, String menuLabel) {
        visible(postTitle(title));
        click(moreButtonOf(title));
        click(idWithText("title", menuLabel));
    }

    /**
     * Nettoyage : supprime definitivement un article, quel que soit l'onglet ou il se trouve.
     * Utilise dans les @AfterEach : chaque test efface ce qu'il a cree.
     */
    public void deletePermanently(String title, PostTab currentTab) {
        if (currentTab != PostTab.TRASHED) {
            openTab(currentTab);
            Snackbar snackbar = trashPost(title);
            snackbar.disappears();
            openTab(PostTab.TRASHED);
        } else {
            openTab(PostTab.TRASHED);
        }
        askPermanentDeletion(title).confirm();
        isPostGone(title);
    }

    static String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        // Texte contenant les deux types de guillemets : concat('...', "'", '...')
        return "concat('" + value.replace("'", "', \"'\", '") + "')";
    }
}
