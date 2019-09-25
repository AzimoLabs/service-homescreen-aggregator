package com.azimo.quokka.aggregator.config.aggregator;

import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.rule.ChatUpdatedRule;
import com.azimo.quokka.aggregator.rule.Rule;
import com.azimo.quokka.aggregator.rule.RuleEngine;
import com.azimo.quokka.aggregator.rule.TransferStatusChangeRule;
import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(AggregatorProperties.class)
public class AggregatorConfiguration {

    @Bean
    public RuleEngine ruleEngine(UserStateRepository repo) {
        return new RuleEngine(repo, getRules(repo));
    }

    private List<Rule> getRules(UserStateRepository repo) {
        ArrayList<Rule> rules = Lists.newArrayList();
        rules.add(new TransferStatusChangeRule(repo));
        rules.add(new ChatUpdatedRule(repo));
        return rules;
    }
}
