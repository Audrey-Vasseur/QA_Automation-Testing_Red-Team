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

/**
 * Suppression d'articles : boite de confirmation et bandeau "UNDO".
 */
@Tag("ui")
class PostDeletionTest extends BaseUiTest {

    @Test
    @DisplayName("UI-07 [Dialogue] Suppression definitive : ANNULER garde l'article, DELETE le supprime")
    void UI_07_deleteDialog_cancelKeeps_confirmDeletes() {
        // Precondition : un article dans la corbeille, cree par ce test.
        String title = TestData.uniqueTitle("UI-07 Dialog");
        PostsListPage list = mySite.openPosts().createDraft(title, List.of());
        cleanup.register(title, PostTab.DRAFTS);
        list.openTab(PostTab.DRAFTS).trashPost(title).disappears();
        cleanup.moved(title, PostTab.TRASHED);
        list.openTab(PostTab.TRASHED);

        // 1) Annuler
        ConfirmDialog dialog = list.askPermanentDeletion(title);
        assertAll("Contenu de la boite de confirmation",
                () -> assertEquals("Delete post?", dialog.title()),
                () -> assertTrue(dialog.message().contains("permanently"), "Le message doit prevenir que c'est definitif"),
                () -> assertEquals("DELETE", dialog.positiveLabel()));
        dialog.cancel();
        assertAll("Apres ANNULER",
                () -> assertTrue(dialog.isClosed(), "La boite doit se fermer"),
                () -> assertTrue(list.isPostListed(title), "L'article doit toujours etre dans la corbeille"));

        // 2) Confirmer
        list.askPermanentDeletion(title).confirm();
        assertTrue(list.isPostGone(title), "Apres DELETE, l'article ne doit plus etre dans la corbeille");
        cleanup.forget(title); // deja supprime par le test
    }

    @Test
    @DisplayName("UI-08 [Element dynamique] Mettre a la corbeille affiche un bandeau UNDO qui restaure l'article puis disparait")
    void UI_08_trashSnackbar_undoRestoresPost() {
        String title = TestData.uniqueTitle("UI-08 Undo");
        PostsListPage list = mySite.openPosts().createDraft(title, List.of());
        cleanup.register(title, PostTab.DRAFTS);
        list.openTab(PostTab.DRAFTS);

        Snackbar snackbar = list.trashPost(title);

        // Le bandeau APPARAIT apres l'action (attente explicite, pas de pause fixe).
        assertAll("Bandeau apres la mise a la corbeille",
                () -> assertTrue(snackbar.appearsWith("Post sent to trash"), "Message 'Post sent to trash' attendu"),
                () -> assertEquals("UNDO", snackbar.actionLabel()));

        snackbar.tapAction();

        assertAll("Apres UNDO",
                () -> assertTrue(list.isPostListed(title), "L'article doit revenir dans les brouillons"),
                () -> assertTrue(snackbar.disappears(), "Le bandeau doit disparaitre de lui-meme"));
    }
}
