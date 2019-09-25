package com.azimo.quokka.aggregator.generator;

import com.azimo.quokka.aggregator.model.component.Component;
import com.azimo.quokka.aggregator.model.component.UserState;
import com.azimo.quokka.aggregator.model.component.support.ActiveChatComponent;
import com.azimo.quokka.aggregator.model.component.transfer.ActiveTransferComponent;
import com.azimo.quokka.aggregator.model.component.transfer.RecentTransferComponent;
import com.azimo.quokka.aggregator.model.component.transfer.Transfer;
import com.azimo.quokka.aggregator.model.component.transfer.TransferStatus;
import com.azimo.quokka.aggregator.model.event.support.ChatStatus;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ComponentGenerator {
    public static final String USER_ID = "userId";
    public static final String MTN_ACTIVE_TRANSFER = "testmtn";
    public static final String MTN_RECENT_TRANSFER_1 = "testmtn2";
    public static final String MTN_RECENT_TRANSFER_2 = "testmtn3";
    public static final String MTN_RECENT_TRANSFER_3 = "testmtn4";
    public static final String MTN_RECENT_TRANSFER_4 = "testmtn5";
    public static final String MTN_RECENT_TRANSFER_5 = "testmtn6";
    private TransferGenerator transferGenerator = new TransferGenerator();

    public List<Component> activeAndRecentTrasnfers() {
        List<Component> components = Lists.newArrayList();
        ActiveTransferComponent transferComponent = createActiveTransferComponent(MTN_ACTIVE_TRANSFER);
        components.add(transferComponent);

        RecentTransferComponent recentTransferComponent = createRecentTransferComponent(true);
        components.add(recentTransferComponent);
        return components;
    }

    public RecentTransferComponent createRecentTransferComponent(boolean hasMore) {
        Transfer recentTransfer1 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_1, USER_ID, 1548323757819l, "300.21", TransferStatus.CLOSED_PAID_OUT);
        Transfer recentTransfer2 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_2, USER_ID, 1545323757819l, "200.21", TransferStatus.CLOSED_REFUNDED);

        Map<String, Transfer> recentTransfers = Maps.newHashMap();
        recentTransfers.put(MTN_RECENT_TRANSFER_1, recentTransfer1);
        recentTransfers.put(MTN_RECENT_TRANSFER_2, recentTransfer2);
        return new RecentTransferComponent(USER_ID, recentTransfers, hasMore);
    }

    public RecentTransferComponent createRecentTransferComponentWithMaxTransfers(boolean hasMore) {
        Transfer recentTransfer1 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_1, USER_ID, 1548323757819l, "300.21", TransferStatus.CLOSED_PAID_OUT);
        Transfer recentTransfer2 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_2, USER_ID, 1545323757819l, "200.21", TransferStatus.CLOSED_REFUNDED);
        Transfer recentTransfer3 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_3, USER_ID, 1549323757819l, "200.21", TransferStatus.CLOSED_PAID_OUT);
        Transfer recentTransfer4 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_4, USER_ID, 1550323757819l, "200.21", TransferStatus.CLOSED_PAID_OUT);
        Transfer recentTransfer5 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_5, USER_ID, 1551323757819l, "200.21", TransferStatus.CLOSED_PAID_OUT);

        Map<String, Transfer> recentTransfers = Maps.newHashMap();
        recentTransfers.put(MTN_RECENT_TRANSFER_1, recentTransfer1);
        recentTransfers.put(MTN_RECENT_TRANSFER_2, recentTransfer2);
        recentTransfers.put(MTN_RECENT_TRANSFER_3, recentTransfer3);
        recentTransfers.put(MTN_RECENT_TRANSFER_4, recentTransfer4);
        recentTransfers.put(MTN_RECENT_TRANSFER_5, recentTransfer5);
        return new RecentTransferComponent(USER_ID, recentTransfers, hasMore);
    }


    public ActiveTransferComponent createActiveTransferComponentWithMaxTransfers() {
        Transfer recentTransfer1 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_1, USER_ID, 1548323757819l, "300.21", TransferStatus.AWAITING_FUNDS);
        Transfer recentTransfer2 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_2, USER_ID, 1545323757819l, "200.21", TransferStatus.AWAITING_FUNDS);
        Transfer recentTransfer3 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_3, USER_ID, 1549323757819l, "200.21", TransferStatus.AWAITING_FUNDS);
        Transfer recentTransfer4 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_4, USER_ID, 1550323757819l, "200.21", TransferStatus.AWAITING_FUNDS);
        Transfer recentTransfer5 = transferGenerator.createTransfer(MTN_RECENT_TRANSFER_5, USER_ID, 1551323757819l, "200.21", TransferStatus.AWAITING_FUNDS);

        Map<String, Transfer> recentTransfers = Maps.newHashMap();
        recentTransfers.put(MTN_RECENT_TRANSFER_1, recentTransfer1);
        recentTransfers.put(MTN_RECENT_TRANSFER_2, recentTransfer2);
        recentTransfers.put(MTN_RECENT_TRANSFER_3, recentTransfer3);
        recentTransfers.put(MTN_RECENT_TRANSFER_4, recentTransfer4);
        recentTransfers.put(MTN_RECENT_TRANSFER_5, recentTransfer5);
        return new ActiveTransferComponent(USER_ID, recentTransfers);
    }

    public List<Component> activeChat() {
        return Collections.singletonList(new ActiveChatComponent(USER_ID, "conversation1", "dombar secret agent", false, "document", ChatStatus.CREATED));
    }

    public RecentTransferComponent createEmptyRecentTransferComponent(boolean hasMore) {
        return new RecentTransferComponent(USER_ID, Maps.newHashMap(), hasMore);
    }

    public ActiveTransferComponent createActiveTransferComponent(String mtn) {
        Transfer activeTransfer = transferGenerator.createTransfer(mtn, USER_ID, 1548425797819l, "100.21", TransferStatus.AWAITING_FUNDS);
        Map<String, Transfer> activeTransfers = Maps.newHashMap();
        activeTransfers.put(mtn, activeTransfer);
        return new ActiveTransferComponent(USER_ID, activeTransfers);
    }

    public UserState createUserState(String userId, List<Component> components) {
        return new UserState(userId, components);
    }
}
