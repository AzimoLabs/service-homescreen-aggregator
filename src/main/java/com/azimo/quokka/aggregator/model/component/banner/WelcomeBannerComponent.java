package com.azimo.quokka.aggregator.model.component.banner;

import com.azimo.quokka.aggregator.dto.ComponentDto;
import com.azimo.quokka.aggregator.dto.banner.WelcomeBannerDto;
import com.azimo.quokka.aggregator.model.component.AbstractComponent;
import com.azimo.quokka.aggregator.model.component.Component;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class WelcomeBannerComponent extends AbstractComponent implements Component {
    private Integer numberOfFeeFreeTransfers;

    public WelcomeBannerComponent() {}

    public WelcomeBannerComponent(String userId, Integer numberOfFeeFreeTransfers) {
        super(userId);
        this.numberOfFeeFreeTransfers = numberOfFeeFreeTransfers;
    }

    public Integer getNumberOfFeeFreeTransfers() {
        return numberOfFeeFreeTransfers;
    }

    public void setNumberOfFeeFreeTransfers(Integer numberOfFeeFreeTransfers) {
        this.numberOfFeeFreeTransfers = numberOfFeeFreeTransfers;
    }

    @Override
    public ComponentDto toDto() {
        return new WelcomeBannerDto(numberOfFeeFreeTransfers);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
