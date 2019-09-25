package com.azimo.quokka.aggregator.rule;

import com.azimo.quokka.aggregator.model.component.ComponentAction;
import com.azimo.quokka.aggregator.model.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface Rule {
    Logger LOG = LoggerFactory.getLogger(Rule.class);

    boolean isSupported(Event event);
    List<ComponentAction> execute(Event event);

    default void logSupport(boolean isSupported, Class<? extends Event> eventType) {
        if(!isSupported)
            LOG.info(String.format("Rule of type: %s is NOT supporting event type: %s. Skipping...", getClass().getSimpleName(), eventType.getSimpleName()));
        else
            LOG.info(String.format("Rule of type: %s is supporting event type: %s", getClass().getSimpleName(), eventType.getSimpleName()));
    }
}
