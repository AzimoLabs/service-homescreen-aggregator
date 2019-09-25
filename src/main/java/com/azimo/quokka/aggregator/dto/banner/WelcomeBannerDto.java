package com.azimo.quokka.aggregator.dto.banner;

import com.azimo.quokka.aggregator.dto.ComponentDto;
import com.azimo.quokka.aggregator.dto.ComponentType;

public class WelcomeBannerDto implements ComponentDto {
    private Integer numberOfFeeFreeTransfers;

    public WelcomeBannerDto(Integer numberOfFeeFreeTransfers) {
        this.numberOfFeeFreeTransfers = numberOfFeeFreeTransfers;
    }

    public Integer getNumberOfFeeFreeTransfers() {
        return numberOfFeeFreeTransfers;
    }

    public void setNumberOfFeeFreeTransfers(Integer numberOfFeeFreeTransfers) {
        this.numberOfFeeFreeTransfers = numberOfFeeFreeTransfers;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.WELCOME_BANNER;
    }
}
