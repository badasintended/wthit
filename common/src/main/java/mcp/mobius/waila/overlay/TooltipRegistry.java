package mcp.mobius.waila.overlay;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class TooltipRegistry<T> {

    private final Map<Class<?>, Set<Entry<T>>> map = new Object2ObjectOpenHashMap<>();
    private final Map<Class<?>, List<T>> cache = new Object2ObjectOpenHashMap<>();

    private final List<Entry<?>> sorter = new ObjectArrayList<>();

    public void add(Class<?> key, T value, int priority) {
        map.computeIfAbsent(key, k -> new ObjectOpenHashSet<>())
            .add(new Entry<>(value, priority));
    }

    public List<T> get(Object obj) {
        if (obj == null) {
            return ObjectLists.emptyList();
        }

        Class<?> clazz = obj.getClass();
        if (clazz == Object.class) {
            return ObjectLists.emptyList();
        }

        if (cache.containsKey(clazz)) {
            return cache.get(clazz);
        }

        sorter.clear();
        map.forEach((k, v) -> {
            if (k.isInstance(obj)) {
                sorter.addAll(v);
            }
        });
        sorter.sort(Comparator.comparingInt(e -> e.priority));
        List<T> list = new ObjectArrayList<>();
        for (Entry<?> entry : sorter) {
            list.add((T) entry.value);
        }

        if (list.isEmpty()) {
            // Discard empty list so it'll GC-ed
            list = ObjectLists.emptyList();
        }

        cache.put(clazz, list);
        return list;
    }

    public Map<Class<?>, Set<Entry<T>>> getMap() {
        return map;
    }

    public static class Entry<T> {

        public final int priority;
        public final T value;

        Entry(T value, int priority) {
            this.value = value;
            this.priority = priority;
        }

    }

}
