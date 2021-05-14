package mcp.mobius.waila.overlay;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

public class TooltipRegistry<T> {

    private final Map<Class<?>, List<Entry<T>>> map = new Object2ObjectOpenHashMap<>();
    private final Map<Class<?>, List<T>> cache = new Object2ObjectOpenHashMap<>();

    private final List<Entry<?>> sorter = new ObjectArrayList<>();

    public void add(Class<?> key, T value, int priority) {
        map.computeIfAbsent(key, k -> new ObjectArrayList<>())
            .add(new Entry<>(value, priority));
    }

    private List<T> get(Class<?> clazz, Object obj) {
        if (clazz == Object.class) {
            return ObjectLists.emptyList();
        }

        if (cache.containsKey(clazz)) {
            return cache.get(clazz);
        }

        List<T> list;
        if (map.containsKey(clazz)) {
            sorter.clear();
            map.forEach((k, v) -> {
                if (k.isInstance(obj)) {
                    sorter.addAll(v);
                }
            });
            sorter.sort(Comparator.comparingInt(e -> e.priority));
            list = new ObjectArrayList<>();
            for (Entry<?> entry : sorter) {
                list.add((T) entry.value);
            }
            if (list.isEmpty()) {
                list = ObjectLists.emptyList();
            }
        } else {
            list = get(clazz.getSuperclass(), obj);
        }
        cache.put(clazz, list);
        return list;
    }

    public List<T> get(Object obj) {
        return obj == null ? ObjectLists.emptyList() : get(obj.getClass(), obj);
    }

    public Map<Class<?>, List<Entry<T>>> getMap() {
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
