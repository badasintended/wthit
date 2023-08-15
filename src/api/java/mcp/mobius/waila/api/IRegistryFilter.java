package mcp.mobius.waila.api;

import java.util.Collection;

import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;

/**
 * Filters registry object based on set of string operators.
 * <p>
 * <b>Operators:</b><ul>
 * <li>{@code @namespace} - Filter objects based on their namespace location.</li>
 * <li>{@code #tag} - Filter objects based on data pack tags.</li>
 * <li>{@code /regex/} - Filter objects based on regular expression.</li>
 * <li>{@code default} - Filter objects with specific ID.</li>
 * </ul>
 * <p>
 * <b>Note:</b> Instances of {@link IRegistryFilter} should only be obtained once,
 * unless the filter list is changed. For dynamic registry, it should be once every server load.
 */
@ApiStatus.NonExtendable
public interface IRegistryFilter<T> {

    static <T> Builder<T> of(Registry<T> registry) {
        return IApiService.INSTANCE.createRegistryFilterBuilder(registry);
    }

    boolean contains(T object);

    Collection<T> getValues();

    @ApiStatus.NonExtendable
    interface Builder<T> {

        Builder<T> parse(String rule);

        Builder<T> parse(Iterable<String> rules);

        Builder<T> parse(String... rules);

        IRegistryFilter<T> build();

    }

}
