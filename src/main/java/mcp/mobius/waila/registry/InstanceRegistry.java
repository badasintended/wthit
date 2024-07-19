package mcp.mobius.waila.registry;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import mcp.mobius.waila.api.IInstanceRegistry;
import org.jetbrains.annotations.Nullable;

public class InstanceRegistry<T> implements IInstanceRegistry<T> {

    private final Map<Class<?>, Set<EntryImpl<T>>> map = new Object2ObjectOpenHashMap<>();
    private final Map<Class<?>, List<Entry<T>>> cache = new Object2ObjectOpenHashMap<>();

    private boolean reversed = false;

    public void reversed() {
        reversed = true;
    }

    @Override
    public void add(Class<?> key, T instance, int priority) {
        map.computeIfAbsent(key, k -> new ObjectLinkedOpenHashSet<>())
            .add(new EntryImpl<>(instance, priority));
    }

    @Override
    public List<Entry<T>> get(@Nullable Object target) {
        if (target == null) {
            return ObjectLists.emptyList();
        }

        var clazz = target.getClass();
        if (clazz == Object.class) {
            return ObjectLists.emptyList();
        }

        if (cache.containsKey(clazz)) {
            return cache.get(clazz);
        }

        List<Entry<T>> entries = new ObjectArrayList<>();
        map.forEach((k, v) -> {
            if (k.isInstance(target)) {
                entries.addAll(v);
            }
        });

        Comparator<Entry<T>> comparator = Comparator.comparingInt(Entry::priority);
        if (reversed) {
            comparator = comparator.reversed();
        }

        entries.sort(comparator);

        List<Entry<T>> result = entries.isEmpty() ? ObjectLists.emptyList() : entries;
        cache.put(clazz, result);
        return result;
    }

    public Map<Class<?>, Set<EntryImpl<T>>> getMap() {
        return map;
    }

    public record EntryImpl<T>(T instance, int priority) implements Entry<T> {

    }

}
