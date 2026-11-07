

package me.shedaniel.rei.api.common.entry;

import me.shedaniel.rei.api.common.entry.type.EntryType;

public interface EntryStack<T> {

    static <T> EntryStack<T> of(EntryType<T> type, T value) {
        throw new AssertionError("stub");
    }

}
