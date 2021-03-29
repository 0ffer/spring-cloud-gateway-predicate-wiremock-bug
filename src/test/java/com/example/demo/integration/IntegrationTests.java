package com.example.demo.integration;

import com.example.demo.DemoApplication;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.allRequests;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.like;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = DemoApplication.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class IntegrationTests {

    public static final String SESSION_TOKEN_HEADER_NAME = "X-Session-Token";
    public static final String USER_TOKEN_HEADER_NAME = "X-User-Token";

    private final static Logger LOGGER = LoggerFactory.getLogger(IntegrationTests.class);

    private static WireMockServer wireMockServer;

    @LocalServerPort
    private int PORT;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
    }

    @BeforeEach
    public void beforeEach() {
        wireMockServer.resetAll();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @ParameterizedTest(name = "{index} - {0}")
    @ArgumentsSource(TestCaseProvider.class)
    void integrationTest(String testCaseName, TestCase testCase) throws IOException, InterruptedException {
        if (testCase.internalSystemRequest != null && testCase.internalSystemResponse != null) {
            wireMockServer.addStubMapping(
                    new StubMapping(testCase.internalSystemRequest, testCase.internalSystemResponse));
        }

        HttpRequest request = buildRequest(testCase.externalSystemRequest);
        LOGGER.info("Prepared external system request: {}", request);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        LOGGER.info("Received response to external system: {} {}", response, response.body());

        assertEquals(testCase.externalSystemResponse.statusCode, response.statusCode());

        Map<String, List<String>> map = new HashMap<>(response.headers().map());
        map.keySet().retainAll(testCase.externalSystemResponse.headers.keySet());
        assertEquals(testCase.externalSystemResponse.headers, map);

        if(testCase.externalSystemResponse.checkSessionTokenExists) {
            assertTrue(response.headers().map().containsKey(SESSION_TOKEN_HEADER_NAME));
        }
        if(testCase.externalSystemResponse.checkUserTokenExists) {
            assertTrue(response.headers().map().containsKey(USER_TOKEN_HEADER_NAME));
        }

        if(StringUtils.isNotBlank(testCase.externalSystemResponse.body)) {
            MatchResult match = new EqualToJsonPattern(
                        testCase.externalSystemResponse.body,
                        true,
                        testCase.externalSystemResponse.ignoreAdditionalBodyNodes)
                    .match(response.body());
            assertTrue(match.isExactMatch());
        }

        if (testCase.internalSystemRequest != null && testCase.internalSystemResponse != null) {
            wireMockServer.verify(1, like(testCase.internalSystemRequest));
        } else {
            wireMockServer.verify(0, allRequests());
        }
    }

    private HttpRequest buildRequest(TestRequest testRequest) {
        HttpRequest.Builder builder = HttpRequest.newBuilder();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost:" + PORT + testRequest.url);
        if (testRequest.queryParams != null && ! testRequest.queryParams.isEmpty()) {
            uriComponentsBuilder.queryParams(new MultiValueMapAdapter<>(testRequest.queryParams)).build().encode();
        }

        builder.uri(uriComponentsBuilder.build().encode().toUri());

        if (testRequest.body != null) {
            builder.method(testRequest.method, HttpRequest.BodyPublishers.ofString(testRequest.body));
        } else {
            builder.method(testRequest.method, HttpRequest.BodyPublishers.noBody());
        }

        if (testRequest.headers != null && ! testRequest.headers.isEmpty()) {
            for (Map.Entry<String, List<String>> headerInfoEntry : testRequest.headers.entrySet()) {
                List<String> headerValues = headerInfoEntry.getValue();
                if (headerValues != null) {
                    headerValues.forEach(headerValue -> builder.setHeader(headerInfoEntry.getKey(), headerValue));
                }
            }
        }

        return builder.build();
    }

}
