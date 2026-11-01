package utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class EvidenceOnFailure implements TestExecutionExceptionHandler {
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Evidence.capture("FAILED_" + context.getRequiredTestClass().getSimpleName()
                + "_" + context.getDisplayName());
        throw throwable;
    }
}
