package mcp.mobius.waila.api;

import mcp.mobius.waila.api.internal.ApiSide;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ServerOnly
@ApiStatus.OverrideOnly
public interface IServerDataProvider<T> {

    /**
     * Callback used server side to return a custom synchronization NBTTagCompound.</br>
     * Will only be called if the implementing class is registered via {@link IRegistrar#addBlockData} or {@link IRegistrar#addEntityData}.</br>
     *
     * @param data   Current synchronization tag (might have been processed by other providers and might be processed by other providers).
     * @param player The player requesting data synchronization (The owner of the current connection).
     * @param world  The world.
     * @param t      The type targeted for synchronization.
     */
    void appendServerData(CompoundTag data, ServerPlayer player, Level world, T t);

}
