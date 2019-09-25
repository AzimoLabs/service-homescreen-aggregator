package com.azimo.quokka.aggregator.rule;

import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.generator.EventGenerator;
import com.azimo.quokka.aggregator.model.component.Component;
import com.azimo.quokka.aggregator.model.component.ComponentAction;
import com.azimo.quokka.aggregator.model.component.command.DeleteCommand;
import com.azimo.quokka.aggregator.model.component.command.UpdateCommand;
import com.azimo.quokka.aggregator.model.component.support.ActiveChatComponent;
import com.azimo.quokka.aggregator.model.event.Event;
import com.azimo.quokka.aggregator.model.event.support.ChatStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ChatUpdatedRuleTest {
    @Mock
    private UserStateRepository repo;
    private Rule rule;
    private EventGenerator eventGenerator = new EventGenerator();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        rule = new ChatUpdatedRule(repo);
    }

    @Test
    public void isSupported() {
        Event event = eventGenerator.chatUpdatedEvent(ChatStatus.CREATED);
        boolean supported = rule.isSupported(event);
        assertThat(supported).isTrue();
    }

    @Test
    public void createdShouldCreateChat() {
        //given
        Event event = eventGenerator.chatUpdatedEvent(ChatStatus.CREATED);

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(1);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(UpdateCommand.class);
        assertComponent(componentAction, ChatStatus.CREATED, true);
    }

    @Test
    public void expiredShouldDeleteChat() {
        //given
        ActiveChatComponent component = new ActiveChatComponent(EventGenerator.USER_ID, EventGenerator.TICKET_ID, EventGenerator.AGENT_TEST,
                false, EventGenerator.ISSUE_TYPE, ChatStatus.CLOSED);
        when(repo.findComponent(anyString(), eq(ActiveChatComponent.class))).thenReturn(Optional.of(component));
        Event event = eventGenerator.chatUpdatedEvent(ChatStatus.EXPIRED);

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(1);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(DeleteCommand.class);
    }

    @Test
    public void closedNotReadShouldUpdateChat() {
        //given
        ActiveChatComponent component = new ActiveChatComponent(EventGenerator.USER_ID, EventGenerator.TICKET_ID, EventGenerator.AGENT_TEST,
                false, EventGenerator.ISSUE_TYPE, ChatStatus.CLOSED);
        when(repo.findComponent(anyString(), eq(ActiveChatComponent.class))).thenReturn(Optional.of(component));
        Event event = eventGenerator.chatUpdatedEvent(ChatStatus.CLOSED);

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(1);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(UpdateCommand.class);
        assertComponent(componentAction, ChatStatus.CLOSED, false);
    }

    @Test
    public void closedReadShouldDeleteChat() {
        //given
        ActiveChatComponent component = new ActiveChatComponent(EventGenerator.USER_ID, EventGenerator.TICKET_ID, EventGenerator.AGENT_TEST,
                true, EventGenerator.ISSUE_TYPE, ChatStatus.CLOSED);
        when(repo.findComponent(anyString(), eq(ActiveChatComponent.class))).thenReturn(Optional.of(component));
        Event event = eventGenerator.chatUpdatedEvent(ChatStatus.CLOSED);

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(1);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(DeleteCommand.class);
    }

    private void assertComponent(ComponentAction componentAction, ChatStatus expectedStatus, boolean expectedChatRead) {
        Component component = componentAction.component;
        assertThat(component).isInstanceOf(ActiveChatComponent.class);
        ActiveChatComponent activeChatComponent = (ActiveChatComponent) component;
        assertThat(activeChatComponent.getAgentName()).isEqualTo(EventGenerator.AGENT_TEST);
        assertThat(activeChatComponent.getConversationId()).isEqualTo(EventGenerator.TICKET_ID);
        assertThat(activeChatComponent.getIssueType()).isEqualTo(EventGenerator.ISSUE_TYPE);
        assertEquals(expectedChatRead, activeChatComponent.isChatRead());
        assertThat(activeChatComponent.getStatus()).isEqualTo(expectedStatus);
    }
}