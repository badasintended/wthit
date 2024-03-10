package mcp.mobius.waila.command;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;

@SuppressWarnings("ConstantConditions")
public class ArgumentBuilderBuilder<S> {

    private final LiteralArgumentBuilder<S> root;
    private final Deque<ArgumentBuilder<S, ?>> deque = new ArrayDeque<>();

    public ArgumentBuilderBuilder(LiteralArgumentBuilder<S> root) {
        this.root = root;
        deque.push(root);
    }

    public ArgumentBuilderBuilder<S> then(Consumer<ArgumentBuilderBuilder<S>> builder) {
        builder.accept(this);
        return this;
    }

    public ArgumentBuilderBuilder<S> then(ArgumentBuilder<S, ?> builder) {
        deque.push(builder);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ArgumentBuilderBuilder<S> suggests(SuggestionProvider<S> provider) {
        Preconditions.checkState(deque.peek() instanceof RequiredArgumentBuilder<?,?>, "not a RequiredArgumentBuilder");
        ((RequiredArgumentBuilder<S, ?>) deque.peek()).suggests(provider);
        return this;
    }

    public ArgumentBuilderBuilder<S> executes(Command<S> command) {
        assertNonRoot();
        deque.peek().executes(command);
        return this;
    }

    public ArgumentBuilderBuilder<S> requires(final Predicate<S> requirement) {
        deque.peek().requires(requirement);
        return this;
    }

    public ArgumentBuilderBuilder<S> pop(String... names) {
        Preconditions.checkArgument(names.length > 0, "names == 0");

        for (var name : names) {
            assertNonRoot();

            var last = deque.peek();
            if (last instanceof LiteralArgumentBuilder<?> literal) {
                Preconditions.checkArgument(literal.getLiteral().equals(name), "literal != name");
            } else if (last instanceof RequiredArgumentBuilder<?,?> required) {
                Preconditions.checkArgument(required.getName().equals(name), "required != name");
            }

            deque.pop();
            deque.peek().then(last);
        }
        return this;
    }

    public void register(CommandDispatcher<S> dispatcher) {
        Preconditions.checkState(deque.peek() == root, "last != root");
        dispatcher.register(root);
    }

    private void assertNonRoot() {
        Preconditions.checkState(deque.peek() != root, "last == root");
    }

}
