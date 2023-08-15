package mcp.mobius.waila.registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.IRegistryFilter;
import mcp.mobius.waila.util.Log;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class RegistryFilter<T> implements IRegistryFilter<T> {

    private static final Log LOG = Log.create();

    private final Set<T> set;

    public RegistryFilter(Set<T> set) {
        this.set = set;
    }

    @Override
    public Collection<T> getValues() {
        return set;
    }

    @Override
    public boolean contains(T object) {
        return set.contains(object);
    }

    public static class Builder<T> implements IRegistryFilter.Builder<T> {

        private final Registry<T> registry;
        private final Set<ResourceLocation> keys;

        public Builder(Registry<T> registry) {
            LOG.debug("Start filter for {}", registry.key().location());

            this.registry = registry;
            this.keys = new HashSet<>(registry.size());
        }

        private void debugKey(ResourceLocation key) {
            LOG.debug("\t\t{}", key);
        }

        private void addKey(ResourceLocation key) {
            debugKey(key);
            keys.add(key);
        }

        @Override
        public IRegistryFilter.Builder<T> parse(String rule) {
            LOG.debug("\tParsing {}", rule);
            char prefix = rule.charAt(0);

            switch (prefix) {
                case '@' -> {
                    String namespace = rule.substring(1);
                    registry.keySet().stream()
                        .filter(it -> it.getNamespace().equals(namespace))
                        .forEach(this::addKey);
                }
                case '#' -> {
                    ResourceLocation tagId = new ResourceLocation(rule.substring(1));
                    TagKey<T> tag = TagKey.create(registry.key(), tagId);
                    registry.holders()
                        .filter(it -> it.is(tag))
                        .forEach(it -> addKey(it.key().location()));
                }
                case '/' -> {
                    Preconditions.checkArgument(rule.endsWith("/"), "Regex filter must also ends with /");
                    Pattern pattern = Pattern.compile(rule.substring(1, rule.length() - 1));
                    registry.keySet().stream()
                        .filter(it -> pattern.matcher(it.toString()).matches())
                        .forEach(this::addKey);
                }
                default -> addKey(new ResourceLocation(rule));
            }

            return this;
        }

        @Override
        public IRegistryFilter.Builder<T> parse(Iterable<String> rules) {
            rules.forEach(this::parse);
            return this;
        }

        @Override
        public IRegistryFilter.Builder<T> parse(String... rules) {
            for (String filter : rules) parse(filter);
            return this;
        }

        @Override
        public IRegistryFilter<T> build() {
            Set<T> set = keys.stream()
                .map(registry::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());

            if (LOG.isDebugEnabled()) {
                LOG.debug("\tFinished, total {} entries", keys.size());
                keys.stream()
                    .sorted()
                    .forEach(this::debugKey);
            }

            return new RegistryFilter<>(set);
        }

    }

}
