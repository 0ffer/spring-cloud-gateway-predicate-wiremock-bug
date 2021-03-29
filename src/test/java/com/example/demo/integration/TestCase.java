package com.example.demo.integration;

import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.matching.RequestPattern;

public class TestCase {
    TestRequest externalSystemRequest;
    TestResponse externalSystemResponse;

    RequestPattern internalSystemRequest;
    ResponseDefinition internalSystemResponse;

    public TestRequest getExternalSystemRequest() {
        return externalSystemRequest;
    }

    public void setExternalSystemRequest(TestRequest externalSystemRequest) {
        this.externalSystemRequest = externalSystemRequest;
    }

    public TestResponse getExternalSystemResponse() {
        return externalSystemResponse;
    }

    public void setExternalSystemResponse(TestResponse externalSystemResponse) {
        this.externalSystemResponse = externalSystemResponse;
    }

    public RequestPattern getInternalSystemRequest() {
        return internalSystemRequest;
    }

    public void setInternalSystemRequest(RequestPattern internalSystemRequest) {
        this.internalSystemRequest = internalSystemRequest;
    }

    public ResponseDefinition getInternalSystemResponse() {
        return internalSystemResponse;
    }

    public void setInternalSystemResponse(ResponseDefinition internalSystemResponse) {
        this.internalSystemResponse = internalSystemResponse;
    }
}
