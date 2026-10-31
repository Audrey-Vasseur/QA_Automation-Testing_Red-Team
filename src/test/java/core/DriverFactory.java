package core;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

/**
 * Cree et range le driver Appium de chaque thread.
 *
 * ThreadLocal = chaque thread de test a SA propre variable "driver".
 * Quand JUnit lance deux tests en parallele, ils ne partagent donc jamais
 * le meme driver (pas d'etat mutable partage, exigence du point 7 de la consigne).
 */
public final class DriverFactory {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<DevicePool.Device> DEVICE = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static AndroidDriver start() {
        DevicePool.Device device = DevicePool.acquire();
        DEVICE.set(device);

        UiAutomator2Options options = new UiAutomator2Options()
                .setUdid(device.udid())
                .setSystemPort(device.systemPort())
                // L'appli est deja installee (et connectee au compte de test) sur le telephone :
                // on la lance directement par son package et son ecran de demarrage.
                .setAppPackage(AppConfig.get("app.package", "org.wordpress.android"))
                .setAppActivity(AppConfig.get("app.activity", "org.wordpress.android.ui.WPLaunchActivity"))
                .setAppWaitActivity("*")
                .setAppWaitDuration(Duration.ofSeconds(60))
                .setNewCommandTimeout(Duration.ofSeconds(120))
                // noReset : on garde le compte WordPress connecte entre deux tests
                // (pas de mot de passe a saisir a chaque test).
                .setNoReset(true)
                .setAutoGrantPermissions(true);
        // ...mais on redemarre l'appli a chaque session : chaque test part
        // toujours du meme ecran ("My Site"), quel que soit le test precedent.
        options.setCapability("appium:forceAppLaunch", true);
        options.setCapability("appium:shouldTerminateApp", true);
        // Option : faire installer l'APK par Appium (necessite les "build-tools" Android, outil aapt2).
        if (Boolean.parseBoolean(AppConfig.get("app.install", "false"))) {
            options.setApp(AppConfig.get("app.path"));
        }

        AndroidDriver driver = new AndroidDriver(serverUrl(), options);
        // Pas d'attente implicite : on n'utilise que des attentes explicites (BasePage).
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        DRIVER.set(driver);
        return driver;
    }

    public static AndroidDriver get() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("Aucun driver demarre pour ce thread");
        }
        return driver;
    }

    public static boolean isStarted() {
        return DRIVER.get() != null;
    }

    public static void quit() {
        try {
            AndroidDriver driver = DRIVER.get();
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DRIVER.remove();
            DevicePool.release(DEVICE.get());
            DEVICE.remove();
        }
    }

    private static URL serverUrl() {
        try {
            return URI.create(AppConfig.get("appium.url", "http://127.0.0.1:4723")).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("appium.url invalide", e);
        }
    }
}
