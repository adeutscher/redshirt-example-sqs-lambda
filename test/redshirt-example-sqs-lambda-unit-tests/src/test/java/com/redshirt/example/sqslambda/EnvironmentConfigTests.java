package com.redshirt.example.sqslambda;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvironmentConfigTests {
    @ParameterizedTest
    @CsvSource({
            "A, A, B",
            "A, a, B",
            "A_B, A_B, C",
            "C_D, CD, E",
            "A__B, A.B, C",
            "X__Y, X__Y, Z",
            "C, C, D"
    })
    void from_mapsEnvironmentAliases(String environmentKey, String configurationKey, String value) {
        EnvironmentConfig configuration = EnvironmentConfig.from(Map.of(environmentKey, value));
        assertEquals(value, configuration.get(configurationKey));
    }
}
