package ui;

import core.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.PostTab;
import pages.PostsListPage;
import pages.components.ConfirmDialog;
import pages.components.Snackbar;
import utils.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
class PostDeletionTest extends BaseUiTest {
    @Test
    @DisplayName("UI-07 [Dialog] Permanent deletion: CANCEL keeps the post, DELETE removes it")
    void UI_07_deleteDialog_cancelKeeps_confirmDeletes() {
        String title = TestData.uniqueTitle("UI-07 Dialog");
        PostsListPage list = mySite.openPosts().createDraft(title, List.of());
        cleanup.register(title, PostTab.DRAFTS);
        list.openTab(PostTab.DRAFTS).trashPost(title).disappears();
        cleanup.moved(title, PostTab.TRASHED);
        list.openTab(PostTab.TRASHED);

        ConfirmDialog dialog = list.askPermanentDeletion(title);
        assertAll("Confirmation dialog content",
                () -> assertEquals("Delete post?", dialog.title()),
                () -> assertTrue(dialog.message().contains("permanently"), "The message should warn that it is permanent"),
                () -> assertEquals("DELETE", dialog.positiveLabel()));
        dialog.cancel();
        assertAll("After CANCEL",
                () -> assertTrue(dialog.isClosed(), "The dialog should close"),
                () -> assertTrue(list.isPostListed(title), "The post should still be in the trash"));

        list.askPermanentDeletion(title).confirm();
        assertTrue(list.isPostGone(title), "After DELETE, the post should no longer be in the trash");
        cleanup.forget(title);
    }

    @Test
    @DisplayName("UI-08 [Dynamic element] Trashing shows an UNDO banner that restores the post, then disappears")
    void UI_08_trashSnackbar_undoRestoresPost() {
        String title = TestData.uniqueTitle("UI-08 Undo");
        PostsListPage list = mySite.openPosts().createDraft(title, List.of());
        cleanup.register(title, PostTab.DRAFTS);
        list.openTab(PostTab.DRAFTS);

        Snackbar snackbar = list.trashPost(title);

        assertAll("Banner after trashing",
                () -> assertTrue(snackbar.appearsWith("Post sent to trash"), "Expected message 'Post sent to trash'"),
                () -> assertEquals("UNDO", snackbar.actionLabel()));

        snackbar.tapAction();

        assertAll("After UNDO",
                () -> assertTrue(list.isPostListed(title), "The post should be back in Drafts"),
                () -> assertTrue(snackbar.disappears(), "The banner should disappear by itself"));
    }
}
