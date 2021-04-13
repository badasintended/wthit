package mcp.mobius.waila.utils;

import java.util.Map;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcp.mobius.waila.api.ITaggableList;

public class TaggableList<T, V> extends ObjectArrayList<V> implements ITaggableList<T, V> {

    protected final Map<T, V> tags = Maps.newHashMap();
    private final Function<T, V> setProcessor;

    public TaggableList(Function<T, V> setProcessor) {
        Preconditions.checkNotNull(setProcessor);

        this.setProcessor = setProcessor;
    }

    @Override
    public void setTag(T tag, V value) {
        Preconditions.checkNotNull(tag);
        Preconditions.checkNotNull(value);

        V old = tags.put(tag, value);
        if (old == null)
            add(setProcessor.apply(tag));
    }

    @Override
    public V removeTag(T tag) {
        Preconditions.checkNotNull(tag);

        return tags.remove(tag);
    }

    @Override
    public V getTag(T tag) {
        Preconditions.checkNotNull(tag);

        return tags.get(tag);
    }

    @Override
    public Map<T, V> getTags() {
        return tags;
    }

    @Override
    public void absorb(ITaggableList<T, V> other) {
        this.addAll(other);
        tags.putAll(other.getTags());
    }

    @Override
    public void clear() {
        super.clear();
        tags.clear();
    }

}
