package mcp.mobius.waila.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public interface IServerDataProvider<T> {

    /**
     * Callback used server side to return a custom synchronization NBTTagCompound.</br>
     * Will only be called if the implementing class is registered via {@link IRegistrar#registerBlockDataProvider}.</br>
     *
     * @param data   Current synchronization tag (might have been processed by other providers and might be processed by other providers).
     * @param player The player requesting data synchronization (The owner of the current connection).
     * @param world  The world.
     * @param t      The type targeted for synchronization.
     */
    void appendServerData(CompoundTag data, ServerPlayerEntity player, World world, T t);

}
