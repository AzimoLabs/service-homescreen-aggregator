package com.azimo.quokka.aggregator.model.component;

import com.azimo.quokka.aggregator.model.component.command.Command;

public class ComponentAction {
    public Component component;
    public Command command;

    public ComponentAction(Component component, Command command) {
        this.component = component;
        this.command = command;
    }

    public static ComponentAction of(Component component, Command command) {
        return new ComponentAction(component, command);
    }
}
