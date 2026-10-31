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

/**
 * Champ numerique "Posts per page" (Site Settings). Plage autorisee observee dans l'appli : 1 a 100.
 */
@Tag("ui")
class SiteSettingsBoundaryTest extends BaseUiTest {

    private static final int MIN = 1;
    private static final int MAX = 100;

    @Test
    @DisplayName("UI-04 [Limites] Posts per page : 1 et 100 acceptes, 0 et 101 refuses")
    void UI_04_postsPerPage_boundaries() {
        SiteSettingsPage settings = mySite.openSiteSettings();
        int original = settings.postsPerPage();
        try {
            // A la limite : acceptees et enregistrees
            int atMin = settings.typePostsPerPage(MIN);
            boolean minSaved = settings.postsPerPageBecomes(MIN);
            int atMax = settings.typePostsPerPage(MAX);
            boolean maxSaved = settings.postsPerPageBecomes(MAX);

            // Juste en dehors : le selecteur doit refuser la valeur
            int belowMin = settings.typePostsPerPage(MIN - 1);
            int savedAfterBelow = settings.postsPerPage();
            int aboveMax = settings.typePostsPerPage(MAX + 1);
            int savedAfterAbove = settings.postsPerPage();

            assertAll("Limites du champ Posts per page",
                    () -> assertEquals(MIN, atMin, "1 doit etre accepte par le selecteur"),
                    () -> assertTrue(minSaved, "1 doit etre enregistre"),
                    () -> assertEquals(MAX, atMax, "100 doit etre accepte par le selecteur"),
                    () -> assertTrue(maxSaved, "100 doit etre enregistre"),
                    () -> assertNotEquals(MIN - 1, belowMin, "0 ne doit pas etre accepte"),
                    () -> assertTrue(isInRange(savedAfterBelow), "Apres 0, la valeur enregistree doit rester entre 1 et 100 (vu : " + savedAfterBelow + ")"),
                    () -> assertNotEquals(MAX + 1, aboveMax, "101 ne doit pas etre accepte"),
                    () -> assertTrue(isInRange(savedAfterAbove), "Apres 101, la valeur enregistree doit rester entre 1 et 100 (vu : " + savedAfterAbove + ")"));
        } finally {
            // Nettoyage : on remet le reglage tel qu'on l'a trouve.
            settings.typePostsPerPage(original);
            settings.postsPerPageBecomes(original);
        }
    }

    private static boolean isInRange(int value) {
        return value >= MIN && value <= MAX;
    }
}
