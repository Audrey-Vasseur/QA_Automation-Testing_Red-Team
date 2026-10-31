package ui;

import core.BaseUiTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import utils.Evidence;

import java.time.Duration;

/**
 * Outil de travail, PAS un test de la suite : il ouvre l'appli et enregistre
 * l'ecran + l'arbre XML dans evidence/, pour recuperer les resource-id
 * (meme role qu'Appium Inspector).
 *
 * Desactive par defaut. Pour le lancer : ajouter -Dexplore=true dans la configuration d'execution.
 */
@Tag("tool")
@EnabledIfSystemProperty(named = "explore", matches = "true")
class AppExplorationTest extends BaseUiTest {

    @Test
    void dumpCurrentScreen() throws InterruptedException {
        // Seule pause fixe du projet : outil manuel, on laisse l'ecran se stabiliser avant la capture.
        Thread.sleep(Duration.ofSeconds(5).toMillis());
        Evidence.capture("explore_" + driver.currentActivity());
    }
}
