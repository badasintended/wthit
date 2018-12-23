package mcp.mobius.waila.api.impl;

import com.google.common.collect.Maps;
import mcp.mobius.waila.api.ITaggedList;

import java.util.*;
import java.util.Map.Entry;

public class TaggedList<E, T> extends ArrayList<E> implements ITaggedList<E, T> {
    Map<E, Set<T>> tags = Maps.newHashMap();

    @Override
    public boolean add(E e, T tag) {
        if (!tags.containsKey(e))
            tags.put(e, new HashSet<>());
        tags.get(e).add(tag);
        return super.add(e);
    }

    @Override
    public boolean add(E e, Collection<? extends T> taglst) {
        if (!tags.containsKey(e))
            tags.put(e, new HashSet<>());
        tags.get(e).addAll(taglst);

        return super.add(e);
    }

    @Override
    public Set<T> getTags(E e) {
        Set<T> ret = tags.get(e);
        if (ret == null && this.contains(e)) {
            tags.put(e, new HashSet<>());
            ret = tags.get(e);
        }
        return ret;
    }

    @Override
    public Set<T> getTags(int index) {
        return this.getTags(this.get(index));
    }

    @Override
    public void addTag(E e, T tag) {
        if (this.contains(e) && !tags.containsKey(e))
            tags.put(e, new HashSet<>());

        tags.get(e).add(tag);
    }

    @Override
    public void addTag(int index, T tag) {
        this.addTag(this.get(index), tag);
    }

    @Override
    public void removeTag(E e, T tag) {
        if (this.contains(e) && !tags.containsKey(e))
            tags.put(e, new HashSet<>());

        tags.get(e).remove(tag);
    }

    @Override
    public void removeTag(int index, T tag) {
        this.removeTag(this.get(index), tag);
    }

    @Override
    public Set<E> getEntries(T tag) {
        Set<E> ret = new HashSet<>();
        for (Entry<E, Set<T>> s : tags.entrySet()) {
            if (s.getValue().contains(tag))
                ret.add(s.getKey());
        }
        return ret;
    }

    @Override
    public void removeEntries(T tag) {
        for (E e : this.getEntries(tag))
            this.remove(e);
    }

    @Override
    public String getTagsAsString(E e) {
        StringBuilder ret = new StringBuilder();
        for (T s : tags.get(e))
            ret.append(s.toString()).append(",");

        if (ret.length() > 0)
            ret = new StringBuilder(ret.substring(0, ret.length() - 1));

        return ret.toString();
    }

    @Override
    public void clear() {
        tags.clear();
        super.clear();
    }

    @Override
    public E set(int index, E element) {
        tags.remove(this.get(index));
        return super.set(index, element);
    }

    @Override
    public E remove(int index) {
        tags.remove(this.get(index));
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        tags.remove(o);
        return super.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        c.forEach(tags::remove);
        return super.removeAll(c);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; i++)
            tags.remove(this.get(i));

        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        for (E e : tags.keySet())
            if (!c.contains(e))
                tags.remove(e);

        return super.retainAll(c);
    }
}
