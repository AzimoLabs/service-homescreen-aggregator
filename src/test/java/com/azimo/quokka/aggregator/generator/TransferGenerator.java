package com.azimo.quokka.aggregator.generator;

import com.azimo.quokka.aggregator.model.component.transfer.Transfer;
import com.azimo.quokka.aggregator.model.component.transfer.TransferStatus;

public class TransferGenerator {
    public Transfer createTransfer(String mtn, String userId, long creationTs, String receivingAmount, TransferStatus status) {
        return new Transfer.Builder()
                .mtn(mtn)
                .userId(userId)
                .creationTs(creationTs)
                .modificationTs(creationTs)
                .receivingAmount(receivingAmount)
                .transferStatus(status)
                .build();
    }
}
