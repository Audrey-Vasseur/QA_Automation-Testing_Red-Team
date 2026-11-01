package core;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

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
                .setAppPackage(AppConfig.get("app.package", "org.wordpress.android"))
                .setAppActivity(AppConfig.get("app.activity", "org.wordpress.android.ui.WPLaunchActivity"))
                .setAppWaitActivity("*")
                .setAppWaitDuration(Duration.ofSeconds(60))
                .setNewCommandTimeout(Duration.ofSeconds(120))
                .setNoReset(true)
                .setAutoGrantPermissions(true);
        options.setCapability("appium:forceAppLaunch", true);
        options.setCapability("appium:shouldTerminateApp", true);
        if (Boolean.parseBoolean(AppConfig.get("app.install", "false"))) {
            options.setApp(AppConfig.get("app.path"));
        }

        AndroidDriver driver = new AndroidDriver(serverUrl(), options);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        DRIVER.set(driver);
        return driver;
    }

    public static AndroidDriver get() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No driver started for this thread");
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
            throw new IllegalStateException("Invalid appium.url", e);
        }
    }
}
