package mcp.mobius.waila.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import mcp.mobius.waila.api.IRegistryFilter;
import mcp.mobius.waila.util.Log;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

public class RegistryFilter<T> implements IRegistryFilter<T> {

    public static final ThreadLocal<@Nullable RegistryAccess> REGISTRY = ThreadLocal.withInitial(() -> null);
    public static final Set<RegistryFilter<?>> INSTANCES = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));

    private static final Log LOG = Log.create();

    private final ResourceKey<? extends Registry<T>> registryKey;
    private final Set<Rule<T>> rules;

    private final ThreadLocal<@Nullable Registry<T>> registry = ThreadLocal.withInitial(() -> null);
    private final ThreadLocal<Set<T>> entries = ThreadLocal.withInitial(Set::of);
    private final ThreadLocal<Boolean> loaded = ThreadLocal.withInitial(() -> true);

    private RegistryFilter(ResourceKey<? extends Registry<T>> registry, Set<Rule<T>> rules) {
        this.registryKey = registry;
        this.rules = rules;

        INSTANCES.add(this);
        attach();
    }

    public static void attach(@Nullable RegistryAccess registryAccess) {
        REGISTRY.set(registryAccess);
        INSTANCES.forEach(RegistryFilter::attach);
    }

    public void attach() {
        var access = REGISTRY.get();
        registry.set(access == null ? null : access.registryOrThrow(registryKey));
        entries.set(Set.of());
        loaded.set(false);
    }

    private void load() {
        if (loaded.get()) return;

        var registry = this.registry.get();
        if (registry == null) return;

        Stopwatch stopwatch = null;
        if (LOG.isDebugEnabled()) {
            stopwatch = Stopwatch.createStarted();
            LOG.debug("Attaching registry to filter, id: {}", hashCode());
        }

        var entries = new HashSet<T>(this.entries.get().size());

        rules.forEach(rule -> registry.holders().forEach(holder -> {
            if (rule.matches(holder)) entries.add(holder.value());
        }));

        this.entries.set(Collections.unmodifiableSet(entries));

        if (stopwatch != null) {
            LOG.debug("Finished in {}ms, {} entries matched", stopwatch.elapsed(TimeUnit.MILLISECONDS), entries.size());
            entries.stream()
                .map(it -> Objects.requireNonNull(registry.getKey(it)).toString())
                .sorted()
                .forEach(it -> LOG.debug("\t{}", it));
        }

        loaded.set(true);
    }

    @Override
    public Collection<T> getMatches() {
        load();
        return entries.get();
    }

    @Override
    public boolean matches(T object) {
        load();
        return entries.get().contains(object);
    }

    private interface Rule<T> {

        boolean matches(Holder.Reference<T> holder);

    }

    public static class Builder<T> implements IRegistryFilter.Builder<T> {

        private final ResourceKey<? extends Registry<T>> registryKey;
        private final Set<Rule<T>> rules;

        private final @Nullable Stopwatch stopwatch;

        public Builder(ResourceKey<? extends Registry<T>> registryKey) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Start filter for {}", registryKey.location());
                stopwatch = Stopwatch.createStarted();
            } else {
                stopwatch = null;
            }

            this.registryKey = registryKey;
            this.rules = new HashSet<>();
        }

        @Override
        public IRegistryFilter.Builder<T> parse(String rule) {
            var prefix = rule.charAt(0);

            switch (prefix) {
                case '@' -> {
                    LOG.debug("\tNamespace: {}", rule);
                    var namespace = rule.substring(1);
                    rules.add(it -> it.key().location().getNamespace().equals(namespace));
                }
                case '#' -> {
                    LOG.debug("\tTag      : {}", rule);
                    var tagId = new ResourceLocation(rule.substring(1));
                    var tag = TagKey.create(registryKey, tagId);
                    rules.add(it -> it.is(tag));
                }
                case '/' -> {
                    LOG.debug("\tRegex    : {}", rule);
                    Preconditions.checkArgument(rule.endsWith("/"), "Regex filter must also ends with /");
                    var pattern = Pattern.compile(rule.substring(1, rule.length() - 1));
                    rules.add(it -> pattern.matcher(it.key().location().toString()).matches());
                }
                default -> {
                    LOG.debug("\tID       : {}", rule);
                    rules.add(it -> it.is(new ResourceLocation(rule)));
                }
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
            for (var filter : rules) parse(filter);
            return this;
        }

        @Override
        public IRegistryFilter<T> build() {
            var res = new RegistryFilter<>(registryKey, rules);

            if (stopwatch != null) {
                LOG.debug("Finished in {}ms, id: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), res.hashCode());
            }

            return res;
        }

    }

}
