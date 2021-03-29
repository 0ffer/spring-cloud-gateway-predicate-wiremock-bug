package com.example.demo.integration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestResponse {
    int statusCode;

    Map<String, List<String>> headers = Collections.emptyMap();
    Boolean checkSessionTokenExists = false;
    Boolean checkUserTokenExists = false;

    String body;
    Boolean ignoreAdditionalBodyNodes = false;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getCheckSessionTokenExists() {
        return checkSessionTokenExists;
    }

    public void setCheckSessionTokenExists(Boolean checkSessionTokenExists) {
        this.checkSessionTokenExists = checkSessionTokenExists;
    }

    public Boolean getCheckUserTokenExists() {
        return checkUserTokenExists;
    }

    public void setCheckUserTokenExists(Boolean checkUserTokenExists) {
        this.checkUserTokenExists = checkUserTokenExists;
    }

    public Boolean getIgnoreAdditionalBodyNodes() {
        return ignoreAdditionalBodyNodes;
    }

    public void setIgnoreAdditionalBodyNodes(Boolean ignoreAdditionalBodyNodes) {
        this.ignoreAdditionalBodyNodes = ignoreAdditionalBodyNodes;
    }
}
