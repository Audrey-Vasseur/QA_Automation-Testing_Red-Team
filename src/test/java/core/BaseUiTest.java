package core;

import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.MySitePage;
import utils.EvidenceOnFailure;
import utils.PostCleanup;

/**
 * Classe mere de tous les tests UI.
 *
 *  - @BeforeEach : chaque test ouvre SA propre session Appium et attend l'ecran "My Site".
 *                  C'est la precondition commune : appli lancee, compte de test connecte.
 *  - @AfterEach  : chaque test supprime les articles qu'il a crees, puis ferme sa session
 *                  et rend son telephone, meme si le test a echoue.
 *  - EvidenceOnFailure : capture d'ecran + arbre XML automatiques si le test echoue.
 */
@ExtendWith(EvidenceOnFailure.class)
public abstract class BaseUiTest {

    protected AndroidDriver driver;
    protected MySitePage mySite;
    /** Nouvel objet a chaque test (JUnit recree la classe de test) : rien n'est partage. */
    protected final PostCleanup cleanup = new PostCleanup();

    @BeforeEach
    void startSession() {
        driver = DriverFactory.start();
        mySite = new MySitePage().waitUntilLoaded();
    }

    @AfterEach
    void stopSession() {
        try {
            cleanup.deleteAll();
        } finally {
            DriverFactory.quit();
        }
    }
}
