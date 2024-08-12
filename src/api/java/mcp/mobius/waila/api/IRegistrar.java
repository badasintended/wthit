package mcp.mobius.waila.api;

import java.nio.file.Path;

import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IHarvestService;
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

    @Override
    void addConfig(ResourceLocation key, boolean defaultValue);

    @Override
    void addConfig(ResourceLocation key, int defaultValue, IntFormat format);

    @Override
    default void addConfig(ResourceLocation key, int defaultValue) {
        addConfig(key, defaultValue, IntFormat.DECIMAL);
    }

    @Override
    void addConfig(ResourceLocation key, double defaultValue);

    @Override
    void addConfig(ResourceLocation key, String defaultValue);

    @Override
    <T extends Enum<T>> void addConfig(ResourceLocation key, T defaultValue);

    @Override
    void addConfig(ResourceLocation key, Path path);

    @Override
    void addFeatureConfig(ResourceLocation key, boolean clientOnly);

    @Override
    void addSyncedConfig(ResourceLocation key, boolean defaultValue, boolean clientOnlyValue);

    @Override
    void addSyncedConfig(ResourceLocation key, int defaultValue, int clientOnlyValue, IntFormat format);

    @Override
    default void addSyncedConfig(ResourceLocation key, int defaultValue, int clientOnlyValue) {
        addSyncedConfig(key, defaultValue, clientOnlyValue, IntFormat.DECIMAL);
    }

    @Override
    void addSyncedConfig(ResourceLocation key, double defaultValue, double clientOnlyValue);

    @Override
    void addSyncedConfig(ResourceLocation key, String defaultValue, String clientOnlyValue);

    @Override
    <T extends Enum<T>> void addSyncedConfig(ResourceLocation key, T defaultValue, T clientOnlyValue);

    @Override
    void addConfigAlias(ResourceLocation actual, ResourceLocation... aliases);

    @Override
    void addEventListener(IEventListener listener, int priority);

    @Override
    default void addEventListener(IEventListener listener) {
        addEventListener(listener, DEFAULT_PRIORITY);
    }

    @Override
    void addBlacklist(int priority, Block... blocks);

    @Override
    void addBlacklist(int priority, BlockEntityType<?>... blockEntityTypes);

    @Override
    default void addBlacklist(Block... blocks) {
        addBlacklist(DEFAULT_PRIORITY, blocks);
    }

    @Override
    default void addBlacklist(BlockEntityType<?>... blockEntityTypes) {
        addBlacklist(DEFAULT_PRIORITY, blockEntityTypes);
    }

    @Override
    void removeBlacklist(int priority, Block... blocks);

    @Override
    void removeBlacklist(int priority, BlockEntityType<?>... blockEntityTypes);

    @Override
    default void removeBlacklist(Block... blocks) {
        removeBlacklist(DEFAULT_PRIORITY, blocks);
    }

    @Override
    default void removeBlacklist(BlockEntityType<?>... blockEntityTypes) {
        removeBlacklist(DEFAULT_PRIORITY, blockEntityTypes);
    }

    @Override
    @ApiStatus.Experimental
    <T> void addRedirect(IBlockComponentProvider provider, Class<T> clazz, int priority);

    @Override
    @ApiStatus.Experimental
    default <T> void addRedirect(IBlockComponentProvider provider, Class<T> clazz) {
        addRedirect(provider, clazz, DEFAULT_PRIORITY);
    }

    @Override
    @ApiSide.ClientOnly
    <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz, int priority);

    @Override
    @ApiSide.ClientOnly
    default <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz) {
        addOverride(provider, clazz, DEFAULT_PRIORITY);
    }

    @Override
    @ApiSide.ClientOnly
    <T> void addIcon(IBlockComponentProvider provider, Class<T> clazz, int priority);

    @Override
    @ApiSide.ClientOnly
    default <T> void addIcon(IBlockComponentProvider provider, Class<T> clazz) {
        addIcon(provider, clazz, DEFAULT_PRIORITY);
    }

    @Override
    @ApiSide.ClientOnly
    <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority);

    @Override
    @ApiSide.ClientOnly
    default <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        addComponent(provider, position, clazz, DEFAULT_PRIORITY);
    }

    @Override
    <T> void addDataContext(IBlockComponentProvider provider, Class<T> clazz);

    @Override
    @ApiSide.ServerOnly
    <T, BE extends BlockEntity> void addBlockData(IDataProvider<BE> provider, Class<T> clazz, int priority);

    @Override
    default <T, BE extends BlockEntity> void addBlockData(IDataProvider<BE> provider, Class<T> clazz) {
        addBlockData(provider, clazz, DEFAULT_PRIORITY);
    }

    @Override
    void addBlacklist(int priority, EntityType<?>... entityTypes);

    @Override
    default void addBlacklist(EntityType<?>... entityTypes) {
        addBlacklist(DEFAULT_PRIORITY, entityTypes);
    }

    @Override
    void removeBlacklist(int priority, EntityType<?>... entityTypes);

    @Override
    default void removeBlacklist(EntityType<?>... entityTypes) {
        removeBlacklist(DEFAULT_PRIORITY, entityTypes);
    }

    @Override
    @ApiStatus.Experimental
    <T> void addRedirect(IEntityComponentProvider provider, Class<T> clazz, int priority);

    @Override
    @ApiStatus.Experimental
    default <T> void addRedirect(IEntityComponentProvider provider, Class<T> clazz) {
        addRedirect(provider, clazz, DEFAULT_PRIORITY);
    }

    @Override
    @ApiSide.ClientOnly
    <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz, int priority);

    @Override
    @ApiSide.ClientOnly
    default <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz) {
        addOverride(provider, clazz, DEFAULT_PRIORITY);
    }

    @Override
    @ApiSide.ClientOnly
    <T> void addIcon(IEntityComponentProvider provider, Class<T> clazz, int priority);

    @Override
    @ApiSide.ClientOnly
    default <T> void addIcon(IEntityComponentProvider provider, Class<T> clazz) {
        addIcon(provider, clazz, DEFAULT_PRIORITY);
    }

    @Override
    @ApiSide.ClientOnly
    <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority);

    @Override
    @ApiSide.ClientOnly
    default <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        addComponent(provider, position, clazz, DEFAULT_PRIORITY);
    }

    @Override
    <T> void addDataContext(IEntityComponentProvider provider, Class<T> clazz);

    @Override
    @ApiSide.ServerOnly
    <T, E extends Entity> void addEntityData(IDataProvider<E> provider, Class<T> clazz, int priority);

    @Override
    default <T, E extends Entity> void addEntityData(IDataProvider<E> provider, Class<T> clazz) {
        addEntityData(provider, clazz, DEFAULT_PRIORITY);
    }

    @Override
    <D extends IData> void addDataType(IData.Type<D> type, StreamCodec<? super RegistryFriendlyByteBuf, ? extends D> codec);

    @Override
    @ApiSide.ClientOnly
    @ApiStatus.Experimental
    <T extends ITheme> void addThemeType(ResourceLocation id, IThemeType<T> type);

    @Override
    @ApiSide.ClientOnly
    @ApiStatus.Experimental
    void addRayCastVector(IRayCastVectorProvider provider, int priority);

    @Override
    @ApiSide.ClientOnly
    @ApiStatus.Experimental
    default void addRayCastVector(IRayCastVectorProvider provider) {
        addRayCastVector(provider, DEFAULT_PRIORITY);
    }

    @Override
    @ApiSide.ClientOnly
    @ApiStatus.Experimental
    default void addToolType(ResourceLocation id, IToolType toolType) {
        IHarvestService.INSTANCE.addToolType(id, toolType);
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
