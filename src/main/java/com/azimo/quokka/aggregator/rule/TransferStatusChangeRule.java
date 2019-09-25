package com.azimo.quokka.aggregator.rule;

import com.azimo.quokka.aggregator.dynamodb.UserStateRepository;
import com.azimo.quokka.aggregator.model.component.ComponentAction;
import com.azimo.quokka.aggregator.model.component.command.DeleteCommand;
import com.azimo.quokka.aggregator.model.component.command.UpdateCommand;
import com.azimo.quokka.aggregator.model.component.transfer.ActiveTransferComponent;
import com.azimo.quokka.aggregator.model.component.transfer.RecentTransferComponent;
import com.azimo.quokka.aggregator.model.component.transfer.Transfer;
import com.azimo.quokka.aggregator.model.component.transfer.TransferStatus;
import com.azimo.quokka.aggregator.model.event.Event;
import com.azimo.quokka.aggregator.model.event.transfer.TransferStatusChangeEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.azimo.quokka.aggregator.model.component.ComponentConstants.MAX_TRANSFER_IN_DB_SIZE;

public class TransferStatusChangeRule implements Rule {
    private UserStateRepository repo;

    public TransferStatusChangeRule(UserStateRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean isSupported(Event event) {
        boolean isSupported = event instanceof TransferStatusChangeEvent;
        logSupport(isSupported, event.getClass());
        return isSupported;
    }

    @Override
    public List<ComponentAction> execute(Event event) {
        TransferStatusChangeEvent transferStatusChange = (TransferStatusChangeEvent) event;
        Transfer transfer = transferStatusChange.transfer;
        TransferStatus status = transfer.getTransferStatus();
        List<ComponentAction> componentActions = Lists.newArrayList();
        if (isActive(status)) {
            updateActiveTransfers(transfer, componentActions);
        } else if (isClosed(status)) {
            updateRecentTransferComponent(transfer, componentActions);

            //for closing event remove that transfer from active component
            closeActiveTransfer(transfer, componentActions);
        }
        return componentActions;
    }

    private void closeActiveTransfer(Transfer transfer, List<ComponentAction> componentActions) {
        Optional<ActiveTransferComponent> activeTransferComponentResult = repo.findComponent(transfer.getUserId(), ActiveTransferComponent.class);
        activeTransferComponentResult.ifPresent(c -> {
            c.getTransfers().remove(transfer.getMtn());
            if(c.getTransfers().isEmpty()) {
                componentActions.add(ComponentAction.of(c, new DeleteCommand()));
            } else {
                componentActions.add(ComponentAction.of(c, new UpdateCommand()));
            }
        });
    }

    private void updateRecentTransferComponent(Transfer transfer, List<ComponentAction> componentActions) {
        Optional<RecentTransferComponent> recentTransferComponentResult = repo.findComponent(transfer.getUserId(), RecentTransferComponent.class);
        RecentTransferComponent component;
        if (recentTransferComponentResult.isPresent()) {
            component = recentTransferComponentResult.get();
            Map<String, Transfer> transfers = component.getTransfers();
            transfers.put(transfer.getMtn(), transfer);
            if (transfers.size() >= MAX_TRANSFER_IN_DB_SIZE) {
                transfers = keepNewestTransfers(transfers);
                component.setTransfers(transfers);
            }
        } else {
            Map<String, Transfer> transfers = Maps.newHashMap();
            transfers.put(transfer.getMtn(), transfer);
            component = new RecentTransferComponent(transfer.getUserId(), transfers, true);
        }
        componentActions.add(ComponentAction.of(component, new UpdateCommand()));
    }

    private Map<String, Transfer> keepNewestTransfers(Map<String, Transfer> transfers) {
        return transfers.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, Transfer> e) -> e.getValue().getModificationTs()).reversed())
                .limit(MAX_TRANSFER_IN_DB_SIZE)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void updateActiveTransfers(Transfer transfer, List<ComponentAction> componentActions) {
        Optional<ActiveTransferComponent> componentResult = repo.findComponent(transfer.getUserId(), ActiveTransferComponent.class);
        ActiveTransferComponent component;
        if (componentResult.isPresent()) {
            component = componentResult.get();
            Map<String, Transfer> transfers = component.getTransfers();
            transfers.put(transfer.getMtn(), transfer);
            if (transfers.size() >= MAX_TRANSFER_IN_DB_SIZE) {
                transfers = keepNewestTransfers(transfers);
                component.setTransfers(transfers);
            }
        } else {
            Map<String, Transfer> transfers = Maps.newHashMap();
            transfers.put(transfer.getMtn(), transfer);
            component = new ActiveTransferComponent(transfer.getUserId(), transfers);
        }
        componentActions.add(ComponentAction.of(component, new UpdateCommand()));
    }

    private boolean isClosed(TransferStatus status) {
        switch (status) {
            case CLOSED_PAID_OUT:
            case CLOSED_REFUNDED:
            case CLOSED_CANCELLED: return true;
            default: return false;
        }
    }

    private boolean isActive(TransferStatus status) {
        switch (status) {
            case IN_PROGRESS:
            case AWAITING_FUNDS: return true;
            default: return false;
        }
    }
}
