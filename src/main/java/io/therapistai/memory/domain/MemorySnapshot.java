package io.therapistai.memory.domain;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class MemorySnapshot {

    private final Map<MemoryType, List<MemoryItem>> memoriesByType;

    private MemorySnapshot(Map<MemoryType, List<MemoryItem>> memoriesByType) {
        this.memoriesByType = Map.copyOf(memoriesByType);
    }

    public static MemorySnapshot empty() {
        return new MemorySnapshot(Map.of());
    }

    public static MemorySnapshot fromList(List<MemoryItem> items) {
        if (items == null || items.isEmpty()) {
            return empty();
        }

        Map<MemoryType, List<MemoryItem>> grouped =
                items.stream()
                        .filter(Objects::nonNull)
                        .filter(item -> item.status() == MemoryStatus.ACTIVE)
                        .collect(Collectors.groupingBy(
                                MemoryItem::type,
                                () -> new EnumMap<>(MemoryType.class),
                                Collectors.toUnmodifiableList()
                        ));

        return grouped.isEmpty()
                ? empty()
                : new MemorySnapshot(grouped);
    }

    public List<MemoryItem> get(MemoryType type) {
        if (type == null) {
            return List.of();
        }

        return memoriesByType.getOrDefault(type, List.of());
    }

    public Map<MemoryType, List<MemoryItem>> all() {
        return memoriesByType;
    }

    public boolean contains(MemoryType type) {
        return type != null && memoriesByType.containsKey(type);
    }

    public boolean isEmpty() {
        return memoriesByType.isEmpty();
    }

    public int size() {
        return memoriesByType.values()
                .stream()
                .mapToInt(List::size)
                .sum();
    }

    @Override
    public String toString() {
        return "MemorySnapshot{" +
                "types=" + memoriesByType.size() +
                ", memories=" + size() +
                '}';
    }
}