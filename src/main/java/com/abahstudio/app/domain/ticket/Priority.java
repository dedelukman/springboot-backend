package com.abahstudio.app.domain.ticket;

import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Priority {
    LOW("low", 1),
    NORMAL("normal", 2),
    HIGH("high", 3),
    CRITICAL("critical", 4);

    private final String code;
    @Getter
    private final int order;

    Priority(String code, int order) {
        this.code = code;
        this.order = order;
    }

    public String getCode() {
        return code;
    }

    public static Priority[] getAllPriorities() {
        return values();
    }

    public static Priority[] getPrioritiesByOrder() {
        return Stream.of(values())
                .sorted(Comparator.comparingInt(Priority::getOrder))
                .toArray(Priority[]::new);
    }

    public static Priority getByOrder(int order) {
        return Stream.of(values())
                .filter(p -> p.getOrder() == order)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No priority with order: " + order));
    }

    public static List<Priority> getPrioritiesAsList() {
        return Stream.of(values())
                .sorted(Comparator.comparingInt(Priority::getOrder))
                .collect(Collectors.toList());
    }
}