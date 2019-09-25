package com.azimo.quokka.aggregator.model.component;

public class AbstractComponent {
    protected String userId;

    public AbstractComponent() {}

    public AbstractComponent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
