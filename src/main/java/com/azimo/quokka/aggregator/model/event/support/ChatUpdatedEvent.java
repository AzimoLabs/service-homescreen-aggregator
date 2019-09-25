package com.azimo.quokka.aggregator.model.event.support;

import com.azimo.quokka.aggregator.model.event.Event;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ChatUpdatedEvent implements Event {
    public String conversationId;
    public String userId;
    public String agentName;
    public boolean chatRead;
    public String issueType;
    public ChatStatus chatStatus;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
