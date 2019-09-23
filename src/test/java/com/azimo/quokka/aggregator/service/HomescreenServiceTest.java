package com.azimo.quokka.aggregator.service;

import com.azimo.quokka.aggregator.config.JacksonConfiguration;
import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.generator.ComponentGenerator;
import com.azimo.quokka.aggregator.model.component.Component;
import com.azimo.quokka.aggregator.model.component.UserState;
import com.azimo.quokka.aggregator.model.component.banner.WelcomeBannerComponent;
import com.azimo.quokka.aggregator.model.component.transfer.RecentTransferComponent;
import com.azimo.quokka.aggregator.model.event.Event;
import com.azimo.quokka.aggregator.rule.RuleEngine;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@Import(JacksonConfiguration.class)
public class HomescreenServiceTest {
    private static final String USER_ID = "userId";
    private static final String USER_FACTS_JSON_ACTIVE = "user_facts_response.json";
    private static final String USER_FACTS_JSON_NEW_USER = "user_facts_response_new_user.json";
    @Mock
    private UserStateRepository userStateRepo;
    @Mock
    private RuleEngine ruleEngine;
    private HomescreenService homescreenService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() {
        homescreenService = new HomescreenService(userStateRepo, ruleEngine);
    }

    @Test
    public void getStateForExistingComponents() {
        //given
        Component component = mock(Component.class);
        List<Component> components = Lists.newArrayList(component);
        when(userStateRepo.findComponents(eq(USER_ID))).thenReturn(components);

        //when
        UserState state = homescreenService.getState(USER_ID);

        //then
        assertThat(state).isNotNull();
        assertThat(state.getId()).isEqualTo(USER_ID);
        assertThat(state.getComponents().size()).isEqualTo(1);
        assertThat(state.getComponents().get(0)).isEqualTo(component);
    }

    @Test
    public void filterEmptyRecentTransfer() {
        //given
        Component component = mock(Component.class);
        RecentTransferComponent recentTransferComponent = new ComponentGenerator().createEmptyRecentTransferComponent(false);
        List<Component> components = Lists.newArrayList(component, recentTransferComponent);
        when(userStateRepo.findComponents(eq(USER_ID))).thenReturn(components);

        //when
        UserState state = homescreenService.getState(USER_ID);

        //then
        assertThat(state).isNotNull();
        assertThat(state.getId()).isEqualTo(USER_ID);
        assertThat(state.getComponents().size()).isEqualTo(1);
        assertThat(state.getComponents().get(0)).isEqualTo(component);
    }

    @Test
    public void notFilterRecentTransferWhenNotEmpty() {
        //given
        Component component = mock(Component.class);
        RecentTransferComponent recentTransferComponent = new ComponentGenerator().createRecentTransferComponent(false);
        List<Component> components = Lists.newArrayList(component, recentTransferComponent);
        when(userStateRepo.findComponents(eq(USER_ID))).thenReturn(components);

        //when
        UserState state = homescreenService.getState(USER_ID);

        //then
        assertThat(state).isNotNull();
        assertThat(state.getId()).isEqualTo(USER_ID);
        assertThat(state.getComponents().size()).isEqualTo(2);
        assertThat(state.getComponents().get(0)).isEqualTo(component);
        assertThat(state.getComponents().get(1)).isEqualTo(recentTransferComponent);
    }

    @Test
    public void updateState() {
        //given
        Event event = mock(Event.class);

        //when
        homescreenService.updateState(event);

        //then
        verify(ruleEngine, times(1)).execute(eq(event));
    }
}