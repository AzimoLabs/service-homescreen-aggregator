package com.azimo.quokka.aggregator.model.event.transfer;

import com.azimo.quokka.aggregator.model.component.transfer.Transfer;
import com.azimo.quokka.aggregator.model.event.Event;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class TransferStatusChangeEvent implements Event {
    public Transfer transfer;

    public TransferStatusChangeEvent() {
    }

    public TransferStatusChangeEvent(Transfer transfer) {
        this.transfer = transfer;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
