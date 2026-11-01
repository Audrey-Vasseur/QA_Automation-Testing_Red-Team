package utils;

import java.util.UUID;

public final class TestData {
    private TestData() {
    }

    public static String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String uniqueTitle(String prefix) {
        return prefix + " " + uniqueSuffix();
    }
}
