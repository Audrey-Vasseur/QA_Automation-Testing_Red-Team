package pages;

import org.openqa.selenium.By;
import pages.components.ConfirmDialog;

public class PostSettingsPage extends BasePage {
    private static final By STATUS_ROW = id("post_status_container");
    private static final By STATUS_VALUE = id("post_status");
    private static final By PUBLISH_DATE_ROW = id("publish_date_container");
    private static final By PUBLISH_DATE_VALUE = id("publish_date");

    public PostSettingsPage waitUntilLoaded() {
        visible(STATUS_VALUE);
        return this;
    }

    public String status() {
        return textOf(STATUS_VALUE);
    }

    public PostSettingsPage selectStatus(String option) {
        click(STATUS_ROW);
        ConfirmDialog dropdown = new ConfirmDialog();
        dropdown.chooseOption(option);
        dropdown.confirm();
        dropdown.isClosed();
        return this;
    }

    public boolean isStatusOptionChecked(String option) {
        click(STATUS_ROW);
        ConfirmDialog dropdown = new ConfirmDialog();
        boolean checked = dropdown.isOptionChecked(option);
        dropdown.cancel();
        dropdown.isClosed();
        return checked;
    }

    public String publishDate() {
        return textOf(PUBLISH_DATE_VALUE);
    }

    public PublishDatePage openPublishDate() {
        click(PUBLISH_DATE_ROW);
        return new PublishDatePage().waitUntilLoaded();
    }

    public EditorPage backToEditor() {
        pressBack();
        return new EditorPage().waitUntilLoaded();
    }
}
