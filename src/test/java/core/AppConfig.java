package core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lit la configuration des tests UI.
 *
 * Ordre de priorite (le premier trouve gagne) :
 *   1. variable d'environnement  (ex : WP_PASSWORD)
 *   2. propriete systeme Java     (ex : -Dwp.password=...)
 *   3. fichier config.properties  (local, ignore par Git, jamais pousse)
 *   4. fichier config.example.properties (versionne, valeurs par defaut sans secret)
 *
 * Pourquoi : la consigne interdit les mots de passe dans le code source.
 * Les secrets ne vivent donc que dans l'environnement ou dans un fichier local non versionne.
 */
public final class AppConfig {

    private static final Properties PROPS = load();

    private AppConfig() {
    }

    private static Properties load() {
        Properties props = new Properties();
        readInto(props, "config.example.properties");
        readInto(props, "config.properties"); // ecrase les valeurs d'exemple si present
        return props;
    }

    private static void readInto(Properties props, String resource) {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream(resource)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire " + resource, e);
        }
    }

    /** Valeur obligatoire : echoue clairement si elle manque, au lieu d'un NullPointerException plus loin. */
    public static String get(String key) {
        String value = find(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Configuration manquante : '" + key
                    + "'. Renseigne-la dans src/test/resources/config.properties ou via la variable "
                    + toEnvName(key));
        }
        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        String value = find(key);
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    public static int getInt(String key, int defaultValue) {
        return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    }

    private static String find(String key) {
        String env = System.getenv(toEnvName(key));
        if (env != null) {
            return env;
        }
        String sys = System.getProperty(key);
        if (sys != null) {
            return sys;
        }
        return PROPS.getProperty(key);
    }

    /** wp.password -> WP_PASSWORD */
    private static String toEnvName(String key) {
        return key.toUpperCase().replace('.', '_');
    }
}
