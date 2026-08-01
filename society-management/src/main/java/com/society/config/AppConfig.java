package com.society.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class AppConfig {

    private static final Properties PROPERTIES = new Properties();

    static {

        try (InputStream inputStream =
                     AppConfig.class
                             .getClassLoader()
                             .getResourceAsStream("application.properties")) {

            Objects.requireNonNull(inputStream,
                    "application.properties not found.");

            PROPERTIES.load(inputStream);

        } catch (IOException e) {

            throw new RuntimeException("Unable to load configuration.", e);

        }

    }

    private AppConfig() {
    }

    public static String get(String key) {

        return PROPERTIES.getProperty(key);

    }

    public static String get(String key, String defaultValue) {

        return PROPERTIES.getProperty(key, defaultValue);

    }

}