package com.azimo.quokka.aggregator.model.component.transfer;

import com.azimo.quokka.aggregator.model.component.AbstractComponent;
import com.google.common.collect.Maps;

import java.util.Map;

public abstract class AbstractTransferComponent extends AbstractComponent {
    protected Map<String, Transfer> transfers = Maps.newHashMap(); //key -> mtn

    public AbstractTransferComponent() {}

    public AbstractTransferComponent(String userId, Map<String, Transfer> transfers) {
        super(userId);
        this.transfers = transfers;
    }

    public void addTransfer(Transfer transfer) {
        transfers.put(transfer.getMtn(), transfer);
    }

    public void removeTransfer(String mtn) {
        transfers.remove(mtn);
    }

    public Map<String, Transfer> getTransfers() {
        return transfers;
    }

    public void setTransfers(Map<String, Transfer> transfers) {
        this.transfers = transfers;
    }
}
