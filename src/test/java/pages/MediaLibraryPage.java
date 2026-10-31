package pages;

import org.openqa.selenium.By;
import pages.components.ConfirmDialog;

import java.time.Duration;

/**
 * Mediatheque du site (menu Media) : envoi d'images et etat de chaque envoi.
 */
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

    /**
     * "+" -> "Choose from device" -> on selectionne l'image la plus recente du telephone
     * (celle que le test vient d'y deposer) -> Confirm. L'envoi vers le site demarre alors.
     */
    public MediaLibraryPage uploadNewestDeviceImage() {
        click(ADD_MEDIA);
        click(androidIdWithText("title", "Choose from device"));
        click(NEWEST_DEVICE_IMAGE);
        click(CONFIRM_SELECTION);
        waitUntilLoaded();
        return this;
    }

    /** Vignette d'un media, identifiee par son nom de fichier (libelle d'accessibilite). */
    private static By mediaItem(String fileName) {
        return a11y("media preview, filename " + fileName);
    }

    public boolean isMediaListed(String fileName) {
        return isShown(mediaItem(fileName), Duration.ofSeconds(20));
    }

    public boolean isMediaGone(String fileName) {
        return isGone(mediaItem(fileName), Duration.ofSeconds(15));
    }

    /**
     * Attend la fin de l'envoi. Pendant l'envoi la vignette affiche un etat
     * ("Uploading", puis "Upload Failed" en cas d'echec) ; un envoi reussi n'affiche plus d'etat.
     *
     * @return l'etat final : "Uploaded" si l'etat a disparu, sinon le texte affiche (ex : "Upload Failed")
     */
    public String waitForUploadResult(String fileName) {
        visible(mediaItem(fileName)); // d'abord : la vignette de CE fichier doit exister
        // WebDriverWait relance la fonction toutes les 500 ms jusqu'a ce qu'elle renvoie autre chose que null.
        return waiter(Duration.ofSeconds(60)).until(d -> {
            var states = d.findElements(UPLOAD_STATE);
            if (states.isEmpty()) {
                return "Uploaded";
            }
            String state = states.get(0).getText();
            return state.toLowerCase().contains("failed") ? state : null; // null = encore en cours
        });
    }

    /** Ouvre le detail d'un media envoye et renvoie le nom de fichier affiche par le site. */
    public String fileNameShownInDetail(String fileName) {
        click(mediaItem(fileName));
        String shown = textOf(DETAIL_FILENAME);
        pressBack();
        waitUntilLoaded();
        return shown;
    }

    /** Nettoyage d'un media envoye : detail -> Trash -> DELETE. */
    public void deleteUploadedMedia(String fileName) {
        click(mediaItem(fileName));
        click(DETAIL_TRASH);
        new ConfirmDialog().confirm();
        waitUntilLoaded();
        isMediaGone(fileName);
    }

    /** Nettoyage d'un envoi echoue : l'icone corbeille sur la vignette le retire directement. */
    public void removeFailedUpload(String fileName) {
        click(TRASH_FAILED_UPLOAD);
        isMediaGone(fileName);
    }

    private static By androidIdWithText(String androidResourceId, String visibleText) {
        return uiSelector("new UiSelector().resourceId(\"android:id/" + androidResourceId + "\").text(" + quote(visibleText) + ")");
    }
}
