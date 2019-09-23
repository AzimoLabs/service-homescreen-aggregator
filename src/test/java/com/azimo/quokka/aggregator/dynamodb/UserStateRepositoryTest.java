package com.azimo.quokka.aggregator.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.azimo.quokka.aggregator.config.aws.DynamoConstants;
import com.azimo.quokka.aggregator.config.repository.RepositoryTestConfiguration;
import com.azimo.quokka.aggregator.generator.ComponentGenerator;
import com.azimo.quokka.aggregator.model.component.Component;
import com.azimo.quokka.aggregator.model.component.banner.WelcomeBannerComponent;
import com.azimo.quokka.aggregator.model.component.transfer.ActiveTransferComponent;
import com.azimo.quokka.aggregator.model.component.transfer.RecentTransferComponent;
import com.azimo.quokka.aggregator.model.component.transfer.Transfer;
import com.azimo.quokka.aggregator.model.component.transfer.TransferStatus;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.azimo.quokka.aggregator.generator.ComponentGenerator.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@DirtiesContext
public class UserStateRepositoryTest {
    private static final String TABLE_NAME = "user-homescreen";
    @Autowired
    private UserStateRepository repository;
    @Autowired
    private AmazonDynamoDB amazonDynamoDB;
    private static boolean initDb;
    private ComponentGenerator generator;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("spring.amazon.aws.dynamodb.table",TABLE_NAME);
    }

    @Before
    public void setup() {
        if (!initDb) {
            createTable(TABLE_NAME, DynamoConstants.PARTITION_KEY_NAME, DynamoConstants.SORT_KEY_NAME);
            initDb = true;
        }
        generator = new ComponentGenerator();
    }

    private void createTable(String tableName, String partitionKeyName, String sortKeyName) {
        ArrayList<KeySchemaElement> keySchema = new ArrayList<>();
        // Partition key
        keySchema.add(new KeySchemaElement().withAttributeName(partitionKeyName).withKeyType(KeyType.HASH));

        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions
                .add(new AttributeDefinition().withAttributeName(partitionKeyName).withAttributeType("S"));

        if (sortKeyName != null) {
            //Sort key
            keySchema.add(new KeySchemaElement().withAttributeName(sortKeyName).withKeyType(KeyType.RANGE));
            attributeDefinitions
                    .add(new AttributeDefinition().withAttributeName(sortKeyName).withAttributeType("S"));
        }

        CreateTableRequest request = new CreateTableRequest().withTableName(tableName).withKeySchema(keySchema)
                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1l)
                        .withWriteCapacityUnits(1l));

        request.setAttributeDefinitions(attributeDefinitions);

        amazonDynamoDB.createTable(request);
    }

    @Test
    public void findAllComponentsForUser() {
        //given
        storeComponents();

        //when
        List<Component> result = repository.findComponents(USER_ID);

        //then
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.size()).isEqualTo(3);
        Component component = result.get(0);
        assertWelcomeBanner(component, 2);

        component = result.get(1);
        assertActiveTransfer(component);

        component = result.get(2);
        assertRecentTransfer(component);
    }

    @Test
    public void findComponentForUser() {
        //given
        storeComponents();

        //when
        Optional<WelcomeBannerComponent> welcomeBanner = repository.findComponent(USER_ID, WelcomeBannerComponent.class);
        Optional<ActiveTransferComponent> activeTransfer = repository.findComponent(USER_ID, ActiveTransferComponent.class);

        //then
        assertWelcomeBanner(welcomeBanner.get(), 2);
        assertActiveTransfer(activeTransfer.get());
    }

    @Test
    public void updateComponent() {
        //given
        storeComponents();
        int numberOfFeeFreeTransfers = 5;
        Component component = generator.welcomeBanner(numberOfFeeFreeTransfers);

        //when
        repository.save(component);

        //then
        assertWelcomeBanner(component, numberOfFeeFreeTransfers);
    }

    @Test
    public void deleteComponent() {
        //given
        storeComponents();

        //when
        repository.deleteComponent(USER_ID, WelcomeBannerComponent.class);

        //then
        Optional<WelcomeBannerComponent> component = repository.findComponent(USER_ID, WelcomeBannerComponent.class);
        assertThat(component.isPresent()).isFalse();
    }

    @Test
    public void deleteNonExistingComponent() {
        //given
        //clearing previous state
        repository.deleteComponent(USER_ID, WelcomeBannerComponent.class);
        Optional<WelcomeBannerComponent> component = repository.findComponent(USER_ID, WelcomeBannerComponent.class);
        assertThat(component.isPresent()).isFalse();

        //when
        repository.deleteComponent(USER_ID, WelcomeBannerComponent.class);

        //then
        component = repository.findComponent(USER_ID, WelcomeBannerComponent.class);
        assertThat(component.isPresent()).isFalse();
    }

    @Test
    public void emptyListForNoComponents() {
        //given
        final String userId = "no-user";

        //when
        List<Component> components = repository.findComponents(userId);

        //then
        assertThat(components.isEmpty()).isTrue();
    }

    private void assertActiveTransfer(Component component) {
        assertThat(component).isInstanceOf(ActiveTransferComponent.class);
        ActiveTransferComponent activeTransfer = (ActiveTransferComponent) component;
        assertThat(activeTransfer.getUserId()).isEqualTo(USER_ID);
        Map<String, Transfer> transfers = activeTransfer.getTransfers();
        assertThat(transfers.size()).isEqualTo(1);
        Transfer transfer = transfers.get(MTN_ACTIVE_TRANSFER);
        assertTrasnfer(transfer, MTN_ACTIVE_TRANSFER, 1548425797819l, "100.21", TransferStatus.AWAITING_FUNDS);
    }

    private void assertRecentTransfer(Component component) {
        assertThat(component).isInstanceOf(RecentTransferComponent.class);
        RecentTransferComponent recentTransfer = (RecentTransferComponent) component;
        assertThat(recentTransfer.getUserId()).isEqualTo(USER_ID);
        Map<String, Transfer> transfers = recentTransfer.getTransfers();
        assertThat(transfers.size()).isEqualTo(2);
        Transfer transfer = transfers.get(MTN_RECENT_TRANSFER_1);
        assertTrasnfer(transfer, MTN_RECENT_TRANSFER_1, 1548323757819l, "300.21", TransferStatus.CLOSED_PAID_OUT);
        transfer = transfers.get(MTN_RECENT_TRANSFER_2);
        assertTrasnfer(transfer, MTN_RECENT_TRANSFER_2, 1545323757819l, "200.21", TransferStatus.CLOSED_REFUNDED);
    }

    private void assertTrasnfer(Transfer transfer, String mtn, long creationTs, String receivingAmount, TransferStatus transferStatus) {
        assertThat(transfer.getMtn()).isEqualTo(mtn);
        assertThat(transfer.getUserId()).isEqualTo(USER_ID);
        assertThat(transfer.getCreationTs()).isEqualTo(creationTs);
        assertThat(transfer.getModificationTs()).isEqualTo(creationTs);
        assertThat(transfer.getReceivingAmount()).isEqualTo(receivingAmount);
    }

    private void assertWelcomeBanner(Component component, int nrOfFeeFreeTransfers) {
        assertThat(component).isInstanceOf(WelcomeBannerComponent.class);
        WelcomeBannerComponent retrievedBanner = (WelcomeBannerComponent) component;
        assertThat(retrievedBanner.getUserId()).isEqualTo(USER_ID);
        assertThat(retrievedBanner.getNumberOfFeeFreeTransfers()).isEqualTo(nrOfFeeFreeTransfers);
    }

    private void storeComponents() {
        Component banner = generator.welcomeBanner(2);
        repository.save(banner);

        generator.activeAndRecentTrasnfers().stream()
                .forEach(repository::save);
    }
}