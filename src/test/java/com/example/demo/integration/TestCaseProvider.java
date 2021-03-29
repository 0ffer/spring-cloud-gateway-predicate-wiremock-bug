package com.example.demo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TestCaseProvider implements ArgumentsProvider {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        Path integrationTestsDataBaseDir = Path.of(new ClassPathResource("IntegrationTestCases/").getURI());

        Map<String, TestCase> testCases = new HashMap<>();

        Files.walk(integrationTestsDataBaseDir).filter(Files::isRegularFile).forEach(testCaseData -> {
            String testCaseName = testCaseData.getFileName().toString().replace(".json", "");
            TestCase testCase = readTestCase(testCaseData);

            testCases.put(testCaseName, testCase);
        });

        return testCases.entrySet().stream().map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    private TestCase readTestCase(Path path) {
        TestCase testCase = null;

        try {
            testCase = mapper.readValue(path.toFile(), TestCase.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return testCase;
    }

}