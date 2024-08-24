package mcp.mobius.waila.api;

import java.nio.file.Path;

import mcp.mobius.waila.api.__internal__.ApiSide;
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
 * @deprecated use {@link ICommonRegistrar} or {@link IClientRegistrar} instead.
 */
@Deprecated
@ApiStatus.NonExtendable
public interface IRegistrar extends ICommonRegistrar, IClientRegistrar {

    int DEFAULT_PRIORITY = WailaConstants.DEFAULT_PRIORITY;

    default void addConfig(ResourceLocation key, boolean defaultValue) {
        localConfig(key, defaultValue);
    }

    default void addConfig(ResourceLocation key, int defaultValue, IntFormat format) {
        localConfig(key, defaultValue, format);
    }

    default void addConfig(ResourceLocation key, int defaultValue) {
        addConfig(key, defaultValue, IntFormat.DECIMAL);
    }

    default void addConfig(ResourceLocation key, double defaultValue) {
        localConfig(key, defaultValue);
    }

    default void addConfig(ResourceLocation key, String defaultValue) {
        localConfig(key, defaultValue);
    }

    default <T extends Enum<T>> void addConfig(ResourceLocation key, T defaultValue) {
        localConfig(key, defaultValue);
    }

    default void addConfig(ResourceLocation key, Path path) {
        externalConfig(key, path);
    }

    default void addFeatureConfig(ResourceLocation key, boolean clientOnly) {
        featureConfig(key, clientOnly);
    }

    default void addSyncedConfig(ResourceLocation key, boolean defaultValue, boolean clientOnlyValue) {
        syncedConfig(key, defaultValue, clientOnlyValue);
    }

    default void addSyncedConfig(ResourceLocation key, int defaultValue, int clientOnlyValue, IntFormat format) {
        syncedConfig(key, defaultValue, clientOnlyValue, format);
    }

    default void addSyncedConfig(ResourceLocation key, int defaultValue, int clientOnlyValue) {
        addSyncedConfig(key, defaultValue, clientOnlyValue, IntFormat.DECIMAL);
    }

    default void addSyncedConfig(ResourceLocation key, double defaultValue, double clientOnlyValue) {
        syncedConfig(key, defaultValue, clientOnlyValue);
    }

    default void addSyncedConfig(ResourceLocation key, String defaultValue, String clientOnlyValue) {
        syncedConfig(key, defaultValue, clientOnlyValue);
    }

    default <T extends Enum<T>> void addSyncedConfig(ResourceLocation key, T defaultValue, T clientOnlyValue) {
        syncedConfig(key, defaultValue, clientOnlyValue);
    }

    default void addConfigAlias(ResourceLocation actual, ResourceLocation... aliases) {
        configAlias(actual, aliases);
    }

    default void addEventListener(IEventListener listener, int priority) {
        eventListener(listener, priority);
    }

    default void addEventListener(IEventListener listener) {
        addEventListener(listener, DEFAULT_PRIORITY);
    }

    default void addBlacklist(int priority, Block... blocks) {
        blacklist(priority, blocks);
    }

    default void addBlacklist(int priority, BlockEntityType<?>... blockEntityTypes) {
        blacklist(priority, blockEntityTypes);
    }

    default void addBlacklist(Block... blocks) {
        addBlacklist(DEFAULT_PRIORITY, blocks);
    }

    default void addBlacklist(BlockEntityType<?>... blockEntityTypes) {
        addBlacklist(DEFAULT_PRIORITY, blockEntityTypes);
    }

    void removeBlacklist(int priority, Block... blocks);

    void removeBlacklist(int priority, BlockEntityType<?>... blockEntityTypes);

    default void removeBlacklist(Block... blocks) {
        removeBlacklist(DEFAULT_PRIORITY, blocks);
    }

    default void removeBlacklist(BlockEntityType<?>... blockEntityTypes) {
        removeBlacklist(DEFAULT_PRIORITY, blockEntityTypes);
    }

    @ApiStatus.Experimental
    default <T> void addRedirect(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        redirect(provider, clazz, priority);
    }

    @ApiStatus.Experimental
    default <T> void addRedirect(IBlockComponentProvider provider, Class<T> clazz) {
        addRedirect(provider, clazz, DEFAULT_PRIORITY);
    }

