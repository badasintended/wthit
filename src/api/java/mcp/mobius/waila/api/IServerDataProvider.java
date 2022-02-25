package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ServerOnly
@ApiStatus.OverrideOnly
public interface IServerDataProvider<T> {

    /**
     * Callback used server side to return a custom synchronization {@link CompoundTag}.</br>
     * Will only be called if the implementing class is registered via {@link IRegistrar#addBlockData} or {@link IRegistrar#addEntityData}.</br>
     *
     * @param data     current synchronization tag (might have been processed by other providers and might be processed by other providers)
     * @param accessor contains the relevant context of the environment
     * @param config   current plugin configurations,
     *                 values <b>could be different</b> from the requesting client unless it was registered via {@link IRegistrar#addSyncedConfig}
     */
    default void appendServerData(CompoundTag data, IServerAccessor<T> accessor, IPluginConfig config) {
    }

    /**
     * @deprecated use {@link #appendServerData(CompoundTag, IServerAccessor, IPluginConfig)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.20")
    default void appendServerData(CompoundTag data, ServerPlayer player, Level world, T t) {
    }

}
