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

/**
 * Envoi d'images dans la mediatheque : une image valide est acceptee, un faux fichier image est rejete.
 *
 * Fichiers de test (src/test/resources/testdata/files) :
 *  - valid_image.png   : vraie image PNG (200 x 120 px)
 *  - not_an_image.png  : fichier TEXTE renomme en .png (format non supporte deguise)
 */
@Tag("ui")
class MediaUploadTest extends BaseUiTest {

    @Test
    @DisplayName("UI-09 [Fichier valide] Envoyer une image PNG : envoi reussi et nom de fichier affiche par le site")
    void UI_09_uploadValidImage_isAccepted() {
        String fileName = "ui09_" + TestData.uniqueSuffix() + ".png";
        String devicePath = DeviceFiles.pushToGallery("valid_image.png", fileName);
        MediaLibraryPage media = mySite.openMedia();
        try {
            media.uploadNewestDeviceImage();
            String result = media.waitForUploadResult(fileName);

            assertAll("Image acceptee",
                    () -> assertEquals("Uploaded", result, "L'envoi doit se terminer sans erreur"),
                    () -> assertTrue(media.isMediaListed(fileName), "L'image doit apparaitre dans la mediatheque"),
                    () -> assertEquals(fileName, media.fileNameShownInDetail(fileName),
                            "Le detail du media doit afficher le nom du fichier envoye"));
        } finally {
            if (media.isMediaListed(fileName)) {
                media.deleteUploadedMedia(fileName);
            }
            DeviceFiles.removeFromGallery(devicePath);
        }
    }

    @Test
    @DisplayName("UI-12 [Echec attendu] Envoyer un fichier qui n'est pas une image : l'envoi est rejete ('Upload Failed')")
    void UI_12_uploadFakeImage_isRejected() {
        String fileName = "ui12_" + TestData.uniqueSuffix() + ".png";
        String devicePath = DeviceFiles.pushToGallery("not_an_image.png", fileName);
        MediaLibraryPage media = mySite.openMedia();
        try {
            media.uploadNewestDeviceImage();
            String result = media.waitForUploadResult(fileName);

            // Comportement attendu : le serveur refuse le fichier et l'appli l'affiche clairement.
            assertEquals("Upload Failed", result,
                    "Un fichier texte deguise en .png doit etre refuse avec l'etat 'Upload Failed'");
        } finally {
            if (media.isMediaListed(fileName)) {
                media.removeFailedUpload(fileName);
            }
            DeviceFiles.removeFromGallery(devicePath);
        }
    }
}
