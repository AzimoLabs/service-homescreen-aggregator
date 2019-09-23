package com.azimo.quokka.aggregator.config.aggregator;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.aggregator")
public class AggregatorProperties {
    private String factsServiceUrl;
    private String feeFreeConfigurationUrl;
    private String feeFreeTokenConfigurationAuthToken;
    private String coreAzimoBasicAuth;

    public String getFeeFreeConfigurationUrl() {
        return feeFreeConfigurationUrl;
    }

    public void setFeeFreeConfigurationUrl(String feeFreeConfigurationUrl) {
        this.feeFreeConfigurationUrl = feeFreeConfigurationUrl;
    }

    public String getFeeFreeTokenConfigurationAuthToken() {
        return feeFreeTokenConfigurationAuthToken;
    }

    public void setFeeFreeTokenConfigurationAuthToken(String feeFreeTokenConfigurationAuthToken) {
        this.feeFreeTokenConfigurationAuthToken = feeFreeTokenConfigurationAuthToken;
    }

    public String getCoreAzimoBasicAuth() {
        return coreAzimoBasicAuth;
    }

    public void setCoreAzimoBasicAuth(String coreAzimoBasicAuth) {
        this.coreAzimoBasicAuth = coreAzimoBasicAuth;
    }

    public String getFactsServiceUrl() {
        return factsServiceUrl;
    }

    public void setFactsServiceUrl(String factsServiceUrl) {
        this.factsServiceUrl = factsServiceUrl;
    }
}
