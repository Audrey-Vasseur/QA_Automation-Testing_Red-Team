package ui;

import core.BaseUiTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import utils.Evidence;

import java.time.Duration;

@Tag("tool")
@EnabledIfSystemProperty(named = "explore", matches = "true")
class AppExplorationTest extends BaseUiTest {
    @Test
    void dumpCurrentScreen() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(5).toMillis());
        Evidence.capture("explore_" + driver.currentActivity());
    }
}
