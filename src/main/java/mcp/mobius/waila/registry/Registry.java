package mcp.mobius.waila.registry;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

public class Registry<T> {

    private final Map<Class<?>, List<Entry<T>>> map = new Object2ObjectOpenHashMap<>();
    private final Map<Class<?>, List<T>> cache = new Object2ObjectOpenHashMap<>();

    private final List<Entry<?>> sorter = new ObjectArrayList<>();

    public void add(Class<?> key, T value, int priority) {
        map.computeIfAbsent(key, k -> new ObjectArrayList<>())
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
            //noinspection unchecked
            list.add((T) entry.value);
        }

        if (list.isEmpty()) {
            // Discard empty list so it'll GC-ed
            list = ObjectLists.emptyList();
        }

        cache.put(clazz, list);
        return list;
    }

    public Map<Class<?>, List<Entry<T>>> getMap() {
        return map;
    }

    public static record Entry<T>(T value, int priority) {

    }

}
