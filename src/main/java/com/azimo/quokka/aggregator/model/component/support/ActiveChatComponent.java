package com.azimo.quokka.aggregator.model.component.support;

import com.azimo.quokka.aggregator.dto.ComponentDto;
import com.azimo.quokka.aggregator.dto.support.ActiveChatComponentDto;
import com.azimo.quokka.aggregator.model.component.AbstractComponent;
import com.azimo.quokka.aggregator.model.component.Component;
import com.azimo.quokka.aggregator.model.event.support.ChatStatus;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ActiveChatComponent extends AbstractComponent implements Component {

    private String conversationId;
    private String agentName;
    private boolean chatRead;
    private String issueType;
    private ChatStatus status;

    public ActiveChatComponent() {
    }

    public ActiveChatComponent(String userId, String conversationId, String agentName, boolean chatRead, String issueType, ChatStatus status) {
        super(userId);
        this.conversationId = conversationId;
        this.agentName = agentName;
        this.chatRead = chatRead;
        this.issueType = issueType;
        this.status = status;
    }

    @Override
    public ComponentDto toDto() {
        return new ActiveChatComponentDto(conversationId, agentName, chatRead, issueType);
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public boolean isChatRead() {
        return chatRead;
    }

    public void setChatRead(boolean chatRead) {
        this.chatRead = chatRead;
    }

    public ChatStatus getStatus() {
        return status;
    }

    public void setStatus(ChatStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
