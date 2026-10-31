package utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

/**
 * Extension JUnit : quand un test echoue, on prend une capture + l'arbre XML
 * AVANT que le @AfterEach ferme le driver, puis on relance l'erreur telle quelle.
 *
 * On ne "rattrape" pas l'erreur : le test reste en echec (on ne cache pas un vrai defaut).
 */
public class EvidenceOnFailure implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Evidence.capture("FAILED_" + context.getRequiredTestClass().getSimpleName()
                + "_" + context.getDisplayName());
        throw throwable;
    }
}
