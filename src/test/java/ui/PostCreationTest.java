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

/**
 * Creation et modification d'articles : saisie de texte (champ titre) et
 * de texte multi-ligne (zone de contenu), puis verification de ce qui est enregistre.
 */
@Tag("ui")
class PostCreationTest extends BaseUiTest {

    @Test
    @DisplayName("UI-01 [Positif] Creer un brouillon avec titre et texte : il apparait dans l'onglet Drafts")
    void UI_01_createDraft_validData_isListedInDrafts() {
        // Donnees de test : titre unique + deux lignes de contenu
        String title = TestData.uniqueTitle("UI-01 Draft");
        List<String> body = List.of("First line of the draft", "Second line of the draft");

        PostsListPage list = mySite.openPosts().createDraft(title, body);
        cleanup.register(title, PostTab.DRAFTS);

        // L'appli confirme l'enregistrement par un bandeau, et l'article est dans la liste des brouillons.
        assertAll("Brouillon enregistre",
                () -> assertTrue(new Snackbar().appearsWith("Draft saved online"),
                        "Le bandeau de confirmation 'Draft saved online' doit apparaitre"),
                () -> assertTrue(list.openTab(PostTab.DRAFTS).isPostListed(title),
                        "Le brouillon '" + title + "' doit apparaitre dans l'onglet Drafts"));
    }

    @Test
    @DisplayName("UI-10 [Etat + Regression] Modifier le titre d'un brouillon : le titre change et le texte multi-ligne est conserve")
    void UI_10_editDraftTitle_contentIsPersisted() {
        // Precondition creee par le test lui-meme : un brouillon avec trois lignes de texte.
        String originalTitle = TestData.uniqueTitle("UI-10 Original");
        String editedTitle = TestData.uniqueTitle("UI-10 Edited");
        List<String> body = List.of("Line one", "Line two", "Line three");

        PostsListPage list = mySite.openPosts().createDraft(originalTitle, body);
        cleanup.register(originalTitle, PostTab.DRAFTS);

        // Changement : on rouvre le brouillon et on modifie seulement le titre.
        list.openTab(PostTab.DRAFTS).openPost(originalTitle)
                .typeTitle(editedTitle)
                .closeAndSaveDraft();
        cleanup.forget(originalTitle);
        cleanup.register(editedTitle, PostTab.DRAFTS);

        // Verification de l'etat enregistre : on rouvre l'article depuis la liste.
        EditorPage reopened = list.openTab(PostTab.DRAFTS).openPost(editedTitle);

        assertAll("Contenu persiste apres modification",
                () -> assertEquals(editedTitle, reopened.title(), "Le nouveau titre doit etre enregistre"),
                // Regression : modifier le titre ne doit pas abimer le contenu existant.
                () -> assertEquals(body, reopened.bodyLines(),
                        "Les trois lignes de texte doivent etre conservees, dans le meme ordre"));
        reopened.closeAndSaveDraft();
    }
}
