package com.azimo.quokka.aggregator.dto.transfer;

import com.azimo.quokka.aggregator.dto.ComponentDto;
import com.azimo.quokka.aggregator.dto.ComponentType;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;

public class ActiveTransferComponentDto implements ComponentDto {
    private List<TransferDto> transfers;
    private boolean hasMore;

    public ActiveTransferComponentDto(List<TransferDto> transfers, boolean hasMore) {
        this.transfers = transfers;
        this.hasMore = hasMore;
    }

    public List<TransferDto> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<TransferDto> transfers) {
        this.transfers = transfers;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.ACTIVE_TRANSFER;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
