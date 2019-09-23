package com.azimo.quokka.aggregator.model.component.command;

import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.model.component.Component;

public interface Command {
    void execute(Component component, UserStateRepository repo);
}
