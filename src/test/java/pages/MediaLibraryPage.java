package pages;

import org.openqa.selenium.By;
import pages.components.ConfirmDialog;

import java.time.Duration;

public class MediaLibraryPage extends BasePage {
    private static final By ADD_MEDIA = id("menu_new_media");
    private static final By NEWEST_DEVICE_IMAGE = uiSelector(
            "new UiSelector().resourceId(\"" + APP_ID + "image_thumbnail\").instance(0)");
    private static final By CONFIRM_SELECTION = id("mnu_confirm_selection");
    private static final By UPLOAD_STATE = id("media_grid_item_upload_state");
    private static final By TRASH_FAILED_UPLOAD = a11y("trash");
    private static final By DETAIL_TRASH = id("menu_trash");
    private static final By DETAIL_FILENAME = id("text_filename");
    private static final By QUOTA = id("quota_text");

    public MediaLibraryPage waitUntilLoaded() {
        visible(QUOTA);
        return this;
    }

    public MediaLibraryPage uploadNewestDeviceImage() {
        click(ADD_MEDIA);
        click(androidIdWithText("title", "Choose from device"));
        click(NEWEST_DEVICE_IMAGE);
        click(CONFIRM_SELECTION);
        waitUntilLoaded();
        return this;
    }

    private static By mediaItem(String fileName) {
        return a11y("media preview, filename " + fileName);
    }

    public boolean isMediaListed(String fileName) {
        return isShown(mediaItem(fileName), Duration.ofSeconds(20));
    }

    public boolean isMediaGone(String fileName) {
        return isGone(mediaItem(fileName), Duration.ofSeconds(15));
    }

    public String waitForUploadResult(String fileName) {
        visible(mediaItem(fileName));
        return waiter(Duration.ofSeconds(60)).until(d -> {
            var states = d.findElements(UPLOAD_STATE);
            if (states.isEmpty()) {
                return "Uploaded";
            }
            String state = states.get(0).getText();
            return state.toLowerCase().contains("failed") ? state : null;
        });
    }

    public String fileNameShownInDetail(String fileName) {
        click(mediaItem(fileName));
        String shown = textOf(DETAIL_FILENAME);
        pressBack();
        waitUntilLoaded();
        return shown;
    }

    public void deleteUploadedMedia(String fileName) {
        click(mediaItem(fileName));
        click(DETAIL_TRASH);
        new ConfirmDialog().confirm();
        waitUntilLoaded();
        isMediaGone(fileName);
    }

    public void removeFailedUpload(String fileName) {
        click(TRASH_FAILED_UPLOAD);
        isMediaGone(fileName);
    }

    private static By androidIdWithText(String androidResourceId, String visibleText) {
        return uiSelector("new UiSelector().resourceId(\"android:id/" + androidResourceId + "\").text(" + quote(visibleText) + ")");
    }
}
