package com.azimo.quokka.aggregator.service;

import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.model.component.Component;
import com.azimo.quokka.aggregator.model.component.UserState;
import com.azimo.quokka.aggregator.model.component.transfer.RecentTransferComponent;
import com.azimo.quokka.aggregator.model.event.Event;
import com.azimo.quokka.aggregator.rule.RuleEngine;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomescreenService {
    private UserStateRepository userStateRepo;
    private RuleEngine ruleEngine;

    public HomescreenService(UserStateRepository userStateRepo, RuleEngine ruleEngine) {
        this.userStateRepo = userStateRepo;
        this.ruleEngine = ruleEngine;
    }

    public UserState getState(String userId) {
        List<Component> components = userStateRepo.findComponents(userId);
            components = components.stream()
                    .filter(c -> !(c instanceof RecentTransferComponent && !((RecentTransferComponent) c).isHasMore() && ((RecentTransferComponent) c).getTransfers().isEmpty()))
                    .collect(Collectors.toList());
        return new UserState(userId, components);
    }

    public void updateState(Event event) {
        ruleEngine.execute(event);
    }
}
