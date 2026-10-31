package utils;

import java.util.UUID;

/**
 * Genere des donnees uniques pour chaque test.
 *
 * Deux tests paralleles qui creent un article "Mon article" se marcheraient dessus.
 * Avec un suffixe unique, chaque test ne retrouve et ne supprime que SES donnees.
 */
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
