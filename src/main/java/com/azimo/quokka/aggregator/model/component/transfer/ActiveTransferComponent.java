package com.azimo.quokka.aggregator.model.component.transfer;

import com.azimo.quokka.aggregator.dto.ComponentDto;
import com.azimo.quokka.aggregator.dto.transfer.ActiveTransferComponentDto;
import com.azimo.quokka.aggregator.dto.transfer.TransferDto;
import com.azimo.quokka.aggregator.model.component.Component;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.azimo.quokka.aggregator.model.component.ComponentConstants.MAX_ACTIVE_TRANSFERS;

public class ActiveTransferComponent extends AbstractTransferComponent implements Component {
    public ActiveTransferComponent() {
    }

    public ActiveTransferComponent(String userId, Map<String, Transfer> transfers) {
        super(userId, transfers);
    }

    @Override
    public ComponentDto toDto() {
        List<TransferDto> transferDtos = transfers.values().stream()
                .sorted(Comparator.comparing(Transfer::getModificationTs).reversed())
                .limit(MAX_ACTIVE_TRANSFERS)
                .map(TransferDto::new)
                .collect(Collectors.toList());
        boolean hasMore = transfers.size() > MAX_ACTIVE_TRANSFERS;
        return new ActiveTransferComponentDto(transferDtos, hasMore);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