    @ApiSide.ClientOnly
    default <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        override(provider, clazz, priority);
    }

    @ApiSide.ClientOnly
    default <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz) {
        addOverride(provider, clazz, DEFAULT_PRIORITY);
    }

    @ApiSide.ClientOnly
    default <T> void addIcon(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        icon(provider, clazz, priority);
    }

    @ApiSide.ClientOnly
    default <T> void addIcon(IBlockComponentProvider provider, Class<T> clazz) {
        addIcon(provider, clazz, DEFAULT_PRIORITY);
    }

    @ApiSide.ClientOnly
    default <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority) {
        switch (position) {
            case HEAD -> head(provider, clazz, priority);
            case BODY -> body(provider, clazz, priority);
            case TAIL -> tail(provider, clazz, priority);
        }
    }

    @ApiSide.ClientOnly
    default <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        addComponent(provider, position, clazz, DEFAULT_PRIORITY);
    }

    default <T> void addDataContext(IBlockComponentProvider provider, Class<T> clazz) {
        dataContext(provider, clazz);
    }

    default <T, BE extends BlockEntity> void addBlockData(IDataProvider<BE> provider, Class<T> clazz, int priority) {
        blockData(provider, clazz, priority);
    }

    default <T, BE extends BlockEntity> void addBlockData(IDataProvider<BE> provider, Class<T> clazz) {
        addBlockData(provider, clazz, DEFAULT_PRIORITY);
    }

    default void addBlacklist(int priority, EntityType<?>... entityTypes) {
        blacklist(priority, entityTypes);
    }

    default void addBlacklist(EntityType<?>... entityTypes) {
        addBlacklist(DEFAULT_PRIORITY, entityTypes);
    }

    void removeBlacklist(int priority, EntityType<?>... entityTypes);

    default void removeBlacklist(EntityType<?>... entityTypes) {
        removeBlacklist(DEFAULT_PRIORITY, entityTypes);
    }

    @ApiStatus.Experimental
    default <T> void addRedirect(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        redirect(provider, clazz, priority);
    }

    @ApiStatus.Experimental
    default <T> void addRedirect(IEntityComponentProvider provider, Class<T> clazz) {
        addRedirect(provider, clazz, DEFAULT_PRIORITY);
    }

    @ApiSide.ClientOnly
    default <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        override(provider, clazz, priority);
    }

    @ApiSide.ClientOnly
    default <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz) {
        addOverride(provider, clazz, DEFAULT_PRIORITY);
    }

    @ApiSide.ClientOnly
    default <T> void addIcon(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        icon(provider, clazz, priority);
    }

    @ApiSide.ClientOnly
    default <T> void addIcon(IEntityComponentProvider provider, Class<T> clazz) {
        addIcon(provider, clazz, DEFAULT_PRIORITY);
    }

    @ApiSide.ClientOnly
    default <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority) {
        switch (position) {
            case HEAD -> head(provider, clazz, priority);
            case BODY -> body(provider, clazz, priority);
            case TAIL -> tail(provider, clazz, priority);
        }
    }

    @ApiSide.ClientOnly
    default <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        addComponent(provider, position, clazz, DEFAULT_PRIORITY);
    }

    default <T> void addDataContext(IEntityComponentProvider provider, Class<T> clazz) {
        dataContext(provider, clazz);
    }

    default <T, E extends Entity> void addEntityData(IDataProvider<E> provider, Class<T> clazz, int priority) {
        entityData(provider, clazz, priority);
    }

    default <T, E extends Entity> void addEntityData(IDataProvider<E> provider, Class<T> clazz) {
        addEntityData(provider, clazz, DEFAULT_PRIORITY);
    }

    default <D extends IData> void addDataType(IData.Type<D> type, StreamCodec<? super RegistryFriendlyByteBuf, ? extends D> codec) {
        dataType(type, codec);
    }

    @ApiSide.ClientOnly
    @ApiStatus.Experimental
    default <T extends ITheme> void addThemeType(ResourceLocation id, IThemeType<T> type) {
        themeType(id, type);
    }

    @ApiSide.ClientOnly
    @ApiStatus.Experimental
    default void addRayCastVector(IRayCastVectorProvider provider, int priority) {
        rayCastVector(provider, priority);
    }

    @ApiSide.ClientOnly
    @ApiStatus.Experimental
    default void addRayCastVector(IRayCastVectorProvider provider) {
        addRayCastVector(provider, DEFAULT_PRIORITY);
    }

    @ApiSide.ClientOnly
    @ApiStatus.Experimental
    default void addToolType(ResourceLocation id, IToolType toolType) {
        toolType(id, toolType);
    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------
    // TODO: Remove

    /**
     * @deprecated use {@link #addFeatureConfig(ResourceLocation, boolean)}
     */
    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.22")
    default void addMergedConfig(ResourceLocation key, boolean defaultValue) {
        addFeatureConfig(key, true);
    }

    /**
     * @deprecated use {@link #addFeatureConfig(ResourceLocation, boolean)}
     */
    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.22")
    default void addMergedSyncedConfig(ResourceLocation key, boolean defaultValue, boolean clientOnlyValue) {
        addFeatureConfig(key, false);
    }

}
