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

/**
 * Enregistre les preuves d'execution dans le dossier evidence/ :
 *  - une capture d'ecran (.png) pour voir ce qu'affichait le telephone ;
 *  - l'arbre des elements de l'ecran (.xml) pour retrouver les identifiants
 *    quand un localisateur casse (utile pour l'analyse d'echec, point 4.3).
 */
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
            // Une preuve qui echoue ne doit jamais masquer le vrai resultat du test.
            System.err.println("[Evidence] capture impossible pour " + name + " : " + e.getMessage());
            return null;
        }
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
