package mcp.mobius.waila.config;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import mcp.mobius.waila.api.IJsonConfig;
import org.jetbrains.annotations.Nullable;

public class CommenterFactories implements Supplier<Function<List<String>, @Nullable String>> {

    private final List<Supplier<IJsonConfig.Commenter>> factories;

    public CommenterFactories(List<Supplier<IJsonConfig.Commenter>> factories) {
        this.factories = factories;
    }

    @Override
    public Function<List<String>, @Nullable String> get() {
        var commenters = factories.stream().map(Supplier::get).toList();

        return path -> {
            StringBuilder builder = null;

            for (var commenter : commenters) {
                var comment = commenter.getComment(path);
                if (comment == null) continue;

                if (builder == null) {
                    builder = new StringBuilder(comment);
                } else {
                    builder.append('\n').append(comment);
                }
            }

            return builder == null ? null : builder.toString();
        };
    }

}
