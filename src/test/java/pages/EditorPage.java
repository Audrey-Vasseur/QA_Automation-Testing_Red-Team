package pages;

import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

public class EditorPage extends BasePage {
    private static final By TITLE_FIELD = uiSelector(
            "new UiSelector().className(\"android.widget.EditText\").descriptionStartsWith(\"Post title\")");
    private static final String BODY_HINT = "Start writing…";
    private static final By EMPTY_BODY = text(BODY_HINT);
    private static final By FOCUSED_FIELD = uiSelector(
            "new UiSelector().className(\"android.widget.EditText\").focused(true)");
    private static final By PARAGRAPH_BLOCKS = uiSelector(
            "new UiSelector().descriptionStartsWith(\"Paragraph Block. Row\")");
    private static final By PRIMARY_ACTION = id("menu_primary_action");
    private static final By MORE_OPTIONS = a11y("More options");
    private static final By CLOSE_EDITOR = id("close_editor_site_icon");

    public EditorPage waitUntilLoaded() {
        visible(TITLE_FIELD);
        return this;
    }

    public EditorPage typeTitle(String title) {
        clearWithKeyboard(TITLE_FIELD);
        typeWithKeyboard(TITLE_FIELD, title);
        waiter().until(d -> title.equals(d.findElement(TITLE_FIELD).getText()));
        return this;
    }

    public String title() {
        return textOf(TITLE_FIELD);
    }

    public EditorPage typeBody(List<String> lines) {
        click(EMPTY_BODY);
        for (int i = 0; i < lines.size(); i++) {
            waitForEmptyFocusedField();
            new Actions(driver).sendKeys(lines.get(i)).perform();
            if (i < lines.size() - 1) {
                driver.pressKey(new KeyEvent(AndroidKey.ENTER));
            }
        }
        return this;
    }

    private void waitForEmptyFocusedField() {
        waiter().until(d -> {
            var focused = d.findElements(FOCUSED_FIELD);
            if (focused.isEmpty()) {
                return false;
            }
            String text = focused.get(0).getText();
            return text.isEmpty() || text.equals(BODY_HINT);
        });
    }

    public List<String> bodyLines() {
        visible(PARAGRAPH_BLOCKS);
        return driver.findElements(PARAGRAPH_BLOCKS).stream()
                .map(block -> block.getAttribute("content-desc"))
                .map(desc -> desc.replaceFirst("^Paragraph Block\\. Row \\d+\\. ", ""))
                .map(String::trim)
                .toList();
    }

    public String primaryActionLabel() {
        return textOf(PRIMARY_ACTION);
    }

    public PostSettingsPage openPostSettings() {
        click(MORE_OPTIONS);
        click(idWithText("title", "Post settings"));
        return new PostSettingsPage().waitUntilLoaded();
    }

    public PostsListPage closeAndSaveDraft() {
        WebElement close = visible(CLOSE_EDITOR);
        close.click();
        return new PostsListPage().waitUntilLoaded();
    }
}
