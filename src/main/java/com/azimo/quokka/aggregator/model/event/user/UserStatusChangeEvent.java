package com.azimo.quokka.aggregator.model.event.user;

import com.azimo.quokka.aggregator.model.event.Event;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class UserStatusChangeEvent implements Event {
    public String userId;
    public UserStatus userStatus;

    public UserStatusChangeEvent() {
    }

    public UserStatusChangeEvent(String userId, UserStatus userStatus) {
        this.userId = userId;
        this.userStatus = userStatus;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
