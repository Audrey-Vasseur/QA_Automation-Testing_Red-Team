package ui;

import core.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import pages.PostTab;
import pages.PostsListPage;
import utils.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test pilote par les donnees : LA MEME logique est executee pour chaque ligne du fichier
 * src/test/resources/testdata/post_titles.csv (une execution = une ligne du rapport JUnit).
 *
 * Pour ajouter un cas, on ajoute une ligne au CSV : aucune ligne de Java a modifier.
 */
@Tag("ui")
class PostTitleDataDrivenTest extends BaseUiTest {

    @ParameterizedTest(name = "UI-11 [{index}] {0}")
    @DisplayName("UI-11 [Data-driven] Creer un brouillon avec differents types de titres")
    @CsvFileSource(resources = "/testdata/post_titles.csv", delimiter = ';', numLinesToSkip = 1, encoding = "UTF-8")
    void UI_11_createDraft_withVariousTitles(String caseName, String titleData) {
        // Suffixe unique : chaque execution cree SON article, meme si le CSV est rejoue.
        String title = titleData + " #" + TestData.uniqueSuffix();

        PostsListPage list = mySite.openPosts().createDraft(title, List.of("Data-driven case: " + caseName));
        cleanup.register(title, PostTab.DRAFTS);

        assertTrue(list.openTab(PostTab.DRAFTS).isPostListed(title),
                "[" + caseName + "] le titre doit etre affiche a l'identique dans les brouillons : " + title);
    }
}
