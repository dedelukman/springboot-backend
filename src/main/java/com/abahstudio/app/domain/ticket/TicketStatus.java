package com.abahstudio.app.domain.ticket;

import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TicketStatus {
    OPEN("open", 1),
    RESPONDED("responded", 2),
    INVESTIGATING("investigating", 3),
    PENDING("pending", 4),
    DONE("done", 5),
    CLOSED("closed", 6);

    private final String code;
    @Getter
    private final int order;

    TicketStatus(String code, int order) {
        this.code = code;
        this.order = order;
    }

    public String getCode() {
        return code;
    }

    public static TicketStatus[] getAllStatuses() {
        return values();
    }

    public static TicketStatus[] getStatusesByOrder() {
        return Stream.of(values())
                .sorted(Comparator.comparingInt(TicketStatus::getOrder))
                .toArray(TicketStatus[]::new);
    }

    public static TicketStatus getByOrder(int order) {
        return Stream.of(values())
                .filter(s -> s.getOrder() == order)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No status with order: " + order));
    }

    public static List<TicketStatus> getStatusesAsList() {
        return Stream.of(values())
                .sorted(Comparator.comparingInt(TicketStatus::getOrder))
                .collect(Collectors.toList());
    }

    // Helper methods untuk workflow
    public static TicketStatus[] getActiveStatuses() {
        return new TicketStatus[]{OPEN, RESPONDED, INVESTIGATING, PENDING};
    }

    public static TicketStatus[] getCompletedStatuses() {
        return new TicketStatus[]{DONE, CLOSED};
    }

    public boolean isActive() {
        return this == OPEN || this == RESPONDED ||
                this == INVESTIGATING || this == PENDING;
    }

    public boolean isCompleted() {
        return this == DONE || this == CLOSED;
    }
}