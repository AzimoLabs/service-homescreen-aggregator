package com.azimo.quokka.aggregator.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.azimo.quokka.aggregator.config.aws.AmazonProperties;
import com.azimo.quokka.aggregator.config.aws.DynamoConstants;
import com.azimo.quokka.aggregator.model.component.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserStateRepository {
    private static final Logger LOG = LoggerFactory.getLogger(UserStateRepository.class);
    private Table table;
    private ObjectMapper objectMapper;

    public UserStateRepository(AmazonDynamoDB dynamoDB, ObjectMapper objectMapper, AmazonProperties props) {
        this.table = new DynamoDB(dynamoDB).getTable(props.getDynamodb().getTable());
        this.objectMapper = objectMapper;
    }

    public void save(Component component) {
        try {
            LOG.info("Storing component: {} ", component);
            String json = objectMapper.writeValueAsString(component);
            table.putItem(Item.fromJSON(json)
                    .withPrimaryKey(DynamoConstants.PARTITION_KEY_NAME, component.getUserId(),
                            DynamoConstants.SORT_KEY_NAME, component.getClass().getName()));
            LOG.info("Successfully stored component: {} ", component);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Cannot parse component: %s into json", component), e);
        }
    }

    public void deleteComponent(String userId, Class<? extends Component> componentClass) {
        LOG.info("Deleting component of type: {} for userId: {}", componentClass, userId);
        table.deleteItem(DynamoConstants.PARTITION_KEY_NAME, userId, DynamoConstants.SORT_KEY_NAME, componentClass.getName());
        LOG.info("Successfully deleted component of type: {} for userId: {}", componentClass, userId);
    }

    public List<Component> findComponents(String userId) {
        LOG.info("Fetching components for userId: {}", userId);
        ItemCollection<QueryOutcome> items = table.query(DynamoConstants.PARTITION_KEY_NAME, userId);
        IteratorSupport<Item, QueryOutcome> it = items.iterator();
        List<Component> components = Lists.newArrayList();
        while(it.hasNext()) {
            Item item = it.next();
            Optional<Component> component = convertToComponent(item);
            component.ifPresent(components::add);
        }
        LOG.info("Found following components: {} for userId: {}", components, userId);
        return components;
    }

    public <T extends Component> Optional<T> findComponent(String userId, Class<T> componentClass) {
        LOG.info("Fetching component of type: {} for userId: {}", componentClass, userId);
        Item item = table.getItem(DynamoConstants.PARTITION_KEY_NAME, userId, DynamoConstants.SORT_KEY_NAME, componentClass.getName());
        Optional<T> component = convertToComponent(item);
        LOG.info("Found following component: {} for userId: {}", component, userId);
        return component;
    }

    private <T extends Component> Optional<T> convertToComponent(Item item) {
        if(item == null) {
            return Optional.empty();
        }
        String componentClass = item.getString(DynamoConstants.SORT_KEY_NAME);
        String json = item.toJSON();
        try {
            T component = (T) objectMapper.readValue(json, Class.forName(componentClass));
            return Optional.of(component);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Cannot convert json: %s into component.", json), e);
        }
    }
}
