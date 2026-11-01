package pages;

import org.openqa.selenium.By;
import pages.components.ConfirmDialog;
import pages.components.Snackbar;

import java.time.Duration;
import java.util.List;

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

    private static By postTitle(String title) {
        return idWithText("title", title);
    }

    private static By moreButtonOf(String title) {
        return By.xpath("//*[@resource-id='" + APP_ID + "container']"
                + "[.//*[@resource-id='" + APP_ID + "title' and @text=" + xpathLiteral(title) + "]]"
                + "//*[@resource-id='" + APP_ID + "more']");
    }

    public boolean isPostListed(String title) {
        return isShown(postTitle(title), Duration.ofSeconds(20));
    }

    public boolean isPostGone(String title) {
        return isGone(postTitle(title), Duration.ofSeconds(15));
    }

    public String postInfoOf(String title) {
        return textOf(By.xpath("//*[@resource-id='" + APP_ID + "container']"
                + "[.//*[@resource-id='" + APP_ID + "title' and @text=" + xpathLiteral(title) + "]]"
                + "//*[@resource-id='" + APP_ID + "post_info']"));
    }

    public EditorPage openPost(String title) {
        click(postTitle(title));
        return new EditorPage().waitUntilLoaded();
    }

    public Snackbar trashPost(String title) {
        openMenuItemOf(title, "Trash");
        return new Snackbar();
    }

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
        return "concat('" + value.replace("'", "', \"'\", '") + "')";
    }
}
