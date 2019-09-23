package com.azimo.quokka.aggregator.rule;

import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.generator.EventGenerator;
import com.azimo.quokka.aggregator.model.component.Component;
import com.azimo.quokka.aggregator.model.component.ComponentAction;
import com.azimo.quokka.aggregator.model.component.banner.WelcomeBannerComponent;
import com.azimo.quokka.aggregator.model.component.command.UpdateCommand;
import com.azimo.quokka.aggregator.model.component.transfer.RecentTransferComponent;
import com.azimo.quokka.aggregator.model.event.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRegisteredRuleTest {
    @Mock
    private UserStateRepository repo;
    private Rule rule;
    private EventGenerator eventGenerator = new EventGenerator();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        rule = new UserRegisteredRule(repo);
    }

    @Test
    public void isSupported() {
        Event event = eventGenerator.userRegisteredEvent();
        boolean supported = rule.isSupported(event);
        assertThat(supported).isTrue();
    }

    @Test
    public void register() {
        //given
        Event event = eventGenerator.userRegisteredEvent();

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(2);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(UpdateCommand.class);
        Component component = componentAction.component;
        assertThat(component).isInstanceOf(WelcomeBannerComponent.class);
        WelcomeBannerComponent welcomeBanner = (WelcomeBannerComponent) component;
        assertThat(welcomeBanner.getNumberOfFeeFreeTransfers()).isEqualTo(2);
        componentAction = componentActions.get(1);
        assertThat(componentAction.command).isInstanceOf(UpdateCommand.class);
        component = componentAction.component;
        assertThat(component).isInstanceOf(RecentTransferComponent.class);
        RecentTransferComponent recentTransferComponent = (RecentTransferComponent) component;
        assertThat(recentTransferComponent.isHasMore()).isFalse();
    }
}