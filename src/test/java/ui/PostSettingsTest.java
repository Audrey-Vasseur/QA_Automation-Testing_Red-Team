package ui;

import core.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.EditorPage;
import pages.PostSettingsPage;
import pages.PostTab;
import pages.PublishDatePage;
import utils.TestData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
class PostSettingsTest extends BaseUiTest {
    private static final DateTimeFormatter SHOWN_DATE = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    @Test
    @DisplayName("UI-05 [Dropdown] Select a status: the selected value is shown and checked")
    void UI_05_selectStatus_selectedValueIsShown() {
        String title = TestData.uniqueTitle("UI-05 Status");
        EditorPage editor = mySite.openPosts().createPost().typeTitle(title);
        PostSettingsPage settings = editor.openPostSettings();

        assertEquals("Draft", settings.status(), "A new post starts as Draft");

        settings.selectStatus("Pending review");
        assertAll("Option 'Pending review' selected",
                () -> assertEquals("Pending review", settings.status()),
                () -> assertTrue(settings.isStatusOptionChecked("Pending review"),
                        "The option should be checked when the list is reopened"));

        settings.selectStatus("Private");
        assertEquals("Private", settings.status(), "The status should follow the last choice");

        settings.selectStatus("Draft");
        settings.backToEditor().closeAndSaveDraft();
        cleanup.register(title, PostTab.DRAFTS);
    }

    @Test
    @DisplayName("UI-06 [Date] Future date is scheduled, past date is backdated and cannot be scheduled")
    void UI_06_publishDate_futureIsScheduled_pastIsBackdated() {
        String title = TestData.uniqueTitle("UI-06 Date");
        LocalDate futureDate = LocalDate.now().plusMonths(1).withDayOfMonth(15);
        LocalDate pastDate = LocalDate.now().minusMonths(2).withDayOfMonth(1);

        EditorPage editor = mySite.openPosts().createPost().typeTitle(title);
        cleanup.register(title, PostTab.DRAFTS);

        PublishDatePage datePage = editor.openPostSettings().openPublishDate().pickDate(futureDate);
        String futureLabel = datePage.dateLabel();
        editor = datePage.backToSettings().backToEditor();
        String actionForFuture = editor.primaryActionLabel();

        assertAll("Future date allows scheduling",
                () -> assertTrue(futureLabel.startsWith("Schedule for: " + futureDate.format(SHOWN_DATE)),
                        "Expected label 'Schedule for: " + futureDate.format(SHOWN_DATE) + "...' but was: " + futureLabel),
                () -> assertEquals("SCHEDULE", actionForFuture, "The primary button should become SCHEDULE"));

        datePage = editor.openPostSettings().openPublishDate().pickDate(pastDate);
        String pastLabel = datePage.dateLabel();
        editor = datePage.backToSettings().backToEditor();
        String actionForPast = editor.primaryActionLabel();

        assertAll("Past date: no scheduling possible",
                () -> assertTrue(pastLabel.startsWith("Backdated for: " + pastDate.format(SHOWN_DATE)),
                        "Expected label 'Backdated for: " + pastDate.format(SHOWN_DATE) + "...' but was: " + pastLabel),
                () -> assertEquals("PUBLISH", actionForPast, "The primary button should be PUBLISH again"));

        editor.closeAndSaveDraft();
    }
}
