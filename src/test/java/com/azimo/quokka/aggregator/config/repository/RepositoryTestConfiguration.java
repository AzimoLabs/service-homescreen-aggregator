package com.azimo.quokka.aggregator.config.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.shared.access.AmazonDynamoDBLocal;
import com.azimo.quokka.aggregator.config.JacksonConfiguration;
import com.azimo.quokka.aggregator.config.aws.AmazonProperties;
import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.util.AwsDynamoDbLocalUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({JacksonConfiguration.class})
@ComponentScan(basePackageClasses = { UserStateRepository.class})
@EnableConfigurationProperties(AmazonProperties.class)
public class RepositoryTestConfiguration {
    @Bean(destroyMethod = "shutdown")
    public AmazonDynamoDB amazonDynamoDB() {
        AwsDynamoDbLocalUtils.initSqLite();

        AmazonDynamoDBLocal amazonDynamoDBLocal = DynamoDBEmbedded.create();
        return amazonDynamoDBLocal.amazonDynamoDB();
    }
}
