package mcp.mobius.waila.api;

import java.util.Collection;

import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

/**
 * Filters registry object based on set of string rules.
 * <p>
 * <b> Rule Operators:</b><ul>
 * <li>{@code @namespace} - Filter objects based on their namespace location.</li>
 * <li>{@code #tag} - Filter objects based on data pack tags.</li>
 * <li>{@code /regex/} - Filter objects based on regular expression.</li>
 * <li>{@code default} - Filter objects with specific ID.</li>
 * </ul>
 * <p>
 * <b>Note:</b> Instances of {@link IRegistryFilter} should only be obtained once, unless the ruleset is changed.
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface IRegistryFilter<T> {

    static <T> Builder<T> of(ResourceKey<? extends Registry<T>> registryKey) {
        return IApiService.INSTANCE.createRegistryFilterBuilder(registryKey);
    }

    /**
     * Returns whether the filter matches the object.
     * <p>
     * <b>Note:</b> When not in world, this will always return {@code false}.
     */
    boolean matches(T object);

    /**
     * Returns <b>read-only</b> collection containing all objects that matches the filter.
     * <p>
     * <b>Note:</b> When not in world, this will always return empty collection.
     */
    Collection<T> getMatches();

    @ApiStatus.Experimental
    @ApiStatus.NonExtendable
    interface Builder<T> {

        Builder<T> parse(String rule);

        Builder<T> parse(Iterable<String> rules);

        Builder<T> parse(String... rules);

        IRegistryFilter<T> build();

    }

}
