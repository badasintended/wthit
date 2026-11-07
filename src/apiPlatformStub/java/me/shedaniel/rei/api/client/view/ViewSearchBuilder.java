package me.shedaniel.rei.api.client.view;

import me.shedaniel.rei.api.common.entry.EntryStack;

public interface ViewSearchBuilder {

    static ViewSearchBuilder builder() {
        throw new AssertionError("stub");
    }

    <T> ViewSearchBuilder addRecipesFor(EntryStack<T> stack);

    <T> ViewSearchBuilder addUsagesFor(EntryStack<T> stack);

    default boolean open() {
        throw new AssertionError("stub");
    }

}
