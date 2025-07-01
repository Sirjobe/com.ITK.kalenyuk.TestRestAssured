package com.ITK.kalenyuk.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Properties config;

    static {
        config = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) throw new IOException("Не найден config.properties");
            config.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return config.getProperty(key);
    }
}
