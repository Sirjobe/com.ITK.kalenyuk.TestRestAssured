package com.ITK.kalenyuk.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestDataProvider {
    public static List<String[]> loadTestData(String resourcePath) {
        List<String[]> records = new ArrayList<>();
        try (InputStream is = TestDataProvider.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            // Пропускаем заголовок CSV
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(values);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data from: " + resourcePath, e);
        }
        return records;
    }
}