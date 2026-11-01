package pages;

import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import pages.components.ConfirmDialog;

public class SiteSettingsPage extends BasePage {
    private static final String POSTS_PER_PAGE = "Posts per page";
    private static final By SETTINGS_LIST = androidId("list");
    private static final By NUMBER_INPUT = androidId("numberpicker_input");

    private static final By POSTS_PER_PAGE_VALUE = uiSelector(
            "new UiSelector().resourceId(\"android:id/title\").text(\"" + POSTS_PER_PAGE + "\")"
                    + ".fromParent(new UiSelector().resourceId(\"android:id/summary\"))");

    public SiteSettingsPage waitUntilLoaded() {
        visible(SETTINGS_LIST);
        return this;
    }

    public int postsPerPage() {
        scrollToText(POSTS_PER_PAGE);
        return Integer.parseInt(textOf(POSTS_PER_PAGE_VALUE).trim());
    }

    public boolean postsPerPageBecomes(int expected) {
        scrollToText(POSTS_PER_PAGE);
        return isShown(uiSelector(
                "new UiSelector().resourceId(\"android:id/title\").text(\"" + POSTS_PER_PAGE + "\")"
                        + ".fromParent(new UiSelector().resourceId(\"android:id/summary\").text(\"" + expected + "\"))"),
                java.time.Duration.ofSeconds(10));
    }

    public int typePostsPerPage(int value) {
        scrollToText(POSTS_PER_PAGE).click();
        type(NUMBER_INPUT, String.valueOf(value));
        driver.pressKey(new KeyEvent(AndroidKey.ENTER));
        String shownInPicker = textOf(NUMBER_INPUT);
        new ConfirmDialog().confirm();
        return Integer.parseInt(shownInPicker.trim());
    }
}
