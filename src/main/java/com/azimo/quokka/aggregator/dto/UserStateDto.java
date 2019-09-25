package com.azimo.quokka.aggregator.dto;

import com.azimo.quokka.aggregator.model.component.Component;
import com.azimo.quokka.aggregator.model.component.UserState;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class UserStateDto {
    private String userId;
    private List<ComponentDto> components;

    public UserStateDto(UserState userState) {
        this.userId = userState.getId();
        this.components = userState.getComponents().stream()
                .map(Component::toDto)
                .collect(Collectors.toList());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ComponentDto> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentDto> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
