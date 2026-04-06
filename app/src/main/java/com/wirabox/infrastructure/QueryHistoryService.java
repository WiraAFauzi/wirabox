package com.wirabox.infrastructure;

import java.util.ArrayList;
import java.util.List;

public class QueryHistoryService {

    private final List<String> history = new ArrayList<>();

    public void addQuery(String query) {
        if (query == null || query.trim().isEmpty()) return;

        // Avoid duplicate consecutive queries
        if (!history.isEmpty() && history.get(history.size() - 1).equals(query)) {
            return;
        }

        history.add(query);
    }

    public List<String> getHistory() {
        return history;
    }

    public void clearHistory() {
        history.clear();
    }
}