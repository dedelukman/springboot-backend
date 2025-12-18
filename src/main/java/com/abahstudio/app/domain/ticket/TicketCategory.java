package com.abahstudio.app.domain.ticket;

import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TicketCategory {
    BUG_ERROR("bug_error", 1),
    FEATURE_QUESTION("feature_question", 2),
    FEATURE_REQUEST("feature_request", 3),
    ACCOUNT("account", 4),
    PAYMENT("payment", 5),
    OTHER("other", 6);

    private final String code;
    @Getter
    private final int order;

    TicketCategory(String code, int order) {
        this.code = code;
        this.order = order;
    }

    public String getCode() {
        return code;
    }

    public static TicketCategory[] getAllCategories() {
        return values();
    }

    public static TicketCategory[] getCategoriesByOrder() {
        return Stream.of(values())
                .sorted(Comparator.comparingInt(TicketCategory::getOrder))
                .toArray(TicketCategory[]::new);
    }

    public static TicketCategory getByOrder(int order) {
        return Stream.of(values())
                .filter(c -> c.getOrder() == order)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No category with order: " + order));
    }

    public static List<TicketCategory> getCategoriesAsList() {
        return Stream.of(values())
                .sorted(Comparator.comparingInt(TicketCategory::getOrder))
                .collect(Collectors.toList());
    }
}
