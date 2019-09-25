package com.azimo.quokka.aggregator.rule;

import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.model.component.ComponentAction;
import com.azimo.quokka.aggregator.model.component.command.DeleteCommand;
import com.azimo.quokka.aggregator.model.component.command.UpdateCommand;
import com.azimo.quokka.aggregator.model.component.support.ActiveChatComponent;
import com.azimo.quokka.aggregator.model.event.Event;
import com.azimo.quokka.aggregator.model.event.support.ChatStatus;
import com.azimo.quokka.aggregator.model.event.support.ChatUpdatedEvent;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

import static com.azimo.quokka.aggregator.model.event.support.ChatStatus.*;

public class ChatUpdatedRule implements Rule {
    private UserStateRepository repo;

    public ChatUpdatedRule(UserStateRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean isSupported(Event event) {
        boolean isSupported = event instanceof ChatUpdatedEvent;
        logSupport(isSupported, event.getClass());
        return isSupported;
    }

    @Override
    public List<ComponentAction> execute(Event event) {
        ChatUpdatedEvent statusChangeEvent = (ChatUpdatedEvent) event;
        String userId = statusChangeEvent.userId;
        ChatStatus status = statusChangeEvent.chatStatus;
        ActiveChatComponent component;
        Optional<ActiveChatComponent> componentResult = repo.findComponent(userId, ActiveChatComponent.class);
        if (componentResult.isPresent()) {
            component = componentResult.get();
            if (status == CREATED) {
                component.setConversationId(statusChangeEvent.conversationId);
                component.setAgentName(statusChangeEvent.agentName);
                component.setChatRead(true);
                component.setIssueType(statusChangeEvent.issueType);
                component.setStatus(CREATED);
            } else if (status == TICKET_ANSWERED_BY_AGENT) {
                component.setAgentName(statusChangeEvent.agentName);
                component.setIssueType(statusChangeEvent.issueType);
                component.setChatRead(false);
            } else if (status == TICKET_READ_BY_CUSTOMER) {
                component.setChatRead(true);
            } else if (status == CLOSED || status == EXPIRED) {
                component.setStatus(status);
            }
        } else {
            component = new ActiveChatComponent(userId, statusChangeEvent.conversationId, statusChangeEvent.agentName,
                    true, statusChangeEvent.issueType, ChatStatus.CREATED);
        }
        List<ComponentAction> componentActions = Lists.newArrayList();
        if (component.getStatus() == EXPIRED ||
                (component.getStatus() == CLOSED && component.isChatRead())) {
            componentActions.add(ComponentAction.of(component, new DeleteCommand()));
        } else {
            componentActions.add(ComponentAction.of(component, new UpdateCommand()));
        }
        return componentActions;
    }

}
