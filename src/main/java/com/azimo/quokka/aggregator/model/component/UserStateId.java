package com.azimo.quokka.aggregator.model.component;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

public class UserStateId {
    private String userId;
    private String componentType;

    public UserStateId() {}

    public UserStateId(String userId, String componentType) {
        this.userId = userId;
        this.componentType = componentType;
    }

    public static UserStateId of(String userId, String componentType) {
        return new UserStateId(userId, componentType);
    }

    @DynamoDBHashKey(attributeName = "UserId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBRangeKey(attributeName = "ComponentType")
    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }
}
