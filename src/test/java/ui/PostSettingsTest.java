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

/**
 * Reglages d'un article : liste deroulante du statut et selecteur de date de publication.
 */
@Tag("ui")
class PostSettingsTest extends BaseUiTest {

    /** Format de date affiche par l'appli, ex : "Nov 18, 2026". */
    private static final DateTimeFormatter SHOWN_DATE = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    @Test
    @DisplayName("UI-05 [Liste deroulante] Choisir un statut : la valeur choisie est affichee et cochee")
    void UI_05_selectStatus_selectedValueIsShown() {
        String title = TestData.uniqueTitle("UI-05 Status");
        EditorPage editor = mySite.openPosts().createPost().typeTitle(title);
        PostSettingsPage settings = editor.openPostSettings();

        assertEquals("Draft", settings.status(), "Un nouvel article commence en statut Draft");

        settings.selectStatus("Pending review");
        assertAll("Option 'Pending review' selectionnee",
                () -> assertEquals("Pending review", settings.status()),
                () -> assertTrue(settings.isStatusOptionChecked("Pending review"),
                        "L'option doit etre cochee quand on rouvre la liste"));

        settings.selectStatus("Private");
        assertEquals("Private", settings.status(), "Le statut doit suivre le dernier choix");

        // On remet "Draft" pour que la fermeture de l'editeur n'envoie rien en ligne.
        settings.selectStatus("Draft");
        settings.backToEditor().closeAndSaveDraft();
        cleanup.register(title, PostTab.DRAFTS);
    }

    @Test
    @DisplayName("UI-06 [Date] Date future = programmation ; date passee = antidatee, pas de programmation")
    void UI_06_publishDate_futureIsScheduled_pastIsBackdated() {
        String title = TestData.uniqueTitle("UI-06 Date");
        LocalDate futureDate = LocalDate.now().plusMonths(1).withDayOfMonth(15);
        LocalDate pastDate = LocalDate.now().minusMonths(2).withDayOfMonth(1);

        EditorPage editor = mySite.openPosts().createPost().typeTitle(title);
        cleanup.register(title, PostTab.DRAFTS);

        // 1) Date future : l'article est programme.
        PublishDatePage datePage = editor.openPostSettings().openPublishDate().pickDate(futureDate);
        String futureLabel = datePage.dateLabel();
        editor = datePage.backToSettings().backToEditor();
        String actionForFuture = editor.primaryActionLabel();

        assertAll("Date future autorisee pour une programmation",
                () -> assertTrue(futureLabel.startsWith("Schedule for: " + futureDate.format(SHOWN_DATE)),
                        "Libelle attendu 'Schedule for: " + futureDate.format(SHOWN_DATE) + "...' mais vu : " + futureLabel),
                () -> assertEquals("SCHEDULE", actionForFuture, "Le bouton principal doit devenir SCHEDULE"));

        // 2) Date passee : l'appli refuse de "programmer" dans le passe, elle antidate l'article.
        datePage = editor.openPostSettings().openPublishDate().pickDate(pastDate);
        String pastLabel = datePage.dateLabel();
        editor = datePage.backToSettings().backToEditor();
        String actionForPast = editor.primaryActionLabel();

        assertAll("Date passee : pas de programmation possible",
                () -> assertTrue(pastLabel.startsWith("Backdated for: " + pastDate.format(SHOWN_DATE)),
                        "Libelle attendu 'Backdated for: " + pastDate.format(SHOWN_DATE) + "...' mais vu : " + pastLabel),
                () -> assertEquals("PUBLISH", actionForPast, "Le bouton principal doit redevenir PUBLISH"));

        editor.closeAndSaveDraft();
    }
}
