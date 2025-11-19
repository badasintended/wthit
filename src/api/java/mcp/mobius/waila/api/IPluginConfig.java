package mcp.mobius.waila.api;

import java.util.Set;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * Read-only interface for Waila internal config storage.
 * <p>
 * An instance of this interface is passed to most of Waila callbacks as a way to change the behavior depending on client settings.
 */
@ApiStatus.NonExtendable
public interface IPluginConfig {

    /**
     * Gets a collection of all the keys for a given namespace.
     *
     * @param namespace the namespace to get keys from
     *
     * @return all the keys for a given namespace.
     */
    Set<Identifier> getKeys(String namespace);

    /**
     * Gets a collection of all keys.
     *
     * @return all registered keys.
     */
    Set<Identifier> getKeys();

    boolean getBoolean(Identifier key);

    int getInt(Identifier key);

    double getDouble(Identifier key);

    String getString(Identifier key);

    <T extends Enum<T>> T getEnum(Identifier key);

}
