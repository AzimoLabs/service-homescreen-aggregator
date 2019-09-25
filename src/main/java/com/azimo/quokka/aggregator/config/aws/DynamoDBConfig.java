package com.azimo.quokka.aggregator.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AmazonProperties.class)
public class DynamoDBConfig {
    @Bean(destroyMethod = "shutdown")
    public AmazonDynamoDB amazonDynamoDB(AmazonProperties amazonProperties) {
        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(amazonAWSCredentials(amazonProperties)))
                .withRegion(amazonProperties.getRegion())
                .build();
        return amazonDynamoDB;
    }

    @Bean
    public AWSCredentials amazonAWSCredentials(AmazonProperties amazonProperties) {
        return new BasicAWSCredentials(amazonProperties.getAccesskey(), amazonProperties.getSecretkey());
    }
}
