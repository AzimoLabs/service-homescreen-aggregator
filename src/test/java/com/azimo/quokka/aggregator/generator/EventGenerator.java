package com.azimo.quokka.aggregator.generator;

import com.azimo.quokka.aggregator.model.component.transfer.TransferStatus;
import com.azimo.quokka.aggregator.model.event.Event;
import com.azimo.quokka.aggregator.model.event.support.ChatStatus;
import com.azimo.quokka.aggregator.model.event.support.ChatUpdatedEvent;
import com.azimo.quokka.aggregator.model.event.transfer.TransferStatusChangeEvent;
import com.azimo.quokka.aggregator.model.event.user.UserStatus;
import com.azimo.quokka.aggregator.model.event.user.UserStatusChangeEvent;

public class EventGenerator {
    public static final String USER_ID = "userId";
    public static final String MTN_EVENT_ACTIVE_TRANSFER = "testmtnevent";
    public static final String MTN_EVENT_CLOSED_TRANSFER = "testmtnevent2";
    public static final String AGENT_TEST = "Agent test";
    public static final String TICKET_ID = "ticket_id";
    public static final String ISSUE_TYPE = "document";
    private TransferGenerator transferGenerator = new TransferGenerator();

    public Event transferStatusActiveChangeEvent() {
        return transferStatusChangeEvent(MTN_EVENT_ACTIVE_TRANSFER, "100.21", TransferStatus.IN_PROGRESS, 1548425797819l);
    }

    public Event transferStatusChangeEvent(String mtn, String receivingAmount, TransferStatus status, long creationTs) {
        TransferStatusChangeEvent event = new TransferStatusChangeEvent();
        event.transfer = transferGenerator.createTransfer(mtn, USER_ID, creationTs, receivingAmount, status);
        return event;
    }

    public Event transferStatusClosedChangeEvent() {
        return transferStatusChangeEvent(MTN_EVENT_CLOSED_TRANSFER, "200.31", TransferStatus.CLOSED_PAID_OUT, 1548425797819l);
    }

    public Event userRegisteredEvent() {
        return new UserStatusChangeEvent(USER_ID, UserStatus.REGISTERED);
    }

    public Event chatUpdatedEvent(ChatStatus status) {
        ChatUpdatedEvent event = new ChatUpdatedEvent();
        event.agentName = AGENT_TEST;
        event.userId = USER_ID;
        event.chatStatus = status;
        event.issueType = ISSUE_TYPE;
        event.conversationId = TICKET_ID;
        return event;
    }
}

