package mcp.mobius.waila.api.impl;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import mcp.mobius.waila.api.ITaggableList;

public class TaggableList<TAG, VALUE> extends ArrayList<VALUE> implements ITaggableList<TAG, VALUE> {

    protected final Map<TAG, VALUE> tags = Maps.newHashMap();
    private final Function<TAG, VALUE> setProcessor;

    public TaggableList(Function<TAG, VALUE> setProcessor) {
        Preconditions.checkNotNull(setProcessor);

        this.setProcessor = setProcessor;
    }

    @Override
    public void setTag(TAG tag, VALUE value) {
        Preconditions.checkNotNull(tag);
        Preconditions.checkNotNull(value);

        VALUE old = tags.put(tag, value);
        if (old == null)
            add(setProcessor.apply(tag));
    }

    @Override
    public VALUE removeTag(TAG tag) {
        Preconditions.checkNotNull(tag);

        return tags.remove(tag);
    }

    @Override
    public VALUE getTag(TAG tag) {
        Preconditions.checkNotNull(tag);

        return tags.get(tag);
    }

    @Override
    public Map<TAG, VALUE> getTags() {
        return tags;
    }

    @Override
    public void absorb(ITaggableList<TAG, VALUE> other) {
        this.addAll(other);
        tags.putAll(other.getTags());
    }

}
