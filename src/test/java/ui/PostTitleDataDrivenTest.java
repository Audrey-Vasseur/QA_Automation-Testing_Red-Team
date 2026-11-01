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

@Tag("ui")
class PostTitleDataDrivenTest extends BaseUiTest {
    @ParameterizedTest(name = "UI-11 [{index}] {0}")
    @DisplayName("UI-11 [Data-driven] Create a draft with different kinds of titles")
    @CsvFileSource(resources = "/testdata/post_titles.csv", delimiter = ';', numLinesToSkip = 1, encoding = "UTF-8")
    void UI_11_createDraft_withVariousTitles(String caseName, String titleData) {
        String title = titleData + " #" + TestData.uniqueSuffix();

        PostsListPage list = mySite.openPosts().createDraft(title, List.of("Data-driven case: " + caseName));
        cleanup.register(title, PostTab.DRAFTS);

        assertTrue(list.openTab(PostTab.DRAFTS).isPostListed(title),
                "[" + caseName + "] the title should be listed unchanged in Drafts: " + title);
    }
}
