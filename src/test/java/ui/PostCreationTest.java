package ui;

import core.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.EditorPage;
import pages.PostTab;
import pages.PostsListPage;
import pages.components.Snackbar;
import utils.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
class PostCreationTest extends BaseUiTest {
    @Test
    @DisplayName("UI-01 [Positive] Create a draft with title and text: it is listed in Drafts")
    void UI_01_createDraft_validData_isListedInDrafts() {
        String title = TestData.uniqueTitle("UI-01 Draft");
        List<String> body = List.of("First line of the draft", "Second line of the draft");

        PostsListPage list = mySite.openPosts().createDraft(title, body);
        cleanup.register(title, PostTab.DRAFTS);

        assertAll("Draft saved",
                () -> assertTrue(new Snackbar().appearsWith("Draft saved online"),
                        "The 'Draft saved online' banner should appear"),
                () -> assertTrue(list.openTab(PostTab.DRAFTS).isPostListed(title),
                        "Draft '" + title + "' should be listed in Drafts"));
    }

    @Test
    @DisplayName("UI-10 [State + Regression] Edit a draft title: the title changes and the multi-line text is kept")
    void UI_10_editDraftTitle_contentIsPersisted() {
        String originalTitle = TestData.uniqueTitle("UI-10 Original");
        String editedTitle = TestData.uniqueTitle("UI-10 Edited");
        List<String> body = List.of("Line one", "Line two", "Line three");

        PostsListPage list = mySite.openPosts().createDraft(originalTitle, body);
        cleanup.register(originalTitle, PostTab.DRAFTS);

        list.openTab(PostTab.DRAFTS).openPost(originalTitle)
                .typeTitle(editedTitle)
                .closeAndSaveDraft();
        cleanup.forget(originalTitle);
        cleanup.register(editedTitle, PostTab.DRAFTS);

        EditorPage reopened = list.openTab(PostTab.DRAFTS).openPost(editedTitle);

        assertAll("Content persisted after edit",
                () -> assertEquals(editedTitle, reopened.title(), "The new title should be saved"),
                () -> assertEquals(body, reopened.bodyLines(),
                        "The three lines of text should be kept, in the same order"));
        reopened.closeAndSaveDraft();
    }
}
