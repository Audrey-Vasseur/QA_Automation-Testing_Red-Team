package core;

import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.MySitePage;
import utils.EvidenceOnFailure;
import utils.PostCleanup;

@ExtendWith(EvidenceOnFailure.class)
public abstract class BaseUiTest {
    protected AndroidDriver driver;
    protected MySitePage mySite;
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
