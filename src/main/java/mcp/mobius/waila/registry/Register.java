package mcp.mobius.waila.registry;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectLists;

public class Register<T> {

    private final Map<Class<?>, Set<Entry<T>>> map = new Object2ObjectOpenHashMap<>();
    private final Map<Class<?>, List<T>> cache = new Object2ObjectOpenHashMap<>();

    private final List<Entry<T>> sorter = new ObjectArrayList<>();

    private boolean reversed = false;

    public void reversed() {
        reversed = true;
    }

    public void add(Class<?> key, T value, int priority) {
        map.computeIfAbsent(key, k -> new ObjectLinkedOpenHashSet<>())
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

        Comparator<Entry<T>> comparator = Comparator.comparingInt(e -> e.priority);
        if (reversed) {
            comparator = comparator.reversed();
        }

        sorter.sort(comparator);
        List<T> list = new ObjectArrayList<>();
        for (Entry<T> entry : sorter) {
            list.add(entry.value);
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

    public record Entry<T>(T value, int priority) {

    }

}
