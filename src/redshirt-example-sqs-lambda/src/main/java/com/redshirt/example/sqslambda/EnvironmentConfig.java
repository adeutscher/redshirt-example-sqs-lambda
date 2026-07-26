package com.redshirt.example.sqslambda;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Environment-variable configuration with segment aliases, mirroring the .NET
 * {@code AddEnvironmentVariablesWithSegmentSupport} helper.
 * Nested segments use {@code __} in env vars and {@code .} in lookups.
 * Lookups are case-insensitive (matching Microsoft.Extensions.Configuration).
 */
public final class EnvironmentConfig {
    private static final Pattern SEGMENT_SEPARATOR = Pattern.compile("__+");

    private final Map<String, String> values;

    private EnvironmentConfig(Map<String, String> values) {
        this.values = values;
    }

    public static EnvironmentConfig fromEnvironment() {
        return from(System.getenv());
    }

    public static EnvironmentConfig from(Map<String, String> rawEnvironmentVariables) {
        Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (Map.Entry<String, String> entry : rawEnvironmentVariables.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Set<String> keySet = new HashSet<>();

            putAlias(result, keySet, key, value);

            String simplified = key.replace("_", "");
            putAlias(result, keySet, simplified, value);

            String parsed = SEGMENT_SEPARATOR.matcher(key).replaceAll(".");
            if (keySet.add(parsed)) {
                result.put(parsed, value);

                String simplified2 = parsed.replace("_", "");
                putAlias(result, keySet, simplified2, value);
            }
        }

        return new EnvironmentConfig(Collections.unmodifiableMap(result));
    }

    private static void putAlias(Map<String, String> result, Set<String> keySet, String key, String value) {
        if (keySet.add(key)) {
            result.put(key, value);
        }
    }

    public String get(String key) {
        return values.get(key);
    }

    public Map<String, String> asMap() {
        return values;
    }
}
