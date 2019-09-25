package com.azimo.quokka.aggregator.config.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.amazon.aws")
public class AmazonProperties {
    private String accesskey;
    private String secretkey;
    private String region;
    private Dynamodb dynamodb;

    public String getAccesskey() {
        return accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Dynamodb getDynamodb() {
        return dynamodb;
    }

    public void setDynamodb(Dynamodb dynamodb) {
        this.dynamodb = dynamodb;
    }

    public static class Dynamodb {
        private String table;

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }
    }
}
