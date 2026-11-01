package ui;

import core.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.SiteSettingsPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
class SiteSettingsBoundaryTest extends BaseUiTest {
    private static final int MIN = 1;
    private static final int MAX = 100;

    @Test
    @DisplayName("UI-04 [Boundary] Posts per page: 1 and 100 accepted, 0 and 101 rejected")
    void UI_04_postsPerPage_boundaries() {
        SiteSettingsPage settings = mySite.openSiteSettings();
        int original = settings.postsPerPage();
        try {
            int atMin = settings.typePostsPerPage(MIN);
            boolean minSaved = settings.postsPerPageBecomes(MIN);
            int atMax = settings.typePostsPerPage(MAX);
            boolean maxSaved = settings.postsPerPageBecomes(MAX);

            int belowMin = settings.typePostsPerPage(MIN - 1);
            int savedAfterBelow = settings.postsPerPage();
            int aboveMax = settings.typePostsPerPage(MAX + 1);
            int savedAfterAbove = settings.postsPerPage();

            assertAll("Posts per page boundaries",
                    () -> assertEquals(MIN, atMin, "1 should be accepted by the picker"),
                    () -> assertTrue(minSaved, "1 should be saved"),
                    () -> assertEquals(MAX, atMax, "100 should be accepted by the picker"),
                    () -> assertTrue(maxSaved, "100 should be saved"),
                    () -> assertNotEquals(MIN - 1, belowMin, "0 should not be accepted"),
                    () -> assertTrue(isInRange(savedAfterBelow), "After 0, the saved value should stay between 1 and 100 (was: " + savedAfterBelow + ")"),
                    () -> assertNotEquals(MAX + 1, aboveMax, "101 should not be accepted"),
                    () -> assertTrue(isInRange(savedAfterAbove), "After 101, the saved value should stay between 1 and 100 (was: " + savedAfterAbove + ")"));
        } finally {
            settings.typePostsPerPage(original);
            settings.postsPerPageBecomes(original);
        }
    }

    private static boolean isInRange(int value) {
        return value >= MIN && value <= MAX;
    }
}
