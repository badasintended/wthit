package mcp.mobius.waila.api;

import java.nio.file.Path;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IRegistrar {

    /**
     * The default priority for all component.
     */
    int DEFAULT_PRIORITY = 1000;

    /**
     * Registers a namespaced config key to be accessed within data providers.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     */
    void addConfig(ResourceLocation key, boolean defaultValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     * @param format       used for formatting text box in plugin config screen
     */
    void addConfig(ResourceLocation key, int defaultValue, IntFormat format);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     */
    default void addConfig(ResourceLocation key, int defaultValue) {
        addConfig(key, defaultValue, IntFormat.DECIMAL);
    }

    /**
     * Registers a namespaced config key to be accessed within data providers.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     */
    void addConfig(ResourceLocation key, double defaultValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     */
    void addConfig(ResourceLocation key, String defaultValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     */
    <T extends Enum<T>> void addConfig(ResourceLocation key, T defaultValue);

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
     */
    void addConfig(ResourceLocation key, Path path);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * When the server has waila installed, the server will send its value and it will be merged with client value.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     */
    void addMergedConfig(ResourceLocation key, boolean defaultValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * When the server has waila installed, the server will send its value and it will be merged with client value.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     */
    void addMergedSyncedConfig(ResourceLocation key, boolean defaultValue, boolean clientOnlyValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     */
    void addSyncedConfig(ResourceLocation key, boolean defaultValue, boolean clientOnlyValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     * @param format          used for formatting text box in plugin config screen
     */
    void addSyncedConfig(ResourceLocation key, int defaultValue, int clientOnlyValue, IntFormat format);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     */
    default void addSyncedConfig(ResourceLocation key, int defaultValue, int clientOnlyValue) {
        addSyncedConfig(key, defaultValue, clientOnlyValue, IntFormat.DECIMAL);
    }

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     */
    void addSyncedConfig(ResourceLocation key, double defaultValue, double clientOnlyValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     */
    void addSyncedConfig(ResourceLocation key, String defaultValue, String clientOnlyValue);

    /**
     * Registers a namespaced config key to be accessed within data providers.
     * These values are sent from the server to the client upon connection.
     *
     * @param key             the namespaced key
     * @param defaultValue    the default value
     * @param clientOnlyValue the value that will be used when the server connected doesn't have waila installed
     */
    <T extends Enum<T>> void addSyncedConfig(ResourceLocation key, T defaultValue, T clientOnlyValue);

    /**
     * Adds an event listener
     */
    void addEventListener(IEventListener listener, int priority);

    /**
     * Adds an event listener
     */
    default void addEventListener(IEventListener listener) {
        addEventListener(listener, DEFAULT_PRIORITY);
    }

    /**
     * Adds the specified entity types to the default blacklist.
     */
    void addBlacklist(Block... blocks);

    /**
     * Adds the specified entity types to the default blacklist.
     */
    void addBlacklist(BlockEntityType<?>... blockEntityTypes);

    /**
     * Registers an {@link IBlockComponentProvider} instance to allow overriding the block being displayed.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance with {@link #DEFAULT_PRIORITY} to allow overriding the block being displayed.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    default <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz) {
        addOverride(provider, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IBlockComponentProvider} instance to allow overriding the displayed icon for a block via the
     * {@link IBlockComponentProvider#getIcon(IBlockAccessor, IPluginConfig)} method.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addIcon(IBlockComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance with {@link #DEFAULT_PRIORITY} to allow overriding the displayed icon for a block via the
     * {@link IBlockComponentProvider#getIcon(IBlockAccessor, IPluginConfig)} method.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    @ApiSide.ClientOnly
    default <T> void addIcon(IBlockComponentProvider provider, Class<T> clazz) {
        addIcon(provider, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IBlockComponentProvider} instance for appending {@link Component} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param position the position on the tooltip this applies to
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance with {@link #DEFAULT_PRIORITY} for appending {@link Component} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param position the position on the tooltip this applies to
     * @param clazz    the highest level class to apply to
     */
    @ApiSide.ClientOnly
    default <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        addComponent(provider, position, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IDataProvider<BlockEntity>} instance for data syncing purposes. A {@link BlockEntity}
     * is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ServerOnly
    <T, BE extends BlockEntity> void addBlockData(IDataProvider<BE> provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IDataProvider<BlockEntity>} instance with {@link #DEFAULT_PRIORITY} for data syncing purposes. A {@link BlockEntity}
     * is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    default <T, BE extends BlockEntity> void addBlockData(IDataProvider<BE> provider, Class<T> clazz) {
        addBlockData(provider, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Adds the specified entity types to the default blacklist.
     */
    void addBlacklist(EntityType<?>... entityTypes);

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow overriding the entity being displayed.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with {@link #DEFAULT_PRIORITY} to allow overriding the entity being displayed.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    default <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz) {
        addOverride(provider, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow displaying an icon via the
     * {@link IEntityComponentProvider#getIcon(IEntityAccessor, IPluginConfig)} method.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addIcon(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with {@link #DEFAULT_PRIORITY} to allow displaying an icon via the
     * {@link IEntityComponentProvider#getIcon(IEntityAccessor, IPluginConfig)} method.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    @ApiSide.ClientOnly
    default <T> void addIcon(IEntityComponentProvider provider, Class<T> clazz) {
        addIcon(provider, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance for appending {@link Component} to the tooltip.
     *
     * @param provider the data provider instance
     * @param position the position on the tooltip this applies to
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with {@link #DEFAULT_PRIORITY} for appending {@link Component} to the tooltip.
     *
     * @param provider the data provider instance
     * @param position the position on the tooltip this applies to
     * @param clazz    the highest level class to apply to
     */
    @ApiSide.ClientOnly
    default <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        addComponent(provider, position, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IDataProvider<Entity>} instance for data syncing purposes.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ServerOnly
    <T, E extends Entity> void addEntityData(IDataProvider<E> provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IDataProvider<Entity>} instance eith {@link #DEFAULT_PRIORITY} for data syncing purposes.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    default <T, E extends Entity> void addEntityData(IDataProvider<E> provider, Class<T> clazz) {
        addEntityData(provider, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers a data type used for syncing data from server to client.
     *
     * @param id         the id of the data type
     * @param type       the type of data
     * @param serializer the data to buffer serializer
     */
    <T extends IData> void addDataType(ResourceLocation id, Class<T> type, IData.Serializer<T> serializer);

    /**
     * Registers an {@link IThemeType} instance.
     *
     * @param id   the theme type id
     * @param type the theme type
     */
    @ApiSide.ClientOnly
    @ApiStatus.Experimental
    <T extends ITheme> void addThemeType(ResourceLocation id, IThemeType<T> type);

    /**
     * Replaces the picker that Waila will use to get the object to show the tooltip to.
     *
     * @param picker   the picker replacement
     * @param priority the replacement priority, if there is picker with higher priority (smaller number) the picker won't get replaced
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    void replacePicker(IObjectPicker picker, int priority);

    /**
     * Replaces the picker that Waila will use to get the object to show the tooltip to.
     */
    @ApiSide.ClientOnly
    default void replacePicker(IObjectPicker picker) {
        replacePicker(picker, DEFAULT_PRIORITY);
    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------
    // TODO: Remove

    /**
     * @deprecated use {@link #addBlockData(IDataProvider, Class)}
     */
    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.21")
    default <T, BE extends BlockEntity> void addBlockData(IServerDataProvider<BE> provider, Class<T> clazz) {
        addBlockData((IDataProvider<? extends BlockEntity>) provider, clazz);
    }

    /**
     * @deprecated use {@link #addEntityData(IDataProvider, Class)}
     */
    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.21")
    default <T, E extends Entity> void addEntityData(IServerDataProvider<E> provider, Class<T> clazz) {
        addEntityData((IDataProvider<? extends Entity>) provider, clazz);
    }

}
