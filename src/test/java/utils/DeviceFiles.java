package utils;

import core.DriverFactory;
import io.appium.java_client.android.AndroidDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Depose des fichiers de test dans la galerie du telephone, puis les retire.
 *
 * Les fichiers viennent de src/test/resources/testdata/files : ce sont des donnees de test
 * versionnees, identiques sur toutes les machines. Chaque test les depose sous un nom UNIQUE,
 * pour que deux executions (ou deux tests en parallele) ne se confondent jamais.
 */
public final class DeviceFiles {

    private static final String GALLERY_DIR = "/sdcard/Pictures/";

    private DeviceFiles() {
    }

    /** @return le chemin du fichier sur le telephone */
    public static String pushToGallery(String resourceName, String deviceFileName) {
        AndroidDriver driver = DriverFactory.get();
        String devicePath = GALLERY_DIR + deviceFileName;
        // L'API Appium attend le contenu encode en Base64.
        driver.pushFile(devicePath, Base64.getEncoder().encode(readResource("testdata/files/" + resourceName)));
        // Prevenir Android qu'un nouveau fichier existe, sinon il n'apparait pas dans la galerie.
        scanMedia(driver, devicePath);
        return devicePath;
    }

    public static void removeFromGallery(String devicePath) {
        AndroidDriver driver = DriverFactory.get();
        driver.executeScript("mobile: shell", Map.of("command", "rm", "args", List.of("-f", devicePath)));
        scanMedia(driver, devicePath);
    }

    private static void scanMedia(AndroidDriver driver, String devicePath) {
        driver.executeScript("mobile: shell", Map.of("command", "am", "args", List.of(
                "broadcast", "-a", "android.intent.action.MEDIA_SCANNER_SCAN_FILE", "-d", "file://" + devicePath)));
    }

    private static byte[] readResource(String path) {
        try (InputStream in = DeviceFiles.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Fichier de test introuvable : " + path);
            }
            return in.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Lecture impossible : " + path, e);
        }
    }
}
