package mcp.mobius.waila.api;

import java.nio.file.Path;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;

/**
 * The registrar for common-sided objects.
 *
 * <h3 id="config-tl">Configuration Translations</h3>
 * These translation keys will be used on the plugin config screen:<ul>
 * <li>{@code config.waila.plugin_[namespace].[path]} for the config name.</li>
 * <li>{@code config.waila.plugin_[namespace].[path]_desc} for the config description.
 * This one is optional and can be left missing.</li></ul>
 * <p>
 * Config options can also be grouped with the same prefix on its path followed by a period [{@code .}],
 * e.g. {@code my_plugin:group.option1} and {@code my_plugin:group.option2}.<br>
 * Then, add a translation for {@code config.waila.plugin_[namespace].[group]} with the group name
 * for it to be visible in the config screen.
 */
@ApiStatus.NonExtendable
public interface ICommonRegistrar {

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * The main purpose of this method is for toggling feature that enabled by default.
     * This method allows server to disable the option remotely for all connected clients,
     * the clients can then toggle the option for their own side only if it is enabled on the server.
     *
     * @param key        the namespaced key to be used with {@link IPluginConfig#getBoolean(ResourceLocation)}
     * @param clientOnly whether the feature available on client-only, e.g. not using {@linkplain IDataProvider server data}
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    void featureConfig(ResourceLocation key, boolean clientOnly);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are accessible from either client or server, but both could
     * be different with each other as they are not synced.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    void localConfig(ResourceLocation key, boolean defaultValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are accessible from either client or server, but both could
     * be different with each other as they are not synced.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     * @param format       used for formatting text box in plugin config screen
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    void localConfig(ResourceLocation key, int defaultValue, IntFormat format);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are accessible from either client or server, but both could
     * be different with each other as they are not synced.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    default void localConfig(ResourceLocation key, int defaultValue) {
        localConfig(key, defaultValue, IntFormat.DECIMAL);
    }

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are accessible from either client or server, but both could
     * be different with each other as they are not synced.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    void localConfig(ResourceLocation key, double defaultValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are accessible from either client or server, but both could
     * be different with each other as they are not synced.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    void localConfig(ResourceLocation key, String defaultValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are accessible from either client or server, but both could
     * be different with each other as they are not synced.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     */
    <T extends Enum<T>> void localConfig(ResourceLocation key, T defaultValue);

    /**
     * Adds an entry to the config screen to open a file with external editor.
     * <p>
     * Does <b>NOT</b> handle file parsing, and will <b>NOT</b> available from {@link IPluginConfig}.
     * Needs to be handled by the user manually.
     *
     * @param key  the namespaced key
     * @param path the path to the file
     *
     * @see IJsonConfig
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    void externalConfig(ResourceLocation key, Path path);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    void syncedConfig(ResourceLocation key, boolean defaultValue, boolean clientOnlyValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     * @param format          used for formatting text box in plugin config screen
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    void syncedConfig(ResourceLocation key, int defaultValue, int clientOnlyValue, IntFormat format);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    default void syncedConfig(ResourceLocation key, int defaultValue, int clientOnlyValue) {
        syncedConfig(key, defaultValue, clientOnlyValue, IntFormat.DECIMAL);
    }

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    void syncedConfig(ResourceLocation key, double defaultValue, double clientOnlyValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    void syncedConfig(ResourceLocation key, String defaultValue, String clientOnlyValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * <p>
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     *
     * @see <a href="#config-tl">The configuration translation documentation</a>
     */
    <T extends Enum<T>> void syncedConfig(ResourceLocation key, T defaultValue, T clientOnlyValue);

    /**
     * Registers config key aliases that will be migrated gracefully to the actual key.
     * <p>
     * Also sync the value using aliased keys, so outdated client can still get the correct value.
     */
    void configAlias(ResourceLocation actual, ResourceLocation... aliases);

    /**
     * Adds the specified entity types to the default blacklist.
     *
     * @param priority the modifier priority, lower number will be called last
     */
    void blacklist(int priority, Block... blocks);

    /**
     * Adds the specified entity types to the default blacklist.
     *
     * @param priority the modifier priority, lower number will be called last
     */
    void blacklist(int priority, BlockEntityType<?>... blockEntityTypes);

    /**
     * Adds the specified entity types to the default blacklist.
     */
    default void blacklist(Block... blocks) {
        blacklist(WailaConstants.DEFAULT_PRIORITY, blocks);
    }

    /**
     * Adds the specified entity types to the default blacklist.
     */
    default void blacklist(BlockEntityType<?>... blockEntityTypes) {
        blacklist(WailaConstants.DEFAULT_PRIORITY, blockEntityTypes);
    }

    /**
     * Removes the specified entity types to the default blacklist.
     *
     * @param priority the modifier priority, lower number will be called last
     */
    void removeBlacklist(int priority, Block... blocks);

    /**
     * Removes the specified entity types to the default blacklist.
     *
     * @param priority the modifier priority, lower number will be called last
     */
    void removeBlacklist(int priority, BlockEntityType<?>... blockEntityTypes);

    /**
     * Removes the specified entity types to the default blacklist.
     */
    default void removeBlacklist(Block... blocks) {
        removeBlacklist(WailaConstants.DEFAULT_PRIORITY, blocks);
    }

    /**
     * Removes the specified entity types to the default blacklist.
     */
    default void removeBlacklist(BlockEntityType<?>... blockEntityTypes) {
        removeBlacklist(WailaConstants.DEFAULT_PRIORITY, blockEntityTypes);
    }

    /**
     * Registers an {@link IDataProvider<BlockEntity>} instance for data syncing purposes. A {@link BlockEntity}
     * is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T, BE extends BlockEntity> void blockData(IDataProvider<BE> provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IDataProvider<BlockEntity>} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} for data syncing purposes.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    default <T, BE extends BlockEntity> void blockData(IDataProvider<BE> provider, Class<T> clazz) {
        blockData(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Adds the specified entity types to the default blacklist.
     *
     * @param priority the modifier priority, lower number will be called last
     */
    void blacklist(int priority, EntityType<?>... entityTypes);

    /**
     * Adds the specified entity types to the default blacklist.
     */
    default void blacklist(EntityType<?>... entityTypes) {
        blacklist(WailaConstants.DEFAULT_PRIORITY, entityTypes);
    }

    /**
     * Removes the specified entity types to the default blacklist.
     *
     * @param priority the modifier priority, lower number will be called last
     */
    void removeBlacklist(int priority, EntityType<?>... entityTypes);

    /**
     * Removes the specified entity types to the default blacklist.
     */
    default void removeBlacklist(EntityType<?>... entityTypes) {
        removeBlacklist(WailaConstants.DEFAULT_PRIORITY, entityTypes);
    }

    /**
     * Registers an {@link IDataProvider<Entity>} instance for data syncing purposes.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T, E extends Entity> void entityData(IDataProvider<E> provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IDataProvider<Entity>} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} for data syncing purposes.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    default <T, E extends Entity> void entityData(IDataProvider<E> provider, Class<T> clazz) {
        entityData(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers a data type used for syncing data from server to client.
     *
     * @param type  the data type
     * @param codec the data-to-buffer codec
     */
    <D extends IData> void dataType(IData.Type<D> type, StreamCodec<? super RegistryFriendlyByteBuf, ? extends D> codec);


}
