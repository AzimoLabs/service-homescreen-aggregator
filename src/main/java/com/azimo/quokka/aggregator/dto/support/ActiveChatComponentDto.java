package com.azimo.quokka.aggregator.dto.support;

import com.azimo.quokka.aggregator.dto.ComponentDto;
import com.azimo.quokka.aggregator.dto.ComponentType;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ActiveChatComponentDto implements ComponentDto {
    final String conversationId;
    final String agentName;
    final boolean chatRead;
    final String issueType;

    public ActiveChatComponentDto(String conversationId, String agentName, boolean chatRead, String issueType) {
        this.conversationId = conversationId;
        this.agentName = agentName;
        this.chatRead = chatRead;
        this.issueType = issueType;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.SUPPORT_ACTIVE_CHAT;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
