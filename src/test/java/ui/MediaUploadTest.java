package ui;

import core.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.MediaLibraryPage;
import utils.DeviceFiles;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
class MediaUploadTest extends BaseUiTest {
    @Test
    @DisplayName("UI-09 [Valid file] Upload a PNG image: upload succeeds and the file name is shown")
    void UI_09_uploadValidImage_isAccepted() {
        String fileName = "ui09_" + TestData.uniqueSuffix() + ".png";
        String devicePath = DeviceFiles.pushToGallery("valid_image.png", fileName);
        MediaLibraryPage media = mySite.openMedia();
        try {
            media.uploadNewestDeviceImage();
            String result = media.waitForUploadResult(fileName);

            assertAll("Image accepted",
                    () -> assertEquals("Uploaded", result, "The upload should finish without error"),
                    () -> assertTrue(media.isMediaListed(fileName), "The image should be listed in the media library"),
                    () -> assertEquals(fileName, media.fileNameShownInDetail(fileName),
                            "The media detail should show the uploaded file name"));
        } finally {
            if (media.isMediaListed(fileName)) {
                media.deleteUploadedMedia(fileName);
            }
            DeviceFiles.removeFromGallery(devicePath);
        }
    }

    @Test
    @DisplayName("UI-12 [Expected failure] Upload a file that is not an image: upload is rejected ('Upload Failed')")
    void UI_12_uploadFakeImage_isRejected() {
        String fileName = "ui12_" + TestData.uniqueSuffix() + ".png";
        String devicePath = DeviceFiles.pushToGallery("not_an_image.png", fileName);
        MediaLibraryPage media = mySite.openMedia();
        try {
            media.uploadNewestDeviceImage();
            String result = media.waitForUploadResult(fileName);

            assertEquals("Upload Failed", result,
                    "A text file renamed .png should be rejected with 'Upload Failed'");
        } finally {
            if (media.isMediaListed(fileName)) {
                media.removeFailedUpload(fileName);
            }
            DeviceFiles.removeFromGallery(devicePath);
        }
    }
}
