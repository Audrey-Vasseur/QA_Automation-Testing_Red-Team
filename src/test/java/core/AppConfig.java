package core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {
    private static final Properties PROPS = load();

    private AppConfig() {
    }

    private static Properties load() {
        Properties props = new Properties();
        readInto(props, "config.example.properties");
        readInto(props, "config.properties");
        return props;
    }

    private static void readInto(Properties props, String resource) {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream(resource)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + resource, e);
        }
    }

    public static String get(String key) {
        String value = find(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing configuration: '" + key
                    + "'. Set it in src/test/resources/config.properties or with the environment variable "
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

    private static String toEnvName(String key) {
        return key.toUpperCase().replace('.', '_');
    }
}
