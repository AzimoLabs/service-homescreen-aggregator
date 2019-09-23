package com.azimo.quokka.aggregator.dto.transfer;

import com.azimo.quokka.aggregator.model.component.transfer.Transfer;
import com.azimo.quokka.aggregator.model.component.transfer.TransferStatus;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.time.Instant;

public class TransferDto {
    public String mtn;
    public String receivingAmount;
    public Instant creationTime;
    public Instant modificationTime;

    public TransferDto(Transfer transfer) {
        this.mtn = transfer.getMtn();
        this.receivingAmount = transfer.getReceivingAmount();
        this.creationTime = Instant.ofEpochMilli(transfer.getCreationTs());
        this.modificationTime = Instant.ofEpochMilli(transfer.getModificationTs());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
