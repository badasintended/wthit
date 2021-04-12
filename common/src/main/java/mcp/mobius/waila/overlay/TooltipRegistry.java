package mcp.mobius.waila.overlay;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

public class TooltipRegistry<T> {

    private final Map<Class<?>, Set<Entry<T>>> map = new Object2ObjectOpenHashMap<>();
    private final Map<Class<?>, List<T>> cache = new Object2ObjectOpenHashMap<>();

    private final TreeSet<Entry<?>> sorter = new TreeSet<>(Comparator.comparingInt(e -> e.priority));

    public void add(Class<?> key, T value, int priority) {
        map.computeIfAbsent(key, k -> new TreeSet<>(Comparator.comparingInt(e -> e.priority)))
            .add(new Entry<>(value, priority));
    }

    public List<T> get(Class<?> key) {
        if (key == Object.class) {
            return ObjectLists.emptyList();
        }

        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        List<T> list;
        if (map.containsKey(key)) {
            sorter.clear();
            sorter.addAll(map.get(key));
            map.forEach((k, v) -> {
                if (k != key && k.isAssignableFrom(key)) {
                    sorter.addAll(v);
                }
            });
            list = new ObjectArrayList<>();
            for (Entry<?> entry : sorter) {
                list.add((T) entry.value);
            }
        } else {
            list = get(key.getSuperclass());
        }
        cache.put(key, list);
        return list;
    }

    public List<T> get(Object obj) {
        return obj == null ? ObjectLists.emptyList() : get(obj.getClass());
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

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Entry<?> entry = (Entry<?>) o;
            return priority == entry.priority && Objects.equals(value, entry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(priority, value);
        }

    }

}
