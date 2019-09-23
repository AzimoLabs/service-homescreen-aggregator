package com.azimo.quokka.aggregator.rule;

import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.generator.ComponentGenerator;
import com.azimo.quokka.aggregator.generator.EventGenerator;
import com.azimo.quokka.aggregator.model.component.Component;
import com.azimo.quokka.aggregator.model.component.ComponentAction;
import com.azimo.quokka.aggregator.model.component.banner.WelcomeBannerComponent;
import com.azimo.quokka.aggregator.model.component.command.DeleteCommand;
import com.azimo.quokka.aggregator.model.component.command.UpdateCommand;
import com.azimo.quokka.aggregator.model.component.transfer.ActiveTransferComponent;
import com.azimo.quokka.aggregator.model.component.transfer.RecentTransferComponent;
import com.azimo.quokka.aggregator.model.component.transfer.Transfer;
import com.azimo.quokka.aggregator.model.component.transfer.TransferStatus;
import com.azimo.quokka.aggregator.model.event.Event;
import com.azimo.quokka.aggregator.model.event.transfer.TransferStatusChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.azimo.quokka.aggregator.generator.ComponentGenerator.MTN_RECENT_TRANSFER_2;
import static com.azimo.quokka.aggregator.generator.EventGenerator.*;
import static com.azimo.quokka.aggregator.model.component.ComponentConstants.MAX_TRANSFER_IN_DB_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class TransferStatusChangeRuleTest {
    @Mock
    private UserStateRepository repo;
    private Rule rule;
    private EventGenerator eventGenerator = new EventGenerator();
    private ComponentGenerator componentGenerator = new ComponentGenerator();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        rule = new TransferStatusChangeRule(repo);
    }

    @Test
    public void isSupported() {
        boolean supported = rule.isSupported(new TransferStatusChangeEvent());
        assertThat(supported).isTrue();
    }

    @Test
    public void activeEventWithZeroComponents() {
        //given
        Event event = eventGenerator.transferStatusActiveChangeEvent();

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(1);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(UpdateCommand.class);
        Component component = componentAction.component;
        assertThat(component).isInstanceOf(ActiveTransferComponent.class);
        ActiveTransferComponent activeTransferComponent = (ActiveTransferComponent) component;
        Map<String, Transfer> transfers = activeTransferComponent.getTransfers();
        assertThat(transfers.size()).isEqualTo(1);
        Transfer transfer = transfers.get(MTN_EVENT_ACTIVE_TRANSFER);
        assertTransfer(transfer, MTN_EVENT_ACTIVE_TRANSFER, 1548425797819l, "100.21", TransferStatus.IN_PROGRESS);
    }

    @Test
    public void activeEventWithWelcomeBannerAndExistingActiveTransfer() {
        //given
        Event event = eventGenerator.transferStatusActiveChangeEvent();
        ActiveTransferComponent expectedActiveTransferComponent = componentGenerator.createActiveTransferComponent(ComponentGenerator.MTN_ACTIVE_TRANSFER);
        when(repo.findComponent(USER_ID, ActiveTransferComponent.class)).thenReturn(Optional.of(expectedActiveTransferComponent));
        int numberOfFeeFreeTransfers = 2;
        WelcomeBannerComponent expectedWelcomeBanner = componentGenerator.welcomeBanner(numberOfFeeFreeTransfers);
        when(repo.findComponent(USER_ID, WelcomeBannerComponent.class)).thenReturn(Optional.of(expectedWelcomeBanner));

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(2);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(UpdateCommand.class);
        Component component = componentAction.component;
        assertThat(component).isInstanceOf(ActiveTransferComponent.class);
        ActiveTransferComponent activeTransferComponent = (ActiveTransferComponent) component;
        Map<String, Transfer> transfers = activeTransferComponent.getTransfers();
        assertThat(transfers.size()).isEqualTo(2);
        Transfer transfer = transfers.get(MTN_EVENT_ACTIVE_TRANSFER);
        assertTransfer(transfer, MTN_EVENT_ACTIVE_TRANSFER, 1548425797819l, "100.21", TransferStatus.IN_PROGRESS);
        transfer = transfers.get(ComponentGenerator.MTN_ACTIVE_TRANSFER);
        assertTransfer(transfer, ComponentGenerator.MTN_ACTIVE_TRANSFER, 1548425797819l, "100.21", TransferStatus.AWAITING_FUNDS);

        componentAction = componentActions.get(1);
        assertThat(componentAction.command).isInstanceOf(DeleteCommand.class);
        component = componentAction.component;
        assertThat(component).isInstanceOf(WelcomeBannerComponent.class);
        WelcomeBannerComponent welcomeBannerComponent = (WelcomeBannerComponent) component;
        assertThat(welcomeBannerComponent.getNumberOfFeeFreeTransfers()).isEqualTo(2);
    }

    @Test
    public void activeEventStatusUpdated() {
        //given
        Event event = eventGenerator.transferStatusChangeEvent(ComponentGenerator.MTN_ACTIVE_TRANSFER, "100.21", TransferStatus.IN_PROGRESS, 1548425797819l);
        ActiveTransferComponent expectedActiveTransferComponent = componentGenerator.createActiveTransferComponent(ComponentGenerator.MTN_ACTIVE_TRANSFER);
        when(repo.findComponent(USER_ID, ActiveTransferComponent.class)).thenReturn(Optional.of(expectedActiveTransferComponent));

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(1);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(UpdateCommand.class);
        Component component = componentAction.component;
        assertThat(component).isInstanceOf(ActiveTransferComponent.class);
        ActiveTransferComponent activeTransferComponent = (ActiveTransferComponent) component;
        Map<String, Transfer> transfers = activeTransferComponent.getTransfers();
        assertThat(transfers.size()).isEqualTo(1);
        Transfer transfer = transfers.get(ComponentGenerator.MTN_ACTIVE_TRANSFER);
        assertTransfer(transfer, ComponentGenerator.MTN_ACTIVE_TRANSFER, 1548425797819l, "100.21", TransferStatus.IN_PROGRESS);
    }

    @Test
    public void closedEventStatusUpdated() {
        //given
        Event event = eventGenerator.transferStatusChangeEvent(ComponentGenerator.MTN_RECENT_TRANSFER_1, "300.21", TransferStatus.CLOSED_CANCELLED, 1548425797819l);
        RecentTransferComponent expectedRecentTransferComponent = componentGenerator.createRecentTransferComponent(true);
        when(repo.findComponent(USER_ID, RecentTransferComponent.class)).thenReturn(Optional.of(expectedRecentTransferComponent));

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(1);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(UpdateCommand.class);
        Component component = componentAction.component;
        assertThat(component).isInstanceOf(RecentTransferComponent.class);
        RecentTransferComponent recentTransferComponent = (RecentTransferComponent) component;
        Map<String, Transfer> transfers = recentTransferComponent.getTransfers();
        assertThat(transfers.size()).isEqualTo(2);
        Transfer transfer = transfers.get(ComponentGenerator.MTN_RECENT_TRANSFER_1);
        assertTransfer(transfer, ComponentGenerator.MTN_RECENT_TRANSFER_1, 1548425797819l, "300.21", TransferStatus.CLOSED_CANCELLED);
    }

    @Test
    public void closeEventWithActiveTransfer() {
        //given
        Event event = eventGenerator.transferStatusClosedChangeEvent();
        ActiveTransferComponent expectedActiveTransferComponent = componentGenerator.createActiveTransferComponent(MTN_EVENT_CLOSED_TRANSFER);
        when(repo.findComponent(USER_ID, ActiveTransferComponent.class)).thenReturn(Optional.of(expectedActiveTransferComponent));

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(2);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(UpdateCommand.class);
        Component component = componentAction.component;
        assertThat(component).isInstanceOf(RecentTransferComponent.class);
        RecentTransferComponent recentTransferComponent = (RecentTransferComponent) component;
        Map<String, Transfer> transfers = recentTransferComponent.getTransfers();
        assertThat(transfers.size()).isEqualTo(1);
        Transfer transfer = transfers.get(MTN_EVENT_CLOSED_TRANSFER);
        assertTransfer(transfer, MTN_EVENT_CLOSED_TRANSFER, 1548425797819l, "200.31", TransferStatus.CLOSED_PAID_OUT);

        componentAction = componentActions.get(1);
        assertThat(componentAction.command).isInstanceOf(DeleteCommand.class);
        component = componentAction.component;
        assertThat(component).isInstanceOf(ActiveTransferComponent.class);
        ActiveTransferComponent activeTransferComponent = (ActiveTransferComponent) component;
        assertThat(activeTransferComponent.getTransfers().isEmpty()).isTrue();
    }

    @Test
    public void recentTransferLimit() {
        //given
        Event event = eventGenerator.transferStatusChangeEvent("testmtn7", "100.23", TransferStatus.CLOSED_PAID_OUT, 1552323757819l);
        RecentTransferComponent recentTransferComponentWithMaxTransfers = componentGenerator.createRecentTransferComponentWithMaxTransfers(true);
        when(repo.findComponent(USER_ID, RecentTransferComponent.class)).thenReturn(Optional.of(recentTransferComponentWithMaxTransfers));

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(1);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(UpdateCommand.class);
        Component component = componentAction.component;
        assertThat(component).isInstanceOf(RecentTransferComponent.class);
        RecentTransferComponent recentTransferComponent = (RecentTransferComponent) component;
        Map<String, Transfer> transfers = recentTransferComponent.getTransfers();
        assertThat(transfers.size()).isEqualTo(MAX_TRANSFER_IN_DB_SIZE);
        Transfer filteredTransfer = transfers.get(MTN_RECENT_TRANSFER_2);
        assertThat(filteredTransfer).isNull();
    }


    @Test
    public void activeTransferLimit() {
        //given
        Event event = eventGenerator.transferStatusChangeEvent("testmtn7", "100.23", TransferStatus.AWAITING_FUNDS, 1552323757819l);
        ActiveTransferComponent activeTransferComponentWithMaxTransfers = componentGenerator.createActiveTransferComponentWithMaxTransfers();
        when(repo.findComponent(USER_ID, ActiveTransferComponent.class)).thenReturn(Optional.of(activeTransferComponentWithMaxTransfers));

        //when
        List<ComponentAction> componentActions = rule.execute(event);

        //then
        assertThat(componentActions.size()).isEqualTo(1);
        ComponentAction componentAction = componentActions.get(0);
        assertThat(componentAction.command).isInstanceOf(UpdateCommand.class);
        Component component = componentAction.component;
        assertThat(component).isInstanceOf(ActiveTransferComponent.class);
        ActiveTransferComponent activeTransferComponent = (ActiveTransferComponent) component;
        Map<String, Transfer> transfers = activeTransferComponent.getTransfers();
        assertThat(transfers.size()).isEqualTo(MAX_TRANSFER_IN_DB_SIZE);
        Transfer filteredTransfer = transfers.get(MTN_RECENT_TRANSFER_2);
        assertThat(filteredTransfer).isNull();
    }

    private void assertTransfer(Transfer transfer, String mtn, long creationTs, String receivingAmount, TransferStatus transferStatus) {
        assertThat(transfer).isNotNull();
        assertThat(transfer.getMtn()).isEqualTo(mtn);
        assertThat(transfer.getUserId()).isEqualTo(USER_ID);
        assertThat(transfer.getCreationTs()).isEqualTo(creationTs);
        assertThat(transfer.getReceivingAmount()).isEqualTo(receivingAmount);
        assertThat(transfer.getTransferStatus()).isEqualTo(transferStatus);
    }
}