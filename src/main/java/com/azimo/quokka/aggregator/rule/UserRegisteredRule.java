package com.azimo.quokka.aggregator.rule;

import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.model.component.ComponentAction;
import com.azimo.quokka.aggregator.model.component.banner.WelcomeBannerComponent;
import com.azimo.quokka.aggregator.model.component.command.UpdateCommand;
import com.azimo.quokka.aggregator.model.component.transfer.RecentTransferComponent;
import com.azimo.quokka.aggregator.model.event.Event;
import com.azimo.quokka.aggregator.model.event.user.UserStatus;
import com.azimo.quokka.aggregator.model.event.user.UserStatusChangeEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Optional;

public class UserRegisteredRule implements Rule {
    private UserStateRepository repo;

    public UserRegisteredRule(UserStateRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean isSupported(Event event) {
        boolean isSupported = event instanceof UserStatusChangeEvent && UserStatus.REGISTERED.equals(((UserStatusChangeEvent) event).userStatus);
        logSupport(isSupported, event.getClass());
        return isSupported;
    }

    @Override
    public List<ComponentAction> execute(Event event) {
        UserStatusChangeEvent statusChangeEvent = (UserStatusChangeEvent) event;
        String userId = statusChangeEvent.userId;
        List<ComponentAction> componentActions = Lists.newArrayList();
        Integer nrFeeFreeTransfers = getNrFeeFreeTransfers();
        WelcomeBannerComponent welcomeBanner = new WelcomeBannerComponent(userId, nrFeeFreeTransfers);
        componentActions.add(ComponentAction.of(welcomeBanner, new UpdateCommand()));

        Optional<RecentTransferComponent> recentTransferComponentResult = repo.findComponent(userId, RecentTransferComponent.class);
        if (!recentTransferComponentResult.isPresent()) {
            RecentTransferComponent recentTransferComponent = new RecentTransferComponent(userId, Maps.newHashMap(), false);
            componentActions.add(ComponentAction.of(recentTransferComponent, new UpdateCommand()));
        }
        return componentActions;
    }

    private int getNrFeeFreeTransfers() {
        return 2;
    }
}
