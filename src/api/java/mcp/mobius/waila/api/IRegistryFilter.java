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
 * <li>{@code @namespace} - include objects based on their namespace location.</li>
 * <li>{@code #tag} - include objects based on data pack tags.</li>
 * <li>{@code /regex/} - include objects based on regular expression.</li>
 * <li>{@code default} - include objects with specific ID.</li>
 * </ul>
 * <p>
 * An exclamation mark (!) prefix can be added which negates the pattern.
 * Any entries matching previous rules will be removed from it.
 * Can be combined with other rule to exclude what matches the rule.
 * <p>
 * <b>Note:</b> Instances of {@link IRegistryFilter} should only be obtained once, unless the ruleset is changed.
 * <p>
 * <b>Example</b><ul>
 * <li>{@code @aether} - include all block from the aether namespace</li>
 * <li>{@code #minecraft:planks} - include all blocks in the planks tag</li>
 * <li>{@code /.*_ore/} - include all blocks that ends with {@code _ore}</li>
 * <li>{@code minecraft:iron_block} - include only the iron block</li>
 * <li>{@code !/.*:oak_.*$/} - exclude all blocks that its path start with {@code oak_}</li>
 * </ul>
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface IRegistryFilter<T> {

    static <T> Builder<T> of(ResourceKey<? extends Registry<T>> registryKey) {
        return IApiService.INSTANCE.createRegistryFilterBuilder(registryKey);
    }

    /**
     * Comment to be used with as a file header.
     * <p>
     * Not a constant so it won't be inlined as it can change.
     */
    static String getHeader() {
        return """
            Operators:
            @namespace - include objects based on their namespace location
            #tag       - include objects based on data pack tags
            /regex/    - include objects based on regular expression
            default    - include objects with specific ID

            An exclamation mark (!) prefix can be added which negates the pattern.
            Any entries matching previous rules will be removed from it.
            Can be combined with other rule to exclude what matches the rule.

            Example:
            @aether              - include all block from the aether namespace
            #minecraft:planks    - include all blocks in the planks tag
            /.*_ore/             - include all blocks that ends with "_ore"
            minecraft:iron_block - include only the iron block
            !/.*:oak_.*$/        - exclude all blocks that its path start with "oak_\"""";
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
