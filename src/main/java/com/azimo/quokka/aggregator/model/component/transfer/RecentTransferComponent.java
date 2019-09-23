package com.azimo.quokka.aggregator.model.component.transfer;

import com.azimo.quokka.aggregator.dto.ComponentDto;
import com.azimo.quokka.aggregator.dto.transfer.RecentTransferComponentDto;
import com.azimo.quokka.aggregator.dto.transfer.TransferDto;
import com.azimo.quokka.aggregator.model.component.Component;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.azimo.quokka.aggregator.model.component.ComponentConstants.MAX_ACTIVE_TRANSFERS;
import static com.azimo.quokka.aggregator.model.component.ComponentConstants.MAX_RECENT_TRANSFERS;

public class RecentTransferComponent extends AbstractTransferComponent implements Component {
    private boolean hasMore;

    public RecentTransferComponent() {
    }

    public RecentTransferComponent(String userId, Map<String, Transfer> transfers, boolean hasMore) {
        super(userId, transfers);
        this.hasMore = hasMore;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    @Override
    public ComponentDto toDto() {
        boolean hasMore = this.hasMore;
        //hasMore attribute that comes from DynamoDb indicates if there are transfers in that component that happened before our aggregation.
        //If it's false but we have more transfers than we want to show, we need to change it to true.
        if(!this.hasMore && transfers.size() > MAX_ACTIVE_TRANSFERS) {
            hasMore = true;
        }
        List<TransferDto> transferDtos = transfers.values().stream()
                .sorted(Comparator.comparing(Transfer::getModificationTs).reversed())
                .limit(MAX_RECENT_TRANSFERS)
                .map(TransferDto::new)
                .collect(Collectors.toList());
        return new RecentTransferComponentDto(transferDtos, hasMore);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
