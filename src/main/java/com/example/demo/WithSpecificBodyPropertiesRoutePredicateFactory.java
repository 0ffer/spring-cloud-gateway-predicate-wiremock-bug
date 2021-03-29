package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.ReadBodyRoutePredicateFactory;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Component
public class WithSpecificBodyPropertiesRoutePredicateFactory extends AbstractRoutePredicateFactory<WithSpecificBodyPropertiesRoutePredicateFactory.Config> {

    private ReadBodyRoutePredicateFactory factory;

    public WithSpecificBodyPropertiesRoutePredicateFactory(List<HttpMessageReader<?>> messageReaders) {
        super(Config.class);
        factory = new ReadBodyRoutePredicateFactory(messageReaders);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("properties");
    }

    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }

    @Override
    public AsyncPredicate<ServerWebExchange> applyAsync(Config config) {
        ReadBodyRoutePredicateFactory.Config config1 = new ReadBodyRoutePredicateFactory.Config();
        config1.setPredicate(JsonNode.class, jsonNode -> {
            if (jsonNode == null) {
                return false;
            }

            return checkBody(config, jsonNode);
        });

        return factory.applyAsync(config1);
    }

    private boolean checkBody(Config config, JsonNode jsonNode) {
        if (jsonNode.size() != config.properties.size()) {
            return false;
        }

        for (String property : config.properties) {
            if (!jsonNode.has(property)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return factory.apply(new ReadBodyRoutePredicateFactory.Config());
    }

    @Validated
    public static class Config {

        private List<String> properties = new ArrayList<>();

        public List<String> getProperties() {
            return properties;
        }

        public void setPatterns(List<String> properties) {
            this.properties = properties;
        }
    }

}
