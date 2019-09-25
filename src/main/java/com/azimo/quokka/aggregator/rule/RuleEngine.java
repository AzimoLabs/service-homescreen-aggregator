package com.azimo.quokka.aggregator.rule;

import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.model.event.Event;

import java.util.List;

public class RuleEngine {
    private List<Rule> rules;
    private UserStateRepository repo;

    public RuleEngine(UserStateRepository repo, List<Rule> rules) {
        this.rules = rules;
        this.repo = repo;
    }

    public void execute(Event event) {
        rules.stream()
                .filter(r -> r.isSupported(event))
                .flatMap(r -> r.execute(event).stream())
                .forEach(c -> c.command.execute(c.component, repo));
    }
}
