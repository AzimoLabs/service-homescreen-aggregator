package com.azimo.quokka.aggregator.model.component.transfer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Transfer {
    private String mtn;
    private String userId;
    private String receivingAmount;
    private TransferStatus transferStatus;
    private Long creationTs;
    private Long modificationTs;

    private Transfer() {}

    private Transfer(Builder builder) {
        mtn = builder.mtn;
        userId = builder.userId;
        receivingAmount = builder.receivingAmount;
        transferStatus = builder.transferStatus;
        creationTs = builder.creationTs;
        modificationTs = builder.modificationTs;
    }

    public String getMtn() {
        return mtn;
    }

    public String getUserId() {
        return userId;
    }

    public String getReceivingAmount() {
        return receivingAmount;
    }

    public TransferStatus getTransferStatus() {
        return transferStatus;
    }

    public Long getCreationTs() {
        return creationTs;
    }

    public Long getModificationTs() {
        return modificationTs;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public static final class Builder {
        private String mtn;
        private String userId;
        private String receivingAmount;
        private TransferStatus transferStatus;
        private Long creationTs;
        private Long modificationTs;

        public Builder() {
        }

        public Builder mtn(String val) {
            mtn = val;
            return this;
        }

        public Builder userId(String val) {
            userId = val;
            return this;
        }

        public Builder receivingAmount(String val) {
            receivingAmount = val;
            return this;
        }

        public Builder transferStatus(TransferStatus val) {
            transferStatus = val;
            return this;
        }

        public Builder creationTs(Long val) {
            creationTs = val;
            return this;
        }

        public Builder modificationTs(Long val) {
            modificationTs = val;
            return this;
        }

        public Transfer build() {
            return new Transfer(this);
        }
    }
}
