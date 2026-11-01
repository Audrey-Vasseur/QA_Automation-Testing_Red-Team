package utils;

import core.DriverFactory;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Evidence {
    private static final Path DIR = Paths.get("evidence");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private Evidence() {
    }

    public static Path capture(String name) {
        if (!DriverFactory.isStarted()) {
            return null;
        }
        try {
            Files.createDirectories(DIR);
            AndroidDriver driver = DriverFactory.get();
            String base = sanitize(name) + "_" + LocalDateTime.now().format(STAMP);
            Path png = DIR.resolve(base + ".png");
            Files.write(png, driver.getScreenshotAs(OutputType.BYTES));
            Files.writeString(DIR.resolve(base + ".xml"), driver.getPageSource());
            return png;
        } catch (IOException | RuntimeException e) {
            System.err.println("[Evidence] capture failed for " + name + " : " + e.getMessage());
            return null;
        }
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
