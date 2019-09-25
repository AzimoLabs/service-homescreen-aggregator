package com.azimo.quokka.aggregator.model.component;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;

public class UserState {
    private String id;
    private List<Component> components;

    public UserState(String id, List<Component> components) {
        this.id = id;
        this.components = components;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
