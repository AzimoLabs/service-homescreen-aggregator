package com.azimo.quokka.aggregator.model.component;

import com.azimo.quokka.aggregator.dto.ComponentDto;

public interface Component {
    ComponentDto toDto();
    String getUserId();
}
